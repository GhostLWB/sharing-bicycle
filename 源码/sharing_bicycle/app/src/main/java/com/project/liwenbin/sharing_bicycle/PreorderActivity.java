package com.project.liwenbin.sharing_bicycle;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.baidu.platform.comapi.map.C;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by liwenbin on 2017/5/3 0003.
 */
@TargetApi(24)
public class PreorderActivity extends Activity {
    //UI related
    private ImageButton backButton;
    private TextView bicycleid_textView;
    private TextView time_from_now;
    private TextView time_to;
    private Button confirmButton;
    //logic related
    private Intent directly_back_intent;
    private String bicycle_ID;
    private String preorder_key="selected_bicycle_id";
    private int hourSelected;
    private int minuteSelected;
    private int hour_now;
    private int minute_now;
    private Context context;
    private int hour_start;
    private int hour_end;
    private int minute_start;
    private int minute_end;
    private int time_interval=-1;
    private String time_now;
    private String key="preorder_interval";
    private ImageView background;
    private ImageView boader;
    private ImageView background2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_preorder);
        backButton=(ImageButton) findViewById(R.id.preorder_back_button);
        directly_back_intent=new Intent(this,MainActivity.class);
        bicycleid_textView=(TextView)findViewById(R.id.preorder_bike_id) ;
        time_from_now=(TextView)findViewById(R.id.preorder_time_from);
        time_to=(TextView)findViewById(R.id.preorder_time_to);
        time_from_now.setClickable(true);
        time_to.setClickable(true);
        confirmButton=(Button)findViewById(R.id.confirm_preorder) ;
        background=(ImageView)findViewById(R.id.imageView7) ;
        boader=(ImageView)findViewById(R.id.imageView8);
        background2=(ImageView)findViewById(R.id.imageView9);
        context=this;

        /**
         * 获取当前系统时间
         */
        Time ti=new Time();
        ti.setToNow();
        hour_now=ti.hour;
        minute_now=ti.minute;
        if (minute_now>=10) {
            time_now = "" + hour_now + ":" + minute_now;
        }else{
            time_now = "" + hour_now + ":" +"0"+ minute_now;
        }
        time_from_now.setText(time_now);
        time_to.setText(time_now);
        minute_start=minute_now;
        hour_start=hour_now;
        /**
         * 选择时间
         */
        time_from_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(context,new TimePickerDialog.OnTimeSetListener(){

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        minute_start=minute;
                        hour_start=hourOfDay;
                        Log.d("PreorderActivity","time minute start is:"+minute_start);
                        if (minute<10) {
                            time_from_now.setText("" + hour_start + ":"+"0"+minute);
                        }else {
                            time_from_now.setText("" + hour_start + ":"+minute);
                        }
                    }
                },hour_now,minute_now,true).show();
            }
        });
        time_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(context,new TimePickerDialog.OnTimeSetListener(){

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        minute_end = minute;
                        hour_end = hourOfDay;
                        Log.d("PreorderActivity", "minute picked from time to is :" + minute);
                        //如果选择了下一个小时：
                        if (minute_end < minute_start) {
                            minute_end += 60;
                        }
                        //如果时间选择超前了
                        if (hour_end <= hour_start && minute_end < minute_start) {
                            Toast.makeText(context, "不可以超前哦", Toast.LENGTH_SHORT).show();
                            hour_end = hour_start;
                            minute_end = minute_start;
                        } else {//时间选择没有超前

                            //计算预约间隔
                            time_interval = (hour_end - hour_start) * 60 + (minute_end - minute_start);
                            Log.d("PreorderActivity", "time_interval is:" + time_interval);
                            //如果预约时间间隔超过15分钟
                            if (time_interval > 15) {
                                time_to.setText(time_now);
                                Toast.makeText(context, "预约时间不可以超过15分钟哦", Toast.LENGTH_SHORT).show();
                                hour_end = hour_start;
                                minute_end = minute_start;
                                time_interval=-1;
                            }else {//预约时间在15分钟之内

                                //排版显示时间
                                if (minute < 10) {
                                    time_to.setText("" + hour_end + ":" + "0" + minute);
                                } else {
                                    time_to.setText("" + hour_end + ":" + minute);
                                }
                            }
                        }
                    }
                },hour_now,minute_now,true).show();

            }
        });




        /**
         * 获取MainActivity传过来的bicycle ID
         */
        Bundle mybundle=getIntent().getExtras();
        bicycle_ID=mybundle.getString(preorder_key);
        bicycleid_textView.setText(bicycle_ID);
        /**
         * 返回键监听
         */
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(directly_back_intent);
                finish();
            }
        });
        /**
         * 确认按钮
         */
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //与服务器同步……
                String preorder_url="http://123.206.80.243:8080/sharing_bicycle/preorder.do?";
                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                params.add(new BasicNameValuePair("bike_id",bicycle_ID));
                Handler handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what)
                        {
                            case 1:
                                String result = (String)msg.obj;
                                net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(result);
                                boolean flag = json.getBoolean("flag");
                                if(flag)
                                {
                                    Toast.makeText(PreorderActivity.this,"预约成功",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(PreorderActivity.this,"预约失败",Toast.LENGTH_SHORT).show();
                                    time_interval=-1;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                };
                NetUtils.postRequest(preorder_url,params,handler);
                if (time_interval!=-1){
                    Intent intent=getIntent();
                    Bundle mybundle=new Bundle();
                    mybundle.putInt(key,time_interval);
                    intent.putExtras(mybundle);
                    setResult(1,intent);
                    intent.setClass(context,MainActivity.class);
                    //startActivity(intent);
                    finish();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(directly_back_intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        ImageViewUtils.releaseImageViewResouce(background);
        ImageViewUtils.releaseImageViewResouce(background2);
        ImageViewUtils.releaseImageViewResouce(boader);
        super.onDestroy();
    }
}
