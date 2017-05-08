package com.project.liwenbin.sharing_bicycle;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by liwenbin on 2017/4/30 0030.
 */
public class CreditFragment extends Fragment {
    private LineChartView credit_chart;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_credit,container,false);
        credit_chart=(LineChartView) view.findViewById(R.id.credit_chart_view);
        return view;
    }
}
