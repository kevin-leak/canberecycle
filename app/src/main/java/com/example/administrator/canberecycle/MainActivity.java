package com.example.administrator.canberecycle;


import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.example.car.CarFragment;
import com.example.common.views.ActivityManager;
import com.example.common.views.NotificationsUtils;
import com.example.home.HomeFragment;
import com.example.message.MessageFragment;
import com.example.mine.MineFragment;
import com.example.recycle.RecycleFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {

    private BottomNavigationBar bottomBar;

    //建立底边的fragment的实例
    private HomeFragment home;
    private MessageFragment message;
    private CarFragment car;
    private MineFragment mine;
    private RecycleFragment recycle;


    /**
     * 上一次点击 back 键的时间
     * 用于双击退出的判断
     */
    private static long lastBackTime = 0;

    /**
     * 当双击 back 键在此间隔内是直接触发 onBackPressed
     */
    private final int BACK_INTERVAL = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initPermission();
        initView();

        //设置背景颜色
        bottomBar.setActiveColor(R.color.colorWhite);
        bottomBar.setBarBackgroundColor(R.color.colorApp);
        bottomBar.setInActiveColor(R.color.colorApp);

        ActivityManager.getInstance().addActivity(this);

        //给底边栏添加条目
        bottomBar
                .addItem(new BottomNavigationItem(R.drawable.ic_home,"大厅"))
                .addItem(new BottomNavigationItem(R.drawable.ic_car,"回收站"))
                .addItem(new BottomNavigationItem(R.drawable.ic_goods,"call回收"))
                .addItem(new BottomNavigationItem(R.mipmap.ic_find,"资讯"))
                .addItem(new BottomNavigationItem(R.drawable.ic_mine,"我的"))
                .setFirstSelectedPosition(0)
                .initialise();

//        bottomBar.setInActiveColor()
        //建立监听底边栏切换的监听事件
        bottomBar.setTabSelectedListener(this);


        setDefaultFragment();
    }

    private void initPermission() {
        String permissions[] = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.CAMERA,
                Manifest.permission.RECEIVE_BOOT_COMPLETED
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.
                Log.e("--------->", "没有权限");
            } else {

                Log.e("--------->", "已经被授权");
            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }


        if (!NotificationsUtils.isNotificationEnabled(this)) {
            final AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.show();

            View view = View.inflate(this, R.layout.dialog, null);
            dialog.setContentView(view);

            TextView context = (TextView) view.findViewById(R.id.tv_dialog_context);
            context.setText("检测到您没有打开通知权限，是否去打开");

            TextView confirm = (TextView) view.findViewById(R.id.btn_confirm);
            confirm.setText("确定");
            confirm.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.cancel();
                    Intent localIntent = new Intent();
                    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (Build.VERSION.SDK_INT >= 9) {
                        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        localIntent.setData(Uri.fromParts("package", MainActivity.this.getPackageName(), null));
                    } else if (Build.VERSION.SDK_INT <= 8) {
                        localIntent.setAction(Intent.ACTION_VIEW);

                        localIntent.setClassName("com.android.settings",
                                "com.android.settings.InstalledAppDetails");

                        localIntent.putExtra("com.android.settings.ApplicationPkgName",
                                MainActivity.this.getPackageName());
                    }
                    startActivity(localIntent);
                }
            });

            TextView cancel = (TextView) view.findViewById(R.id.btn_off);
            cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
        }


    }


    /**
     * 设置最开始的页面
     */
    private void setDefaultFragment() {
        FragmentManager manager = this.getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (home == null){
            home = new HomeFragment();
        }
        transaction.replace(R.id.tb,home);
        transaction.commit();
    }

    private void initView() {
        bottomBar = findViewById(R.id.bottomBar);
    }


    //绑定监听fragment的切换
    @Override
    public void onTabSelected(int position) {

        FragmentManager manager = this.getFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();

        switch (position){
            case 0:
                if (home == null){
                    home = new HomeFragment();
                }
                transaction.replace(R.id.tb,home);
                break;
            case 1:
                if (car == null){
                    car = new CarFragment();
                }
                transaction.replace(R.id.tb, car);
                break;
            case 2:
                if (recycle == null){
                    recycle = new RecycleFragment();
                }
                transaction.replace(R.id.tb, recycle);
                break;
            case 3:
                if (message == null){
                    message = new MessageFragment();
                }
                transaction.replace(R.id.tb, message);
                break;
            case 4:
                if (mine == null){
                    mine = new MineFragment();
                }
                transaction.replace(R.id.tb,mine);
                break;
        }
        transaction.commit();
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBackTime < BACK_INTERVAL) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "双击 back 退出", Toast.LENGTH_SHORT).show();
        }
        lastBackTime = currentTime;
    }
}
