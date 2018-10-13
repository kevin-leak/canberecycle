package com.example.common.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.example.common.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import cn.leancloud.chatkit.Goods;
import cn.leancloud.chatkit.activity.LCIMConversationActivity;
import cn.leancloud.chatkit.utils.LCIMConstants;

/**
 * Created by Administrator on 2018/7/4.
 */

public class DisplayActivity extends Activity {

    private ImageView iv_goods_image;
    private TextView tv_goods_info;
    private TextView tv_goods_name;
    private TextView tv_goods_price;
    private TextView tv_goods_account;
    private ImageButton ib_message;
    private ImageButton ib_phone;
    private ImageButton ib_add_car;
    private EditText et_quantity;
    private Goods goods;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        goods = (Goods) getIntent().getSerializableExtra("goods");

        initView();

        ib_message.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (Objects.equals(AVUser.getCurrentUser().getObjectId(), goods.getUserId())){
                    Toast.makeText(DisplayActivity.this, "自己发布的产品", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(DisplayActivity.this, LCIMConversationActivity.class);
                    intent.putExtra(LCIMConstants.PEER_ID, goods.getUserId());
                    startActivity(intent);
                }

            }
        });


        if (!TextUtils.isEmpty(goods.getImageUrl())) {
//            iv_goods_image.setImageURI(Uri.parse(goods.getImageUrl()));
            Picasso.with(this).load(goods.getImageUrl()).into(iv_goods_image);
        } else {
            iv_goods_image.setImageResource(R.drawable.lcim_default_avatar_icon);
        }

        setCall();
    }

    @SuppressLint({"SetTextI18n", "CutPasteId"})
    private void initView() {
        iv_goods_image = findViewById(R.id.iv_goods_image);
        tv_goods_info = findViewById(R.id.tv_goods_info);
        tv_goods_name = findViewById(R.id.tv_goods_name);
        tv_goods_price = findViewById(R.id.tv_goods_price);
        tv_goods_account = findViewById(R.id.tv_goods_account);
        ib_message = findViewById(R.id.ib_message);
        ib_phone = findViewById(R.id.ib_phone);
        ib_add_car = findViewById(R.id.ib_add_car);
        et_quantity = findViewById(R.id.et_quantity);

        tv_goods_name.setText(goods.getGoodsName());
        tv_goods_info.setText(goods.getInfo());
        tv_goods_price.setText("¥" + goods.getPrice());
        tv_goods_account.setText(goods.getQuantity());


        ib_add_car.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View v) {

                if (Objects.equals(AVUser.getCurrentUser().getObjectId(), goods.getUserId())){
                    Toast.makeText(DisplayActivity.this, "自己发布的产品", Toast.LENGTH_SHORT).show();
                }else {
                    String s =  et_quantity.getText().toString();
                    int q = Integer.parseInt(s); //获取输入的商品数量
                    int a = Integer.parseInt(tv_goods_account.getText().toString()); //获取原有的数目
                    if ( q> a || q < 0){
                        Toast.makeText(DisplayActivity.this, "超出数量", Toast.LENGTH_SHORT).show();
                        et_quantity.setText("");
                    }else {
                        Toast.makeText(DisplayActivity.this, "输入的数量" + q + "原有数量" + a, Toast.LENGTH_SHORT).show();
                        showProgressDialog("", "加入购物车中");
                        AVObject carGoods = new AVObject("Goods"+AVUser.getCurrentUser().getObjectId());
                        setCarGoods(carGoods, goods, q);
                    }
                }
            }
        });

    }


    private void setCarGoods(AVObject carGoods, Goods goods, int i) {
        carGoods.put("name",goods.getUserName());
        carGoods.put("place",goods.getPlace());
        carGoods.put("phone",goods.getPhone());
        carGoods.put("info",goods.getInfo());
        carGoods.put("price", goods.getPrice());
        carGoods.put("quantity", goods.getQuantity());
        carGoods.put("ownerId", goods.getUserId());
        carGoods.put("category", goods.getCategory());
        carGoods.put("image", goods.getImageUrl());
        carGoods.put("avatar", goods.getAvatarUrl());
        carGoods.put("userName", goods.getUserName());
        carGoods.put("goodsId", goods.getObjectId());
        carGoods.put("num",  i);
        carGoods.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null){
                    hideProgressDialog();
                    Toast.makeText(DisplayActivity.this, "成功加入到购物车", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    hideProgressDialog();
                    Toast.makeText(DisplayActivity.this, e.getMessage().toString(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void showProgressDialog(String title, String message) {
        if (progressDialog == null) {

            progressDialog = ProgressDialog.show(DisplayActivity.this, title,
                    message, true, false);
        } else if (progressDialog.isShowing()) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
        }

        progressDialog.show();

    }

    public void hideProgressDialog() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }


    /**
     *拨打电话
     */
    private void setCall() {
        ib_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + goods.getPhone()));
                //call动作为直接拨打电话(需要加CALL权限)
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + goods.getPhone()));
//                //dial动作为调用拨号盘
//                if (ActivityCompat.checkSelfPermission(DisplayActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return;
//                }
                startActivity(intent);
            }
        });
    }
}
