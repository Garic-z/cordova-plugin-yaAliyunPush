package com.ya.aliyunpush;


import android.app.Activity;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;

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


    final CloudPushService pushService = PushServiceFactory.getCloudPushService();

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        LOG.d(LOG_TAG, "AliyunPush#initialize");
        super.initialize(cordova, webView);
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

        if ("getRegisterId".equalsIgnoreCase(action)) {
            callbackContext.success(pushService.getDeviceId());
            sendNoResultPluginResult(callbackContext);
            ret =  true;
        }else if ("bindAccount".equalsIgnoreCase(action)) {
            final String account=args.getString(0);
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    LOG.d(LOG_TAG, "PushManager#bindAccount");
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
                    LOG.d(LOG_TAG, "PushManager#bindTags");

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
                    LOG.d(LOG_TAG, "PushManager#unbindTags");

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
                    LOG.d(LOG_TAG, "PushManager#listTags");
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