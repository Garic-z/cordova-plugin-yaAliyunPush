# cordova-plugin-yaAliyunpush

## iOS:请将阿里云官网下载的配置文件AliyunEmasServices-Info.plist  已 source code 方式替换本插件.plist

## Install

> 注意：
> - 应用的包名一定要和 APP_KEY 对应应用的包名一致，否则推送服务无法注册成功。
> - ANDROID_KEY:对应的android key
> - ANDROID_SECRET:ANDROID SECRET
> - IOS_KEY:ios key
> - IOS_SECRET:ios SECRET
> - Capabilities 中打开 Push Notifications 开关在 XCode7 中这里的开关不打开，推送也是可以正常使用的，但是在 XCode8 中，这里的开关必须要打开

- 通过 Cordova Plugins 安装，要求 Cordova CLI 5.0+：

  ```shell
  cordova plugin add https://github.com/Garic-z/cordova-plugin-yaAliyunPush.git --variable ANDROID_KEY=${ANDROID_KEY} --variable ANDROID_SECRET=${ANDROID_SECRET} --variable IOS_KEY=${IOS_KEY} --variable IOS_SECRET=${IOS_SECRET}
  ```
  cordova plugin add https://github.com/Garic-z/cordova-plugin-yaAliyunPush.git --variable ANDROID_KEY=1111111 --variable ANDROID_SECRET=1111111 --variable IOS_KEY=1111111 --variable IOS_SECRET=1111111
  
- 或下载到本地安装：

  ```shell
  cordova plugin add Your_Plugin_Path --variable ANDROID_KEY=${ANDROID_KEY} --variable ANDROID_SECRET=${ANDROID_SECRET} --variable IOS_KEY=${IOS_KEY} --variable IOS_SECRET=${IOS_SECRET}
  ```
  cordova plugin add Your_Plugin_Path --variable ANDROID_KEY=1111111 --variable ANDROID_SECRET=1111111 --variable IOS_KEY=1111111 --variable IOS_SECRET=1111111


## Usage

### API
/**
* 获取设备唯一标识deviceId，deviceId为阿里云移动推送过程中对设备的唯一标识（并不是设备UUID/UDID）
* @param  {Function} successCallback 成功回调
* @param  {Function} errorCallback   失败回调
* @return {void}  
*/

###  getRegisterId: function(successCallback, errorCallback)

/**
  * 阿里云推送绑定账号名
  * @param  {string} account         账号
  * @param  {Function} successCallback 成功回调
  * @param  {Function} errorCallback   失败回调
  * @return {void} 
  */

###  bindAccount: function(account, successCallback, errorCallback) 

/**
  * 阿里云推送绑定标签
  * @param  {string[]} tags            标签列表
  * @param  {Function} successCallback 成功回调
  * @param  {Function} errorCallback   失败回调
  * @return {void}  
  */

###  bindTags: function(tags, successCallback, errorCallback) 

/**
  * 阿里云推送解除绑定标签
  * @param  {string[]} tags            标签列表
  * @param  {Function} successCallback 成功回调
  * @param  {Function} errorCallback   失败回调
  * @return {void}               
  */

###  unbindTags: function(tags, successCallback, errorCallback)

/**
  * 阿里云推送解除绑定标签
  * @param  {Function} successCallback 成功回调
  * @param  {Function} errorCallback   失败回调
  * @return {void}           
  */
listTags: function(successCallback, errorCallback) 


/**
  * 阿里云推送消息透传回调
  * @param  {Function} successCallback 成功回调
  */

###  onMessage(sucessCallback) ;

# sucessCallback:调用成功回调方法，注意没有失败的回调，返回值结构如下：

/**
  * #json: {
  *  type:string 消息类型,
  *  title:string '阿里云推送',
  *  content:string '推送的内容',
  *  extra:string | Object<k,v> 外健,
  *  url:路由
  *}
  *
  *#消息类型
  *{
  *  message:透传消息，
  *  notification:通知接收，
  *  notificationOpened:通知点击，
  *  notificationReceived：通知到达，
  *  notificationRemoved：通知移除，
  *  notificationClickedWithNoAction：通知到达，
  *  notificationReceivedInApp：通知到达打开 app
  *}
  *
  */

##  第三方辅助通道配置&Android8.0+设备接收通知设置

  [移动辅助通道配置](https://help.aliyun.com/document_detail/30067.html?spm=5176.doc30064.6.621.uWVKlw)
### 关于小米、华为开发者账号的注册请参照：
  小米开发者账号注册：[小米开放平台](https://dev.mi.com/console/);
  华为开发者账号注册：[华为开发者联盟](https://developer.huawei.com/consumer/cn/?spm=5176.doc30067.2.14.rPh7O7)
### 在应用中初始化辅助通道
###各渠道相应的id和key需要在插件项目同提前填入配置;
### MainApplication.initCloudChannel(Context applicationContext)下初始化
/**{
  * MiPushRegister.register(applicationContext, "App_Id", "App_key"); // 初始化小米辅助推送
  * HuaWeiRegister.register(applicationContext); // 接入华为辅助推送
  * GcmRegister.register(applicationContext, "send_id", "application_id"); // 接入FCM/GCM初始化推送
  *}
  *
  */

### 移动推送Android SDK: Android 8.0以上设备通知接收不到？

   [Android SDK: Android 8.0以上设备](https://help.aliyun.com/knowledge_detail/67398.html)

###MainApplication.initCloudChannel(Context applicationContext)下调用如下代码:
/**private void createNotificationChannel() {
  * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
  *    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
  *    // 通知渠道的id
  *    String id = "1";
  *    // 用户可以看到的通知渠道的名字.
  *    CharSequence name = "notification channel";
  *    // 用户可以看到的通知渠道的描述
  *    String description = "notification description";
  *    int importance = NotificationManager.IMPORTANCE_HIGH;
  *    NotificationChannel mChannel = new NotificationChannel(id, name, importance);
  *    // 配置通知渠道的属性
  *    mChannel.setDescription(description);
  *    // 设置通知出现时的闪灯（如果 android 设备支持的话）
  *    mChannel.enableLights(true);
  *    mChannel.setLightColor(Color.RED);
  *    // 设置通知出现时的震动（如果 android 设备支持的话）
  *    mChannel.enableVibration(true);
  *    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
  *    //最后在notificationmanager中创建该通知渠道
  *    mNotificationManager.createNotificationChannel(mChannel);
  *    }
  * }
  *
  */