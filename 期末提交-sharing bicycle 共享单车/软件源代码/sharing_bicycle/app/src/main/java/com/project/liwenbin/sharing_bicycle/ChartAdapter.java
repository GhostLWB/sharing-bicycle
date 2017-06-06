package com.project.liwenbin.sharing_bicycle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by liwenbin on 2017/4/30 0030.
 */
public class ChartAdapter extends FragmentPagerAdapter {
    private List<Fragment> list_fragment;                         //fragment列表
    private List<String> list_Title;                              //tab名的列表

    public ChartAdapter(FragmentManager fragmentManager, List<Fragment> list_fragment, List<String> list_Title){
        super(fragmentManager);
        this.list_fragment = list_fragment;
        this.list_Title = list_Title;
    }

    public Fragment getItem(int position) {
        return list_fragment.get(position);
    }

    @Override
    public int getCount() {
        return list_Title.size();
    }

    //此方法用来显示tab上的名字
    @Override
    public CharSequence getPageTitle(int position) {

        return list_Title.get(position % list_Title.size());
    }
}
