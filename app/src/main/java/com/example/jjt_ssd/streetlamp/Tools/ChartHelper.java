package com.example.jjt_ssd.streetlamp.Tools;

import android.graphics.Color;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * 曲线处理
 * Created by JJT-ssd on 2016/9/10.
 */
public  class ChartHelper {
    private  List<PointValue> mPointValues = new ArrayList<PointValue>();
    private  List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    Line line = new Line(mPointValues).setColor(Color.parseColor("#10CC88")).setStrokeWidth(2);  //折线的颜色、粗细
    List<Line> lines = new ArrayList<Line>();
    Viewport v ;
    LineChartData data = new LineChartData();
    Axis axisX = new Axis(); //X轴
    //X轴的标注
    public String[] dateTemp = {"00:00","03:00","06:00","09:00","12:00","15:00","18:00","21:00","24:00"};
    public  String[] datePm = {"1","3","5","7","9","11","13","15","17","19","21","23","25","27","29","31"};

    //图表的数据点
    public  String[] temps= {"17","35","28","27","25","29","20","24","31"};
    public  String[] humis= {"60","55","80","47","85","47","68","70","34"};
    public  String[] winds= {"8","5","2","7","5","3","4","1","6"};
    public String[] pms= {"18","35","28","27","25","29","20","24","31","20","24","31","20","24","31","34"};
    public  String[] nioses= {"35","37","45","70","20","50","20","24","31"};


    public  void initLineChart(LineChartView lineChart,String[] axesX,String[] dataValue,String str){

        getAxisXLables(axesX);//获取x轴的标注
        getAxisPoints(dataValue,str);//获取坐标点

//        Line line = new Line(mPointValues).setColor(Color.parseColor("#10CC88")).setStrokeWidth(2);  //折线的颜色、粗细
//        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line.setFilled(true);//是否填充曲线的面积
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
        line.setPointRadius(8);//座标点大小
        //line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        //line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        lines.add(line);
//        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
       // Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.parseColor("#bdbdbd"));  //设置字体颜色
        axisX.setLineColor(Color.parseColor("#bdbdbd"));
        //axisX.setName("date");  //表格名称
        axisX.setTextSize(9);//设置坐标轴字体大小
      //  axisX.setMaxLabelChars(8); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称


        data.setAxisXBottom(axisX); //x 轴在底部
        data.setValueLabelBackgroundColor(Color.TRANSPARENT);//此处设置坐标点旁边的文字背景
        data.setValueLabelBackgroundEnabled(false);
        data.setValueLabelsTextColor(Color.WHITE); //此处设置坐标点旁边的文字颜色
        data.setValueLabelTextSize(16);//设置坐标点旁边的字体大小

        int valueTemp = getArrMinValue(dataValue);
        if (valueTemp>=0)data.setBaseValue(0);// 设置反向覆盖区域颜色
        else data.setBaseValue(valueTemp);
        //data.setAxisXTop(axisX);  //x 轴在顶部
        //axisX.setHasLines(true); //x 轴分割线

        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
//        Axis axisY = new Axis();  //Y轴
//        axisY.setName("");//y轴标注
//        axisY.setTextSize(10);//设置字体大小
//        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //data.setAxisYRight(axisY);  //y轴设置在右边

        //设置行为属性，支持缩放、滑动以及平移
        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL);
        lineChart.setMaxZoom((float) 2);//最大方法比例
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);

        /**注：下面的7，10只是代表一个数字去类比而已
         * 当时是为了解决X轴固定数据个数。见（http://forum.xda-developers.com/tools/programming/library-hellocharts-charting-library-t2904456/page2）;
         */
//        Viewport v = new Viewport(lineChart.getMaximumViewport());
        v = new Viewport(lineChart.getMaximumViewport());
        if (valueTemp>0)  v.bottom=0;
        else  v.bottom=valueTemp;

        v.left = 0;
        v.right= dataValue.length-1;
        //set chart data to initialize viewport, otherwise it will be[0,0;0,0
        v.top = v.top + 16; //example max value
        lineChart.setMaximumViewport(v);
        //Optional step: disable viewport recalculations, thanks to this animations will not change viewport automatically.
        lineChart.setViewportCalculationEnabled(true);
        lineChart.setCurrentViewport(v);

    }

    private  int getArrMinValue(String[] dateStr)
    {
        int min = Integer.parseInt(dateStr[0]);
        for(int i=1;i<dateStr.length;i++){
            int tempStr = Integer.parseInt(dateStr[i]);
            if(tempStr<min){
                min = tempStr;
            }
        }
        return min;
    }

    /**
     * 设置X 轴的显示
     */
    private  void getAxisXLables(String[] axisX){
        for (int i = 0; i < axisX.length; i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(axisX[i]));
        }
    }

    /**
     * 图表的每个点的显示
     */
    private  void getAxisPoints(String[] dataValue,String str) {
        for (int i = 0; i < dataValue.length; i++) {
            mPointValues.add(new PointValue(i, Integer.parseInt(dataValue[i])).setLabel(dataValue[i]+str));
        }
    }
}
