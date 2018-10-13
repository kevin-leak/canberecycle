package com.example.home;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.leancloud.chatkit.Goods;

/**
 * Created by Administrator on 2018/7/3.
 */

public class GoodsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    /**
     * 所有 Adapter 成员的list
     */
    private List<MemberItem> memberList = new ArrayList<MemberItem>();


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GoodsItemHolder(parent.getContext(), parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((GoodsItemHolder) holder).bindData(memberList.get(position).goods);
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }



    /**
     * 设置成员列表，然后更新索引
     */
    public void setMemberList(List<Goods> goodsList) {      //  将商品加入到数组中
        memberList.clear();
        if (null != goodsList) {
            for (Goods goods : goodsList) {
                MemberItem item = new MemberItem();
                item.goods = goods;
                memberList.add(item);
            }
        }
    }


    public static class MemberItem {
        public Goods goods;
    }
}
