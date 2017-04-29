package com.project.liwenbin.sharing_bicycle;

import android.support.design.widget.NavigationView;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private Button botton1;
    private Button botton2;
    private Context context;
    private Button requestLocButton;
    //UI 相关
    private Button unlockButton;
    private ImageButton navigteButton;
    private ImageButton preorderButton;
    private ImageButton reportButton;
    private Button showChartButton;

    //图表相关
    private FragmentTransaction fragmentTransaction;

    // 地图相关
    public LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    public BitmapDescriptor mCurrentMarker;
    MapView mMapView;
    BaiduMap mBaiduMap;
    BDLocation myLocation;
    //用户信息相关
    private String wallet="账户余额";
    private String credit="我的积分";
    private String tab="         ";
    private int balance=100;
    private int user_credit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main2);
        context = this;
        registerWiget();

        //百度地图控件注册
        mBaiduMap = mMapView.getMap();
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;

        /** Bundle myBundle=this.getIntent().getExtras();
         String myaccount=myBundle.getString("account");
         String mypassword=myBundle.getString("password");
         Toast toast1=Toast.makeText(this,"account:"+myaccount,Toast.LENGTH_SHORT);
         toast1.setGravity(Gravity.CENTER,100,100);
         toast1.show();
         Toast toast2=Toast.makeText(this,"password:"+mypassword,Toast.LENGTH_SHORT);
         toast2.setGravity(Gravity.CENTER,100,130);
         toast2.show();
         */

        /**
         * 设置导航栏
         */
        DrawerLayout drawer=(DrawerLayout)findViewById(R.id.drawer_layout) ;
        NavigationView navigationView=(NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        navigationView.getMenu().getItem(0).getSubMenu().getItem(0).setTitle(wallet+tab+balance);
        navigationView.getMenu().getItem(0).getSubMenu().getItem(1).setTitle(credit+tab+user_credit);

        /**
         * 设置定位相关选项
         */
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(getApplicationContext());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型国测局经纬度坐标系(gcj02)，百度墨卡托坐标系(bd09)，百度经纬度坐标系(bd09ll)
        option.setIsNeedAddress(true);//是否需要地址
        mLocClient.setLocOption(option);
        mLocClient.start();

        /**
         * 设置定位模式：普通
         */
        mBaiduMap
                .setMyLocationConfigeration(new MyLocationConfiguration(
                        mCurrentMode, true, mCurrentMarker));

        /**
         * 点击定位按钮的实现，令地图中心点回到我的当前位置
         */
        requestLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(myLocation!=null){
                    LatLng ll = new LatLng(myLocation.getLatitude(),
                            myLocation.getLongitude());//设置地图新的中心点
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(ll).zoom(21.0f);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

                }
            }
        });

        /**
         *
         * 按钮点击事件响应
         */

        unlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"正在开发中...", Toast.LENGTH_SHORT).show();
            }
        });
        navigteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"正在开发中...", Toast.LENGTH_SHORT).show();
            }
        });
        preorderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"正在开发中...", Toast.LENGTH_SHORT).show();
            }
        });
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"正在开发中...", Toast.LENGTH_SHORT).show();
            }
        });
        botton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        botton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Register.class);
                startActivity(intent);
                finish();
            }
        });
    }


        /**
         * 定位SDK监听类
         */
        public class MyLocationListenner implements BDLocationListener {


            @Override
            public void onReceiveLocation(BDLocation location) {
                myLocation=location;
                // map view 销毁后不在处理新接收的位置
                if (location == null || mMapView == null) {
                    return;
                }
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        // 此处设置获取到的方向信息，顺时针0-360
                        .direction(100).latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();
                mBaiduMap.setMyLocationData(locData);


                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());//设置地图新的中心点
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(21.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

            }
            public void onReceivePoi(BDLocation poiLocation) {
            }
    }
    /**
     * 注册控件的方法
     */
    private void registerWiget(){
        //按钮绑定
        mMapView = (MapView) findViewById(R.id.bmapView);
        botton1 = (Button) findViewById(R.id.button1);
        botton2 = (Button) findViewById(R.id.button2);
        requestLocButton=(Button)findViewById(R.id.locatebutton);
        unlockButton=(Button)findViewById(R.id.unlockButton);
        navigteButton=(ImageButton)findViewById(R.id.navigateButton);
        preorderButton=(ImageButton)findViewById(R.id.preorderButton);
        reportButton=(ImageButton)findViewById(R.id.reportButton);

    }
    /**
     * 监听返回键，当返回键按下时，由导航栏回到主界面
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.wallet) {
            // Handle the camera action
        } else if (id == R.id.credit) {

        } else if (id == R.id.trip) {
            Intent intent=new Intent(context,ChartActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.credit) {

        } else if (id == R.id.credit) {

        } else if (id == R.id.credit) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

        /**
         * Actiity生命周期管理
         */
        @Override
        protected void onPause() {
            mMapView.onPause();
            super.onPause();
        }

        @Override
        protected void onResume() {
            mMapView.onResume();
            super.onResume();
        }

        @Override
        protected void onDestroy() {
            // 退出时销毁定位
            mLocClient.stop();
            // 关闭定位图层
            mBaiduMap.setMyLocationEnabled(false);
            mMapView.onDestroy();
            mMapView = null;
            super.onDestroy();
        }

}
