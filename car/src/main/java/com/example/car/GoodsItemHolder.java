package com.example.car;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.common.views.DisplayActivity;
import com.squareup.picasso.Picasso;

import cn.leancloud.chatkit.Goods;
import cn.leancloud.chatkit.viewholder.LCIMCommonViewHolder;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2018/7/3.
 */

public class GoodsItemHolder extends LCIMCommonViewHolder<Goods> {


    private final Context context;
    public Goods goods;
    public LinearLayout ll_item;
    public ImageView iv_goodsImage;
    public TextView tv_goodsName;
    public TextView tv_goodsPrice;
    public TextView tv_info;
    public TextView tv_sort;
    public TextView tv_select_account;
    public ImageView add_account;
    public ImageView cut_account;
    public CheckBox cb_checked;
    private int num;

    public GoodsItemHolder(Context context, ViewGroup root) {           //  建立每个条目的布局，并初始化
        super(context, root, R.layout.car_goods_item);
        initView();

        this.context = context;
    }

    @SuppressLint("CutPasteId")
    private void initView() {
        ll_item = itemView.findViewById(R.id.ll_item);
        iv_goodsImage = itemView.findViewById(R.id.iv_goodsImage);
        tv_goodsName = itemView.findViewById(R.id.tv_goodsName);
        tv_goodsPrice = itemView.findViewById(R.id.tv_goodsPrice);
        tv_info = itemView.findViewById(R.id.tv_info);
        tv_sort = itemView.findViewById(R.id.tv_sort);
        cb_checked = itemView.findViewById(R.id.cb_checked);


        tv_select_account = itemView.findViewById(R.id.tv_select_account);
        add_account = itemView.findViewById(R.id.add_account);
        cut_account = itemView.findViewById(R.id.cut_account);




        // 设置监听事件

        ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DisplayActivity.class);
//                intent.putExtra(LCIMConstants.PEER_ID, goods.getUserId());
                intent.putExtra("goods", goods);
                getContext().startActivity(intent);
            }
        });


        //在闪屏页面初始化了值
        final SharedPreferences pref1 = getContext().getSharedPreferences("checked", MODE_PRIVATE);
        if (pref1.getBoolean("checked", false)){
            cb_checked.setChecked(true);
        }else {
            cb_checked.setChecked(false);
        }

        cb_checked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_checked.isChecked()){
                    pref1.edit().putBoolean("checked ", true).apply();
                }else {
                    pref1.edit().putBoolean("checked ", false).apply();
                }

            }

        });
    }




    @SuppressLint("SetTextI18n")
    @Override
    public void bindData(final Goods goods) {     //对于每个item进行数据绑定
        this.goods = goods;

        final String avatarUrl = goods.getImageUrl();

        if (!TextUtils.isEmpty(avatarUrl)) {
            Picasso.with(getContext()).load(avatarUrl).into(iv_goodsImage);
        } else {
            iv_goodsImage.setImageResource(R.drawable.lcim_default_avatar_icon);
        }
        tv_goodsName.setText(goods.getGoodsName());
        tv_goodsPrice.setText("¥"+goods.getPrice());
        tv_info.setText(goods.getInfo());
        tv_sort.setText(goods.getCategory());
        num = Integer.parseInt(goods.getNum());
        tv_select_account.setText(Integer.toString(num));


        cut_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num--;
                if (num <= 0){
                    num = 0;
                }else if(num >= Integer.parseInt(goods.getQuantity())){
                    num = Integer.parseInt(goods.getNum());
                }
                tv_select_account.setText(Integer.toString(num));
                goods.setNum(Integer.toString(num));
            }
        });

        add_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num++;
                if (num <= 0){
                    num = 0;
                }else if(num >= Integer.parseInt(goods.getQuantity())){
                    num = Integer.parseInt(goods.getNum());
                }
                tv_select_account.setText(Integer.toString(num));
                goods.setNum(Integer.toString(num));
            }
        });
    }


    public static ViewHolderCreator HOLDER_CREATOR = new ViewHolderCreator<GoodsItemHolder>() {
        @Override
        public GoodsItemHolder createByViewGroupAndType(ViewGroup parent, int viewType) {
            return new GoodsItemHolder(parent.getContext(), parent);
        }
    };
}
