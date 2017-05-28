package com.project.liwenbin.sharing_bicycle;

import android.graphics.Typeface;
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

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by liwenbin on 2017/4/30 0030.
 */
public class RideTimeFragment extends Fragment {
    private PieChartView chart;
    private PieChartData data;

    private boolean hasLabels = true;
    private boolean hasLabelsOutside = false;
    private boolean hasCenterCircle = false;
    private boolean hasCenterText1 = true;
    private boolean hasCenterText2 = false;
    private boolean isExploded = false;
    private boolean hasLabelForSelected = false;
    private User user=User.getUser();
    private String[] bike_ids_t;
    private int[] riding_times_t;
    private String[] date_times_t;
    private String[] bike_ids;
    private int[] riding_times;
    private String[] date_times;
    String[] each_record=null;
    private int validRecord=0;
    //for test

    public RideTimeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_ridetime, container, false);

        chart = (PieChartView) rootView.findViewById(R.id.ridetime_chart);
        chart.setOnValueTouchListener(new ValueTouchListener());
        chart.setVisibility(View.INVISIBLE);

        queryData();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //wait
                if (each_record==null){
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

    private void queryData(){

        String rideUrl="http://123.206.80.243:8080/sharing_bicycle/cycling_record.do";
        List<BasicNameValuePair> params=new ArrayList<>();
        params.add(new BasicNameValuePair("user_id",user.getAccount()));
        Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        validRecord=0;
                        String dataString = (String)msg.obj;
                        each_record=new String[3];
                        JSONArray json_raw = JSONArray.fromObject(dataString);
                        bike_ids=new String[json_raw.size()];
                        riding_times=new int[json_raw.size()];
                        date_times=new String[json_raw.size()];
                        bike_ids_t=new String[json_raw.size()];
                        riding_times_t=new int[json_raw.size()];
                        date_times_t=new String[json_raw.size()];

                        for (int i=0;i<json_raw.size();i++){
                            boolean samefalg=false;
                            JSONObject js=(JSONObject)json_raw.get(i);
                            if (i==0){
                                bike_ids[validRecord]=js.getString("bike_id");
                                riding_times[validRecord]=js.getInt("riding_time");
                                Log.d("RideTimeFragment","No."+i+" record:bike_id is :"+bike_ids[i]);
                                Log.d("RideTimeFragment","bike"+bike_ids[i]+" \'s riding time is:" +riding_times[i]);
                                date_times[validRecord]=js.getString("date_time");
                                //raw_data.add(each_record);
                                //Log.d("RideTimeFragment","add bike "+each_record[0]+" to vector raw_data");
                                validRecord++;
                            }else{
                                bike_ids_t[i]=js.getString("bike_id");
                                Log.d("RideTimeFragment","No."+i+" record:bike_id is :"+bike_ids_t[i]);
                                riding_times_t[i]=js.getInt("riding_time");
                                Log.d("RideTimeFragment","No."+i+" record:riding time is :"+riding_times[i]);
                                date_times_t[i]=js.getString("date_time");
                                //Log.d("RideTimeFragment","raw_data now has :"+raw_data.size());
                                for (int j=0;j<validRecord;j++){
                                    if (bike_ids_t[i].equals(bike_ids[j])){
                                        Log.d("RideTimeFragment","bike"+bike_ids_t[i]+" eaquals to "+bike_ids[j]);
                                        riding_times[j]+=riding_times_t[i];
                                        Log.d("RideTimeFragment","bike"+bike_ids[j]+"\'s riding time now is  "+riding_times[j]);
                                        samefalg=true;
                                    }
                                }
                                if (!samefalg){
                                    bike_ids[validRecord]=bike_ids_t[i];
                                    riding_times[validRecord]=riding_times_t[i];
                                    date_times[validRecord]=date_times_t[i];
                                    validRecord++;
                                }
                            }


                        }
                }
            }
        };
        NetUtils.postRequest(rideUrl,params,handler);
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
        int numValues = validRecord;
        Log.d("RideTimeFragment","validRecord is :"+numValues);

        List<SliceValue> values = new ArrayList<SliceValue>();
        for (int i = 0; i < numValues; ++i) {
            Log.d("RideTimeFragment","each pie's value is :"+riding_times[i]);
            SliceValue sliceValue = new SliceValue(riding_times[i]*1.0f, ChartUtils.pickColor());
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
//            Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Italic.ttf");
//            data.setCenterText1Typeface(tf);

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

        chart.setPieChartData(data);
    }


    private class ValueTouchListener implements PieChartOnValueSelectListener {

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            Toast.makeText(getActivity(), "您使用了"+bike_ids[arcIndex]+"号车辆"+"骑行了"+riding_times[arcIndex]+"分钟", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }
}


