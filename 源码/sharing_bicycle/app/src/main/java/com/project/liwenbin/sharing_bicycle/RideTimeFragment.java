package com.project.liwenbin.sharing_bicycle;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by liwenbin on 2017/4/30 0030.
 */
public class RideTimeFragment extends Fragment {
    private ColumnChartView chart;
    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ridetime,container,false);
        chart=(ColumnChartView)view.findViewById(R.id.ridetime_chart);
        return view;
    }

}
