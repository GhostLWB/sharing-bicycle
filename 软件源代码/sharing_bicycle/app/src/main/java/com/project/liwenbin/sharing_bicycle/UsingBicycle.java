package com.project.liwenbin.sharing_bicycle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by liwenbin on 2017/5/15 0015.
 */
public class UsingBicycle extends Activity {
    private ImageButton endUsing;
    private String user_account;
    private User user;
    private String bicycle_id;
    private Runnable runnable=null;
    private TextView min1;
    private TextView min2;
    private TextView sec1;
    private TextView sec2;
    private TextView using_promt;
    private ImageView Using_bg;
    int min1_time=0;
    int min2_time=0;
    int sec1_time=0;
    int sec2_time=0;
    int time_total=0;
    int time_total_min=0;
    int user_credit_get=0;
    double user_consume=0;
    private Handler count_time_handler;
    Vector bicycles=new Vector();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.using);

        endUsing=(ImageButton)findViewById(R.id.using_end);
        min1=(TextView)findViewById(R.id.using_min1);
        min2=(TextView)findViewById(R.id.using_min2);
        sec1=(TextView)findViewById(R.id.using_sec1);
        sec2=(TextView)findViewById(R.id.using_sec2);
        using_promt=(TextView)findViewById(R.id.using_promt);
        count_time_handler=new Handler();
        //Using_bg=(ImageView)findViewById(R.id.using_bg);
        user=User.getUser();


        min1.setText(min1_time+"");
        min2.setText(min2_time+"");
        sec1.setText(sec1_time+"");
        sec2.setText(sec2_time+"");

        Intent startIntent=UsingBicycle.this.getIntent();
        Bundle usingBundle=startIntent.getExtras();
        user_account=usingBundle.getString("MainUser");
        bicycle_id=usingBundle.getString("MainBicycle");
        Log.d("UsingBicycle","get from MainActivity->user_account:"+user_account);
        Log.d("UsingBicycle","get from MainActivity->bicycle_id:"+bicycle_id);
        count_time();
        count_time_handler.postDelayed(runnable,1000);
        using_promt.setText("您正在使用"+bicycle_id+"号车");
        /**
         * 查询车辆信息
         */
        String bike_url = "http://123.206.80.243:8080/sharing_bicycle/bike_full";
        Handler bicycle_handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        String data = (String)msg.obj;
                        bicycles=Bicycle_data.get_data(data);
                        break;
                    default:
                        break;
                }
            }
        };
        NetUtils.getRequest(bike_url,null,bicycle_handler);

        /**
         * 结束用车按钮
         */
        endUsing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_credit_get=countCredit();
                user_consume=countConsume();
                user_consume=user_consume*-1;
                time_total_min=time_total/60;
                if (time_total_min<1){
                    time_total_min+=1;
                }

                count_time_handler.removeCallbacks(runnable);

                /**
                 * 向服务器发送请求，更改相应信息
                 */
                final String finishUrl="http://123.206.80.243:8080/sharing_bicycle/finish_use.do";
                List<BasicNameValuePair> param=new ArrayList<BasicNameValuePair>();
                param.add(new BasicNameValuePair("bike_id",bicycle_id));
                param.add(new BasicNameValuePair("user_id",user_account));
                param.add(new BasicNameValuePair("credit",user_credit_get+""));
                param.add(new BasicNameValuePair("balance",user_consume+""));
                param.add(new BasicNameValuePair("riding_time",time_total_min+""));
                Handler finishHandler=new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what){
                            case 1:
                                String result = (String)msg.obj;

                                if (result.equals("success")){
                                    PreorderHelp preorderHelp=PreorderHelp.getPreorderHelp();
                                    preorderHelp.setTime_interval_help(-1);

                                    Toast.makeText(UsingBicycle.this,"还车成功！",Toast.LENGTH_SHORT).show();
                                    Intent usingIntent=new Intent(UsingBicycle.this,MainActivity.class);
                                    startActivity(usingIntent);
                                    finish();
                                }else if (result.equals("fail")){
                                    Toast.makeText(UsingBicycle.this,"出现了一点小错误，请重试",Toast.LENGTH_SHORT).show();
                                }
                        }
                    }
                };
                NetUtils.postRequest(finishUrl,param,finishHandler);
            }
        });

    }

    /**
     * 屏蔽返回键
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    /**
     * 计时
     *
     */
    private void count_time(){
        boolean isstop=true;
        if (runnable==null) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    time_total++;
                    sec2_time++;
                    if (sec2_time >= 10) {
                        sec2_time = 0;
                        sec1_time++;
                        if (sec1_time >= 6) {
                            sec1_time = 0;
                            min2_time++;
                            if (min2_time >= 10) {
                                min2_time = 0;
                                min1_time++;

                            }
                        }
                    }
                    min1.setText(min1_time + "");
                    min2.setText(min2_time + "");
                    sec1.setText(sec1_time + "");
                    sec2.setText(sec2_time + "");
                    Log.d("UsingBicycle", "time_total:" + time_total + "");
                    count_time_handler.postDelayed(this, 1000);

            }
            };
        }else{
            runnable.run();
        }
    }

    private int countCredit(){
        int credit_get=0;
        Bicycle bicycle;
        int time_level=0;
        time_level=isWhichLevelTime(time_total);
        if (time_level==5){
            credit_get=20;
        }else{
            credit_get=time_level*5;
        }
        bicycle=Bicycle_data.findBicycleById(bicycle_id);
        if (bicycle==null){
            Log.d("UsingBicycle","bicycle is null");
        }else if (user_account==bicycle.getUser_preorder()){
            credit_get+=5;
        }

        return credit_get;
    }
    private double countConsume(){
        double consume=0;
        double time_level=0;
        double credit_level=0;
        DecimalFormat    df   = new DecimalFormat("######0.00");
        time_level=isWhichLevelTime(time_total);
        credit_level=isWhichLevelCredit((int)(user.getCredit()));

        consume=Math.sqrt(time_level)+Math.sqrt(credit_level);
        consume=consume/100;
        df.format(consume);
        return consume;
    }

    /**
     * 判断是哪个时间区间
     * @param time_raw
     * @return
     */
    private int isWhichLevelTime(int time_raw){
        if (time_raw<=300){//0-5min
            return 1;
        }else if (time_raw<=600){//5-10min
            return 2;
        }else if (time_raw<=1800){//10-30min
            return 3;
        }else if (time_raw<=3600){//30-60min
            return 4;
        }else{//60+ min
            return 5;
        }
    }

    /**
     * 判断是哪个积分区间
     * @param credit_raw
     * @return
     */
    private int isWhichLevelCredit(int credit_raw){

        if (credit_raw<=30){
            return 1;
        }else if (credit_raw<=100){
            return 2;
        }else if(credit_raw<=300){
            return 3;
        }else if (credit_raw<=600){
            return 4;
        }else {
            return 5;
        }
    }

    @Override
    protected void onDestroy() {
        ReleaseImageViewUtils.releaseImage(endUsing);
        ImageViewUtils.releaseImageViewResouce(endUsing);
        super.onDestroy();
    }
}
