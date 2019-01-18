package com.ya.yaaliyunpush;


import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.register.GcmRegister;
import com.alibaba.sdk.android.push.register.HuaWeiRegister;
import com.alibaba.sdk.android.push.register.MiPushRegister;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class AliyunPush extends CordovaPlugin {
    /** LOG TAG */
    private static final String LOG_TAG = AliyunPush.class.getSimpleName();

    /** JS回调接口对象 */
    public static CallbackContext pushCallbackContext = null;


    static final CloudPushService pushService = PushServiceFactory.getCloudPushService();

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        LOG.d(LOG_TAG, "AliyunPush#initialize");
        super.initialize(cordova, webView);
    }
    /**
     * 初始化云推送通道
     * @param applicationContext
     */
    public static void initCloudChannel(Context applicationContext) {
        // 创建notificaiton channel
//        createNotificationChannel(applicationContext);
        PushServiceFactory.init(applicationContext);
//        CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d(LOG_TAG, "init cloudchannel success");
            }
            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.d(LOG_TAG, "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });

    }

    public static void createNotificationChannel(Context applicationContext, String ficationId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
            // 通知渠道的id
            String id = ficationId;
            // 用户可以看到的通知渠道的名字.
            CharSequence name = "notification channel";
            // 用户可以看到的通知渠道的描述
            String description = "notification description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // 配置通知渠道的属性
            mChannel.setDescription(description);
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            //最后在notificationmanager中创建该通知渠道
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }



    /**
     * 插件主入口
     */
    @Override
    public boolean execute(final String action, final JSONArray args,
                           final CallbackContext callbackContext) throws JSONException {
        LOG.d(LOG_TAG, "AliyunPush#execute");

        boolean ret = false;

        if("onMessage".equalsIgnoreCase(action)){
            pushCallbackContext = callbackContext;
            ret =  true;
        }
        if("initForAndroid".equalsIgnoreCase(action)) {
            LOG.d(LOG_TAG, "AliyunPush#initForAndroid");

            Context applicationContext = cordova.getActivity().getApplicationContext();
//            String MIID = preferences.getString("MIID", "");
//            String MIKEY = preferences.getString("MIKEY", "");
            JSONObject json =  args.getJSONObject(0);
            try {
                MiPushRegister.register(applicationContext, json.getString("miid"),  json.getString("mikey")); // 初始化小米辅助推送
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HuaWeiRegister.register(applicationContext); // 接入华为辅助推送
            try {
                createNotificationChannel(applicationContext,json.getString("id")); // 初始化android 8.0+辅助推送通道
            } catch (JSONException e) {
                e.printStackTrace();
            }

//            GcmRegister.register(applicationContext, "send_id", "application_id"); // 接入FCM/GCM初始化推送
            callbackContext.success(MIID+","+MIKEY);
//            sendNoResultPluginResult(callbackContext);
            Toast.makeText(applicationContext, "初始化成功!", Toast.LENGTH_SHORT).show();
            ret =  true;
        } else if ("getRegisterId".equalsIgnoreCase(action)) {
             LOG.d(LOG_TAG, "AliyunPush#getRegisterId");
             callbackContext.success(pushService.getDeviceId());
             sendNoResultPluginResult(callbackContext);
             ret =  true;
        }else if ("bindAccount".equalsIgnoreCase(action)) {
            final String account=args.getString(0);
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    LOG.d(LOG_TAG, "AliyunPush#bindAccount");
                        pushService.bindAccount(account, new CommonCallback() {
                            @Override
                            public void onSuccess(String s) {
                                callbackContext.success(s);
                            }

                            @Override
                            public void onFailed(String s, String s1) {
                                resError(callbackContext,s,s1);
                            }
                        });
                    }
            });
            sendNoResultPluginResult(callbackContext);
            ret = true;
        }
        else if ("bindTags".equalsIgnoreCase(action)) {
            final String [] tags = getTagsFromArgs(args);

            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    LOG.d(LOG_TAG, "AliyunPush#bindTags");

                    if (tags != null && tags.length > 0) {
                        pushService.bindTag(pushService.DEVICE_TARGET, tags, null, new CommonCallback() {
                            @Override
                            public void onSuccess(String s) {
                                callbackContext.success(s);
                            }

                            @Override
                            public void onFailed(String s, String s1) {
                                resError(callbackContext,s,s1);
                            }
                        });
                    }

                }
            });
            sendNoResultPluginResult(callbackContext);
            ret = true;
        } else if ("unbindTags".equalsIgnoreCase(action)) {
            final String [] tags = getTagsFromArgs(args);
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    LOG.d(LOG_TAG, "AliyunPush#unbindTags");

                    if (tags != null && tags.length > 0) {

                        pushService.unbindTag(pushService.DEVICE_TARGET,tags,"",new CommonCallback(){
                            @Override
                            public void onFailed(String s, String s1) {
                                resError(callbackContext,s,s1);
                            }

                            @Override
                            public void onSuccess(String s) {
                                LOG.d(LOG_TAG,"onSuccess:"+s);
                                callbackContext.success(s);
                            }
                        });
                    }

                }
            });
            sendNoResultPluginResult(callbackContext);
            ret = true;
        }else if ("listTags".equalsIgnoreCase(action)) {

            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    LOG.d(LOG_TAG, "AliyunPush#listTags");
                        pushService.listTags(pushService.DEVICE_TARGET,new CommonCallback(){
                            @Override
                            public void onFailed(String s, String s1) {
                                resError(callbackContext,s,s1);
                            }

                            @Override
                            public void onSuccess(String s) {
                                LOG.d(LOG_TAG,"onSuccess:"+s);
                                callbackContext.success(s);
                            }
                        });
                }
            });
            sendNoResultPluginResult(callbackContext);
            ret = true;
        }
        return ret;
    }


    /**
     * 将json字符串转换为列表
     * @param args json字符串
     * @return tags的列表
     */
    private String[] getTagsFromArgs(JSONArray args) throws JSONException{
        List<String> tags = null;
        args = args.getJSONArray(0);
        if (args != null && args.length() > 0) {
            int len = args.length();
            tags = new ArrayList<String>(len);
            for (int inx = 0; inx < len; inx++) {
                try {
                    tags.add(args.getString(inx));
                } catch (JSONException e) {
                    LOG.e(LOG_TAG, e.getMessage(), e);
                }
            }
        }

        return tags.toArray(new String[tags.size()]);
    }
   private void resError(CallbackContext callbackContext,String reason, String res){
       LOG.d(LOG_TAG,"onFailed reason:"+reason+"res:"+res);
       JSONObject jsonObject = new JSONObject();
       try {
           jsonObject.put("message", res);
           jsonObject.put("reason", reason);
       } catch (JSONException e) {
           e.printStackTrace();
       }
       callbackContext.error(jsonObject);
   }

    private void sendNoResultPluginResult(CallbackContext callbackContext) {
        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }

    /**
     * 接收推送内容并返回给前端JS
     *
     * @param data JSON对象
     */
  public  static void pushData(final JSONObject data) {
       if(pushCallbackContext==null) {
           return;
       }
      PluginResult result = new PluginResult(PluginResult.Status.OK, data);
      result.setKeepCallback(true);
      pushCallbackContext.sendPluginResult(result);
    }
}