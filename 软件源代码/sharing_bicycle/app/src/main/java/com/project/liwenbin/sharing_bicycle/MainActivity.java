package com.project.liwenbin.sharing_bicycle;

import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import net.sf.json.JSONArray;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private Context context;
    private Button requestLocButton;
    //UI 相关
    private Button unlockButton;
    private ImageButton navigteButton;
    private ImageButton preorderButton;
    private ImageButton reportButton;
    final BitmapDescriptor bd = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding);
    final BitmapDescriptor bd_selected = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding_selected);
    final BitmapDescriptor bd_preorder = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding_preorder);
    final BitmapDescriptor bd_unlockable = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding_unlockable);
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private CircularProgressView circularProgressView;
    private ImageView main_roll_one;
    private ImageView main_roll_two;
    private ImageView main_roll_three;
    private Animation scrollBackgroundAnimation1;
    private Animation scrollBackgroundAnimation2;
    private Animation scrollBackgroundAnimation3;
    private ImageView count_time_background;
    private TextView count_time_promt;
    private TextView count_time;
    private boolean ispreorder_done=false;
    PreorderHelp mainPreHelp;
    private ImageButton abortPreorder;
    private TextView abortPreorderText;
    // 地图相关
    public LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    public BitmapDescriptor mCurrentMarker;
    MapView mMapView;
    BaiduMap mBaiduMap;
    BDLocation myLocation;
    Vector<Marker> bicycles_marker=new Vector<>();

    //用户信息相关
    private User user=null;
    private TextView user_id;
    private String wallet="账户余额";
    private String credit="我的积分";
    private String tab="         ";
    private String account_show;
    private String account_raw;
    private double balance=0;
    private double user_credit=0;
    private static final String preorderKey="selected_bicycle_id";
    private int preorder_interval=-1;
    private int count_time_minute=0;
    private int count_time_second=0;
    double[] userWalletAndCredit = {0.0, 0.0};
    private int timeremainning=-1;//负责从savedInstanceState中获得时间数据

    //车辆相关
    Vector<Bicycle>bicycles=new Vector<>();
    String bicycle_raw_data=null;
    Marker selectedBicycleMarker=null;
    Bicycle selectedBicycle=null;
    String Preordered_bike=null;

    Handler handler = new Handler();
    static Runnable runnable=null;
    int recLen = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main2);
        context = this;
        if (savedInstanceState!=null){
            timeremainning=savedInstanceState.getInt("recountTime");
           // Preordered_bike=savedInstanceState.getString("preorderbike");
            if (savedInstanceState.getString("mainUser") != null) {
                user=User.getUser();
                user.setAccount(savedInstanceState.getString("mainUser"));
            }
        }
        mainPreHelp=PreorderHelp.getPreorderHelp();

        //控件注册都在这个方法中
        registerWiget();
        scroll();//使背景滚动
        circularProgressView.setVisibility(View.INVISIBLE);

        //百度地图控件注册
        mBaiduMap = mMapView.getMap();
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;

        //用户信息更新
        user=User.getUser();
        balance=user.getBalance();
        user_credit=user.getCredit();
        if (user.getAccount()!=null){
            userWalletAndCredit =queryUserWalletAndCredit(user.getAccount()) ;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    user.setBalance(userWalletAndCredit[0]);
                    user.setCredit(userWalletAndCredit[1]);
                }
            },500);
        }

        //查询自行车数据
        queryBicycleLoc();


        /**
         * 设置导航栏
         */

        navigationView.setNavigationItemSelectedListener(this);
        drawer=(DrawerLayout)findViewById(R.id.drawer_layout) ;
       drawer.setDrawerListener(new DrawerLayout.DrawerListener() {
           @Override
           public void onDrawerSlide(View drawerView, float slideOffset) {
               user=User.getUser();
               if (user.getAccount()!=null) {//用户已经登录之后的导航栏显示用户账号
                   account_raw = user.getAccount();
                   if (account_raw!=null)
                       if (account_raw.length()==11)
                           user_id.setText(proccessAccount(account_raw));
               }else{//若用户没有登录
                   user_id.setText("请登录");
                   balance=0;
                   user_credit=0;
                   navigationView.getMenu().getItem(2).getSubMenu().getItem(0).setTitle("登录/注册");
                   navigationView.getMenu().getItem(2).getSubMenu().getItem(0).setIcon(R.drawable.log_in);

               }
               Log.d("MainActivity","onDrawerSlide:user balance is :"+balance);
               navigationView.setItemIconTintList(null);

           }

           @Override
           public void onDrawerOpened(View drawerView) {
               if (user.getAccount()!=null){
                   balance=user.getBalance();
                   user_credit=user.getCredit();
               }else{
                   balance=0.0;
                   user_credit=0.0;
               }
               Log.d("MainActivity","onDrawerOpened :user balance is :"+balance);
               navigationView.getMenu().getItem(0).getSubMenu().getItem(0).setTitle(wallet+tab+balance);
               navigationView.getMenu().getItem(0).getSubMenu().getItem(1).setTitle(credit+tab+user_credit);
           }

           @Override
           public void onDrawerClosed(View drawerView) {

           }

           @Override
           public void onDrawerStateChanged(int newState) {

           }
       });



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
         * 点击定位按钮的实现，令地图中心点回到我的当前位置，并刷新，加载自行车的位置
         */
        requestLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryBicycleLoc();
                selectedBicycle=null;
                selectedBicycleMarker=null;
                //将地图中心定位到画面中央
                if(myLocation!=null){
                    LatLng ll = new LatLng(myLocation.getLatitude(),
                            myLocation.getLongitude());//设置地图新的中心点
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(ll).zoom(21.0f);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

                }
                //查询用户信息
                if (user.getAccount()!=null){
                    userWalletAndCredit =queryUserWalletAndCredit(user.getAccount()) ;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            user.setBalance(userWalletAndCredit[0]);
                            user.setCredit(userWalletAndCredit[1]);
                        }
                    },500);
                }


                String url = "http://123.206.80.243:8080/sharing_bicycle/bike_full";
                RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        JSONArray json = JSONArray.fromObject(s);
                        for(int i=0;i<json.size();i++)
                        {
                            bicycle_raw_data=bicycle_raw_data+" "+json.get(i).toString();
                        }
                        //proccessRawData(bicycle_raw_data);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MainActivity.this,"网络故障",Toast.LENGTH_SHORT).show();
                    }
                });
                mQueue.add(stringRequest);

            }
        });

        /**
         *
         * 按钮点击事件响应
         */

        /**
         * 用车按钮
         */
        unlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circularProgressView.setVisibility(View.VISIBLE);
                //用户有没有登录
                user=User.getUser();

                if (selectedBicycle!=null)
                Log.d("MainActivity","距离为:"+DistanceUtils.getDistanceOfMeter(myLocation.getLatitude(), myLocation.getLongitude(), selectedBicycle.getLocation().latitude,
                        selectedBicycle.getLocation().longitude));

                if (user.getAccount()==null){
                    Log.d("MainActivity","user is null");
                    Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
                    Intent myintent=new Intent(context,LoginActivity.class);
                    startActivity(myintent);
                    finish();
                //用户有没有选择车辆
                }else if (selectedBicycle==null){
                    Log.d("MainActivity","user id is:"+user.getAccount());
                    final Snackbar snackbar=Snackbar.make(getWindow().getDecorView(),"您还没有选择车辆",Snackbar.LENGTH_SHORT);
                    SnackBarUtil.setSnackbarColor(snackbar,SnackBarUtil.blue);
                    snackbar.show();
                    circularProgressView.setVisibility(View.INVISIBLE);
                //用户是否已经在用车

                }else if (DistanceUtils.getDistanceOfMeter(myLocation.getLatitude(), myLocation.getLongitude(), selectedBicycle.getLocation().latitude,
                                            selectedBicycle.getLocation().longitude) <= 20.0) {//是否在20米范围内


                    circularProgressView.setVisibility(View.VISIBLE);
                    /**
                     * 清空一些变量的值
                     */

                    mainPreHelp=PreorderHelp.getPreorderHelp();
                    //将timeremainning与mainPreHelp.setTime_interval_help(-1)都置为空，表示清除预约信息
                    timeremainning=-1;
                    mainPreHelp.setTime_interval_help(-1);
                    handler.removeCallbacks(runnable);
                    //Preordered_bike=null;
                    mainPreHelp.setPreorder_bike_id(null);

                    String usrBikeUrl = "http://123.206.80.243:8080/sharing_bicycle/use_bike.do";
                    List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                    params.add(new BasicNameValuePair("user_id", user.getAccount()));
                    params.add(new BasicNameValuePair("bike_id", selectedBicycle.getBike_id() + ""));
                    Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            switch (msg.what) {
                                case 1:
                                    String result = (String) msg.obj;
                                    net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(result);
                                    boolean flag = json.getBoolean("flag");
                                    circularProgressView.setVisibility(View.INVISIBLE);
                                    if (flag) {

                                        final Snackbar snackbar=Snackbar.make(getWindow().getDecorView(),"车辆与您的距离为"+DistanceUtils.getDistanceOfMeter(myLocation.getLatitude(), myLocation.getLongitude(), selectedBicycle.getLocation().latitude,
                                                selectedBicycle.getLocation().longitude)+"米,开锁成功",Snackbar.LENGTH_SHORT);
                                        SnackBarUtil.setSnackbarColor(snackbar,SnackBarUtil.blue);
                                        snackbar.show();
                                        Bundle using_bundle = new Bundle();
                                        Intent usingIntent = new Intent(context, UsingBicycle.class);
                                        using_bundle.putString("MainUser", user.getAccount());
                                        using_bundle.putString("MainBicycle", selectedBicycle.getBike_id() + "");
                                        usingIntent.putExtras(using_bundle);
                                        startActivity(usingIntent);
                                        finish();
                                    } else {
                                        String reason = json.getString("reason");
                                        //Toast.makeText(context, "您无法用车，因为" + reason, Toast.LENGTH_SHORT).show();
                                        final Snackbar snackbar=Snackbar.make(getWindow().getDecorView(),"您无法用车，因为" + reason,Snackbar.LENGTH_SHORT);
                                        SnackBarUtil.setSnackbarColor(snackbar,SnackBarUtil.blue);
                                        snackbar.show();
                                    }

                                    break;
                            }

                        }
                    };
                    NetUtils.postRequest(usrBikeUrl, params, handler);




                }else{

                    final Snackbar snackbar=Snackbar.make(getWindow().getDecorView(),"车辆与您的距离为"+DistanceUtils.getDistanceOfMeter(myLocation.getLatitude(), myLocation.getLongitude(), selectedBicycle.getLocation().latitude,
                            selectedBicycle.getLocation().longitude)+"米,请再靠近一些",Snackbar.LENGTH_SHORT);
                    SnackBarUtil.setSnackbarColor(snackbar,SnackBarUtil.blue);
                    snackbar.show();
                    circularProgressView.setVisibility(View.INVISIBLE);
                }
            }
        });
        /**
         * 导航按钮
         */
        navigteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,NavigateActivity.class);
                startActivity(intent);
                finish();
            }
        });
        /**
         * 预约按钮
         */
        preorderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(context,PreorderActivity.class);
                Bundle mybundle=new Bundle();
                user=User.getUser();
                /**
                 * 判断前提条件：1.用户已经登录 2.用户已经选择车辆
                 */
                if (user.getAccount()==null){
                    Log.d("MainActivity","user is null");
                    Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
                    Intent myTempintent=new Intent(context,LoginActivity.class);
                    startActivity(myTempintent);
                    finish();
                }else if (selectedBicycle==null){
                    Log.d("MainActivity","user id is:"+user.getAccount());

                    final Snackbar snackbar=Snackbar.make(getWindow().getDecorView(),"您还未选择车辆",Snackbar.LENGTH_SHORT);
                    SnackBarUtil.setSnackbarColor(snackbar,SnackBarUtil.blue);
                    snackbar.show();
                }else if (selectedBicycle.isOrder()) {

                    final Snackbar snackbar=Snackbar.make(getWindow().getDecorView(),"车辆已经被预约",Snackbar.LENGTH_SHORT);
                    SnackBarUtil.setSnackbarColor(snackbar,SnackBarUtil.blue);
                    snackbar.show();
                }else {
                    //Preordered_bike=null;
                    Log.d("MainActivity","bike id is:"+selectedBicycle.getBike_id()+"");
                        circularProgressView.setVisibility(View.VISIBLE);
                        //使用bundle向PreorderActivity传数据并接收返回的数据
                        mybundle.putString(preorderKey,selectedBicycle.getBike_id()+"");
                        intent.putExtras(mybundle);
                        startActivity(intent);
                        circularProgressView.setVisibility(View.INVISIBLE);
                        finish();
                    }
                }
        });
        /**
         * 取消预约按钮
         *
         */
        abortPreorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View snackView=v;
                mainPreHelp=PreorderHelp.getPreorderHelp();
                Log.d("MainActivity","abortButton clicked");
                String finishPreorderUrl="http://123.206.80.243:8080/sharing_bicycle/finish_preorder.do";
                List<BasicNameValuePair> param=new ArrayList<>();
                param.add(new BasicNameValuePair("user_id",user.getAccount()));
                param.add(new BasicNameValuePair("bike_id",mainPreHelp.getPreorder_bike_id()));
                Handler finishPreorderHandler=new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what){
                            case 1:
                                String result = (String)msg.obj;
                                net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(result);
                                boolean flag = json.getBoolean("flag");
                                if (flag){
                                    final Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), "取消预约成功", Snackbar.LENGTH_SHORT);
                                    SnackBarUtil.setSnackbarColor(snackbar,SnackBarUtil.blue);
                                    snackbar.show();

                                }else{
                                    final Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), "取消预约失败", Snackbar.LENGTH_SHORT);
                                    SnackBarUtil.setSnackbarColor(snackbar,SnackBarUtil.blue);
                                    snackbar.show();

                                }
                                handler.removeCallbacks(runnable);
                                timeremainning=-1;
                                mainPreHelp.setTime_interval_help(-1);
                                mainPreHelp.setPreorder_bike_id(null);
                                mainPreHelp=null;
                                count_time_background.setVisibility(View.INVISIBLE);
                                count_time_promt.setVisibility(View.INVISIBLE);
                                count_time.setVisibility(View.INVISIBLE);
                                abortPreorderText.setVisibility(View.INVISIBLE);
                                abortPreorder.setVisibility(View.INVISIBLE);
                                //mainPreHelp=null;
                        }
                    }
                };
                NetUtils.postRequest(finishPreorderUrl,param,finishPreorderHandler);

            }
        });
        /**
         * 反馈按钮
         */
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user=User.getUser();
                if (user.getAccount()==null){
                    Log.d("MainActivity","user is null");
                    Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
                    Intent myTempintent=new Intent(context,LoginActivity.class);
                    startActivity(myTempintent);
                    finish();
                }else{
                    if (selectedBicycle==null){
                        Bundle reportBundle=new Bundle();
                        Intent reportIntent=new Intent(context,ReportActivity.class);
                        reportBundle.putInt("ReportActivity1",0);
                        reportBundle.putString("ReportActivity2",myLocation.getLatitude()+myLocation.getLongitude()+"");
                        reportIntent.putExtras(reportBundle);
                        startActivity(reportIntent);
                        finish();
                    }else{//用户有选择车辆，发送车辆ID
                        Bundle reportBundle=new Bundle();
                        Intent reportIntent=new Intent(context,ReportActivity.class);
                        reportBundle.putInt("ReportActivity1",1);
                        reportBundle.putString("ReportActivity2",selectedBicycle.getBike_id()+"");
                        reportIntent.putExtras(reportBundle);
                        startActivity(reportIntent);
                        finish();
                    }
                }

            }
        });

        /**
         * Marker点击事件响应,获取自行车
         */
        BaiduMap.OnMarkerClickListener markerClickListener=new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                /**
                 * 用户之前没有选择车辆
                 */
                if (selectedBicycleMarker==null) {
                    selectedBicycleMarker = marker;
                    selectedBicycle = findBicycleByID(selectedBicycleMarker.getTitle());
                    marker.setIcon(bd_selected);

                    final Snackbar snackbar=Snackbar.make(getWindow().getDecorView(),"您选择了"+selectedBicycle.getBike_id()+"号车辆",Snackbar.LENGTH_SHORT);
                    SnackBarUtil.setSnackbarColor(snackbar,SnackBarUtil.blue);
                    snackbar.show();
                    Log.d("MainActivity","bicycle selected:"+selectedBicycle.getBike_id());
                    return true;
                /**
                 * 用户之前选择过车辆
                 */
                }else{
                    selectedBicycleMarker.setIcon(bd);
                    selectedBicycle=null;
                    selectedBicycleMarker=marker;
                    selectedBicycle=findBicycleByID(selectedBicycleMarker.getTitle());
                    marker.setIcon(bd_selected);

                    final Snackbar snackbar=Snackbar.make(getWindow().getDecorView(),"您选择了"+selectedBicycle.getBike_id()+"号车辆",Snackbar.LENGTH_SHORT);
                    SnackBarUtil.setSnackbarColor(snackbar,SnackBarUtil.blue);
                    snackbar.show();
                    return  true;
                    }

            }
        };
        mBaiduMap.setOnMarkerClickListener(markerClickListener);
    }//end of method onCreate()



    /**
     * 将marker标注在地图上
     */
    private void addMarkerToMap(){

        for (Bicycle iterator:bicycles){
            Marker bicycleMarker;
            if (iterator.isBreakedown){
                OverlayOptions markOptions=new MarkerOptions().position(iterator.getLocation())
                        .icon(bd_unlockable).zIndex(9).draggable(false);
                bicycleMarker=(Marker)mBaiduMap.addOverlay(markOptions);
            } else if (iterator.isOrder||iterator.in_use){
                OverlayOptions markOptions=new MarkerOptions().position(iterator.getLocation())
                        .icon(bd_preorder).zIndex(9).draggable(false);
                 bicycleMarker=(Marker)mBaiduMap.addOverlay(markOptions);
            }else{
                OverlayOptions markOptions=new MarkerOptions().position(iterator.getLocation())
                        .icon(bd).zIndex(9).draggable(false);
                bicycleMarker=(Marker)mBaiduMap.addOverlay(markOptions);

            }
            bicycleMarker.setTitle(iterator.getBike_id()+"");
            bicycles_marker.add(bicycleMarker);
            Log.d("BaiduMap","add Marker"+iterator.location.toString()+"on map");
        }
    }

    /**
     * 处理用户的账号，将中间四位隐藏
     * @param account
     * @return
     */
    private String proccessAccount(String account){
        String result;
        char[] front=new char[3];
        char[] end=new char[4];
        account.getChars(0,3,front,0);
        account.getChars(7,11,end,0);
        result=""+front[0]+front[1]+front[2]+"****"+end[0]+end[1]+end[2]+end[3];
        return result;

    }
    /**
     * 定位SDK监听类
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {

            myLocation=location;
            // map view 销毁后不在处理新接收的位置
            if (location == null ||mMapView == null) {
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
    }//end of class MyLocationListenner

    /**
     * 向服务器查询获取用户的余额和积分
     *
     * @param id
     * @return
     */
    private double[] queryUserWalletAndCredit(String id) {
        final String userid = id;
        final double[] result = new double[2];
        String url = "http://123.206.80.243:8080/sharing_bicycle/wallet_credit.do";
        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                net.sf.json.JSONObject js = net.sf.json.JSONObject.fromObject(s);
                if (js.getString("balance")!="")
                result[0] = Double.parseDouble(js.getString("balance"));
                if (js.getString("credit")!="")
                result[1] = Double.parseDouble(js.getString("credit"));
                Log.d("LoginActivity", "user's balance is" + result[0]);
                Log.d("LoginActivity", "user's credit is" + result[1]);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                // Toast.makeText(MainActivity.this,volleyError.toString(),Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("id", userid);
                return map;
            }
        };
        mQueue.add(stringRequest);
        return result;
    }

    /**
     * 注册控件的方法
     */
    private void registerWiget(){
        //按钮绑定
        navigationView=(NavigationView)findViewById(R.id.nav_view);
        mMapView = (MapView) findViewById(R.id.bmapView);
        requestLocButton=(Button)findViewById(R.id.locatebutton);
        unlockButton=(Button)findViewById(R.id.unlockButton);
        navigteButton=(ImageButton)findViewById(R.id.navigateButton);
        preorderButton=(ImageButton)findViewById(R.id.preorderButton);
        reportButton=(ImageButton)findViewById(R.id.reportButton);
        user_id=(TextView)navigationView.getHeaderView(0).findViewById(R.id.user_acccount);
        circularProgressView=(CircularProgressView)findViewById(R.id.main_progress_view) ;
        main_roll_one=(ImageView)findViewById(R.id.main_roll_one);
        main_roll_two=(ImageView)findViewById(R.id.main_roll_two);
        main_roll_three=(ImageView)findViewById(R.id.main_roll_three);
        scrollBackgroundAnimation1=new TranslateAnimation(-1920,-300,00,00);
        scrollBackgroundAnimation2=new TranslateAnimation(-50,-1600,40,40);
        scrollBackgroundAnimation3=new TranslateAnimation(-1900,-100,60,60);
        count_time_background=(ImageView)findViewById(R.id.count_time_imageview);
        count_time_promt=(TextView)findViewById(R.id.count_time_prompt);
        count_time=(TextView)findViewById(R.id.count_time);
        abortPreorder=(ImageButton)findViewById(R.id.abort_preorder_button);
        abortPreorderText=(TextView)findViewById(R.id.abort_text);
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

    /**
     * 使背景动起来
     */
    private void scroll(){

        scrollBackgroundAnimation1.setDuration(12000);               //设置每一次动画的持续时间
        scrollBackgroundAnimation1.setRepeatCount(Animation.INFINITE);               //设置重复次数
        scrollBackgroundAnimation1.setRepeatMode(Animation.REVERSE);    //反方向执行
        main_roll_one.setAnimation(scrollBackgroundAnimation1);             //设置动画效果
        scrollBackgroundAnimation1.startNow();                      //启动动画

        scrollBackgroundAnimation2.setDuration(9000);               //设置每一次动画的持续时间
        scrollBackgroundAnimation2.setRepeatCount(Animation.INFINITE);               //设置重复次数
        scrollBackgroundAnimation2.setRepeatMode(Animation.REVERSE);    //反方向执行
        main_roll_two.setAnimation(scrollBackgroundAnimation2);             //设置动画效果
        scrollBackgroundAnimation2.startNow();

        scrollBackgroundAnimation3.setDuration(13000);               //设置每一次动画的持续时间
        scrollBackgroundAnimation3.setRepeatCount(Animation.INFINITE);               //设置重复次数
        scrollBackgroundAnimation3.setRepeatMode(Animation.REVERSE);    //反方向执行
        main_roll_three.setAnimation(scrollBackgroundAnimation3);             //设置动画效果
        scrollBackgroundAnimation3.startNow();

    }

    /**
     * 侧滑菜单点击事件的处理
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.wallet) {
            user=User.getUser();
            if (user.getAccount()==null){
                Log.d("MainActivity","user is null");
                Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
                Intent myintent=new Intent(context,LoginActivity.class);
                startActivity(myintent);
                finish();
            }else{
                Intent intent=new Intent(context,WalletActivity.class);
                startActivity(intent);
                finish();
            }
        } else if (id == R.id.credit) {
            user=User.getUser();
            if (user.getAccount()==null){
                Log.d("MainActivity","user is null");
                Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
                Intent myintent=new Intent(context,LoginActivity.class);
                startActivity(myintent);
                finish();
            }else{
                Intent intent=new Intent(context,CreditActivity.class);
                startActivity(intent);
                finish();
            }
        } else if (id == R.id.trip) {
            Intent intent=new Intent(context,ChartActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.help) {
                Intent helpintent=new Intent(this,HelpActivity.class);
                startActivity(helpintent);
                finish();
        } else if (id == R.id.log) {
            Log.d("MainActivity","R.id.log selected");
            if (user_id.getText().equals("请登录")||user.getAccount()==null){
                Intent intent=new Intent(this,LoginActivity.class);
                startActivity(intent);
                finish();
            }else
            {
                mainPreHelp=PreorderHelp.getPreorderHelp();
                //如果用户还在预约，提示取消预约再退出
                if (mainPreHelp.getTime_interval_help()>0){
                    Toast.makeText(context,"请取消预约再退出！",Toast.LENGTH_SHORT).show();
                }else {
                    Log.d("MainActivity","log out selected");
                    user=null;
                    user=User.getUser();
                    user.setAccount(null);
                    balance=0;
                    user_credit=0;

                    mainPreHelp.setTime_interval_help(-1);
                    mainPreHelp.setPreorder_bike_id(null);
                    handler.removeCallbacks(runnable);
                    //Log.d("MainActivity","user object is null"+user.equals(null));
                }

            }
        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /**
     * 根据id查找Bicycle
     */
    private Bicycle findBicycleByID(String initid){
        int id= Integer.parseInt(initid);
        for (Bicycle iterator:bicycles){
            if (iterator.getBike_id()==id){
                return iterator;
            }
        }
        return null;
    }


    /**
     * 倒计时
     */
    private void count_time(final int interval){
        recLen=interval;
//        if (runnable==null){
            runnable = new Runnable() {
                @Override
                public void run() {

                    count_time_minute=recLen/60;
                    count_time_second=recLen%60;
                    recLen--;
                    timeremainning=recLen;
                    mainPreHelp.setTime_interval_help(recLen);

                    Log.d("MainActivity", "count_time_minute is: " + count_time_minute);
                    Log.d("MainActivity", "count_time_second is: " + count_time_second);

                    String minuText=count_time_minute+"";
                    String secondText=count_time_second+"";
                    if (count_time_minute<10){
                        minuText="0"+count_time_minute;
                    }
                    if (count_time_second<10){
                        secondText="0"+count_time_second;
                    }
                    count_time.setText(minuText+":"+secondText);
                    handler.postDelayed(this, 1000);
                    if (recLen<=0){
                        handler.removeCallbacks(runnable);

                        final Snackbar snackbar=Snackbar.make(getWindow().getDecorView(),"预约时间到",Snackbar.LENGTH_SHORT);
                        SnackBarUtil.setSnackbarColor(snackbar,SnackBarUtil.blue);
                        snackbar.show();
                        timeremainning=-1;
                        mainPreHelp.setTime_interval_help(-1);
                        mainPreHelp=null;
                        //设置UI
                        count_time_background.setVisibility(View.INVISIBLE);
                        count_time_promt.setVisibility(View.INVISIBLE);
                        count_time.setVisibility(View.INVISIBLE);
                        abortPreorder.setVisibility(View.INVISIBLE);
                        abortPreorderText.setVisibility(View.INVISIBLE);

                        mainPreHelp=PreorderHelp.getPreorderHelp();
                        String finishPreorderUrl="http://123.206.80.243:8080/sharing_bicycle/finish_preorder.do";
                        List<BasicNameValuePair> param=new ArrayList<>();
                        param.add(new BasicNameValuePair("user_id",user.getAccount()));
                        param.add(new BasicNameValuePair("bike_id",mainPreHelp.getPreorder_bike_id()));
                        Handler finishPreorderHandler=new Handler(){
                            @Override
                            public void handleMessage(Message msg) {
                                switch (msg.what){
                                    case 1:
                                        String result = (String)msg.obj;
                                        net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(result);
                                        boolean flag = json.getBoolean("flag");
                                        if (flag){
                                            Log.d("PreorderInfo","成功结束预约");
                                            mainPreHelp.setPreorder_bike_id(null);
                                        }else{
                                            Log.d("PreorderInfo","结束预约失败");
                                        }
                                }
                            }
                        };
                        NetUtils.postRequest(finishPreorderUrl,param,finishPreorderHandler);

                    }
                }
            };
            mainPreHelp.setTimeRunnable(runnable);

    }


    /**
     * 向服务器查询自行车的位置并将自行车实例添加到Vector里
     */
    private void queryBicycleLoc(){
        bicycles_marker.clear();
        bicycles.clear();
        selectedBicycle=null;
        selectedBicycleMarker=null;
        mBaiduMap.clear();
        String bike_url = "http://123.206.80.243:8080/sharing_bicycle/bike_full";
        Handler bicycle_handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        String data = (String)msg.obj;
                        bicycles=Bicycle_data.get_data(data);
                        addMarkerToMap();
                        break;
                    default:
                        break;
                }
            }
        };
        NetUtils.getRequest(bike_url,null,bicycle_handler);
        addMarkerToMap();
        for (Marker iterator:bicycles_marker){
            Log.d("MainActivity","distance is :"+DistanceUtils.getDistanceOfMeter(myLocation.getLatitude(),myLocation.getLongitude()
                    ,findBicycleByID(iterator.getTitle()).getLocation().latitude,
                    findBicycleByID(iterator.getTitle()).getLocation().longitude));
            if (DistanceUtils.getDistanceOfMeter(myLocation.getLatitude(),myLocation.getLongitude()
                    ,findBicycleByID(iterator.getTitle()).getLocation().latitude,
                    findBicycleByID(iterator.getTitle()).getLocation().longitude)<=20.0){
                iterator.setIcon(bd_unlockable);
            }
        }
    }
    /**
     * Actiity生命周期管理
     */
        @Override
        protected void onPause() {
            mMapView.onPause();
            if (timeremainning>0){
                //mainPreHelp.setTime_interval_help(timeremainning);
            }
            super.onPause();
        }

        @Override
        protected void onResume() {
            mMapView.onResume();
            user=User.getUser();
            mainPreHelp=PreorderHelp.getPreorderHelp();

            //设置UI
            count_time_background.setVisibility(View.INVISIBLE);
            count_time_promt.setVisibility(View.INVISIBLE);
            count_time.setVisibility(View.INVISIBLE);
            abortPreorderText.setVisibility(View.INVISIBLE);
            abortPreorder.setVisibility(View.INVISIBLE);

            Log.d("MainActivity", "timeramainTemp is: " + timeremainning);
            Log.d("MainActivity", "mainPreHelp.getTime_interval_help() is: " + mainPreHelp.getTime_interval_help());

            //timeremainning是从savedInstance里面获取的剩余倒计时，mainPreHelp.getTime_interval_help是全局的，用于在activity之间跳转时保存剩余时间信息的
            //如果timeremainning大于-1，或者mainPreHelp.getTime_interval_help()>-1，表示上次预约的倒计时没有结束或者已经完成一次预约
            if ((timeremainning>-1)||mainPreHelp.getTime_interval_help()>-1) {
                //ispreorder_done = true;
                if (timeremainning!=-1){
                    preorder_interval = timeremainning;
                }
                if (mainPreHelp.getTime_interval_help()!=-1){
                    preorder_interval = mainPreHelp.getTime_interval_help();
                }

                count_time_background.setVisibility(View.VISIBLE);
                count_time_promt.setVisibility(View.VISIBLE);
                count_time.setVisibility(View.VISIBLE);
                abortPreorder.setVisibility(View.VISIBLE);
                abortPreorderText.setVisibility(View.VISIBLE);

                Log.d("MainActivity", "preorder_interval is: " + preorder_interval);
                handler.removeCallbacks(runnable);
                count_time(preorder_interval);
                handler.postDelayed(runnable, 1000);
            }else{
                    mainPreHelp=PreorderHelp.getPreorderHelp();
                    mainPreHelp.setTime_interval_help(-1);
                    timeremainning=-1;
                    handler.removeCallbacks(runnable);

                    String finishPreorderUrl="http://123.206.80.243:8080/sharing_bicycle/finish_preorder.do";
                    List<BasicNameValuePair> param=new ArrayList<>();
                    param.add(new BasicNameValuePair("user_id",user.getAccount()));
                    param.add(new BasicNameValuePair("bike_id",mainPreHelp.getPreorder_bike_id()));
                    Handler finishPreorderHandler=new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                            switch (msg.what){
                                case 1:

                                    String result = (String)msg.obj;
                                    net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(result);
                                    boolean flag = json.getBoolean("flag");
                                    if (flag){
                                        Log.d("PreorderInfo","成功结束预约");
                                    }else{
                                        Log.d("PreorderInfo","结束预约失败");
                                    }
                            }
                        }
                    };
                    NetUtils.postRequest(finishPreorderUrl,param,finishPreorderHandler);
            }
            super.onResume();
        }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt("recountTime",timeremainning);
        //outState.putString("preorderbike",Preordered_bike);
        outState.putString("mainUser",user.getAccount());
    }

    @Override
        protected void onDestroy() {
            // 退出时销毁定位
            mLocClient.stop();
            // 关闭定位图层
            mBaiduMap.setMyLocationEnabled(false);
            mMapView.onDestroy();
            mMapView = null;
            ImageViewUtils.releaseImageViewResouce(main_roll_one);
            ImageViewUtils.releaseImageViewResouce(main_roll_two);
            ImageViewUtils.releaseImageViewResouce(main_roll_three);
            super.onDestroy();
        }

}
//  ┏┓　　　┏┓
//┏┛┻━━━┛┻┓
//┃　　　　　　　┃ 　
//┃　　　━　　　┃
//┃　┳┛　┗┳　┃
//┃　　　　　　　┃
//┃　　　┻　　　┃
//┃　　　　　　　┃
//┗━┓　　　┏━┛
//   ┃　　　┃   神兽保佑　　　　　　　　
//   ┃　　　┃   代码无BUG
//   ┃　　　┗━━━┓
//   ┃　　　　　　　┣┓
//   ┃　　　　　　　┏┛
//   ┗┓┓┏━┳┓┏┛
//     ┃┫┫　┃┫┫
//     ┗┻┛　┗┻┛ 　