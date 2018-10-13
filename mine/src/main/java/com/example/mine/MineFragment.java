package com.example.mine;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.example.common.views.CircleImageView;
import com.squareup.picasso.Picasso;

import cn.leancloud.chatkit.activity.LCIMConversationActivity;
import cn.leancloud.chatkit.activity.LCIMConversationListActivity;

/**
 * Created by Administrator on 2018/3/28.
 */

public class MineFragment extends Fragment {

    private View mineView;
    private TextView tvName;
    private AVUser currentUser;
    private LinearLayout ll_set;
    private CircleImageView civAvatar;
    private LinearLayout ll_message;
    private LinearLayout ll_needRecycle;
    private TextView tvFraction;
    private ImageView iv_change;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mineView = inflater.inflate(R.layout.fragment_mine, null);
        currentUser = AVUser.getCurrentUser();


        initView();

        ll_set.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        ll_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),LCIMConversationListActivity.class);
                startActivity(intent);
            }
        });

        ll_needRecycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NeedActivity.class);
                startActivity(intent);
            }
        });

        iv_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });



        return mineView;
    }

    private void showInputDialog() {
    /*@setView 装入一个EditView
     */
        final EditText editPhone = new EditText(getActivity());
        final EditText editPass = new EditText(getActivity());
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(getActivity());
        inputDialog.setTitle("修改");
        LinearLayout ll = new LinearLayout(getActivity());
        ll.addView(editPhone);
        ll.addView(editPass);
        editPhone.setHint("修改电话号码");
        editPass.setHint("修改密码");
        editPass.setWidth(800);
        editPhone.setWidth(800);
        ll.setOrientation(LinearLayout.VERTICAL);
        editPhone.setInputType(InputType.TYPE_CLASS_PHONE);
        inputDialog.setView(ll);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editPass.getText().toString().length() > 30 || editPass.getText().toString().length() < 8){
                            Toast.makeText(getActivity(), "密码长度不符合", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!TextUtils.isEmpty(editPhone.getText().toString())){
                            AVUser.getCurrentUser().setMobilePhoneNumber(editPhone.getText().toString());
                            AVUser.getCurrentUser().setPassword(editPass.getText().toString());
                            AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null){
                                        Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }else {
                            Toast.makeText(getActivity(), "修改失败，电话号码为空", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();

    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        tvName = mineView.findViewById(R.id.tvName);
        ll_set = mineView.findViewById(R.id.ll_set);
        civAvatar = mineView.findViewById(R.id.civAvatar);
        ll_message = mineView.findViewById(R.id.ll_message);
        ll_needRecycle = mineView.findViewById(R.id.ll_needRecycle);
        tvFraction = mineView.findViewById(R.id.tvFraction);
        iv_change = mineView.findViewById(R.id.iv_change);

        if (currentUser != null){
            tvName.setText(currentUser.getUsername());
            tvFraction.setText("积分（"+ currentUser.get("integral").toString() +")");
        }else {
            tvName.setText("canerecycle");
        }

        final String avatarUrl = currentUser.getAVFile("avatar").getUrl();
        if (!TextUtils.isEmpty(avatarUrl)) {
            Picasso.with(getActivity()).load(avatarUrl).into(civAvatar);
        } else {
            civAvatar.setImageResource(R.drawable.lcim_default_avatar_icon);
        }
    }
}
