package com.example.mine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.common.views.GoodsProvider;

import java.lang.reflect.Field;

import cn.leancloud.chatkit.view.LCIMDividerItemDecoration;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2018/7/5.
 */

public class NeedActivity extends Activity{


    private View homeView;
    private SearchView sv_search_text;

    protected SwipeRefreshLayout refreshLayout;
    protected RecyclerView recyclerView;
    //    private MembersAdapter itemAdapter;
    LinearLayoutManager layoutManager;
    private SearchView calSearchView;
    private GoodsAdapter itemAdapter;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need);
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

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new LCIMDividerItemDecoration(this));


        itemAdapter = new GoodsAdapter();
        recyclerView.setAdapter(itemAdapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMembers();
            }
        });

    }


    @SuppressLint("CutPasteId")
    public void initView() {
        recyclerView = findViewById(R.id.rv_goods_list);
        refreshLayout =findViewById(R.id.sf_goods_list);
        Toast.makeText(this, "长按可以删除", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onDestroy() {
        //将事件的处理移除
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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
        itemAdapter.setMemberList(GoodsProvider.getInstance().getAllGoods(1, this));
        itemAdapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }
}
