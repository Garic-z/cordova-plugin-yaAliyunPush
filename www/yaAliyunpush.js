var exec = require('cordova/exec');

exports.coolMethod = function (arg0, success, error) {
    exec(success, error, 'AliyunPush', 'coolMethod', [arg0]);
};

var AliyunPush = {
    registered: false,
    errorCallback: function (msg) {
        console.log('AliyunPush Callback Error: ' + msg)
    },

    callNative: function (name, args, successCallback, errorCallback) {
        if (errorCallback) {
            cordova.exec(successCallback, errorCallback, 'AliyunPush', name, args)
        } else {
            cordova.exec(successCallback, this.errorCallback, 'AliyunPush', name, args)
        }
    },

    initForAndroid: function (successCallback, errorCallback) {
        this.callNative('initForAndroid', [], successCallback);
    },
    initForAndroid: function (tags, successCallback, errorCallback) {
        this.callNative('initForAndroid', [tags], successCallback, errorCallback)
    },

    /**
     * 获取设备唯一标识deviceId，deviceId为阿里云移动推送过程中对设备的唯一标识（并不是设备UUID/UDID）
     * @param  {Function} successCallback 成功回调
     * @param  {Function} errorCallback   失败回调
     * @return {void}  
     * 
     * 示例
     * window.AliyunPush.getRegisterId((data) => {
         alert('推送注册Deviceid' + data);
     });
     */
    getRegisterId: function (successCallback, errorCallback) {
        this.callNative('getRegisterId', [], successCallback);
    },

    /**
     * 阿里云推送绑定账号名
     * @param  {string} account         账号
     * @param  {Function} successCallback 成功回调
     * @param  {Function} errorCallback   失败回调
     * @return {void} 
     */
    bindAccount: function (account, successCallback, errorCallback) {
        this.callNative('bindAccount', [account], successCallback, errorCallback);
    },

    /**
     * 阿里云推送绑定标签
     * @param  {string[]} tags            标签列表
     * @param  {Function} successCallback 成功回调
     * @param  {Function} errorCallback   失败回调
     * @return {void}  
     */
    bindTags: function (tags, successCallback, errorCallback) {
        this.callNative('bindTags', [tags], successCallback, errorCallback)
    },

    /**
     * 阿里云推送解除绑定标签
     * @param  {string[]} tags            标签列表
     * @param  {Function} successCallback 成功回调
     * @param  {Function} errorCallback   失败回调
     * @return {void}               
     */
    unbindTags: function (tags, successCallback, errorCallback) {
        this.callNative('unbindTags', [tags], successCallback, errorCallback)
    },

    /**
     * 阿里云推送解除绑定标签
     * @param  {Function} successCallback 成功回调
     * @param  {Function} errorCallback   失败回调
     * @return {void}           
     */
    listTags: function (successCallback, errorCallback) {
        this.callNative('listTags', [], successCallback)
    },

    /**
     * 阿里云推送消息透传回调
     * @param  {Function} successCallback 成功回调
     * @return {void}  
     * JS示例
     * type: string 消息类型,title: string '阿里云推送',
     * content: string '推送的内容',extra: string | Object < k, v > 外健, url: 路由
     * window.AliyunPush.onMessage((res) => {
         alert('成功:' + res.type + "," + res.title + "," + res.content);
     });

     *消息类型
     * message: 透传消息，
     * notification: 通知接收，
     * notificationOpened: 通知点击，
     * notificationReceived： 通知到达，
     * notificationRemoved： 通知移除，
     * notificationClickedWithNoAction： 通知到达，
     * notificationReceivedInApp： 通知到达打开 app
     * 
     * 示例
     * window.AliyunPush.onMessage((res) => {
     *  if (res.type === notificationOpened) {
     *    alert('点击通知了，请做相关操作')
     * }
     });
     */
    onMessage: function (successCallback) {
        this.callNative('onMessage', [], successCallback)
    },

    AliyunPush: AliyunPush
}
module.exports = AliyunPush;