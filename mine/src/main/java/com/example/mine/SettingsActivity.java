package com.example.mine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.example.common.views.ActivityManager;

import java.util.Objects;

import cn.leancloud.chatkit.LCChatKit;

/**
 * Created by Administrator on 2018/7/2.
 */

public class SettingsActivity extends Activity {

    private View settingsView;
    private LinearLayout ll_logout;
    private Switch sw_char;
    private SharedPreferences pref1;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ll_logout = findViewById(R.id.ll_logout);
        sw_char = findViewById(R.id.sw_char);

        ActivityManager.getInstance().addActivity(this);


        ll_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVUser.logOut();
                LCChatKit.getInstance().close(new AVIMClientCallback() {
                    @Override
                    public void done(AVIMClient avimClient, AVIMException e) {
                        if (null != e) {
                            e.printStackTrace();
                        } else {
                            ActivityManager.getInstance().exit();
                        }
                    }
                });
            }
        });

        pref1 = getSharedPreferences("Database", MODE_PRIVATE);
        final String character = pref1.getString("character", "角色一");
        if (Objects.equals(character, "角色一")){
            sw_char.setChecked(true);
//            Log.e("character", "角色一");
        }else {
            sw_char.setChecked(false);
//            Log.e("character", character);
        }

        sw_char.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint({"CommitPrefEdits", "ShowToast"})
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sw_char.isChecked()){
            SharedPreferences.Editor edit1 = pref1.edit();
            edit1.putString("character", "角色一");
            //提交edit
            edit1.apply();
            edit1.commit();
            Toast.makeText(SettingsActivity.this, "角色一，需要手动刷新大厅页面", Toast.LENGTH_SHORT).show();
        }else {
            SharedPreferences.Editor edit1 = pref1.edit();
            edit1.putString("character", "角色二");
            //提交edit
            edit1.apply();
            edit1.commit();
            Toast.makeText(SettingsActivity.this, "角色二，需要手动刷新大厅页面", Toast.LENGTH_SHORT).show();
        }
    }
}
