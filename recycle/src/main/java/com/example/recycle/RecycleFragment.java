package com.example.recycle;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.example.common.views.RealPathFromUriUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/7.
 */

public class RecycleFragment extends Fragment {

    private View recycleView;

    /**
     * 选择拍照为途径
     */
    private static final int IMAGE_CAPTURE = 0;
    /**
     * 选择相册
     */
    private static final int GET_CONTENT = 1;

    private ImageView ivGoodsImage;
    private EditText etGoodsName;
    private EditText etGoodsPlace;
    private EditText etGoodsInfo;
    private Button ibCommit;
    private FragmentManager manager;

    private ImageButton ibInfoImageAdd;
    private LayoutInflater inflater;
    private View selectPicView;
    private AlertDialog dialog;

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


    private Spinner sp_category;

    private EditText et_unit_price;
    private EditText et_quantity;
    private Spinner sp_char;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        recycleView = inflater.inflate(R.layout.fragment_recycle, null);
        this.inflater = inflater;
        getDisplay();

        initView(recycleView);

        List<String> list = new ArrayList<String>(9);
        list.add("类别一");
        list.add("类别二");
        list.add("类别三");
        list.add("类别四");
        list.add("类别五");
        list.add("类别六");
        list.add("类别七");
        list.add("类别八");
        list.add("类别九");

        List<String> characterList = new ArrayList<String>(2);
        characterList.add("角色一");
        characterList.add("角色二");
        setSpinner(sp_char, characterList);
        setSpinner(sp_category, list);

