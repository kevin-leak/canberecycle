package com.example.common.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.leancloud.chatkit.Goods;
import cn.leancloud.chatkit.LCChatProfileProvider;
import cn.leancloud.chatkit.LCChatProfilesCallBack;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2018/7/3.
 */

public class GoodsProvider implements LCChatProfileProvider {


    private static GoodsProvider goodsProvider;
    //所有的商品
    private List<Goods> allGoods = new ArrayList<>();
    //发布的商品
    private List<Goods> myGoods = new ArrayList<>();
    //回收车的物品
    private List<Goods> carGoods = new ArrayList<>();

    public synchronized static GoodsProvider getInstance() {
        if (null == goodsProvider) {
            goodsProvider = new GoodsProvider();
        }
        return goodsProvider;
    }

    private GoodsProvider() {
    }

    @Override
    public void fetchProfiles(List<String> list, LCChatProfilesCallBack callBack) {
        List<Goods> goodsList = new ArrayList<Goods>();
        for (String userId : list) {
            for (Goods goods : allGoods) {
                if (goods.getUserId().equals(userId)) {
                    goodsList.add(goods);
                    break;
                }
            }
        }
        callBack.done(goodsList, null);
    }


    /**
     * @param flag 0 代表着返回除去自己发布的所有的物品
     * @return
     */
    public List<Goods> getAllGoods(int flag, Context context) {

        SharedPreferences pref1 = context.getSharedPreferences("Database", MODE_PRIVATE);
        final String character = pref1.getString("character", "角色一");

        if (flag == 3){
            AVQuery<AVObject> query = new AVQuery<AVObject>("Goods"+AVUser.getCurrentUser().getObjectId());
            query.findInBackground(new FindCallback<AVObject>() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void done(List<AVObject> list, AVException e) {
                    carGoods.clear();
                    if (list != null){
                        for (AVObject object: list){
                            //Goods(String userId, String userName, String avatarUrl)
//                        object.getAVObject("goodOwner").getString("objectId")
                            Goods goods = new Goods(object, 3);
                            carGoods.add(goods);
                        }
                    }
                }
            });

        }else {
            AVQuery<AVObject> query = new AVQuery<AVObject>("Goods");
            query.findInBackground(new FindCallback<AVObject>() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void done(List<AVObject> list, AVException e) {
                    allGoods.clear();
                    myGoods.clear();
                    if (list != null){
                        for (AVObject object: list){
                            //Goods(String userId, String userName, String avatarUrl)
//                        object.getAVObject("goodOwner").getString("objectId")
                            Goods goods = new Goods(object);
                            if (Objects.equals(goods.getUserId(), AVUser.getCurrentUser().getObjectId())){
                                myGoods.add(goods);
                            }else if (Objects.equals(goods.getCharacter(), character)){
                                allGoods.add(goods);
                            }
                        }
                    }
                }
            });
        }
        if (flag == 0){
            return allGoods;
        }else if (flag == 1){
            return myGoods;
        }else {
            return carGoods;
        }
    }
}
