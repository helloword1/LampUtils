package com.example.jjt_ssd.streetlamp.Tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by JJT-ssd on 2016/8/17.
 * 时间格式的封装与获取
 */

public class TimeFormat {
    //获取当前的格式
    private static  SimpleDateFormat sdftime=new SimpleDateFormat("HH:mm");
    private static  SimpleDateFormat sdfyear=new SimpleDateFormat("yy");
    private static String mMonth;
    private static String mDay;
    private static String mWay;
    private  static String timeStr;
    private  static String yearStr;

    public static String StringData(){
        return getDate()+getWeek()+" "+getTimes();
    }

    public static String getDate()
    {
        yearStr=sdfyear.format(new java.util.Date());
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码

        return yearStr + "/" + mMonth +"/" + mDay;
    }


    public static String getDate(int temp)//0表示今天
    {
        java.util.Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        c.add(Calendar.DAY_OF_MONTH, temp);
        java.text.SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
        String format = sdf.format(c.getTime());
        return format;
    }

    public  static  String getWeek()
    {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if("1".equals(mWay)){
            mWay ="天";
        }else if("2".equals(mWay)){
            mWay ="一";
        }else if("3".equals(mWay)){
            mWay ="二";
        }else if("4".equals(mWay)){
            mWay ="三";
        }else if("5".equals(mWay)){
            mWay ="四";
        }else if("6".equals(mWay)){
            mWay ="五";
        }else if("7".equals(mWay)){
            mWay ="六";
        }
        return "星期"+mWay;
    }

    public static String getTimes()
    {
        //获取当前时间
        timeStr=sdftime.format(new java.util.Date());
        return timeStr;
    }

    public static String getStrTimes()
    {
        java.util.Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        c.add(Calendar.DAY_OF_MONTH, 0);
        java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        return sdf.format(c.getTime())+getWeek()+getTimes();
    }
}