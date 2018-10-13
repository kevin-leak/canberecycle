package com.example.administrator.canberecycle;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.example.common.views.GoodsProvider;

import cn.leancloud.chatkit.LCChatKit;

/**
 * Created by Administrator on 2018/5/7.
 */

public class CanBeRecycleApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        String ID = "SDDiY1XOt1nKoNOacXDbNxbR-gzGzoHsz";
        String KEY = "8ShSSjlPr1ailzq4rhBUSlY3";

        LCChatKit.getInstance().setProfileProvider(GoodsProvider.getInstance());
        //在应用发布之前，请关闭调试日志，以免暴露敏感数据。
        AVOSCloud.setDebugLogEnabled(true);
        LCChatKit.getInstance().init(getApplicationContext(), ID, KEY);
        AVIMClient.setAutoOpen(false);
        PushService.setDefaultPushCallback(this, MainActivity.class);

    }

}
