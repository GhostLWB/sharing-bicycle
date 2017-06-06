package com.project.liwenbin.sharing_bicycle;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwenbin on 2017/4/30 0030.
 */
public class ChartFragment extends Fragment {
    private TabLayout chartTabLayout;                            //定义TabLayout
    private ViewPager chart_viewPager;                             //定义viewPager
    private FragmentPagerAdapter fAdapter;                               //定义adapter

    private List<Fragment> list_fragment;                                //定义要装fragment的列表
    private List<String> list_title;
    private RideTimeFragment rideTimeFragment;
    private ConsumeFragment consumeFragment;
    private CreditFragment creditFragment;
    private GetCreditFragment getCreditFragment;
    private WeeklyFragment weeklyFragment;
    private ImageButton analysis_back;
    private ImageView chartBackground;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chart_layout, container, false);
        analysis_back=(ImageButton)view.findViewById(R.id.analysis_back_button);
        chartBackground=(ImageView)view.findViewById(R.id.imageView18);
        initControls(view);

        analysis_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent anaIntent=new Intent(getActivity(),MainActivity.class);
                startActivity(anaIntent);
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        ReleaseImageViewUtils.releaseImage(chartBackground);
        ImageViewUtils.releaseImageViewResouce(chartBackground);
        super.onDestroy();
    }

    /**
     * 初始化各控件
     * @param view
     */
    private void initControls(View view) {

        chartTabLayout = (TabLayout)view.findViewById(R.id.chart_tab);
        chart_viewPager = (ViewPager)view.findViewById(R.id.chart_pager);

        //初始化各fragment
        consumeFragment=new ConsumeFragment();
        creditFragment=new CreditFragment();
        getCreditFragment=new GetCreditFragment();
        rideTimeFragment=new RideTimeFragment();
        weeklyFragment=new WeeklyFragment();

        //将fragment装进列表中
        list_fragment = new ArrayList<>();
        list_fragment.add(rideTimeFragment);
        list_fragment.add(consumeFragment);
        list_fragment.add(creditFragment);
        list_fragment.add(getCreditFragment);
        list_fragment.add(weeklyFragment);

        //将名称加载tab名字列表，正常情况下，我们应该在values/arrays.xml中进行定义然后调用
        list_title = new ArrayList<>();
        list_title.add("骑行时间");
        list_title.add("消费金额");
        list_title.add("积分变化");
        list_title.add("积分获得");
        list_title.add("一周统计");

        //设置TabLayout的模式
        chartTabLayout.setTabMode(TabLayout.MODE_FIXED);
        //为TabLayout添加tab名称
        chartTabLayout.addTab(chartTabLayout.newTab().setText(list_title.get(0)));
        chartTabLayout.addTab(chartTabLayout.newTab().setText(list_title.get(1)));
        chartTabLayout.addTab(chartTabLayout.newTab().setText(list_title.get(2)));
        chartTabLayout.addTab(chartTabLayout.newTab().setText(list_title.get(3)));

        fAdapter = new ChartAdapter(getChildFragmentManager(),list_fragment,list_title);

        //viewpager加载adapter
        chart_viewPager.setAdapter(fAdapter);

        //chartTabLayout.setViewPager(vp_FindFragment_pager);
        //TabLayout加载viewpager
        chartTabLayout.setupWithViewPager(chart_viewPager);
        //chartTabLayout.set
    }

}
