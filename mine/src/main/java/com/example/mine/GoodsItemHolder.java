package com.example.mine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CloudQueryCallback;
import com.example.common.views.DisplayActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import cn.leancloud.chatkit.Goods;
import cn.leancloud.chatkit.viewholder.LCIMCommonViewHolder;

/**
 * Created by Administrator on 2018/7/3.
 */

public class GoodsItemHolder extends LCIMCommonViewHolder<Goods> {


    public Goods goods;
    public LinearLayout ll_item;
    public ImageView iv_goodsImage;
    public TextView tv_goodsName;
    public TextView tv_goodsPrice;
    public TextView tv_info;
    public TextView tv_amount;
    public TextView tv_sort;
    public String deleteString;

    public GoodsItemHolder(Context context, ViewGroup root) {           //  建立每个条目的布局，并初始化
        super(context, root, R.layout.common_goods_item);
        initView();
    }

    private void initView() {
        ll_item = itemView.findViewById(R.id.ll_item);
        iv_goodsImage = itemView.findViewById(R.id.iv_goodsImage);
        tv_goodsName = itemView.findViewById(R.id.tv_goodsName);
        tv_goodsPrice = itemView.findViewById(R.id.tv_goodsPrice);
        tv_info = itemView.findViewById(R.id.tv_info);
        tv_sort = itemView.findViewById(R.id.tv_sort);
        tv_amount = itemView.findViewById(R.id.tv_amount);

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
        tv_amount.setText(goods.getQuantity());
        tv_info.setText(goods.getInfo());
        tv_sort.setText(goods.getCategory());

    }


    public static ViewHolderCreator HOLDER_CREATOR = new ViewHolderCreator<GoodsItemHolder>() {
        @Override
        public GoodsItemHolder createByViewGroupAndType(ViewGroup parent, int viewType) {
            return new GoodsItemHolder(parent.getContext(), parent);
        }
    };


    interface DeleteGoods{
        void delete(Goods goods);
    }
}
