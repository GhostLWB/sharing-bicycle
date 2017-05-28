package com.project.liwenbin.sharing_bicycle;

import android.graphics.Color;
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

import lecho.lib.hellocharts.animation.ChartAnimationListener;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
/**
 * Created by liwenbin on 2017/4/30 0030.
 */
public class CreditFragment extends Fragment {
private LineChartView chart;
private LineChartData data;
private int numberOfLines = 1;
private int maxNumberOfLines = 4;
private int numberOfPoints = 7;
float[][] credit_records =null;
private boolean hasAxes = true;
private boolean hasAxesNames = true;
private boolean hasLines = true;
private boolean hasPoints = true;
private ValueShape shape = ValueShape.CIRCLE;
private boolean isFilled = true;
private boolean hasLabels = true;
private boolean isCubic = true;
private boolean hasLabelForSelected = false;
private boolean pointsHaveDifferentColor;
private boolean hasGradientToTransparent = false;
private User user=User.getUser();

private Vector<Credit_record> raw_data;

public CreditFragment() {
        }

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_credit, container, false);

        chart = (LineChartView) rootView.findViewById(R.id.credit_chart_view);
        chart.setVisibility(View.INVISIBLE);
        chart.setOnValueTouchListener(new ValueTouchListener());

        // 向服务器请求数据，放在raw_data中
        generateValues();
        //延迟执行，将数据填充到图表
        new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                        //wait
                        if (credit_records==null){
                                Toast.makeText(getActivity(),"网络情况不佳，请重试",Toast.LENGTH_SHORT).show();

                        }else {
                                generateData();
                                // Disable viewport recalculations, see toggleCubic() method for more info.
                                chart.setViewportCalculationEnabled(false);
                                resetViewport();
                                chart.setVisibility(View.VISIBLE);
                        }
                }
        }, 600);


        return rootView;
        }


private void generateValues() {
        String creditUrl="http://123.206.80.243:8080/sharing_bicycle/credit_record.do";
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
                                        numberOfPoints=json_raw.size();
                                        Log.d("ChartCredit","1. numberOfPoints is:"+numberOfPoints);
                                        for (int i=0;i<json_raw.size();i++){
                                                JSONObject js=(JSONObject)json_raw.get(i);
                                                Log.d("ChartCredit","get amount:"+js.getString("amount"));
                                                Log.d("ChartCredit","description is :"+js.getString("description"));
                                                raw_data.add(new Credit_record(js.getInt("amount")+"",js.getString("description"),js.getString("date_time")));

                                                Log.d("ChartCredit","raw_data vector's size is :"+raw_data.size());
                                        }
                                        credit_records= new float[maxNumberOfLines][numberOfPoints];

                        }
                }
        };
        NetUtils.postRequest(creditUrl,params,creditHandler);
    /**
     * 延迟执行300毫秒，因为向服务器请求数据会有轻微延迟，比程序执行顺序慢
     * 将每一次的积分情况累加起来,放到credit_records中
     */
        new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                        Log.d("ChartCredit","2. numberOfPoints is:"+numberOfPoints);
                        if (raw_data!=null){
                                for (int j=0;j<numberOfPoints;j++){
                                        credit_records[0][j]=getAmountEach(j+1);
                                        Log.d("ChartCredit","each point is:"+getAmountEach(j+1));
                                }
                        }else{
                                Toast.makeText(getActivity(),"网络状况不佳，请重试",Toast.LENGTH_SHORT).show();
                        }
                }
        },300);
        }
        /**
         * 获得每一个点的累计值
         * @param
         * @param curr
         * @return
         */
        private float getAmountEach(int curr){
                int amount_each=0;
                for (int i=0;i<curr;i++){
                        amount_each+=Integer.parseInt(raw_data.get(raw_data.size()-1-i).getCredit_amount());
                        Log.d("ChartCredit"," each ont data is "+Integer.parseInt(raw_data.get(i).getCredit_amount()));
                }
                Log.d("ChartCredit"," curr is:"+ curr);
                Log.d("ChartCredit"," amount_each is:"+ amount_each);
                return amount_each*1.0f;
        }


private void resetViewport() {
// Reset viewport height range
final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = 0;
        if (numberOfPoints==0){
                v.top=10;
        }else{
                v.top = credit_records[0][numberOfPoints-1]+20;
        }
        v.left = 0;
        v.right = numberOfPoints ;
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
        }

