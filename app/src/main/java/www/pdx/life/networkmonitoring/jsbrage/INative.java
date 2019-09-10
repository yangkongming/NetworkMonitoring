package www.pdx.life.networkmonitoring.jsbrage;

/**
 * 能够解析Message的对象
 * Created by Jingyi on 17-3-16.
 */

public interface INative {

    void doNative(IBridgeClient client, Message message);
}
