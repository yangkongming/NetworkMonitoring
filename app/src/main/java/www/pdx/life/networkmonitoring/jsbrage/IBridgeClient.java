package www.pdx.life.networkmonitoring.jsbrage;

import android.content.Context;

/**
 * 和JsBridge交互的对象
 * Created by Jingyi on 17-3-16.
 */
public interface IBridgeClient {

    Context getContext();

    void callback(String func, String param);

    <T extends INative> INative dispatchNative(Class<T> clazz);
}