        return recycleView;



    }

    public void setSpinner(Spinner sp, List<String> list) {
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        sp.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        manager = getFragmentManager();

        ibCommit.setOnClickListener(new View.OnClickListener() {
            boolean count = true;
            @Override
            public void onClick(View view) {
                if (count){
                    count = false;
                    Toast.makeText(getActivity(),"再次点击确认无误",Toast.LENGTH_SHORT).show();
                }else {
                    // TODO: 2017/12/22 按时间计算两次点击，是否应该改变参量
                    count = true;
                    commitToInternet();
                }
            }
        });

        setImageAddAnimator();

        ibInfoImageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });


    }


    /**
     * 获取到屏幕信息进行适配
     */
    private void getDisplay() {
        if (getActivity()!= null){
            windowManager = getActivity().getWindowManager();
            display = windowManager.getDefaultDisplay();
        }
        //设置键盘弹出的模式
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //取选择照片的返回数据
        switch (requestCode){
            case IMAGE_CAPTURE:
                if(data != null){
                    //只会返回bitmap
                    Bundle bundle = data.getExtras();
                    if (bundle == null){
                        return;
                    }
                    //取图片，todo 处理一下返回数据，有的数据不是bitmap
                    Bitmap bitmap = bundle.getParcelable("data");
                    if (bitmap != null){
                        imageData = DrawableUtils.bitmapToByte(bitmap);
                        dialog.dismiss();
                        ivGoodsImage.setImageBitmap(bitmap);
                        ibInfoImageAdd.setVisibility(View.GONE);
                    }
                } else {
                    return;
                }
                break;
            case GET_CONTENT:
                if (data != null){

                    //必须动态获取权限
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                    }
                    Uri uri = data.getData();
                    String realPathFromUri = RealPathFromUriUtils.getRealPathFromUri(getActivity(), data.getData());
                    Bitmap bitmap = BitmapFactory.decodeFile(realPathFromUri);
                    imageData = DrawableUtils.bitmapToByte(bitmap);
                    dialog.dismiss();
                    ivGoodsImage.setImageURI(uri);
                }
                break;

        }
    }





    private void initView(View view) {
        ibInfoImageAdd = view.findViewById(R.id.ibInfoImageAdd);
        ivGoodsImage = view.findViewById(R.id.ivGoodsImage);
        etGoodsName = view.findViewById(R.id.etGoodsName);
        etGoodsPlace = view.findViewById(R.id.etGoodsPlace);
        etGoodsInfo = view.findViewById(R.id.etGoodsInfo);
        ibCommit = view.findViewById(R.id.btn_commit);

        sp_category = view.findViewById(R.id.sp_category);
        sp_char = view.findViewById(R.id.sp_char);

        et_unit_price = view.findViewById(R.id.et_unit_price);
        et_quantity = view.findViewById(R.id.et_quantity);


        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivGoodsImage.getLayoutParams();
        params.height = (int) (display.getHeight() * 0.21);
        ivGoodsImage.setLayoutParams(params);

        //处理是否被遮挡
        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) etGoodsInfo.getLayoutParams();
        param.height = (int) (display.getHeight() * 0.21);
        etGoodsInfo.setLayoutParams(param);
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
     * 初始化选取照片的方法的dialog的view
     */
    private void initDialogView() {
        selectPicView = inflater.inflate(R.layout.dailog_select_pic,null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialog = builder.create();
        dialog.show();
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setContentView(selectPicView);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (display.getWidth() * 0.9);
        dialogWindow.setAttributes(lp);
        tvCamera = selectPicView.findViewById(R.id.tvCamera);
        tvAlbum = selectPicView.findViewById(R.id.tvAlbum);
    }

    /**
     * 设置对dialog的监听事件
     */
    private void initEventListen() {
        //拍照
        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intents[0] = new Intent();
                intents[0].setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intents[0],IMAGE_CAPTURE);
            }
        });

        //选取相册的图片
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

        ibInfoImageAdd.startAnimation(animator);
    }

    public void showProgressDialog(String title, String message) {
        if (progressDialog == null) {

            progressDialog = ProgressDialog.show(getActivity(), title,
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
     * 将信息数据提交到网络
     * 数据储存方式
     * 有name，place，time，info，user，file
     */
    private void commitToInternet() {
        showProgressDialog("", "信息上传中...");
        AVUser user = AVUser.getCurrentUser();
        AVObject goods = new AVObject("Goods");
        setGoods(user, goods);
    }

    private void setGoods(AVUser user, AVObject goods) {
        goods.put("name",etGoodsName.getText().toString());
        goods.put("place",etGoodsPlace.getText().toString());
        goods.put("phone",user.getMobilePhoneNumber());
        goods.put("info",etGoodsInfo.getText().toString());
        goods.put("price", et_unit_price.getText().toString());
        goods.put("quantity", et_quantity.getText().toString());
        goods.put("ownerId", user.getObjectId());
        goods.put("category", sp_category.getSelectedItem().toString());
        goods.put("character", sp_char.getSelectedItem());


        if (imageData != null){
            goods.put("image",new AVFile(etGoodsName.getText().toString()+"Image",getCopress(imageData)));
        }else {
            imageData = DrawableUtils.DrawableToByte(getResources().getDrawable(R.mipmap.energy));
            goods.put("image",new AVFile(etGoodsName.getText().toString()+"Image",imageData));
        }
        goods.put("goodOwner",user);
        goods.put("avatar", user.getAVFile("avatar").getUrl());
        goods.put("userName", user.getString("username"));

        goods.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null){
                    clearInfo();
                    Toast.makeText(getActivity(),"成功",Toast.LENGTH_LONG).show();
                    hideProgressDialog();
                }else {
                    clearInfo();
                    hideProgressDialog();
                    Toast.makeText(getActivity(), e.getMessage().toString(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private byte[] getCopress(byte[] bs) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bs, 0, bs.length);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
        return bos.toByteArray();
    }
    /**
     * 由于framgment不是重新加载，需要收到清除数据
     */
    private void clearInfo() {
        etGoodsInfo.setText("");
        etGoodsName.setText("");
        etGoodsPlace.setText("");
        et_unit_price.setText("");
        et_quantity.setText("");
        sp_category.setSelection(0);
        sp_char.setSelection(0);
        ivGoodsImage.setImageBitmap(null);
    }


}
