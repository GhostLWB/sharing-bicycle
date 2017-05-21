package com.example.listdemo;

import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private CyclingAdapter cyclingAdapter;
    private List<Cycling_record> list;
    private SwipeRefreshLayout mSwipeLayout;
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=(ListView)findViewById(R.id.listView);
        list=new ArrayList<Cycling_record>();
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        list.add(new Cycling_record("5","2017-5-15 16:39",2));
                        cyclingAdapter.refresh(list);
                        mSwipeLayout.setRefreshing(false);
                        break;
                    default:
                        break;

                }
            }
        };
        mSwipeLayout =(SwipeRefreshLayout)findViewById(R.id.id_swipe_ly);
        list.add(new Cycling_record("2","2017-5-15 15:30",18));
        list.add(new Cycling_record("7","2017-5-15 14:30",10));
        list.add(new Cycling_record("1","2017-5-15 16:30",18));
        list.add(new Cycling_record("3","2017-5-15 16:37",2));
        list.add(new Cycling_record("5","2017-5-15 16:39",2));
        cyclingAdapter=new CyclingAdapter(list,R.layout.list_item,this);
        listView.setAdapter(cyclingAdapter);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Message message = new Message();
                message.what=1;
                mHandler.sendMessage(message);
            }
        });

    }
}
