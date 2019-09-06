package www.pdx.life.networkmonitoring.httpdebug.interceptor;

import java.util.HashMap;
import java.util.Map;

/**
 * Http 事件打点
 */

public class HttpReportEvent {
    public static String REQUEST_ERROR = "requestError"; //请求失败
    public static String REQUEST_TIMEOUT = "requestTimeout"; //请求超时
    public Map<String, String> map = new HashMap<>();
    public String key;
    public Throwable t;
    private String mPath = "path"; //请求路径
    private String mCode = "code";  //错误码
    private String mMsg = "msg";   //错误信息
    private String mTime = "time";   //请求时常，秒
    private String mFullMsg = "full_msg";   //详细信息

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public HttpReportEvent(String key, String path, String code, String msg, String time, String fullMsg) {
        this.key = key;
        this.map.put(mPath, path);
        this.map.put(mCode, code);
        this.map.put(mMsg, msg);
        this.map.put(mTime, time);
        this.map.put(mFullMsg, fullMsg);
    }

    public HttpReportEvent(String path, String code, String msg, String time, String fullMsg) {
        this.map.put(mPath, path);
        this.map.put(mCode, code);
        this.map.put(mMsg, msg);
        this.map.put(mTime, time);
        this.map.put(mFullMsg, fullMsg);
    }

}
