package cn.leancloud.chatkit.cache;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.AVCallback;
import com.avos.avoscloud.AVException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.leancloud.chatkit.Goods;
import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.LCChatProfileProvider;
import cn.leancloud.chatkit.LCChatProfilesCallBack;


/**
 * Created by wli on 16/2/25.
 * 用户信息缓存
 * 流程：
 * 1、如果内存中有则从内存中获取
 * 2、如果内存中没有则从 db 中获取
 * 3、如果 db 中没有则通过调用开发者设置的回调 LCChatProfileProvider.fetchProfiles 来获取
 * 同时获取到的数据会缓存到内存与 db
 */
public class LCIMProfileCache {

  private static final String USER_NAME = "user_name";
  private static final String USER_AVATAR = "user_avatar";
  private static final String USER_ID = "user_id";

  private Map<String, Goods> userMap;
  private LCIMLocalStorage profileDBHelper;

  private LCIMProfileCache() {
    userMap = new HashMap<>();
  }

  private static LCIMProfileCache profileCache;

  public static synchronized LCIMProfileCache getInstance() {
    if (null == profileCache) {
      profileCache = new LCIMProfileCache();
    }
    return profileCache;
  }

  /**
   * 因为只有在第一次的时候需要设置 Context 以及 clientId，所以单独拎出一个函数主动调用初始化
   * 避免 getInstance 传入过多参数
   *
   * @param context
   * @param clientId
   */
  public synchronized void initDB(Context context, String clientId) {
    profileDBHelper = new LCIMLocalStorage(context, clientId, "ProfileCache");
  }

  /**
   * 根据 id 获取用户信息
   * 先从缓存中获取，若没有再调用用户回调获取
   *
   * @param id
   * @param callback
   */
  public synchronized void getCachedUser(final String id, final AVCallback<Goods> callback) {
    getCachedUsers(Arrays.asList(id), new AVCallback<List<Goods>>() {
      @Override
      protected void internalDone0(List<Goods> lcimUserProfiles, AVException e) {
        Goods Goods =
          (null != lcimUserProfiles && !lcimUserProfiles.isEmpty() ? lcimUserProfiles.get(0) : null);
        callback.internalDone(Goods, e);
      }
    });
  }

  /**
   * 获取多个用户的信息
   * 先从缓存中获取，若没有再调用用户回调获取
   *
   * @param idList
   * @param callback
   */
  public synchronized void getCachedUsers(List<String> idList, final AVCallback<List<Goods>> callback) {
    if (null != callback) {
      if (null == idList || idList.isEmpty()) {
        callback.internalDone(null, new AVException(new Throwable("idList is empty!")));
      } else {
        final List<Goods> profileList = new ArrayList<Goods>();
        final List<String> unCachedIdList = new ArrayList<String>();

        for (String id : idList) {
          if (userMap.containsKey(id)) {
            profileList.add(userMap.get(id));
          } else {
            unCachedIdList.add(id);
          }
        }

        if (unCachedIdList.isEmpty()) {
          callback.internalDone(profileList, null);
        } else if (null != profileDBHelper) {
          profileDBHelper.getData(idList, new AVCallback<List<String>>() {
            @Override
            protected void internalDone0(List<String> strings, AVException e) {
              if (null != strings && !strings.isEmpty() && strings.size() == unCachedIdList.size()) {
                List<Goods> profileList = new ArrayList<Goods>();
                for (String data : strings) {
                  Goods userProfile = getUserProfileFromJson(data);
                  if (null != userProfile) {
                    userMap.put(userProfile.getUserId(), userProfile);
                    profileList.add(userProfile);
                  }
                }
                callback.internalDone(profileList, null);
              } else {
                getProfilesFromProvider(unCachedIdList, profileList, callback);
              }
            }
          });
        } else {
          getProfilesFromProvider(unCachedIdList, profileList, callback);
        }
      }
    }
  }

  /**
   * 根据 id 通过开发者设置的回调获取用户信息
   *
   * @param idList
   * @param callback
   */
  private void getProfilesFromProvider(List<String> idList, final List<Goods> profileList,
                                       final AVCallback<List<Goods>> callback) {
    LCChatProfileProvider profileProvider = LCChatKit.getInstance().getProfileProvider();
    if (null != profileProvider) {
      // TODO: 2017/12/11 获取用户信息
      profileProvider.fetchProfiles(idList, new LCChatProfilesCallBack() {
        @Override
        public void done(List<Goods> userList, Exception e) {
          if (null != userList) {
            for (Goods userProfile : userList) {
              cacheUser(userProfile);
            }
          }
          profileList.addAll(userList);
          callback.internalDone(profileList, null != e ? new AVException(e) : null);
        }
      });
    } else {
      callback.internalDone(null, new AVException(new Throwable("please setProfileProvider first!")));
    }
  }

  /**
   * 根据 id 获取用户名
   *
   * @param id
   * @param callback
   */
  public void getUserName(String id, final AVCallback<String> callback) {
    getCachedUser(id, new AVCallback<Goods>() {
      @Override
      protected void internalDone0(Goods userProfile, AVException e) {
        String userName = (null != userProfile ? userProfile.getUserName() : null);
        callback.internalDone(userName, e);
      }
    });
  }

  /**
   * 根据 id 获取用户头像
   *
   * @param id
   * @param callback
   */
  public void getUserAvatar(String id, final AVCallback<String> callback) {
    getCachedUser(id, new AVCallback<Goods>() {
      @Override
      protected void internalDone0(Goods userProfile, AVException e) {
        String avatarUrl = (null != userProfile ? userProfile.getAvatarUrl() : null);
        callback.internalDone(avatarUrl, e);
      }
    });
  }

  /**
   * 内存中是否包相关 Goods 的信息
   *
   * @param id
   * @return
   */
  public synchronized boolean hasCachedUser(String id) {
    return userMap.containsKey(id);
  }

  /**
   * 缓存 Goods 信息，更新缓存同时也更新 db
   * 如果开发者 Goods 信息变化，可以通过调用此方法刷新缓存
   *
   * @param userProfile
   */
  public synchronized void cacheUser(Goods userProfile) {
    if (null != userProfile && null != profileDBHelper) {
      userMap.put(userProfile.getUserId(), userProfile);
      profileDBHelper.insertData(userProfile.getUserId(), getStringFormUserProfile(userProfile));
    }
  }

  /**
   * 从 db 中的 String 解析出 Goods
   *
   * @param str
   * @return
   */
  private Goods getUserProfileFromJson(String str) {
    try {
      JSONObject jsonObject = JSONObject.parseObject(str);
      String userName = jsonObject.getString(USER_NAME);
      String userId = jsonObject.getString(USER_ID);
      String userAvatar = jsonObject.getString(USER_AVATAR);
      return new Goods(userId, userName, userAvatar);
    } catch (Exception e) {
    }
    return null;
  }

  /**
   * Goods 转换成 json String
   *
   * @param userProfile
   * @return
   */
  private String getStringFormUserProfile(Goods userProfile) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put(USER_NAME, userProfile.getUserName());
    jsonObject.put(USER_AVATAR, userProfile.getAvatarUrl());
    jsonObject.put(USER_ID, userProfile.getUserId());
    return jsonObject.toJSONString();
  }
}
