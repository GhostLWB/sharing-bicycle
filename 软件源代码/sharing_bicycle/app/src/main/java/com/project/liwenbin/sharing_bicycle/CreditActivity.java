package com.project.liwenbin.sharing_bicycle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwenbin on 2017/5/24 0024.
 */
public class CreditActivity extends Activity {
    private User user=User.getUser();
    private ListView creditListView;
    private List<Credit_record> credit_lists;
    private SwipeRefreshLayout mSwipeLayout;
    private CreditAdapter creditAdapter;
    private Handler cHandler;
    private ImageButton credit_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_credit);
            credit_back=(ImageButton)findViewById(R.id.credit_back_button);

            creditListView=(ListView)findViewById(R.id.credit_listView);
            mSwipeLayout =(SwipeRefreshLayout)findViewById(R.id.id_swipe_ly_c);
            credit_lists=new ArrayList<>();

            cHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what){
                        case 1:
                            String data = (String)msg.obj;
                            credit_lists=new ArrayList<>();
                            JSONArray json_raw = JSONArray.fromObject(data);
                            Log.d("CreditActivity","the size of jsonarray is :"+json_raw.size());
                            for (int i=0;i<json_raw.size();i++){
                                Credit_record tempRecord;
                                String credit_descrip=null;
                                JSONObject js=(JSONObject)json_raw.get(i);
//                                switch (js.getString("description")){
//                                    case "1":
//                                        credit_descrip="骑行消费获得的积分";
//                                        break;
//                                    case "2":
//                                        credit_descrip="车辆报修获得的积分";
//                                        break;
//                                    default:
//                                        break;
//                                }
                                if(js.getString("description").equals("1")){
                                    credit_descrip="骑行消费获得的积分";
                                }else{
                                    credit_descrip="车辆报修获得的积分";
                                }
                                tempRecord=new Credit_record(js.getDouble("amount")+"",credit_descrip,js.getString("date_time"));
                                Log.d("CreditActivity","get record:"+js.getDouble("amount")+js.getString("date_time"));
                                credit_lists.add(tempRecord);
                                Log.d("CreditActivity","wallet_list's size is :"+credit_lists.size());
                            }
                            creditAdapter.refresh(credit_lists);
                            mSwipeLayout.setRefreshing(false);
                            break;
                        default:
                            break;

                    }
                }
            };

            user=User.getUser();
            credit_lists.clear();
            String walletUrl="http://123.206.80.243:8080/sharing_bicycle/credit_record.do";
            List<BasicNameValuePair> params=new ArrayList<>();
            params.add(new BasicNameValuePair("user_id",user.getAccount()));
            NetUtils.postRequest(walletUrl,params,cHandler);

            creditAdapter =new CreditAdapter(credit_lists,R.layout.credit_list_item,this);
            creditListView.setAdapter(creditAdapter);
            mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    user=User.getUser();
                    credit_lists.clear();
                    String walletUrl="http://123.206.80.243:8080/sharing_bicycle/credit_record.do";
                    List<BasicNameValuePair> params=new ArrayList<>();
                    params.add(new BasicNameValuePair("user_id",user.getAccount()));
                    NetUtils.postRequest(walletUrl,params,cHandler);
                }
            });

            credit_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(CreditActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

        }

        @Override
        public void onBackPressed() {
            Intent intent=new Intent(CreditActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
}
