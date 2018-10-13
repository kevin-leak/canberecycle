package com.example.car;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

import cn.leancloud.chatkit.Goods;

/**
 * Created by Administrator on 2018/7/3.
 */

public class GoodsAdapter extends RecyclerView.Adapter<GoodsItemHolder>{

    /**
     * 所有 Adapter 成员的list
     */
    private List<MemberItem> memberList = new ArrayList<MemberItem>();
    private List<MemberItem> selectList = new ArrayList<MemberItem>();
    private List<Integer> indexList = new ArrayList<Integer>();
    private GoodsAdapterListener listener;
    private List<GoodsItemHolder> holderList = new ArrayList<>();





    @Override
    public GoodsItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        GoodsItemHolder itemHolder = new GoodsItemHolder(parent.getContext(), parent);
        holderList.add(itemHolder);
        return itemHolder;

    }

    @Override
    public void onBindViewHolder(final GoodsItemHolder holder, final int position) {
        holder.bindData(memberList.get(position).goods);
        holder.cb_checked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.cb_checked.isChecked()){
                    indexList.add(position);
                    selectList.add(memberList.get(position));
                    listener.selectItem(memberList, indexList, selectList);

                }
            }
        });

        holder.setIsRecyclable(false);
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




    static class MemberItem {
        public Goods goods;
    }

    void setGoodsAdapterLisenter(GoodsAdapterListener listener){
        this.listener = listener;
    }

    interface GoodsAdapterListener{
        void selectItem(List memberList, List<Integer> add, List<MemberItem> selectList);
//        void setAllChecked(GoodsItemHolder holder, List memberList, List<Integer> add, List<MemberItem> selectList);
//        void setAllNotChecked(GoodsItemHolder holder, List memberList, List<Integer> add, List<MemberItem> selectList);

    }




    /**
     * 全不选
     */
    void setAllNotChecked() {
        indexList.clear();
        selectList.clear();
        listener.selectItem(memberList,indexList,selectList);
        for (GoodsItemHolder goodsItemHolder : holderList){
            goodsItemHolder.cb_checked.setChecked(false);
        }
    }

    /**
     * 全选
     */
    void setAllChecked(){
        indexList.clear();
        selectList.clear();
        int position = 0;
        if (holderList == null) return;
        for (GoodsItemHolder goodsItemHolder : holderList){
            goodsItemHolder.cb_checked.setChecked(true);
            indexList.add(position);
            selectList.add(memberList.get(position));
            listener.selectItem(memberList, indexList, selectList);
            position++;
        }
    }

    /**
     * @return 获取的总额
     */
    int getAllMoney(){
        int sum = 0;
        for (MemberItem item: selectList){
            sum += Integer.parseInt(item.goods.getNum()) * Integer.parseInt(item.goods.getPrice());
        }

        return sum;
    }
}
