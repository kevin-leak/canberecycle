package com.example.home;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.common.views.GoodsProvider;

import java.lang.reflect.Field;

import cn.leancloud.chatkit.view.LCIMDividerItemDecoration;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2018/3/28.
 */

public class HomeFragment extends Fragment {

    private View homeView;
    private SearchView sv_search_text;

    protected SwipeRefreshLayout refreshLayout;
    protected RecyclerView recyclerView;
//    private MembersAdapter itemAdapter;
    LinearLayoutManager layoutManager;
    private SearchView calSearchView;
    private GoodsAdapter itemAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        homeView = inflater.inflate(R.layout.fragment_home, null);
        initView();

        if (calSearchView != null) {
            try {        //--拿到字节码
                Class<?> argClass = calSearchView.getClass();
                //--指定某个私有属性,mSearchPlate是搜索框父布局的名字
                Field ownField = argClass.getDeclaredField("mSearchPlate");
                //--暴力反射,只有暴力反射才能拿到私有属性
                ownField.setAccessible(true);
                View mView = (View) ownField.get(calSearchView);
                //--设置背景
                mView.setBackgroundColor(Color.TRANSPARENT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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

        return homeView;
    }

    @SuppressLint("CutPasteId")
    public void initView() {
        sv_search_text = homeView.findViewById(R.id.sv_search_text);
        //去除下划线
        calSearchView = homeView.findViewById(R.id.sv_search_text);
        recyclerView = homeView.findViewById(R.id.rv_goods_list);
        refreshLayout = homeView.findViewById(R.id.sf_goods_list);

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
    }

    /**
     * 重新获取到相关的成员
     */
    private void refreshMembers() {
        itemAdapter.setMemberList(GoodsProvider.getInstance().getAllGoods(0, getActivity()));
        itemAdapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }


}
