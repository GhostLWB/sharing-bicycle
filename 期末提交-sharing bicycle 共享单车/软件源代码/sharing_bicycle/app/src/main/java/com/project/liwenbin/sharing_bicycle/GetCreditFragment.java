package com.project.liwenbin.sharing_bicycle;

import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by liwenbin on 2017/4/30 0030.
 */
public class GetCreditFragment extends Fragment {

    private PieChartView chart;
    private PieChartData data;

    private boolean hasLabels = true;
    private boolean hasLabelsOutside = false;
    private boolean hasCenterCircle = false;
    private boolean hasCenterText1 = false;
    private boolean hasCenterText2 = false;
    private boolean isExploded = false;
    private boolean hasLabelForSelected = false;
    private User user=User.getUser();
    private int[] each_type;
    private int numberOfType=3;//用车奖励，预约奖励，报修奖励


    public GetCreditFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_getcredit, container, false);

        chart = (PieChartView) rootView.findViewById(R.id.getcredit_chart);
        chart.setOnValueTouchListener(new ValueTouchListener());
        chart.setVisibility(View.INVISIBLE);

        queryData();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //wait
                if (each_type==null){
                    Toast.makeText(getActivity(),"网络情况不佳，请重试",Toast.LENGTH_SHORT).show();

                }else {
                    generateData();
                    // Disable viewport recalculations, see toggleCubic() method for more info.
                    chart.setViewportCalculationEnabled(false);
                    chart.setVisibility(View.VISIBLE);
                }
            }
        }, 400);

        return rootView;
    }

    private void queryData() {

        String creditUrl = "http://123.206.80.243:8080/sharing_bicycle/credit_record.do";
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("user_id", user.getAccount()));
        Handler creditHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        each_type=new int[3];
                        String dataString = (String) msg.obj;
                        int[] eachData = new int[2];

                        JSONArray json_raw = JSONArray.fromObject(dataString);

                        for (int i = 0; i < json_raw.size(); i++) {
                            JSONObject js = (JSONObject) json_raw.get(i);
                            eachData[0] = js.getInt("amount");
                            Log.d("GetCredit", "get amount:" + js.getInt("amount"));
                            eachData[1] = js.getInt("description");
                            switch (eachData[1]){
                                case 0://预约奖励
                                    each_type[0]+=eachData[0];
                                    Log.d("GetCredit", "type 0,now value is:" + each_type[0]);
                                    break;
                                case 1://用车奖励
                                    each_type[1]+=eachData[0];
                                    Log.d("GetCredit", "type 1,now value is:" + each_type[1]);
                                    break;
                                case 2://报修奖励
                                    each_type[2]+=eachData[0];
                                    Log.d("GetCredit", "type 2,now value is:" + each_type[2]);
                                    break;
                                default:
                                    break;

                            }
                        }
                       // credit_records = new float[maxNumberOfLines][numberOfPoints + 1];

                }
            }

        };
        NetUtils.postRequest(creditUrl,params,creditHandler);
    }

    private void reset() {
        chart.setCircleFillRatio(1.0f);
        hasLabels = false;
        hasLabelsOutside = false;
        hasCenterCircle = false;
        hasCenterText1 = false;
        hasCenterText2 = false;
        isExploded = false;
        hasLabelForSelected = false;
    }

    private void generateData() {
        int numValues = numberOfType;
        Log.d("RideTimeFragment","vector raw_data's size is :"+numValues);

        List<SliceValue> values = new ArrayList<SliceValue>();
        for (int i = 0; i < numValues; ++i) {
            SliceValue sliceValue = new SliceValue(each_type[i]*1.0f, ChartUtils.pickColor());
            values.add(sliceValue);
        }

        data = new PieChartData(values);
        data.setHasLabels(hasLabels);
        data.setHasLabelsOnlyForSelected(hasLabelForSelected);
        data.setHasLabelsOutside(hasLabelsOutside);
        data.setHasCenterCircle(hasCenterCircle);

        if (isExploded) {
            data.setSlicesSpacing(24);
        }

        if (hasCenterText1) {
            data.setCenterText1("Hello!");

            // Get roboto-italic font.
            Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Italic.ttf");
            data.setCenterText1Typeface(tf);

            // Get font size from dimens.xml and convert it to sp(library uses sp values).
            data.setCenterText1FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                    (int) getResources().getDimension(R.dimen.pie_chart_text1_size)));
        }

        if (hasCenterText2) {
            data.setCenterText2("Charts (Roboto Italic)");

            Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Italic.ttf");

            data.setCenterText2Typeface(tf);
            data.setCenterText2FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                    (int) getResources().getDimension(R.dimen.pie_chart_text2_size)));
        }
        if (chart!=null)
        chart.setPieChartData(data);
    }




    private class ValueTouchListener implements PieChartOnValueSelectListener {

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            switch (arcIndex){
                case 0:
                    final Snackbar snackbar=Snackbar.make(getActivity().getWindow().getDecorView(),"这是您通过预约车辆获得的" +each_type[0]+"个积分",Snackbar.LENGTH_SHORT);
                    SnackBarUtil.setSnackbarColor(snackbar,SnackBarUtil.blue);
                    snackbar.show();
                    break;
                case 1:
                    final Snackbar snackbar1=Snackbar.make(getActivity().getWindow().getDecorView(),"这是您通过使用车辆获得的" +each_type[1]+"个积分",Snackbar.LENGTH_SHORT);
                    SnackBarUtil.setSnackbarColor(snackbar1,SnackBarUtil.blue);
                    snackbar1.show();
                    break;
                case 2:
                    final Snackbar snackbar2=Snackbar.make(getActivity().getWindow().getDecorView(),"这是您通过报修获得的" +each_type[2]+"个积分",Snackbar.LENGTH_SHORT);
                    SnackBarUtil.setSnackbarColor(snackbar2,SnackBarUtil.blue);
                    snackbar2.show();
                    break;
            }

        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }

    @Override
    public void onDestroy() {
        chart=null;
        super.onDestroy();
    }
}
