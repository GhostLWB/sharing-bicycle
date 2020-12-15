package com.project.liwenbin.sharing_bicycle;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

/**
 * Created by liwenbin on 2017/5/24 0024.
 */
public class ReleaseImageViewUtils {
    public static void releaseImage(ImageView imageView){
        if(imageView != null && imageView.getDrawable() != null){

            Bitmap oldBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

            imageView.setImageDrawable(null);

            if(oldBitmap != null){

                oldBitmap.recycle();

                oldBitmap = null;

            }

        }

        // Other code.

        System.gc();
    }
}
