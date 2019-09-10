package www.pdx.life.networkmonitoring.jsbrage.web;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import www.pdx.life.networkmonitoring.jsbrage.IBridgeConverter;
import www.pdx.life.networkmonitoring.jsbrage.JsBridge;
import www.pdx.life.networkmonitoring.jsbrage.Message;

/**
 * JsBridge Helper
 * Created by GuoJingyi on 17-3-16.
 * http://wiki.okcoin-inc.com/pages/viewpage.action?pageId=39387372
 */

public class JsBridgeHelper {
    private static final String URI_WINDOW = "window";
    private static final String URI_REQUEST = "request";
    static final String METHOD_CLOSE = "close";//关闭页面
    static final String METHOD_NAV = "nav";//本地映射跳转
    static final String METHOD_LOGIN_INFO = "loginInfo";//同步登录信息到本地
    static final String METHOD_NATIVE_LOGIN = "nativeLogin";//调起原生登录, 登录后同步登录状态到h5
    static final String METHOD_REQUEST_LOGIN = "login";//调用本地登录接口
    static final String METHOD_SHARE = "share";//分享图文链接
    static final String METHOD_NATIVE_SHARE = "nativeShare";//分享图文链接
    static final String METHOD_SHARE_IMAGE = "shareImage";//分享图片
    static final String METHOD_SHARE_IMAGE_BASE64 = "shareImageBase64";//分享base64图片
    static final String METHOD_OPEN_BROWSER = "openBrowser";//开启外部浏览器
    static final String METHOD_COPY = "copy";
    static final String METHOD_GET_TOKEN = "getToken";
    static final String METHOD_GET_TK = "getTK";//获取用户tk
    static final String METHOD_GET_TDEVICEID = "getDeviceID";//获取设备id
    static final String METHOD_GET_WEEX_CONFIG = "getWeexConfig";  //获取weex config
    static final String METHOD_CREATE_IMAGE_WITH_MARK = "createImageWithMark";//H5调用APP获取分享图片
    static final String METHOD_REQUEST_ERCODE = "createQRCodeImage";//H5调用APP获取二维码图片
    static final String METHOD_REQUEST_VERIFY = "verify";//
    static final String METHOD_REQUEST_SETTING = "setting";//webview设置

    public static void init() {
        JsBridge.Builder.create()
                .addNative(URI_WINDOW, Window.class)
                .addNative(URI_REQUEST, Request.class)
                .setConverter(new BridgeConverter())
                .build();
    }

    private static class BridgeConverter implements IBridgeConverter {

        @Override
        public Message parseMessage(String msgStr) {
            if (msgStr.startsWith("{")) {
                return JSON.parseObject(msgStr, Message.class);
            } else {
                try {
                    String[] s1 = msgStr.split("\\?");
                    String[] s2 = s1[0].split("\\.");
                    String uri = s2[0];
                    if (TextUtils.isEmpty(uri)) return null;
                    String method = s2[1];
                    if (TextUtils.isEmpty(method)) return null;
                    Message message = new Message();
                    message.uri = uri;
                    message.method = method;
                    try {
                        if (s1.length > 1) {
                            String[] s3 = s1[1].split("&");
                            for (String s4 : s3) {
                                String[] ss = s4.split("=");
                                if ("data".equals(ss[0])) message.data = ss[1];
                                else if ("success".equals(ss[0])) message.success = ss[1];
                                else if ("fail".equals(ss[0])) message.fail = ss[1];
                            }
                        }
                    } catch (Exception e) {
                        return message;
                    }
                    return message;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }
}
