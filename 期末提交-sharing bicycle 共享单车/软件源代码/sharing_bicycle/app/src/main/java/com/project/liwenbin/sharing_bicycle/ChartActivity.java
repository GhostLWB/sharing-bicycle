package com.project.liwenbin.sharing_bicycle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by liwenbin on 2017/4/29 0029.
 */
public class ChartActivity  extends AppCompatActivity{
    private FrameLayout chartFrame;
    private TabLayout chartTab;
    private FragmentTransaction fragmentTransaction;
    private Context context;
    private User user=User.getUser();
//    private Fragment RideTimeFragment,ConsumeFragment,CreditFragment,GetCreditFragment,WeeklyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_activity_layout);

        context=this;
        user=User.getUser();
//        if (user==null){
//            Log.d("ChartActivity","user is null");
//            Toast.makeText(this,"您还没有登录",Toast.LENGTH_SHORT).show();
//            Intent myTempintent=new Intent(context,LoginActivity.class);
//            startActivity(myTempintent);
//            finish();
//        }
        if (user.getAccount()==null){
            Log.d("ChartActivity","user account is null");
            Toast.makeText(this,"您还没有登录",Toast.LENGTH_SHORT).show();
            Intent myTempintent=new Intent(context,LoginActivity.class);
            startActivity(myTempintent);
            finish();
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.chart_activity_frame, new ChartFragment()).commit();
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
