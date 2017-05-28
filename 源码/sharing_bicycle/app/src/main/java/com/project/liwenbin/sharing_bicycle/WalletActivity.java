package com.project.liwenbin.sharing_bicycle;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
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
 * Created by liwenbin on 2017/5/23 0023.
 */
public class WalletActivity extends Activity {
    private User user=User.getUser();
    private ListView walletListView;
    private List<Wallet_record> wallet_lists;
    private SwipeRefreshLayout mSwipeLayout;
    private WalletAdapter walletAdapter;
    private Handler wHandler;
    private ImageButton wallet_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_wallet);
        wallet_back=(ImageButton)findViewById(R.id.wallet_back_button);

        walletListView=(ListView)findViewById(R.id.wallet_listView);
        mSwipeLayout =(SwipeRefreshLayout)findViewById(R.id.id_swipe_ly);
        wallet_lists=new ArrayList<>();



        wHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        String data = (String)msg.obj;
                        wallet_lists=new ArrayList<>();
                        JSONArray json_raw = JSONArray.fromObject(data);
                        Log.d("WalletActivity","the size of jsonarray is :"+json_raw.size());
                        for (int i=0;i<json_raw.size();i++){
                            Wallet_record tempRecord;
                            JSONObject js=(JSONObject)json_raw.get(i);
                            tempRecord=new Wallet_record(js.getDouble("amount")+"",js.getString("date_time"));
                            Log.d("WalletActivity","get record:"+js.getDouble("amount")+js.getString("date_time"));
                            wallet_lists.add(tempRecord);
                            Log.d("WalletActivity","wallet_list's size is :"+wallet_lists.size());
                        }
                        walletAdapter.refresh(wallet_lists);
                        mSwipeLayout.setRefreshing(false);
                        break;
                    default:
                        break;

                }
            }
        };

        user=User.getUser();
        wallet_lists.clear();
        String walletUrl="http://123.206.80.243:8080/sharing_bicycle/wallet_record.do";
        List<BasicNameValuePair> params=new ArrayList<>();
        params.add(new BasicNameValuePair("user_id",user.getAccount()));
        NetUtils.postRequest(walletUrl,params,wHandler);
        walletAdapter =new WalletAdapter(wallet_lists,R.layout.credit_list_item,this);
        walletListView.setAdapter(walletAdapter);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                user=User.getUser();
                wallet_lists.clear();
                String walletUrl="http://123.206.80.243:8080/sharing_bicycle/wallet_record.do";
                List<BasicNameValuePair> params=new ArrayList<>();
                params.add(new BasicNameValuePair("user_id",user.getAccount()));
                NetUtils.postRequest(walletUrl,params,wHandler);
            }
        });

        wallet_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WalletActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(WalletActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
