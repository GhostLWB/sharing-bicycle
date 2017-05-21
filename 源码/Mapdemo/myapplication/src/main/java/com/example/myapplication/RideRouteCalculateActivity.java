package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviLatLng;



public class RideRouteCalculateActivity extends BaseActivity {
    private NaviLatLng from=null;
    private NaviLatLng to=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.navi);
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);
        getParams();
    }
    private void getParams(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        from = new NaviLatLng(bundle.getDouble("my.lat"),bundle.getDouble("my.lon"));
        to=new NaviLatLng(bundle.getDouble("end.lat"),bundle.getDouble("end.lon"));
    }

    @Override
    public void onInitNaviSuccess() {
        super.onInitNaviSuccess();
        mAMapNavi.calculateRideRoute(from,to);
    }

    @Override
    public void onCalculateRouteSuccess() {
        super.onCalculateRouteSuccess();
        mAMapNavi.startNavi(NaviType.EMULATOR);
    }

    @Override
    public void onLocationChange(AMapNaviLocation location) {
        super.onLocationChange(location);
        Toast.makeText(RideRouteCalculateActivity.this,location.toString(),Toast.LENGTH_SHORT).show();
    }
}
