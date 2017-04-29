package com.project.liwenbin.sharing_bicycle;

import android.app.Activity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by liwenbin on 2017/4/29 0029.
 */
public class ChartActivity  extends Activity {
    private LineChartView lineChart;
    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLines = true;
    private boolean hasPoints = true;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean hasLabels = false; // 是否显示点的数据
    private boolean isCubic = false;
    private boolean hasLabelForSelected = false;
    private boolean pointsHaveDifferentColor;

    private LineChartData data;
    private int numberOfLines = 1; // 只显示一行数据
    private int maxNumberOfLines = 1; // 如果为4则表示最多显示4行，1表示只有一行数据
    private int numberOfPoints = 10; // 每行数据有多少个点
    // 存储数据
    float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_layout);

        lineChart=(LineChartView) findViewById(R.id.chart);
        generateData();

    }
    private void generateData() {
        int whiteColor = getResources().getColor(R.color.white);

       generateValues();
        List<Line> lines = new ArrayList<Line>();

        for (int i = 0; i < numberOfLines; ++i) {

            List<PointValue> values = new ArrayList<PointValue>();

            for (int j = 0; j < numberOfPoints; ++j) {
                values.add(new PointValue(j+1, randomNumbersTab[i][j]));
            }

            Line line = new Line(values);
            //line.setColor(ChartUtils.COLORS[i]); // 多条数据时选择这个即可
            line.setColor(whiteColor); // 定制线条颜色
            line.setShape(shape);
            line.setCubic(isCubic);
            line.setFilled(isFilled);
            line.setHasLabels(hasLabels);
            line.setHasLabelsOnlyForSelected(hasLabelForSelected);
            line.setHasLines(hasLines);
            line.setHasPoints(hasPoints);
            if (pointsHaveDifferentColor){
                //多条数据时选择这个即可
                //line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
                line.setPointColor(whiteColor);
            }
            lines.add(line);
        }

        data = new LineChartData(lines);

        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("最近10次考试成绩");
                axisY.setName("");
                axisX.setTextColor(whiteColor);
                axisY.setTextColor(whiteColor);
                axisY.setLineColor(whiteColor);
                axisX.setLineColor(whiteColor);
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        data.setBaseValue(Float.NEGATIVE_INFINITY);
        lineChart.setLineChartData(data);

    }
    private void generateValues(){
        for (int i=0;i<numberOfLines;i++){
            for (int j=0;j<numberOfPoints;j++)
            {
                randomNumbersTab[i][j]=j*200;
            }
        }
    }
}
