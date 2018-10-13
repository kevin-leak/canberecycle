package com.example.administrator.canberecycle;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;

import java.util.ArrayList;

import cn.leancloud.chatkit.LCChatKit;

/**
 * Created by Administrator on 2018/3/28.
 */

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);


        if (!isConn(this)){
            Toast.makeText(SplashActivity.this, "请连接网络", Toast.LENGTH_SHORT).show();
            finish();
        }



        //购物车里面设置初始选定值
        SharedPreferences sp = getSharedPreferences("checked", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean("checked", false);
        //提交edit
        edit.apply();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AVUser.getCurrentUser() == null) {
                    Intent intent;
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    LCChatKit.getInstance().open(AVUser.getCurrentUser().getObjectId(), new AVIMClientCallback() {
                        @Override
                        public void done(AVIMClient avimClient, AVIMException e) {
                            if (null == e) {
                                Intent intent;
                                intent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(SplashActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }
        },3000);
    }







    public static boolean isConn(Context context){

        boolean ConnFlag=false;
        ConnectivityManager conManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = conManager.getActiveNetworkInfo();
        if(network!=null){
            ConnFlag=conManager.getActiveNetworkInfo().isAvailable();
        }
        return ConnFlag;
    }
}
