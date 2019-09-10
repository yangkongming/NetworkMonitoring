package www.pdx.life.networkmonitoring.jsbrage;

import android.support.annotation.Keep;

/**
 * Js -> Native
 * Created by Jingyi on 17-3-16.
 */
@Keep
public class Message {

    public String uri;
    public String method;
    public String data;
    public String success;
    public String fail;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{uri:").append(uri).append(";");
        sb.append("method:").append(method).append(";");
        sb.append("data:").append(data).append(";");
        sb.append("success:").append(success).append(";");
        sb.append("fail:").append(fail).append("}");
        return sb.toString();
    }
}
