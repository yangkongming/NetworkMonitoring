package www.pdx.life.networkmonitoring.jsbrage;


import android.util.Log;

import java.util.HashMap;

/**
 * Simple JsBridge for md
 * Created by Jingyi on 17-3-16.
 */

public class JsBridge {

    private static class JsBridgeHolder {
        private static JsBridge instance = new JsBridge();
    }

    private static JsBridge getInstance() {
        return JsBridgeHolder.instance;
    }

    public static void handleMessage(IBridgeClient client, String msgStr){
        JsBridgeHolder.instance._handleMessage(client, msgStr);
    }

    public static void handleMessage(IBridgeClient client, Message message){
        JsBridgeHolder.instance._handleMessage(client, message);
    }

    public static Message parseMessage(String msgStr){
        return JsBridgeHolder.instance._parseMessage(msgStr);
    }

    private JsBridge(){
        mNativeClassMap = new HashMap<>();
        mNativeMap = new HashMap<>();
    }

    private IBridgeConverter mConverter;
    private HashMap<String, Class<? extends INative>> mNativeClassMap;
    private HashMap<String, INative> mNativeMap;
    private boolean isInit;

    private Message _parseMessage(String msgStr){
        if(!isInit){
            Log.e("Jsbridge", "JsBridge is not init...");
            return null;
        }
        return mConverter.parseMessage(msgStr);
    }

    private void _handleMessage(IBridgeClient client, String msgStr){
        Message message=_parseMessage(msgStr);
        _handleMessage(client,message);
    }

    private void _handleMessage(IBridgeClient client, Message message){
        if(message == null){
            Log.e("Jsbridge", "Message is illegal");
            return;
        }
        if(mNativeMap.containsKey(message.uri)) {
            mNativeMap.get(message.uri).doNative(client, message);
        } else if(mNativeClassMap.containsKey(message.uri)){
            INative iNative = client.dispatchNative(mNativeClassMap.get(message.uri));
            if(iNative == null){
                try {
                    iNative = mNativeClassMap.get(message.uri).newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Jsbridge", message.uri + " cannot instance");
                    return;
                }
            }
            if(iNative != null) iNative.doNative(client, message);
        }
        return;
    }

    public static class Builder {
        private JsBridge mJsBridge;

        public static Builder create(){
            return new Builder();
        }

        private Builder(){
            mJsBridge = JsBridge.getInstance();
        }

        public Builder addNative(String name, INative iNative){
            if(iNative != null) mJsBridge.mNativeMap.put(name, iNative);
            return this;
        }

        public Builder addNative(String name, Class<? extends INative> clazz){
            if(clazz != null) mJsBridge.mNativeClassMap.put(name, clazz);
            return this;
        }

        public Builder setConverter(IBridgeConverter converter){
            mJsBridge.mConverter = converter;
            return this;
        }

        public void build(){
            if(mJsBridge.mConverter != null)
                mJsBridge.isInit = true;
        }
    }
}
