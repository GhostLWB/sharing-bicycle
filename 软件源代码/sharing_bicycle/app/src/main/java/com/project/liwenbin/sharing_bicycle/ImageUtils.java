package com.project.liwenbin.sharing_bicycle;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by liwenbin on 2017/5/24 0024.
 */
public class ImageUtils {
    public static int calculateInSampleSize( //参2和3为ImageView期待的图片大小
     BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 图片的实际大小
        final int height = options.outHeight;
        final int width = options.outWidth;
        //默认值
        int inSampleSize = 1;
        //动态计算inSampleSize的值
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height/2;
            final int halfWidth = width/2;
            while( (halfHeight/inSampleSize) >= reqHeight && (halfWidth/inSampleSize) >= reqWidth){
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // 计算inSampleSize，因为前面已经设置过标志位并调用了decode方法，所以参数option包含了真实宽高信息
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // 别忘记将opts.inJustDecodeBound设置回false，否则获取的bitmap对象还是null
        options.inJustDecodeBounds = false;
        //重新加载图片
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap compressImage(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //质量压缩方法，参数100表示不压缩，把压缩后的数据存放到baos中
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        //循环判断如果压缩后图片大小>50kb就继续压缩
        while ( baos.toByteArray().length/1024 > 50) {
            //清空baos
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;//每次都减少10
        }
        //把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        //把ByteArrayInputStream数据生成图片
        Bitmap newBitmap = BitmapFactory.decodeStream(isBm, null, null);
        return newBitmap;
    }
}
