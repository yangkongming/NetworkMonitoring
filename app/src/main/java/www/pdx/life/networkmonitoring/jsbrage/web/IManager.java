package www.pdx.life.networkmonitoring.jsbrage.web;

import android.content.Context;

import www.pdx.life.networkmonitoring.jsbrage.Message;


/**
 * Created by lanmang on 2017/7/27.
 */

public abstract class IManager {

    public Context mContext;
    public BridgeWebViewManager.Callback mCallback;

    IManager(Context context, BridgeWebViewManager.Callback callback) {
        this.mContext = context;
        this.mCallback = callback;
    }


    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public Context getContext() {
        return mContext;
    }

    public void release() {
        mContext = null;
        mCallback = null;
    }

    void invokeSuccess(Message message, String param) {
        mCallback.callback(message.success, param);
    }

    void invokeFail(Message message, String param) {
        mCallback.callback(message.fail, param);
    }

}
