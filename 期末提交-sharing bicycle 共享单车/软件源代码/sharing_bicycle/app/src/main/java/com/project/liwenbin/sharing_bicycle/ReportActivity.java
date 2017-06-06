package com.project.liwenbin.sharing_bicycle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.beanutils.converters.BooleanArrayConverter;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by liwenbin on 2017/5/19 0019.
 */
public class ReportActivity extends Activity {
    private ImageButton reportBack;
    private EditText inputText;
    private ImageButton abortButton;
    private ImageButton submitButton;
    private Spinner spinner;
    private String content=null;
    private String type=null;
    private List<String> list=new ArrayList<>();
    private ArrayAdapter<String>arrayAdapter;
    private ArrayAdapter<String>array_bike;
    private List<String>bike_id_list=new ArrayList<>();
    private String bike_id=null;
    private int hint=0;
    private String result=null;
    private User user=User.getUser();
    private Spinner input_bike;
    private RelativeLayout input_bike_layout;
    private Bundle reportBundle;
    private boolean hasBike_id=false;
    private TextView bike_id_hint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_report);
        reportBack=(ImageButton)findViewById(R.id.report_back_button);
        inputText=(EditText)findViewById(R.id.report_input);
        abortButton=(ImageButton)findViewById(R.id.report_abort_button);
        submitButton=(ImageButton)findViewById(R.id.report_submit_button);
        spinner=(Spinner)findViewById(R.id.report_spinner);
        input_bike=(Spinner)findViewById(R.id.bike_id_spinner);
        input_bike_layout=(RelativeLayout)findViewById(R.id.report_bike_layout);
        input_bike_layout.setVisibility(View.INVISIBLE);
        bike_id_hint=(TextView)findViewById(R.id.bike_id_hint);


        bike_id_hint.setVisibility(View.INVISIBLE);

        Intent getReportIntent=getIntent();
        reportBundle=getReportIntent.getExtras();
        hint=reportBundle.getInt("ReportActivity1");
        /**
         * 用户没有选择车辆
         */
        Log.d("ReportActivity","hint is :"+hint);
        if (hint==0){
            bike_id=null;
            input_bike_layout.setVisibility(View.VISIBLE);
            hasBike_id=false;

        }else{
            /**
             * 用户选择了车辆
             */
            hasBike_id=true;
            bike_id_hint.setVisibility(View.VISIBLE);
            bike_id=reportBundle.getString("ReportActivity2");
            bike_id_hint.setText("您要报修的是"+bike_id+"号车");
        }
        Log.d("ReportActivity","get bike id is :"+bike_id);

        /**
         * 添加预置报修类型
         */
        list.add("车胎没气");
        list.add("车链脱落");
        list.add("踏板损坏");
        list.add("无法识别");
        list.add("车座丢失");
        list.add("无法解锁");
        list.add("无法还车");
        list.add("被加私锁");
        list.add("其他");

        /**
         * 获取所有车辆的ID
         */
        String bike_url = "http://123.206.80.243:8080/sharing_bicycle/bike_full";
        Handler bicycle_handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        Vector<Bicycle> bicycles;
                        String data = (String)msg.obj;
                        bicycles=Bicycle_data.get_data(data);
                        for (Bicycle iterator:bicycles){
                            array_bike.add(iterator.getBike_id()+"");
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        NetUtils.getRequest(bike_url,null,bicycle_handler);

        arrayAdapter=new ArrayAdapter<>(ReportActivity.this,android.R.layout.simple_list_item_1,list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    type=arrayAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                type=arrayAdapter.getItem(0);
            }
        });

        array_bike=new ArrayAdapter<>(ReportActivity.this,android.R.layout.simple_list_item_1,bike_id_list);
        array_bike.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        input_bike.setAdapter(array_bike);
        input_bike.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bike_id=array_bike.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                bike_id=reportBundle.getString("ReportActivity2");
            }
        });

        abortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ReportActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        reportBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ReportActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content=inputText.getText().toString();
                if (hasBike_id){
                    bike_id=reportBundle.getString("ReportActivity2");
                }
                String reportUrl="http://123.206.80.243:8080/sharing_bicycle/report.do";
                List<BasicNameValuePair> params=new ArrayList<>();
                Log.d("ReportActivity","bike id is :"+bike_id);
                params.add(new BasicNameValuePair("user_id",user.getAccount()));
                params.add(new BasicNameValuePair("bike_id",bike_id));
                params.add(new BasicNameValuePair("description",content));
                Handler reportHandler=new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what){
                            case 1:
                                String result =(String)msg.obj;
                                net.sf.json.JSONObject js = net.sf.json.JSONObject.fromObject(result);
                                /**
                                 * 可以通过相应的key对返回的JSON对象进行解析，获取对象中封装的值
                                 */
                                boolean flag = js.getBoolean("flag");
                                if (flag){
                                    Toast.makeText(ReportActivity.this,"报修成功！",Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(ReportActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Toast.makeText(ReportActivity.this,"报修失败，因为"+js.getString("reason"),Toast.LENGTH_SHORT).show();
                                }
                        }
                    }
                };
                NetUtils.postRequest(reportUrl,params,reportHandler);
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(ReportActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
