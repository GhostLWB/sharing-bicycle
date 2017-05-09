package com.project.liwenbin.sharing_bicycle;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MyLocationOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.inner.GeoPoint;

/**
 * Created by liwenbin on 2017/4/29 0029.
 */
public class NavigateActivity extends Activity {
    private EditText startAddr;
    private Button startNavigate;
    private Button clearRoute;
    private MapView mapView;
    private BMapManager mMapManager = null;
    private MyLocationOverlay myLocationOverlay = null;
    //onResume时注册此listener，onPause时需要Remove,注意此listener不是Android自带的，是百度API中的
    private LocationListener locationListener = null;
    //搜索模块
    private MKSearch searchModel = null;
    private GeoPoint pt = null;
    private SharedPreferences sharedPreferences;
    private String mMapKey = "q6LFOUiBxNwO0ud9V2inkOzFFRGoDGkQ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_navigate);
        startAddr=(EditText)findViewById(R.id.navigate_addr);
        startNavigate=(Button)findViewById(R.id.navigation_start);
        clearRoute=(Button)findViewById(R.id.navigation_clear);
        mapView=(MapView)findViewById(R.id.bmapView_navigate);
        sharedPreferences = this.getSharedPreferences("navigation_pre", Context.MODE_WORLD_WRITEABLE);

        //初始化地图管理器
    }
}
