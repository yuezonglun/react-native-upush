package com.liuyun.upush;

import android.app.Notification;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UPushModule extends ReactContextBaseJavaModule {


    public static final String TAG = UPushModule.class.getName();

    public static final String OnReceiveMessage = "onReceiveMessage";
    public static final String OnOpenMessage = "onOpenMessage";


    private ReactApplicationContext reactContext;

    public UPushModule(ReactApplicationContext reactContext) {
        super(reactContext);

        this.reactContext = reactContext;

        addListeners();
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(OnReceiveMessage, OnReceiveMessage);
        constants.put(OnOpenMessage, OnOpenMessage);
        return constants;
    }

    @Override
    public String getName() {
        return "UPush";
    }


    /**
     * 获取devicetoken
     *
     * @param callback
     */
    @ReactMethod
    public void getDeviceToken(Callback callback) {
        PushAgent mPushAgent = PushAgent.getInstance(reactContext);
        String registrationId = mPushAgent.getRegistrationId();
        callback.invoke(registrationId);
    }

    private void addListeners() {
        PushAgent mPushAgent = PushAgent.getInstance(reactContext);

        mPushAgent.setNotificationClickHandler(new UmengNotificationClickHandler() {
            @Override
            public void launchApp(Context context, UMessage msg) {
                super.launchApp(context, msg);
                sendMessage(UPushModule.OnOpenMessage, msg);
            }

            @Override
            public void openUrl(Context context, UMessage msg) {
                super.openUrl(context, msg);
                sendMessage(UPushModule.OnOpenMessage, msg);
            }

            @Override
            public void openActivity(Context context, UMessage msg) {
                super.openActivity(context, msg);
                sendMessage(UPushModule.OnOpenMessage, msg);
            }

            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                super.dealWithCustomAction(context, msg);
                sendMessage(UPushModule.OnOpenMessage, msg);
            }
        });

        //设置消息和通知的处理
        mPushAgent.setMessageHandler(new UmengMessageHandler() {
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                //发送
                sendMessage(UPushModule.OnReceiveMessage, msg);
                return super.getNotification(context, msg);
            }

            @Override
            public void dealWithCustomMessage(Context context, UMessage msg) {
                super.dealWithCustomMessage(context, msg);
                //这里可以自定义通知打开动作
            }
        });
    }

    /**
     * 向前台发送消息
     * @param type
     * @param msg
     */
    private void sendMessage(String type, UMessage msg) {
        if(reactContext.hasActiveCatalystInstance()) {
            WritableMap params = convertToWriteMap(msg);
            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(type, params);
        }
        else {
            Log.i(TAG, "not hasActiveCatalystInstance");
        }
    }

    private WritableMap convertToWriteMap(UMessage msg) {
        WritableMap map = Arguments.createMap();
        //遍历Json
        JSONObject jsonObject = msg.getRaw();
        Iterator<String> keys = jsonObject.keys();
        String key;
        while (keys.hasNext()) {
            key = keys.next();
            try {
                map.putString(key, jsonObject.get(key).toString());
            }
            catch (Exception e) {
                Log.e(TAG, "putString fail");
            }
        }
        return map;
    }


}
