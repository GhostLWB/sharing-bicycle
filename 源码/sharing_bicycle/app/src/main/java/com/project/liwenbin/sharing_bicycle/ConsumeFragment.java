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
public class ConsumeFragment extends Fragment{
    private ColumnChartView consume_chart;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consume,container,false);
        consume_chart=(ColumnChartView)view.findViewById(R.id.consume_chart);
        return view;
    }
}
