import React, {
    NativeModules,
    NativeAppEventEmitter,
    DeviceEventEmitter,
    AppState,
    Platform,
} from 'react-native';

const UPushModule = NativeModules.UmengPush;


export default UPush = {
    getDeviceToken(handler: Function) {
        UPushModule.getDeviceToken(handler);
    },

    onReceiveListener(handler: Function){
        //处于后台时，拦截收到的消息

        this.addEventListener("onReceiveMessage", (params) =>{
            if(AppState.currentState === 'background') {
                return;
            }
            handler(params);
        });
    },

    onOpenListener(handler: Function){
        //处于后台时，拦截收到的消息
        DeviceEventEmitter.addListener("onOpenMessage", (params) =>{
            if(AppState.currentState === 'background') {
                return;
            }
            handler(params);
        });
    },

    addEventListener(eventName: string, handler: Function) {
        if(Platform.OS === 'android') {
            return DeviceEventEmitter.addListener(eventName, (event) => {
                handler(event);
            });
        }
        else {
            return NativeAppEventEmitter.addListener(
                eventName, (userInfo) => {
                    handler(userInfo);
                });
        }
    },
};

