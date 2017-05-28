package com.project.liwenbin.sharing_bicycle;

import android.annotation.TargetApi;
import android.icu.util.Calendar;
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
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by liwenbin on 2017/4/30 0030.
 */
public class WeeklyFragment extends Fragment {
    private ColumnChartView chart;
    private ColumnChartData data;
    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLabels = true;
    private boolean hasLabelForSelected = false;
    private User user=User.getUser();
    private int numColumns = 7;
    private Vector<RidingRecord> raw_data;
    private Vector<RidingRecord> valid_data;
    private Vector<RidingRecord> day_data;
    private int[] eachDayOfWeek=new int[7];


    public WeeklyFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_weekly, container, false);

        chart = (ColumnChartView) rootView.findViewById(R.id.weekly_chart);
        chart.setVisibility(View.INVISIBLE);
        chart.setOnValueTouchListener(new ValueTouchListener());

        queryData();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (raw_data!=null){
                    Log.d("Consume","start to reach generateData()");
                    selectRecord();
                    generateData();
                    resetViewport();
                    chart.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(getActivity(),"网络状况不佳，请重试",Toast.LENGTH_SHORT).show();
                }

            }
        },400);


        return rootView;
    }

    /**
     * 获取所有的记录
     */
    private void queryData(){
        String creditUrl="http://123.206.80.243:8080/sharing_bicycle/cycling_record.do";
        List<BasicNameValuePair> params=new ArrayList<>();
        params.add(new BasicNameValuePair("user_id",user.getAccount()));
        Handler creditHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        String data = (String)msg.obj;
                        raw_data=new Vector<>();
                        JSONArray json_raw = JSONArray.fromObject(data);
                        for (int i=0;i<json_raw.size();i++){
                            JSONObject js=(JSONObject)json_raw.get(i);
                            raw_data.add(new RidingRecord(js.getInt("riding_time"),js.getString("date_time")));

                            Log.d("WeeklyFragment","get riding time:"+(js.getInt("riding_time")));
                            Log.d("WeeklyFragment","raw_data vector's size is :"+raw_data.size());
                        }
                }
            }
        };
        NetUtils.postRequest(creditUrl,params,creditHandler);

    }

    /**
     * 筛选数据，将同一天的时间加起来，然后筛选最近7天
     */
    private void selectRecord(){
        String date1,date2;
        boolean issameDay;
        valid_data=new Vector<>(7);
        day_data=new Vector<>();
        if (raw_data.size()!=0){
            day_data.add(raw_data.get(0));
        }
        for (int i=1;i<raw_data.size();i++){
            issameDay=false;
            RidingRecord rawRecord=raw_data.get(i);
            date1=rawRecord.getDate_time().split(" ")[0];
            for (int j=0;j<day_data.size();j++){
                RidingRecord rawRecord1=day_data.get(j);
                date2=rawRecord1.getDate_time().split(" ")[0];
//                Log.d("WeeklyFragment","raw_data date_time:"+rawRecord.getDate_time());
//                Log.d("WeeklyFragment","day_data date_time:"+rawRecord1.getDate_time());
                //Log.d("WeeklyFragment","date1:"+date1);
                //Log.d("WeeklyFragment","date2:"+date2);
                if (date1.equals(date2)){
                    Log.d("WeeklyFragment","date1 equals to date2");
                    rawRecord1.setRidetime(rawRecord1.getRidetime()+rawRecord.ridetime);
                    Log.d("WeeklyFragment","ridetime total add to :"+rawRecord1.getRidetime());
                    day_data.set(j,rawRecord1);
                    issameDay=true;
                }
            }
            if (!issameDay){
                day_data.add(rawRecord);
            }
        }
/**
 * 如果所有数据的量大于7，则截取最近的7条数据
 */
        if (day_data.size()>7){
            for (int i=0;i<7;i++){
                valid_data.add(day_data.get(6-i));
            }
        }else{
            for (int i=0;i<day_data.size();i++){
                valid_data.add(day_data.get(day_data.size()-1-i));
            }
        }
        Log.d("WeeklyFragment","valid_data's size is:"+valid_data.size());
    }
    @TargetApi(24)
    private int getDate(String raw){
        int date_week=0;
        String date;
        String[] spliteRawResult=raw.split(" ");
        date=spliteRawResult[0];
        String[] spliteDateResult=date.split("-");
        Calendar calendar=Calendar.getInstance();
        calendar.set(Integer.parseInt(spliteDateResult[0]),Integer.parseInt(spliteDateResult[1]),Integer.parseInt(spliteDateResult[2]));
        date_week=calendar.get(Calendar.DAY_OF_WEEK);
        return date_week;
    }

    private void generateDefaultData() {
        String[] tempArray;
        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values;

        for (int i = 0; i < valid_data.size(); ++i) {
            values = new ArrayList<>();
            values.add(new SubcolumnValue(valid_data.get(i).getRidetime(), ChartUtils.pickColor()));
            Log.d("WeeklyFragment","WeeklyFragment_record"+i+"is :"+valid_data.get(i).getRidetime());
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
                axisX.setName("近7天");
                axisY.setName("骑行时间");
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
        String[] temp;
        /**
         * 计算最大值
         */
        for (int i=0;i<valid_data.size();i++){
            if (valid_data.get(i).getRidetime()>max){
                max=valid_data.get(i).getRidetime();
            }
        }
        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = 0;
        v.top = max+10;
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
            Log.d("WeeklyFragment","No"+columnIndex+"column");
            Toast.makeText(getActivity(), "您在"+valid_data.get(columnIndex).getDate_time().split(" ")[0]+"这天骑行了"+valid_data.get(columnIndex).getRidetime()+"分钟", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }

}
