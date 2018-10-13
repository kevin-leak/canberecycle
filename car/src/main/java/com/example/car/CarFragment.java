package com.example.car;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.LoginFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CloudQueryCallback;
import com.example.common.views.GoodsProvider;

import java.util.List;

import cn.leancloud.chatkit.view.LCIMDividerItemDecoration;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2018/3/28.
 */

public class CarFragment extends Fragment {

    private View carView;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private LinearLayoutManager layoutManager;
    private GoodsAdapter itemAdapter;
    public List<GoodsAdapter.MemberItem>  memberList;
    private TextView tv_delete;
    private List<Integer> integerList;
    private List<GoodsAdapter.MemberItem> selectList;
    private CheckBox cb_all;
    private TextView tv_pay;
    private TextView tv_sum_money;
    private List<GoodsItemHolder> goodHolder;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        carView = inflater.inflate(R.layout.fragment_car, null);
        initView(carView);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new LCIMDividerItemDecoration(getActivity()));


        itemAdapter = new GoodsAdapter();
        recyclerView.setAdapter(itemAdapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMembers();
            }
        });


        itemAdapter.setGoodsAdapterLisenter(new GoodsAdapter.GoodsAdapterListener() {
            @Override
            public void selectItem(List mList, List<Integer> add, List<GoodsAdapter.MemberItem> sList) {
                memberList  = mList;
                integerList = add;
                selectList = sList;
            }
        });


        tv_pay.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                tv_sum_money.setText(Integer.toString(itemAdapter.getAllMoney()));
                itemAdapter.setAllNotChecked();
                toPay();
            }
        });


        deleteCarGoods();


        selectAll();


        return carView;
    }

    /**
     * 进入支付宝支付逻辑
     */
    private void toPay() {
        Toast.makeText(getActivity(), "进入支付宝支付",Toast.LENGTH_LONG).show();
    }

    /**
     * 删除购物车的商品
     */
    public void deleteCarGoods() {
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (GoodsAdapter.MemberItem item : selectList){
                    memberList.remove(item);
//                    itemAdapter.notifyItemRemoved(integerList.get(i));
                    itemAdapter.notifyDataSetChanged();
                    String id = item.goods.getObjectId();
                    String s = "Goods" + AVUser.getCurrentUser().getObjectId();
                    String deleteString = "delete from " + s + " where objectId=\'" + id + "\'";
                    AVQuery.doCloudQueryInBackground(deleteString, new CloudQueryCallback<AVCloudQueryResult>() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void done(AVCloudQueryResult avCloudQueryResult, AVException e) {
                            // 如果 e 为空，说明保存成功
                            if (e == null){
                                Toast.makeText(getContext(), "成功删除", Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
//                    Toast.makeText(getActivity(), "删除成功" , Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    /**
     * 设置全选
     */
    public void selectAll() {
        cb_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_all.isChecked()){
                    itemAdapter.setAllChecked();
                }else {
                    itemAdapter.setAllNotChecked();
                }
            }
        });
    }


    @SuppressLint("CutPasteId")
    public void initView(View view) {
        recyclerView = view.findViewById(R.id.rv_goods_list);
        refreshLayout = view.findViewById(R.id.sf_goods_list);
        tv_delete = view.findViewById(R.id.tv_delete);
        cb_all = view.findViewById(R.id.cb_all);
        tv_pay = view.findViewById(R.id.tv_pay);
        tv_sum_money = view.findViewById(R.id.tv_sum_money);

    }


    @Override
    public void onDestroyView() {
        //将事件的处理移除
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        //当重新开始的时候刷星一下成员
        refreshMembers();
        cb_all.setChecked(false);
        itemAdapter.setAllNotChecked();
    }


    /**
     * 重新获取到相关的成员
     */
    private void refreshMembers() {
        itemAdapter.setMemberList(GoodsProvider.getInstance().getAllGoods(3, getActivity()));
        itemAdapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }

}
