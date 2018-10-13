package com.example.recycle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

/**
 * 建立用于选取照片是的数据转化
 * Created by Administrator on 2017/12/21.
 */

public class DrawableUtils {

    /**
     * 将drawable转化为bitmap
     * @return 返回一个bitmap数据。
     */
    public static Bitmap drawableTOBitmap(Drawable drawable) {
        //判断类型，是否是可以拉伸的位图
        if (drawable instanceof BitmapDrawable){
            return ((BitmapDrawable) drawable).getBitmap();
        }else if (drawable instanceof NinePatchDrawable){
            //获取到相关的drawable配置；
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            //配置bitmap参数
            boolean isEqual = (drawable.getOpacity() != PixelFormat.OPAQUE);
            Bitmap.Config config =  isEqual? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
            Bitmap bitmap = Bitmap.createBitmap(width, height,config);
            //选择画布，重新画
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0,0,width,height);
            drawable.draw(canvas);
            return bitmap;
        }else {
            return null;
        }
    }

    /**
     * 将bitmap转化为drawable文件
     * @param bitmap 传入bitmap阐述
     * @return 返回drawable资源
     */
    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    /**
     * 将bitmap数据转化为byte[]
     * @param bmp 传入bitmap的值
     * @return 返回一个被压缩的byte[]
     */
    public static byte[] bitmapToByte(Bitmap bmp){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //将数据进行压缩
        bmp.compress(Bitmap.CompressFormat.PNG,100,stream);

//        int bytes = bmp.getByteCount();
//
//        ByteBuffer buf = ByteBuffer.allocate(bytes);
//        bmp.copyPixelsToBuffer(buf);
//
//        byte[] byteArray = buf.array();

        return stream.toByteArray();
    }

    /**
     * 将byte[]转化为bitmap
     * @param bytes 传入你要修改的byte[]数据
     * @return 返回bitmap数据
     */
    public static Bitmap byteToBitmap(byte[] bytes){
        if (bytes != null){
            return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        }else {
            return null;
        }
    }

    /**
     *  Drawable 文件改变为 byte[]
     * @param drawable
     * @return
     */
    public static byte[] DrawableToByte(Drawable drawable) {
        return bitmapToByte(drawableTOBitmap(drawable));
    }
}
