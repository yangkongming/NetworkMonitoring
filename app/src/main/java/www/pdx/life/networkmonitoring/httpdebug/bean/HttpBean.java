package www.pdx.life.networkmonitoring.httpdebug.bean;


import okhttp3.Headers;

public class HttpBean {
    public String method;
    public String code;
    public String time;
    public String path;
    public Headers responseHeaders;
    public Headers requestHeaders;
    public String body;

    public HttpBean(String method, String code, String time, String path, Headers responseHeaders, Headers requestHeaders, String body) {
        this.method = method;
        this.code = code;
        this.time = time;
        this.path = path;
        this.responseHeaders = responseHeaders;
        this.requestHeaders = requestHeaders;
        this.body = body;
    }

}
