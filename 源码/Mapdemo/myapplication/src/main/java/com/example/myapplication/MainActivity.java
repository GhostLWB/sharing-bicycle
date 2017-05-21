package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import overlay.RideRouteOverlay;

public class MainActivity extends Activity {
    MapView mMapView = null;
    private AMap aMap=null;
    private AutoCompleteTextView search_edittext;
    private ListView search_list;
    private TextView textView;
    private String city;
    private SearchAdapter searchAdapter;
    LocationSource.OnLocationChangedListener mListener;
    AMapLocationClient mlocationClient;
    AMapLocationClientOption mLocationOption;
    private Marker myMarker;
    private Marker endMarker;
    private boolean flag=false;
    private RouteSearch mRouteSearch;
    private RideRouteResult rideRouteResult;
    private Button bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);

        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        iniView();
        iniSearch();


    }


    private void iniView()
    {
//        MyLocationStyle myLocationStyle;
//        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
//        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
//        myLocationStyle.radiusFillColor(Color.WHITE);
//        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
//        aMap.getUiSettings().setMyLocationButtonEnabled(true);
//        aMap.setMyLocationEnabled(true);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        // 设置定位监听
        aMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener listener) {
                mListener = listener;
                if (mlocationClient == null) {
                    //初始化定位
                    mlocationClient = new AMapLocationClient(MainActivity.this);
                    //初始化定位参数
                    mLocationOption = new AMapLocationClientOption();
                    //设置定位回调监听
                    mlocationClient.setLocationListener(new AMapLocationListener() {
                        @Override
                        public void onLocationChanged(AMapLocation amapLocation) {
                            if (mListener != null&&amapLocation != null) {
                                if (amapLocation != null
                                        &&amapLocation.getErrorCode() == 0) {

                                    if(myMarker==null){
                                        myMarker=aMap.addMarker(new MarkerOptions().position(new LatLng(amapLocation.getLatitude(),amapLocation.getLongitude())));
                                    }
                                    else {
                                        myMarker.setPosition(new LatLng(amapLocation.getLatitude(),amapLocation.getLongitude()));
                                        if(flag==false){
                                            aMap.moveCamera(CameraUpdateFactory.newLatLng(myMarker.getPosition()));
                                            flag=true;
                                        }
                                    }
                                } else {
                                    String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                                    Log.e("AmapErr",errText);
                                }
                            }
                        }
                    });
                    //设置为高精度定位模式
                    mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                    //设置定位参数
                    mlocationClient.setLocationOption(mLocationOption);
                    // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
                    // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
                    // 在定位结束后，在合适的生命周期调用onDestroy()方法
                    // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
                    mlocationClient.startLocation();//启动定位
                }
            }

            @Override
            public void deactivate() {

                mListener = null;
                if (mlocationClient != null) {
                    mlocationClient.stopLocation();
                    mlocationClient.onDestroy();
                }
                mlocationClient = null;
            }
           });
       aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
   // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
   // 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种

        bt=(Button)findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((myMarker!=null)&&(endMarker!=null)){
                    Intent intent = new Intent(MainActivity.this,RideRouteCalculateActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putDouble("my.lat",myMarker.getPosition().latitude);
                    bundle.putDouble("my.lon",myMarker.getPosition().longitude);
                    bundle.putDouble("end.lat",endMarker.getPosition().latitude);
                    bundle.putDouble("end.lon",endMarker.getPosition().longitude);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });



    }

    private void iniSearch(){
        search_edittext= (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        search_list= (ListView) findViewById(R.id.listView);
        textView=(TextView)findViewById(R.id.textView);
        city=mlocationClient.getLastKnownLocation().getCity();
        search_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(search_list.getVisibility()==View.INVISIBLE)
                {
                    search_list.setVisibility(View.VISIBLE);
                    mMapView.setVisibility(View.INVISIBLE);
                    bt.setVisibility(View.INVISIBLE);
                }
                String content=s.toString().trim();//获取自动提示输入框的内容
                InputtipsQuery inputtipsQuery=new InputtipsQuery(content,city);//初始化一个输入提示搜索对象，并传入参数
                inputtipsQuery.setCityLimit(true);//将获取到的结果进行城市限制筛选
                Inputtips inputtips=new Inputtips(MainActivity.this,inputtipsQuery);//定义一个输入提示对象，传入当前上下文和搜索对象
                inputtips.setInputtipsListener(new Inputtips.InputtipsListener() {
                    @Override
                    public void onGetInputtips(List<Tip> list, int returnCode) {
                        if(returnCode== AMapException.CODE_AMAP_SUCCESS){//如果输入提示搜索成功
                            List<HashMap<String,String>> searchList=new ArrayList<HashMap<String, String>>() ;
                            List<Tip> list_tip=new ArrayList<Tip>();
                            for (int i=0;i<list.size();i++){
                                if((list.get(i).getPoint()==null)||(list.get(i).getDistrict()).equals(""))
                                {
                                    continue;
                                }
                                HashMap<String,String> hashMap=new HashMap<String, String>();
                                hashMap.put("name",list.get(i).getName());
                                hashMap.put("address",list.get(i).getDistrict());//将地址信息取出放入HashMap中
                                searchList.add(hashMap);//将HashMap放入表中
                                list_tip.add(list.get(i));
                            }
                            searchAdapter=new SearchAdapter(MainActivity.this,searchList,list_tip);//新建一个适配器
                            search_list.setAdapter(searchAdapter);//为listview适配





                        }else{
                            Log.d("fail",String.valueOf(returnCode));

                        }
                    }
                });//设置输入提示查询的监听，实现输入提示的监听方法onGetInputtips()
                inputtips.requestInputtipsAsyn();//输入查询提示的异步接口实现
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        search_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tip tip=(Tip) searchAdapter.getItem(position);
                search_edittext.setText(tip.getName());
                LatLng latLng = new LatLng(tip.getPoint().getLatitude(),tip.getPoint().getLongitude());
                mMapView.setVisibility(View.VISIBLE);
                search_list.setVisibility(View.INVISIBLE);
                if(endMarker==null) {
                    endMarker=aMap.addMarker(new MarkerOptions().position(latLng));
                }
                else {
                    endMarker.setPosition(latLng);
                }
                mRouteSearch = new RouteSearch(MainActivity.this);
                mRouteSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
                    @Override
                    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

                    }

                    @Override
                    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

                    }

                    @Override
                    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

                    }

                    @Override
                    public void onRideRouteSearched(RideRouteResult result, int rCode) {

                        Log.d("success",String.valueOf(rCode));
                        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                            if (result != null && result.getPaths() != null
                                    && result.getPaths().size() > 0) {
                                rideRouteResult = result;
                                RidePath ridePath = rideRouteResult.getPaths().get(0);
                                aMap.clear();// 清理地图上的所有覆盖物
                                RideRouteOverlay routeOverlay = new RideRouteOverlay(MainActivity.this, aMap,
                                        ridePath, rideRouteResult.getStartPos(),
                                        rideRouteResult.getTargetPos());
                                routeOverlay.removeFromMap();
                                routeOverlay.addToMap();
                                routeOverlay.zoomToSpan();
                                bt.setVisibility(View.VISIBLE);
                            } else {

                            }
                        } else{

                        }
                    }
                });
                final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(new LatLonPoint(myMarker.getPosition().latitude,myMarker.getPosition().longitude),
                        new LatLonPoint(endMarker.getPosition().latitude,endMarker.getPosition().longitude));
                RouteSearch.RideRouteQuery query = new RouteSearch.RideRouteQuery(fromAndTo);
                mRouteSearch.calculateRideRouteAsyn(query);



            }
        });
        textView.setText(" "+city+" ");

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        if(null != mlocationClient){
            mlocationClient.onDestroy();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }
}