private void generateData() {

        List<Line> lines = new ArrayList<Line>();
        Vector<Credit_record> data_raw7=new Vector<>();
        float[][] amount_records7=new float[1][7];

    /**
     * 如果所有数据的量大于7，则截取最近的7条数据
     */
        Log.d("CreditTest","raw_data.size() is:"+raw_data.size());
        if (raw_data.size()>7){
            for (int j=0;j<7;j++){
                data_raw7.add(raw_data.get(6-j));
                    Log.d("ChartCredit"," add record date :"+ raw_data.get(6-j).getDate_time());
                amount_records7[0][j]=credit_records[0][raw_data.size()-8+j];
            }
        raw_data=data_raw7;
        credit_records=amount_records7;
        }else{
                for (int j=0;j<raw_data.size();j++){
                        data_raw7.add(raw_data.get(raw_data.size()-1-j));
                        Log.d("ChartCredit"," add record date :"+ raw_data.get(raw_data.size()-1-j).getDate_time());
                        amount_records7[0][j]=credit_records[0][raw_data.size()-1-j];

                }
                raw_data=data_raw7;
                //credit_records=amount_records7;
        }

        if (credit_records[0].length<7){
            numberOfPoints=credit_records[0].length;
        }else
        {
                numberOfPoints=7;
        }
        for (int i = 0; i < numberOfLines; ++i) {
                List<PointValue> values = new ArrayList<>();

                for (int j = 0; j < numberOfPoints; ++j) {
                values.add(new PointValue(j, credit_records[i][j]));
                Log.d("ChartCredit","add "+ credit_records[i][j]+"to line");
        }

        Line line = new Line(values);
        line.setColor(ChartUtils.COLORS[i]);
        line.setShape(shape);
        line.setCubic(isCubic);
        line.setFilled(isFilled);
        line.setHasLabels(hasLabels);
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
        line.setHasLines(hasLines);
        line.setHasPoints(hasPoints);
        //line.setHasGradientToTransparent(hasGradientToTransparent);
        if (pointsHaveDifferentColor){
        line.setPointColor(Color.parseColor("#ffffff"));
        }
        lines.add(line);
        }

        data = new LineChartData(lines);

        if (hasAxes) {
        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        if (hasAxesNames) {
        axisX.setName("最近7次积分变化记录");
        axisY.setName("积分变化");
        }
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        } else {
        data.setAxisXBottom(null);
        data.setAxisYLeft(null);
        }

        data.setBaseValue(Float.NEGATIVE_INFINITY);
        chart.setLineChartData(data);

        }



private void toggleCubic() {
        isCubic = !isCubic;

        generateData();

        if (isCubic) {
// It is good idea to manually set a little higher max viewport for cubic lines because sometimes line
// go above or below max/min. To do that use Viewport.inest() method and pass negative value as dy
// parameter or just set top and bottom values manually.
// In this example I know that Y values are within (0,100) range so I set viewport height range manually
// to (-5, 105).
// To make this works during animations you should use Chart.setViewportCalculationEnabled(false) before
// modifying viewport.
// Remember to set viewport after you call setLineChartData().
final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = -5;
        v.top = 105;
        // You have to set max and current viewports separately.
        chart.setMaximumViewport(v);
        // I changing current viewport with animation in this case.
        chart.setCurrentViewportWithAnimation(v);
        } else {
// If not cubic restore viewport to (0,100) range.
final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = 0;
        v.top = 100;

        // You have to set max and current viewports separately.
        // In this case, if I want animation I have to set current viewport first and use animation listener.
        // Max viewport will be set in onAnimationFinished method.
        chart.setViewportAnimationListener(new ChartAnimationListener() {

@Override
public void onAnimationStarted() {
        // TODO Auto-generated method stub

        }

@Override
public void onAnimationFinished() {
        // Set max viewpirt and remove listener.
        chart.setMaximumViewport(v);
        chart.setViewportAnimationListener(null);

        }
        });
        // Set current viewpirt with animation;
        chart.setCurrentViewportWithAnimation(v);
        }

        }



private class ValueTouchListener implements LineChartOnValueSelectListener {

    @Override
    public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {

        Toast.makeText(getActivity(),"这是您在"+"("+raw_data.get(pointIndex).getDate_time()+")"+"的积分情况" , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onValueDeselected() {
        // TODO Auto-generated method stub

    }

}
}