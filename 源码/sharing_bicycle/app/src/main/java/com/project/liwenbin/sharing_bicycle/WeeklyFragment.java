package com.project.liwenbin.sharing_bicycle;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by liwenbin on 2017/4/30 0030.
 */
public class WeeklyFragment extends Fragment {
    private ColumnChartView weekly_chart;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly,container,false);
        weekly_chart=(ColumnChartView)view.findViewById(R.id.weekly_chart);
        return view;
    }
}
