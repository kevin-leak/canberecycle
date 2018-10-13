package com.example.mine;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CloudQueryCallback;

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
    private String deleteString;
    private List<Goods> goodsList;


    @Override
    public GoodsItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GoodsItemHolder(parent.getContext(), parent);
    }

    @Override
    public void onBindViewHolder(final GoodsItemHolder holder, final int position) {
        ((GoodsItemHolder) holder).bindData(memberList.get(position).goods);

        deleteString = "delete from Goods where objectId=\'" +  holder.goods.getObjectId() + "\'";
        holder.ll_item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                memberList.remove(position);
                goodsList.remove(position);
                AVQuery.doCloudQueryInBackground(deleteString, new CloudQueryCallback<AVCloudQueryResult>() {
                    @Override
                    public void done(AVCloudQueryResult avCloudQueryResult, AVException e) {
                        // 如果 e 为空，说明保存成功
                        if (e == null){
                            Toast.makeText(holder.getContext(), "成功删除,请刷新页面", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(holder.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                notifyDataSetChanged();
                return true;
            }
        });

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
        this.goodsList = goodsList;
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
