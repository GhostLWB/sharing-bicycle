package com.project.liwenbin.sharing_bicycle;

import android.os.Handler;
import android.os.Message;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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

import java.util.HashMap;
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
    private boolean islock=true;
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

    //车辆相关
    Vector<Bicycle>bicycles=new Vector<>();
    String bicycle_raw_data=null;
    Marker selectedBicycleMarker=null;
    Bicycle selectedBicycle=null;

    Handler handler = new Handler();
    Runnable runnable;
    int recLen = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main2);
        context = this;


        //控件注册都在这个方法中
        registerWiget();
        scroll();
        circularProgressView.setVisibility(View.INVISIBLE);


        //百度地图控件注册
        mBaiduMap = mMapView.getMap();
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        user=User.getUser();
        balance=user.getBalance();
        user_credit=user.getCredit();

        queryBicycleLoc();

        /**
         * 设置导航栏
         */

        navigationView.setNavigationItemSelectedListener(this);
        drawer=(DrawerLayout)findViewById(R.id.drawer_layout) ;
       drawer.setDrawerListener(new DrawerLayout.DrawerListener() {
           @Override
           public void onDrawerSlide(View drawerView, float slideOffset) {

               if (user!=null) {
                   account_raw = user.getAccount();
                   if (account_raw!=null)
                       if (account_raw.length()==11)
                           user_id.setText(proccessAccount(account_raw));
               }else{
                   user_id.setText("请登录");
                   balance=0;
                   user_credit=0;
               }
               navigationView.setItemIconTintList(null);
               navigationView.getMenu().getItem(0).getSubMenu().getItem(0).setTitle(wallet+tab+balance);
               navigationView.getMenu().getItem(0).getSubMenu().getItem(1).setTitle(credit+tab+user_credit);
               if (account_raw==null) {
                   navigationView.getMenu().getItem(2).getSubMenu().getItem(0).setTitle("登录/注册");
                   navigationView.getMenu().getItem(2).getSubMenu().getItem(0).setIcon(R.drawable.log_in);
               }else{
                   navigationView.getMenu().getItem(2).getSubMenu().getItem(0).setTitle("退出登录");
                   navigationView.getMenu().getItem(2).getSubMenu().getItem(0).setIcon(R.drawable.log_out);
               }
           }

           @Override
           public void onDrawerOpened(View drawerView) {
               balance=user.getBalance();
               user_credit=user.getCredit();
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
                if(myLocation!=null){
                    LatLng ll = new LatLng(myLocation.getLatitude(),
                            myLocation.getLongitude());//设置地图新的中心点
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(ll).zoom(21.0f);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

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
                        Toast.makeText(MainActivity.this,volleyError.toString(),Toast.LENGTH_SHORT).show();
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
                if (user.getAccount()==null){
                    Log.d("MainActivity","user is null");
                    Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
                    Intent myintent=new Intent(context,LoginActivity.class);
                    startActivity(myintent);
                }else if (selectedBicycle==null){
                    Log.d("MainActivity","user id is:"+user.getAccount());
                    Toast.makeText(context, "您还未选择车辆", Toast.LENGTH_SHORT).show();
                }else {
                    circularProgressView.setVisibility(View.VISIBLE);
                    if (islock) {//上锁状态，点击按钮的作用是解锁
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if ((selectedBicycle != null)&&!selectedBicycle.isOrder) {
                                    if (DistanceUtils.getDistanceOfMeter(myLocation.getLatitude(), myLocation.getLongitude(), selectedBicycle.getLocation().latitude,
                                            selectedBicycle.getLocation().longitude) <= 20.0) {
                                        circularProgressView.setVisibility(View.INVISIBLE);

                                        //与服务器同步，改变selectedbicycle的in_use和is_lock;

                                        unlockButton.setText("我要还车");
                                        user.setHaveBicycleID(selectedBicycle.getBike_id() + "");
                                        Toast.makeText(context, "开锁成功！", Toast.LENGTH_SHORT).show();
                                        islock = false;
                                        circularProgressView.setVisibility(View.INVISIBLE);
                                    }else{
                                        Toast.makeText(context,"距离太远啦，尝试走近一点",Toast.LENGTH_SHORT).show();
                                        circularProgressView.setVisibility(View.INVISIBLE);
                                    }
                                }

                            }
                        }, 1000);
                    } else {//解锁状态，点击按钮的作用是上锁
                        selectedBicycle = null;
                        user.setHaveBicycleID(null);
                        unlockButton.setText("我要用车");
                        islock = true;

                        //与服务器同步，更新车辆的is_lock和in_use状态.
                    }


                }


            }
        });
        /**
         * 导航按钮
         */
        navigteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"正在开发中...", Toast.LENGTH_SHORT).show();
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
                if (user.getAccount()==null){
                    Log.d("MainActivity","user is null");
                    Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
                    Intent myintent=new Intent(context,LoginActivity.class);
                    startActivity(myintent);
                }else if (selectedBicycle==null){
                    Log.d("MainActivity","user id is:"+user.getAccount());
                    Toast.makeText(context, "您还未选择车辆", Toast.LENGTH_SHORT).show();
                }else{
                    circularProgressView.setVisibility(View.VISIBLE);
                    mybundle.putString(preorderKey,selectedBicycle.getBike_id()+"");
                    intent.putExtras(mybundle);
                    startActivityForResult(intent,1);
                    circularProgressView.setVisibility(View.INVISIBLE);
                    //finish();
                }
            }
        });
        /**
         * 反馈按钮
         */
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"正在开发中...", Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * Marker点击事件响应,获取自行车
         */
        BaiduMap.OnMarkerClickListener markerClickListener=new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (selectedBicycleMarker==null) {
                    selectedBicycleMarker = marker;
                    selectedBicycle = findBicycleByID(selectedBicycleMarker.getTitle());
                    marker.setIcon(bd_selected);
                    Log.d("MainActivity","bicycle selected:"+selectedBicycle.getBike_id());
                    return true;
                }else if (marker==selectedBicycleMarker){
                    selectedBicycleMarker=null;
                    selectedBicycle=null;
                    marker.setIcon(bd);
                }else{
                    selectedBicycle=null;
                    Toast.makeText(context,"一次只能选择一辆车哦",Toast.LENGTH_SHORT).show();
                }                return false;
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
            if (iterator.isOrder){
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
        scrollBackgroundAnimation1=new TranslateAnimation(-2000,-300,00,00);
        scrollBackgroundAnimation2=new TranslateAnimation(-300,-1300,40,40);
        scrollBackgroundAnimation3=new TranslateAnimation(-1900,-400,60,60);
        count_time_background=(ImageView)findViewById(R.id.count_time_imageview);
        count_time_promt=(TextView)findViewById(R.id.count_time_prompt);
        count_time=(TextView)findViewById(R.id.count_time);
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

        scrollBackgroundAnimation1.setDuration(19000);               //设置每一次动画的持续时间
        scrollBackgroundAnimation1.setRepeatCount(Animation.INFINITE);               //设置重复次数
        scrollBackgroundAnimation1.setRepeatMode(Animation.REVERSE);    //反方向执行
        main_roll_one.setAnimation(scrollBackgroundAnimation1);             //设置动画效果
        scrollBackgroundAnimation1.startNow();                      //启动动画


        scrollBackgroundAnimation2.setDuration(17000);               //设置每一次动画的持续时间
        scrollBackgroundAnimation2.setRepeatCount(Animation.INFINITE);               //设置重复次数
        scrollBackgroundAnimation2.setRepeatMode(Animation.REVERSE);    //反方向执行
        main_roll_two.setAnimation(scrollBackgroundAnimation2);             //设置动画效果
        scrollBackgroundAnimation2.startNow();

        scrollBackgroundAnimation3.setDuration(16000);               //设置每一次动画的持续时间
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
            // Handle the camera action
        } else if (id == R.id.credit) {

        } else if (id == R.id.trip) {
            Intent intent=new Intent(context,ChartActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.report) {

        } else if (id == R.id.log) {
            Log.d("MainActivity","R.id.log selected");
            if (account_raw==null){
                Intent intent=new Intent(this,LoginActivity.class);
                startActivity(intent);
                finish();
            }else
            {
                Log.d("MainActivity","log out selected");
                user=null;
                //Log.d("MainActivity","user object is null"+user.equals(null));
            }

        } else if (id == R.id.credit) {

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
     * 处理从PreorderActivity回传的参数
     * requestCode:0->向PreorderActivity传递bicycleID时用的
     * resultCode:0->从PreorderActivity返回预约时间间隔时用的
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1&&resultCode==1){
            if (data.getExtras()!=null) {
                ispreorder_done=true;
                Bundle mybundle = data.getExtras();
                preorder_interval=mybundle.getInt("preorder_interval");
                Log.d("MainActivity","time interval is :"+preorder_interval);
            }
        }
    }
    /**
     * 倒计时
     */
    private void count_time(final int interval){
        recLen=interval*60;
        runnable = new Runnable() {
            @Override
            public void run() {
                count_time_minute=recLen/60;
                count_time_second=recLen%60;
                recLen--;
                count_time.setText(count_time_minute+":"+count_time_second);
                handler.postDelayed(this, 1000);
                if (recLen==0){
                    Toast.makeText(context, "预约时间已到", Toast.LENGTH_SHORT).show();
                    count_time_background.setVisibility(View.INVISIBLE);
                    count_time_promt.setVisibility(View.INVISIBLE);
                    count_time.setVisibility(View.INVISIBLE);
                }
            }
        };
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
            super.onPause();
        }

        @Override
        protected void onResume() {
            mMapView.onResume();

            if (!ispreorder_done){
                count_time_background.setVisibility(View.INVISIBLE);
                count_time_promt.setVisibility(View.INVISIBLE);
                count_time.setVisibility(View.INVISIBLE);
            }else{
                count_time_background.setVisibility(View.VISIBLE);
                count_time_promt.setVisibility(View.VISIBLE);
                count_time.setVisibility(View.VISIBLE);
                if (preorder_interval!=-1){
                    count_time(preorder_interval);
                }
                handler.postDelayed(runnable,1000);
            }
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
            ImageViewUtils.releaseImageViewResouce(main_roll_one);
            ImageViewUtils.releaseImageViewResouce(main_roll_two);
            ImageViewUtils.releaseImageViewResouce(main_roll_three);
            super.onDestroy();
        }

}
