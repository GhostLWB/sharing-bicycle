package com.project.liwenbin.sharing_bicycle;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

/**
 * Created by liwenbin on 2017/4/29 0029.
 */
public class ChartActivity  extends AppCompatActivity{
    private FrameLayout chartFrame;
    private TabLayout chartTab;
    private FragmentTransaction fragmentTransaction;
//    private Fragment RideTimeFragment,ConsumeFragment,CreditFragment,GetCreditFragment,WeeklyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_activity_layout);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.chart_activity_frame, new ChartFragment()).commit();
        }
    }

}
