package www.pdx.life.networkmonitoring.httpdebug.interceptor;

import android.util.Log;

import com.okinc.biz.config.BizConfigKey;
import com.okinc.components.track.TrackExtKt;
import com.okinc.core.config.ConfigExtKt;
import com.okinc.core.net.http.HttpReportEvent;
import com.okinc.debugbox.FloatWindowManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor.Logger;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Requests Debug 调试
 */

public class HttpInterceptor implements Interceptor {

    private static final String TAG = "HttpInterceptor";
    private long REPORT_TIMEOUT_MS = 2 * 1000;

    public HttpInterceptor(Logger logger) {
        this.logger = logger;
    }

    private final Logger logger;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        String requestStartMessage = "--> " + request.method() + ' ' + request.url().encodedPath();
        logger.log(requestStartMessage);

        long startNs = System.nanoTime();
        Response response = null;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            logger.log("<-- HTTP FAILED: " + e);

            try {
                if (response != null && response.request() != null && response.request().url() != null) {
                    // 返回错误，上报打点
                    HttpReportEvent httpReportEvent = new HttpReportEvent(
                            HttpReportEvent.REQUEST_ERROR,
                            String.valueOf(response.code()) + "|" + response.request().url().encodedPath(),
                            String.valueOf(response.code()),
                            response.message(),
                            String.valueOf(TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startNs)),
                            String.valueOf(response.code()) + "|"
                                    + response.request().url().encodedPath() + "|"
                                    + TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startNs) + "|"
                                    + response.message());
                    TrackExtKt.onEvent(httpReportEvent.key, httpReportEvent.map);
                    if (ConfigExtKt.getConfigValue(BizConfigKey.KEY_IS_HTTP_DEBUG_ENABLE, false)) {
                        FloatWindowManager.Companion.get().addMsg(request, e.getMessage());
                    }
                } else {
                    if (ConfigExtKt.getConfigValue(BizConfigKey.KEY_IS_HTTP_DEBUG_ENABLE, false)) {
                        FloatWindowManager.Companion.get().addMsg(request, e.getMessage());
                    }
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        long contentLength;
        try {
            contentLength = response.body().contentLength();
            if (ConfigExtKt.getConfigValue(BizConfigKey.KEY_IS_HTTP_DEBUG_ENABLE, false)) {
                ResponseBody body = cloneResponseBody(response);
                FloatWindowManager.Companion.get().addMsg(response, body);
            }

        } catch (Exception ex) {
            contentLength = -1;
        }
        String bodySize = contentLength != -1 ? contentLength + "-byte body" : "";
        logger.log("<-- " + response.code() + ' ' + response.request().url().encodedPath() +
                " (" + tookMs + "ms" + ", " + bodySize + ")");

        // 返回超过限定时长 或 请求不成功，上报打点
        try {
            if (response != null
                    && (!response.isSuccessful() || tookMs > REPORT_TIMEOUT_MS)
                    && response.request() != null
                    && response.request().url() != null) {

                HttpReportEvent httpReportEvent = new HttpReportEvent(
                        String.valueOf(response.code()) + "|" + response.request().url().encodedPath(),
                        String.valueOf(response.code()),
                        response.message(),
                        String.valueOf(Math.round((float) tookMs / 1000)),
                        String.valueOf(response.code()) + "|"
                                + response.request().url().encodedPath() + "|"
                                + String.valueOf(Math.round((float) tookMs / 1000)) + "|"
                                + response.message());

                if (!response.isSuccessful()) {
                    httpReportEvent.setKey(HttpReportEvent.REQUEST_ERROR);
                } else if (tookMs > REPORT_TIMEOUT_MS) {
                    httpReportEvent.setKey(HttpReportEvent.REQUEST_TIMEOUT);
                }
                TrackExtKt.onEvent(httpReportEvent.key, httpReportEvent.map);
                Log.d(TAG, "request timeout :" + "request " + response.code() + ' ' + response.request().url().encodedPath() +
                        " (" + tookMs + "ms" + ", " + bodySize + ")");
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return response;
    }


    /**
     * @param response 当前请求的返回对象
     * @return 当前请求的返回对象的Body 的克隆对象
     */
    private ResponseBody cloneResponseBody(Response response) {
        /**
         *  这里的方法会对 ResponseBody的 source 进行克隆并返回指定的长度!
         *  我们需哟的是是一份完全的克隆体 所以传入的就是原ResponseBody的 长度
         */
        ResponseBody result;
        try {
            ResponseBody body = response.body();
            BufferedSource source = body.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer();
            Buffer copyBuffer = buffer.clone();
            /***
             * 也可以直接生成String 返回
             */
//            String json = copyBuffer.readString(charset(body));
//            result = new RealResponseBody(body.contentType(), copyBuffer);
            result = ResponseBody.create(body.contentType(), copyBuffer.size(), copyBuffer);
        } catch (IOException e) {
            result = null;
        }
        return result;
    }

}
