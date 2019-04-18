package com.example.administrator.canberecycle;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SignUpCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.example.common.views.CircleImageView;
import com.example.common.views.NotificationsUtils;
import com.example.common.views.RealPathFromUriUtils;
import com.example.recycle.DrawableUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import cn.leancloud.chatkit.LCChatKit;

/**
 * Created by Administrator on 2018/3/28.
 */

public class LoginActivity extends AppCompatActivity {


    /** 存储的文件名 */
    public static final String DATABASE = "Database";
    /** 存储后的文件路径：/data/data/<package name>/shares_prefs + 文件名.xml */
    @SuppressLint("SdCardPath")
    public static final String PATH = "/data/data/code.sharedpreferences/shared_prefs/Database.xml";

    private CircleImageView iv_select;
    private EditText tv_account;
    private EditText tv_pass;
    private TextView btn_submit;
    private TextView tv_switch;
    private TextView tv_forget;
    private boolean isRegister;
    private TextView tv_name;
    private Spinner sp_category;
    private RelativeLayout rl_category;
    private RelativeLayout rl_name;
    private TextView tvCamera;
    private TextView tvAlbum;


    /**
     * 定义跳转意图
     */
    final Intent[] intents = new Intent[3];
    private byte[] imageData ;
    private WindowManager windowManager;
    private Display display;
    private ProgressDialog progressDialog;

    /**
     * 选择拍照为途径
     */
    private static final int IMAGE_CAPTURE = 0;
    /**
     * 选择相册
     */
    private static final int GET_CONTENT = 1;
    private LayoutInflater inflater;
    private View selectPicView;
    private AlertDialog dialog;
    private ImageView iv_see;
    private Button btn_getCode;
    private EditText et_code;
    private RelativeLayout rl_code;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        initPermission();
        initView();

//        setCharacter();


        //注册登入信息提交按钮
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String account =  tv_account.getText().toString();
                final String password = tv_pass.getText().toString();

