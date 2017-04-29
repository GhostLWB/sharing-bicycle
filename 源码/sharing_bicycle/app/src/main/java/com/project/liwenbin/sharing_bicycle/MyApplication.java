package com.project.liwenbin.sharing_bicycle;

import android.app.Application;
import android.content.Context;

/**
 * Created by liwenbin on 2017/4/26 0026.
 */
public class MyApplication extends Application {
    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
    }

    public static Context getContextObject(){
        return context;
    }
}
