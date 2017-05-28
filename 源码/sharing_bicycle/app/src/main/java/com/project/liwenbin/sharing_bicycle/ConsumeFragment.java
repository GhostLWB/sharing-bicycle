package com.project.liwenbin.sharing_bicycle;

import android.os.Handler;
import android.os.Message;
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

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by liwenbin on 2017/4/30 0030.
 */
public class ConsumeFragment extends Fragment{
    private ColumnChartView chart;
    private ColumnChartData data;
    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLabels = false;
    private boolean hasLabelForSelected = true;
    private User user=User.getUser();
    private Vector<Wallet_record> data_raw=null;
    private int numColumns = 7;

    public ConsumeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_consume, container, false);

        chart = (ColumnChartView) rootView.findViewById(R.id.consume_chart);
        chart.setVisibility(View.INVISIBLE);
        chart.setOnValueTouchListener(new ValueTouchListener());

        queryData();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (data_raw!=null){
                    Log.d("Consume","start to reach generateData()");
                    generateData();
                    chart.setVisibility(View.VISIBLE);
                    resetViewport();
                }else{
                    Toast.makeText(getActivity(),"网络状况不佳，请重试",Toast.LENGTH_SHORT).show();
                }

            }
        },400);


        return rootView;
    }

    private void queryData(){
        String creditUrl="http://123.206.80.243:8080/sharing_bicycle/wallet_record.do";
        List<BasicNameValuePair> params=new ArrayList<>();
        params.add(new BasicNameValuePair("user_id",user.getAccount()));
        Handler creditHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        String data = (String)msg.obj;
                        data_raw=new Vector<>();
                        JSONArray json_raw = JSONArray.fromObject(data);
                        //numColumns=json_raw.size();
                        Log.d("Consume","the number of columns is :"+numColumns);
                        for (int i=0;i<json_raw.size();i++){
                            JSONObject js=(JSONObject)json_raw.get(i);
                            Log.d("Consume","get consume record:"+(js.getDouble("amount")));
                            Log.d("Consume","get consume date:"+js.getString("date_time"));
                            data_raw.add(new Wallet_record(js.getDouble("amount")*-1+"",js.getString("date_time")));
                            Log.d("Consume","data_raw vector's size is :"+data_raw.size());
                        }
                }
            }
        };
        NetUtils.postRequest(creditUrl,params,creditHandler);

    }

    private void generateDefaultData() {
        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values;
        Vector<Wallet_record> data_raw7=new Vector<>();
/**
 * debug
 */
        for (int i=0;i<data_raw.size();i++){
            Log.d("Consume","data_raw "+i+"'s date is:"+data_raw.get(i).getDate_time());
        }
        if (data_raw.size()>=7){
            for (int j=0;j<7;j++){
                data_raw7.add(data_raw.get(6-j));
                Log.d("Consume","extract date  "+data_raw.get(6-j).getDate_time());
            }
            data_raw=data_raw7;
        }else{
            for (int j=0;j<data_raw.size();j++){
                data_raw7.add(data_raw.get(data_raw.size()-1-j));
                Log.d("Consume","extract date  "+data_raw.get(data_raw.size()-1-j).getDate_time());
            }
            data_raw=data_raw7;
        }
        for (int i = 0; i < data_raw.size(); ++i) {
            values = new ArrayList<>();
            values.add(new SubcolumnValue(Float.parseFloat(data_raw.get(i).getAmount()), ChartUtils.pickColor()));
            Log.d("Consume","consume_record"+i+"is :"+Float.parseFloat(data_raw.get(i).getAmount()));
            Log.d("Consume","consume_date"+i+"is :"+data_raw.get(i).getDate_time());
            Column column = new Column(values);
            column.setHasLabels(hasLabels);
            column.setHasLabelsOnlyForSelected(hasLabelForSelected);
            columns.add(column);
        }

        data = new ColumnChartData(columns);

        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("最近7天消费记录");
                axisY.setName("消费金额");
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        chart.setColumnChartData(data);

    }


    private int getSign() {
        int[] sign = new int[]{-1, 1};
        return sign[Math.round((float) Math.random())];
    }


    private void resetViewport() {
        float max=0;
        // Reset viewport height range
        for (Wallet_record itera:data_raw){
            if (Float.parseFloat(itera.getAmount())>=max){
                max=Float.parseFloat(itera.getAmount());
            }
        }
        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = 0;
        v.top = max+0.1f;
        //v.top = 100;
        v.left = 0;
        v.right = 7 ;
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
    }

    private void generateData() {
        generateDefaultData();
    }
    private class ValueTouchListener implements ColumnChartOnValueSelectListener {

        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {

            Toast.makeText(getActivity(), "您在"+data_raw.get(columnIndex).getDate_time()+"消费了"+data_raw.get(columnIndex).getAmount()+"元", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }

}
