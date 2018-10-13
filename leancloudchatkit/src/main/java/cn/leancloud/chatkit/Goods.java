package cn.leancloud.chatkit;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;

import java.io.Serializable;

/**
 * Created by wli on 16/2/2.
 * LCChatKit 中的用户类，仅包含三个变量，暂不支持继承扩展
 */
public  class Goods implements Serializable {
    /**
     * 发布的商品的id
     */
    private String goodsId;
    private  String num;
    private  String character;
    /**
     *发布这个商品的用户的id
     */
    private String userId;
    private String avatarUrl;
    /**
     * 发布这个商品的用户的名字
     */
    private String name;
  /**
   * category : 类别七
   * place : 江西理工大学信息学院
   * ACL : {"*":{"read":true,"write":true}}
   * phone : 18870742168
   * ownerId : 5b3b4e959f5454003b6da580
   * goodOwner : {"__type":"Pointer","className":"_User","objectId":"5b3b4e959f5454003b6da580"}
   * info : 机械键盘实用有价值
   * image : {"name":"键盘Image","url":"http://lc-SDDiY1XO.cn-n1.lcfile.com/6e3LnpcOshdE2mgWxEpeYwrV9L040uOfJ50Z7RJr","mime_type":"application/octet-stream","bucket":"SDDiY1XO","metaData":{"owner":"5b3b4e959f5454003b6da580","_checksum":"5d80a1567d8100a25c7596dd99adf319","size":2798,"_name":"键盘Image"},"objectId":"5b3b68b19f5454003b6eb4ca","createdAt":"2018-07-03T12:14:41.960Z","updatedAt":"2018-07-03T12:14:41.960Z"}
   * quantity : 2
   * price : 2000
   * objectId : 5b3b68b267f356003823d697
   * createdAt : 2018-07-03T12:14:42.698Z
   * updatedAt : 2018-07-03T12:14:42.698Z
   */

  private String category;
  private String place;
  private String phone;
  private String info;
  private String goodsName;
  private String imageUrl;
  private String quantity;
  private String price;
    /**
     * 当前对象的id
     */
  private String objectId;


  public Goods(String userId, String userName, String avatarUrl) {
    this.userId = userId;
    this.avatarUrl = avatarUrl;
    this.name = userName;
  }


  public Goods(AVObject object){
      category = object.getString("category");
      place = object.getString("place");
      phone = object.getString("phone");
      info = object.getString("info");
      if (object.getString("image") != null){ //为了迎合购物车的加载
          imageUrl = object.getString("image");
      }else {
          imageUrl = object.getAVFile("image").getUrl();
      }
      quantity = object.getString("quantity");
      price = object.getString("price");
      goodsName = object.getString("name");
      objectId = object.getObjectId();
      userId = object.getString("ownerId");
      avatarUrl = object.getString("avatar");
      name = object.getString("userName");
      character = object.getString("character");
  }
  public Goods(AVObject object, int flag) {
      this(object);
      num = object.get("num").toString();
      goodsId = object.getString("goodsId");
  }




  public String getUserId() {
    return userId;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public String getUserName() {
    return name;
  }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }
}
