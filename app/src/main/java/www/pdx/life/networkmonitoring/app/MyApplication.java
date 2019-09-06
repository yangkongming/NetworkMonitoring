package www.pdx.life.networkmonitoring.app;

import android.app.Application;
import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import www.pdx.life.networkmonitoring.httpdebug.interceptor.HttpInterceptor;
import www.pdx.life.networkmonitoring.httpdebug.interceptor.RequestsLogger;

public class MyApplication extends Application {

    public static Context context;
    OkHttpClient.Builder builder;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        builder = new OkHttpClient
                .Builder()
                .readTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        HttpInterceptor logging = new HttpInterceptor(new RequestsLogger());
        builder.addInterceptor(logging);
    }

    public OkHttpClient.Builder getBuilder() {
        return builder;
    }

    public void setBuilder(OkHttpClient.Builder builder) {
        this.builder = builder;
    }
}
