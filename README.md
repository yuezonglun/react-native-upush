# react-native-upush
近期由于产品需求，需要在react-native项目上集成友盟推送，笔者翻阅各种这方面资料后，看到RN论坛上面有位大神给出了具体的集成方案，请参考：[react-native-umeng-push](https://github.com/liuchungui/react-native-umeng-push)。
不过笔者测试后发现ios很是顺畅，能够正常接收到消息，但是android却各种不行，官网查询的结果是“无状态”或者“离线”，各种尝试后终于没能成功。无奈之下将友盟的sdk升级到最新的4.0版本，最后终于可以收到推送了。
# 安装
```
npm install react-native-upush
react-native link
```
# ios集成
在Appdelegate.m中对应的位置添加如下三个API：
```
#import "RCTUmengPush.h"

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
  //注册友盟推送
  [RCTUmengPush registerWithAppkey:@"your app key" launchOptions:launchOptions];
}

- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken
{
  //获取deviceToken
  [RCTUmengPush application:application didRegisterDeviceToken:deviceToken];
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo
{
  //获取远程推送消息
  [RCTUmengPush application:application didReceiveRemoteNotification:userInfo];
}
```
参考： https://developer.apple.com/library/content/documentation/IDEs/Conceptual/AppDistributionGuide/AddingCapabilities/AddingCapabilities.html#//apple_ref/doc/uid/TP40012582-CH26-SW6
启用推送设置 Enabling Push Notifications（否则会报 iOS device_token 无效）

# Android集成
注意：笔者是针对友盟sdk4.0的集成教程，pushsdk 2.8版本传送门：[react-native-umeng-push](https://github.com/liuchungui/react-native-umeng-push)。
## 1、pushsdk集成
由于这个库依赖于[react-native-upush-sdk](https://github.com/wwx193433/react-native-upush-sdk)，需要在你的工程settings.gradle文件中添加pushsdk。

```
include ':pushsdk'
project(':pushsdk').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-upush-sdk/android/pushsdk')
```

## 2、友盟的sdk初始化
注意：友盟的官方规定一定要在项目的主进程中进行初始化操作，所以我们可以将初始化的方法放在MainApplication中的onCreate中。关键参考代码：
```
public class MainApplication extends Application implements ReactApplication {

    private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
        ....
        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                    new MainReactPackage(),
                    new UPushPackage()
            );
        ...
    };

    @Override
    public void onCreate() {
        super.onCreate();
        
        //友盟配置
        UMConfigure.init(this, "your key", null, UMConfigure.DEVICE_TYPE_PHONE, "your secret");
        
        //推送服务注册
        registerUPush();
        
        SoLoader.init(this, /* native exopackage */ false);
    }

    private void registerUPush() {
        PushAgent mPushAgent = PushAgent.getInstance(this);
        //注册推送服务
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                ...
            }
            @Override
            public void onFailure(String s, String s1) {
               ...
            }
        });
        PushAgent.getInstance(this).onAppStart();
    }
}
```
## 3、常见问题
 1、[android获取不到deviceToken问题？](http://bbs.umeng.com/thread-5547-1-1.html)<br/>
 2、[设备不在状态、离线问题或pushservice服务问题？](http://bbs.umeng.com/thread-14055-1-1.html)

## 4、其它
注：如果是android6.0以上的api编译，需要在pushsdk的build.gradle文件的android{}块内添加<br/>
useLibrary 'org.apache.http.legacy'。

# API调用
|API|Note|
------------- | -------------
|getDeviceToken	|获取DeviceToken|
|onReceiveListener|接收到推送消息回调的方法|
|onOpenListener	|点击推送消息打开应用回调的方法|

# Usage
```
import UPush from 'react-native-upush';

export default class App extends Component {
    constructor(props) {
        super(props);
    }
    componentDidMount(){
        UPush.getDeviceToken(deviceToken => {
            console.log("deviceToken: ", deviceToken);
        });

        UPush.onReceiveListener((msg)=>{
            console.log(msg);
        });

        UPush.onOpenListener((msg)=>{
            console.log(msg);
        });
    }
    render() {
        return (
            <View style={styles.container}>
            </View>
        );
    }
}
```

# 特此鸣谢
 [liuchungui/react-native-umeng-push](https://github.com/liuchungui/react-native-umeng-push)
 
# other
* 欢迎大家Pull Request
* 有什么疑问，欢迎提问题
* 觉得好的，来一个star

      
