package www.pdx.life.networkmonitoring.jsbrage.web;

import android.content.Context;

import www.pdx.life.networkmonitoring.jsbrage.IBridgeClient;
import www.pdx.life.networkmonitoring.jsbrage.INative;
import www.pdx.life.networkmonitoring.jsbrage.Message;

import static www.pdx.life.networkmonitoring.jsbrage.web.JsBridgeHelper.METHOD_COPY;
import static www.pdx.life.networkmonitoring.jsbrage.web.JsBridgeHelper.METHOD_CREATE_IMAGE_WITH_MARK;
import static www.pdx.life.networkmonitoring.jsbrage.web.JsBridgeHelper.METHOD_GET_TDEVICEID;
import static www.pdx.life.networkmonitoring.jsbrage.web.JsBridgeHelper.METHOD_GET_TK;
import static www.pdx.life.networkmonitoring.jsbrage.web.JsBridgeHelper.METHOD_GET_TOKEN;
import static www.pdx.life.networkmonitoring.jsbrage.web.JsBridgeHelper.METHOD_GET_WEEX_CONFIG;
import static www.pdx.life.networkmonitoring.jsbrage.web.JsBridgeHelper.METHOD_NATIVE_SHARE;
import static www.pdx.life.networkmonitoring.jsbrage.web.JsBridgeHelper.METHOD_REQUEST_ERCODE;
import static www.pdx.life.networkmonitoring.jsbrage.web.JsBridgeHelper.METHOD_REQUEST_LOGIN;
import static www.pdx.life.networkmonitoring.jsbrage.web.JsBridgeHelper.METHOD_REQUEST_SETTING;
import static www.pdx.life.networkmonitoring.jsbrage.web.JsBridgeHelper.METHOD_REQUEST_VERIFY;
import static www.pdx.life.networkmonitoring.jsbrage.web.JsBridgeHelper.METHOD_SHARE;
import static www.pdx.life.networkmonitoring.jsbrage.web.JsBridgeHelper.METHOD_SHARE_IMAGE;
import static www.pdx.life.networkmonitoring.jsbrage.web.JsBridgeHelper.METHOD_SHARE_IMAGE_BASE64;

/**
 * Created by lanmang on 2017/7/27.
 * http://wiki.okcoin-inc.com/pages/viewpage.action?pageId=39387372
 * <p>
 * 线上的测试地址
 * web
 * *http://daily-test.okcoin-inc.com/cdn/assets/okfe/util/jsbridge/jsbridge/
 * weex
 * http://daily-test.okcoin-inc.com/cdn/assets/okfe/redbag/0.0.1/www/bridge/bridge.js?wh_weex=true
 */
public class Request extends IManager implements INative {


    public Request(Context context, BridgeWebViewManager.Callback callback) {
        super(context, callback);
    }

    public void release() {
        super.release();
    }

    @Override
    public void doNative(IBridgeClient client, Message message) {
        if (mContext == null) return;
        switch (message.method) {
            case METHOD_REQUEST_LOGIN:
                break;
            case METHOD_NATIVE_SHARE:
                break;
            case METHOD_SHARE:
                break;
            case METHOD_SHARE_IMAGE:
                break;
            case METHOD_SHARE_IMAGE_BASE64:
                break;
            case METHOD_COPY:
                break;
            case METHOD_GET_TOKEN:
                break;
            case METHOD_GET_TK:
                break;
            case METHOD_GET_TDEVICEID:
                getDeviceID(message);
                break;
            case METHOD_GET_WEEX_CONFIG:
                break;
            case METHOD_CREATE_IMAGE_WITH_MARK:
                break;
            case METHOD_REQUEST_ERCODE:
                break;
            case METHOD_REQUEST_VERIFY:
                break;
            case METHOD_REQUEST_SETTING:

                break;
            default:
                invokeFail(message, "function undefined");
                break;
        }
    }

    /**
     * 获取设备Id
     *
     * @param message
     */
    private void getDeviceID(final Message message) {
        String deviceId = "1234567890";
        invokeSuccess(message, deviceId);
    }

}
