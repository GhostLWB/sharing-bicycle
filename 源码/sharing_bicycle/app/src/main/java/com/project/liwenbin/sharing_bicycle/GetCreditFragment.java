package com.project.liwenbin.sharing_bicycle;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by liwenbin on 2017/4/30 0030.
 */
public class GetCreditFragment extends Fragment {
    private PieChartView getCredit_chart;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_getcredit,container,false);
        getCredit_chart=(PieChartView) view.findViewById(R.id.getcredit_chart);
        return view;
    }
}
