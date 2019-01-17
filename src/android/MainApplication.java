package com.ya.yaaliyunpush;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.register.GcmRegister;
import com.alibaba.sdk.android.push.register.HuaWeiRegister;
import com.alibaba.sdk.android.push.register.MiPushRegister;
import static com.ya.yaaliyunpush.AliyunPush.initCloudChannel;
public class MainApplication extends Application {
    private static final String TAG = "Init";
    @Override
    public void onCreate() {
        super.onCreate();
        initCloudChannel(this);
    }

}