                if (tv_pass.getText().toString().length() < 8){
                    Toast.makeText(LoginActivity.this, "密码长度不符合", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (tv_name.getText().toString().length() < 0 && !isRegister){
                    Toast.makeText(LoginActivity.this, "昵称必须填", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
                    if (!isRegister) {
                        //登入逻辑
                        login(account, password);
                    } else {
                        //注册逻辑
                        register(account, password);
                    }
                }
            }
        });

//        final boolean flag = true;p
        iv_see.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_see.setImageResource(R.drawable.ic_eye_open);
                tv_pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        iv_see.setImageResource(R.drawable.ic_eye_close);
                    }
                },1000);
            }
        });


        btn_getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "未开通", Toast.LENGTH_SHORT).show();
            }
        });

        changeView();


        setImageAddAnimator();

        iv_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

    }

    /**
     * 改变view变成注册页面
     */
    private void changeView() {
        // 注册登入切换按钮
        tv_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRegister = true;
                tv_forget.setVisibility(View.INVISIBLE);
                tv_switch.setVisibility(View.INVISIBLE);
//                rl_category.setVisibility(View.VISIBLE);
                rl_name.setVisibility(View.VISIBLE);
                rl_code.setVisibility(View.VISIBLE);
                iv_select.setClickable(true);
                iv_select.setImageResource(R.drawable.ic_camera);
                iv_select.setBorderColor(Color.parseColor("#83ae46"));
                iv_select.setBorderWidth(2);
                btn_submit.setText("注册");



            }
        });
    }

    /**
     * 设置角色
     */
    private void setCharacter() {

        List<String> list = new ArrayList<String>();
        list.add("角色一");
        list.add("角色二");

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        sp_category.setAdapter(adapter);
    }


    /**
     * 在登入或者注册的时候时间进度弹框的显示
     * */
    public void showProgressDialog(String title, String message) {
        if (progressDialog == null) {

            progressDialog = ProgressDialog.show(this, title,
                    message, true, false);
        } else if (progressDialog.isShowing()) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
        }

        progressDialog.show();

    }

    /**
     * 在登入或者注册的时候时间进度弹框的显示
     * */
    public void hideProgressDialog() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }


    /**
     * 注册逻辑
     * @param account 账号
     * @param password 密码
     */
    public void register(final String account, final String password) {
        showProgressDialog("", "注册中");
        AVUser user = new AVUser();
        String name = tv_name.getText().toString();
        if (TextUtils.isEmpty(name)){
            name = "n_" + account;
        }
        if (imageData != null){
            user.put("avatar",new AVFile(name+"Image",getCopress(imageData)));
        }else {
            imageData = DrawableUtils.DrawableToByte(getResources().getDrawable(com.example.recycle.R.mipmap.energy));
            user.put("avatar",new AVFile(name+"Image",imageData));
        }
        String character = sp_category.getSelectedItem().toString();
        user.put("character", character);
        user.put("integral", 0);
        user.setUsername(name);
        user.setPassword(password);
        user.setMobilePhoneNumber(account);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                login(account, password);
            }
        });
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
            final android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(this).create();
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
                        localIntent.setData(Uri.fromParts("package", LoginActivity.this.getPackageName(), null));
                    } else if (Build.VERSION.SDK_INT <= 8) {
                        localIntent.setAction(Intent.ACTION_VIEW);

                        localIntent.setClassName("com.android.settings",
                                "com.android.settings.InstalledAppDetails");

                        localIntent.putExtra("com.android.settings.ApplicationPkgName",
                                LoginActivity.this.getPackageName());
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
     * 登入逻辑
     * @param account 账号
     * @param password 密码
     */
    public void login(String account, String password) {
        showProgressDialog("", "登入中");
        AVUser.loginByMobilePhoneNumberInBackground(account, password, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                if (e != null) {
                    hideProgressDialog();
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    LCChatKit.getInstance().open(AVUser.getCurrentUser().getObjectId(), new AVIMClientCallback() {
                        @Override
                        public void done(AVIMClient avimClient, AVIMException e) {
                            if (null == e) {
//                                saveCharacter();
                                hideProgressDialog();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 保存这个角色在本地
     */
    public void saveCharacter() {
        SharedPreferences sp = getSharedPreferences(DATABASE, MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("character", sp_category.getSelectedItem().toString());
        //提交edit
        edit.apply();
    }

    private void initView() {
        iv_select = findViewById(R.id.iv_select);
        tv_account = findViewById(R.id.tv_account);
        tv_pass = findViewById(R.id.tv_pass);
        btn_submit = findViewById(R.id.btn_submit);
        tv_switch = findViewById(R.id.tv_switch);
        tv_forget = findViewById(R.id.tv_forget);
        tv_name = findViewById(R.id.tv_name);
        sp_category = findViewById(R.id.sp_category);
//        rl_category = findViewById(R.id.rl_category);
        rl_name = findViewById(R.id.rl_name);
        iv_see = findViewById(R.id.iv_see);
        btn_getCode = findViewById(R.id.btn_getCode);
        et_code = findViewById(R.id.et_code);
        rl_code = findViewById(R.id.rl_code);

        iv_select.setImageResource(R.mipmap.energy);
        iv_select.setClickable(false);


        inflater =  getLayoutInflater();
        getDisplay();
    }



    /**
     * 将dialog展现出来
     *
     */
    private void showDialog() {
        initDialogView();
        initEventListen();
    }

    /**
     * 设置对dialog的监听事件
     */
    private void initEventListen() {
        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intents[0] = new Intent();
                intents[0].setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intents[0],IMAGE_CAPTURE);
            }
        });

        tvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intents[1] = new Intent();
                intents[1].setType("image/*");
                intents[1].setAction(intents[1].ACTION_GET_CONTENT);
                startActivityForResult(intents[1],GET_CONTENT);
            }
        });
    }


    /**
     * 图片循环的动画
     */
    private void setImageAddAnimator() {
        TranslateAnimation animator =new TranslateAnimation(-3,3,-3,3);
        //设置持续时间
        animator.setDuration(1500);
        //设置重复次数
        animator.setRepeatCount(ValueAnimator.INFINITE);
        //设置方向直行
        animator.setRepeatMode(Animation.REVERSE);

        iv_select.startAnimation(animator);
    }


    /**
     * 初始化选取照片的方法的dialog的view
     */
    private void initDialogView() {
        selectPicView = inflater.inflate(com.example.recycle.R.layout.dailog_select_pic,null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder.create();
        dialog.show();
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setContentView(selectPicView);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (display.getWidth() * 0.9);
        dialogWindow.setAttributes(lp);
        tvCamera = selectPicView.findViewById(com.example.recycle.R.id.tvCamera);
        tvAlbum = selectPicView.findViewById(com.example.recycle.R.id.tvAlbum);
    }


    /**
     * 获取到屏幕信息进行适配
     */
    private void getDisplay() {
        windowManager = this.getWindowManager();
        display = windowManager.getDefaultDisplay();
        //设置键盘弹出的模式
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //取选择照片的返回数据
        switch (requestCode){
            case IMAGE_CAPTURE:
                if(data != null){
                    //只会返回bitmap
                    Bundle bundle = data.getExtras();

                    //取图片，todo 处理一下返回数据，有的数据不是bitmap
                    Bitmap bitmap = bundle.getParcelable("data");

                    if (bitmap != null){
                        imageData = DrawableUtils.bitmapToByte(bitmap);
                        dialog.dismiss();
                        iv_select.setImageBitmap(bitmap);
                    }
                } else {
                    return;
                }
                break;
            case GET_CONTENT:
                if (data != null){
                    //必须动态获取权限
                    if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                    }

                    Uri uri = data.getData();
                    String realPathFromUri = RealPathFromUriUtils.getRealPathFromUri(LoginActivity.this, data.getData());
                    Bitmap bitmap = BitmapFactory.decodeFile(realPathFromUri);
                    imageData = DrawableUtils.bitmapToByte(bitmap);
                    dialog.dismiss();
                    iv_select.setImageBitmap(bitmap);
                }
                break;

        }
    }

    private byte[] getCopress(byte[] bs) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bs, 0, bs.length);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
        byte[] bytes = bos.toByteArray();
        return bytes;
    }
}

