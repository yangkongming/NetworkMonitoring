package www.pdx.life.networkmonitoring.jsbrage.web;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;


import java.util.HashMap;
import java.util.Map;

import www.pdx.life.networkmonitoring.jsbrage.IBridgeClient;
import www.pdx.life.networkmonitoring.jsbrage.INative;

/**
 * Created by lanmang on 2017/7/26.
 * http://wiki.okcoin-inc.com/pages/viewpage.action?pageId=39387372
 */

public class BridgeWebViewManager implements IBridgeClient {

    private WebView mWebView;
    private HashMap<Class<? extends INative>, INative> map = new HashMap();
    private Callback mCallback = new Callback() {
        @Override
        public void callback(String func, String param) {
            BridgeWebViewManager.this.callback(func, param);
        }
    };

    public BridgeWebViewManager(WebView webView) {
        this.mWebView = webView;
        map.put(Window.class, new Window(getContext(), mCallback));
        map.put(Request.class, new Request(getContext(), mCallback));
    }

    @Override
    public Context getContext() {
        return mWebView.getContext();
    }

    @Override
    public void callback(String func, String param) {
        if (TextUtils.isEmpty(func)) {
            Log.w("web", "empty callback function name!");
            return;
        }
        if (TextUtils.isEmpty(param)) {
            mWebView.loadUrl("javascript:" + func + "();");
        } else {
            mWebView.loadUrl("javascript:" + func + "(\'" + param + "\');");
        }
    }

    @Override
    public <T extends INative> INative dispatchNative(Class<T> clazz) {
        return map.get(clazz);
    }

    public void release() {
        mWebView = null;
        for (Map.Entry<Class<? extends INative>, INative> entry : map.entrySet()) {
            INative iNative = entry.getValue();
            if (iNative instanceof IManager) {
                ((IManager) iNative).release();
            }
        }
        map.clear();
    }

    public interface Callback {
        void callback(String func, String param);
    }

}
