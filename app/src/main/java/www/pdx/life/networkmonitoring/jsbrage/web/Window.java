package www.pdx.life.networkmonitoring.jsbrage.web;

import android.content.Context;

import www.pdx.life.networkmonitoring.jsbrage.IBridgeClient;
import www.pdx.life.networkmonitoring.jsbrage.INative;
import www.pdx.life.networkmonitoring.jsbrage.Message;


/**
 * 窗口操作类
 * Created by GuoJingyi on 17-3-16.
 * http://wiki.okcoin-inc.com/pages/viewpage.action?pageId=39387372
 */

public class Window extends IManager implements INative {


    private Message mNativeLoginMessage;

    public Window(Context context, final BridgeWebViewManager.Callback callback) {
        super(context, callback);

    }

    @Override
    public void doNative(IBridgeClient client, Message message) {
        if (mContext == null) return;
        switch (message.method) {
            case JsBridgeHelper.METHOD_CLOSE:
                return;
            case JsBridgeHelper.METHOD_NAV:
                return;
            case JsBridgeHelper.METHOD_LOGIN_INFO:
                return;
            case JsBridgeHelper.METHOD_NATIVE_LOGIN:
                return;
            case JsBridgeHelper.METHOD_OPEN_BROWSER:
                return;
            default:
                invokeFail(message, "function undefined");
                break;
        }
    }

}
