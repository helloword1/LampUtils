package com.example.jjt_ssd.streetlamp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jjt_ssd.streetlamp.Tools.ChartHelper;
import com.example.jjt_ssd.streetlamp.Tools.NotNull;
import com.example.jjt_ssd.streetlamp.Tools.StringUtils;
import com.example.jjt_ssd.streetlamp.Tools.TimeFormat;
import com.example.jjt_ssd.streetlamp.Tools.bean.LampStatus;
import com.example.jjt_ssd.streetlamp.mqtt.MQTTService;
import com.example.jjt_ssd.streetlamp.mqtt.mqttobserver.MqttObserver;
import com.google.gson.Gson;

import lecho.lib.hellocharts.view.LineChartView;

//环境监测页面
public class EMActivity extends BaseActivity implements MqttObserver {

    //参数存储
    AppData app;

    EMActivity emact = this;
    //温度图表切换按钮
    Button bt_EMTem;
    //湿度图表切换按钮
    Button bt_EMHum;
    //风力图表切换按钮
    Button bt_EMWinp;
    //PM2.5图表切换按钮
    Button bt_EMPm;
    //噪声图表切换按钮
    Button bt_Noi;

    //
    ImageView bt_DateLeft;
    ImageView bt_DateRight;
    ImageView bt_Today;

    TextView tv_Temperature;//温度值
    TextView tv_Humidity;//湿度值
    TextView tv_WindPower;//风力值
    TextView tv_PM;//PM2.5a值
    TextView tv_Noise;//噪声值
    TextView tv_Company;//单位

    ImageView iv_EMSli;
    TextView EMTempTimes;

    //动画初始坐标
    private int curX = 0;
    private int curY = 0;
    private MQTTService service;
    ServiceConnection conn = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MQTTService.MyBinder myBinder = (MQTTService.MyBinder) iBinder;
            service = myBinder.getService();
            service.registerObserver(EMActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    //图表参数
    private LineChartView lineChart;
    private ChartHelper chartHelper;
    private static final String TAG = "EMActivity";
    //////////////////////传感器数据获取/////////////////////////////////
    public Handler handler = new Handler();
    private Runnable task = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            //对界面数据进行赋值
//            setPageValue(lampStatus);
            //设置延迟时间，此处是1分钟
            handler.postDelayed(this, 1 * 1000);
        }
    };
    ///////////////////////////////////////////////////////////////////////////////

    @Override
    protected int getLoyoutId() {
        return R.layout.activity_em;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUI();
        EventHandle();
    }

    //获取UI参数
    private void setupUI() {
        //设置标题
        setTitle("环境监测");

        //获取数据初始设置
        app = (AppData) getApplication();

        EMTempTimes = (TextView) findViewById(R.id.EMDataTimes);
        EMTempTimes.setText(TimeFormat.getDate(0));

        bt_EMTem = (Button) findViewById(R.id.EMMidBtnTem);
        bt_EMHum = (Button) findViewById(R.id.EMMidBtnHum);
        bt_EMWinp = (Button) findViewById(R.id.EMMidBtnWin);
        bt_EMPm = (Button) findViewById(R.id.EMMidBtnPM2_5);
        bt_Noi = (Button) findViewById(R.id.EMMidBtnNoi);

        bt_DateLeft = (ImageView) findViewById(R.id.EMLeftButton);
        bt_DateRight = (ImageView) findViewById(R.id.EMRightButton);
        bt_Today = (ImageView) findViewById(R.id.EMTodayButton);

        iv_EMSli = (ImageView) findViewById(R.id.EMSlider);
        lineChart = (LineChartView) findViewById(R.id.chart);

        tv_Temperature = (TextView) findViewById(R.id.EMTemperature);
        tv_Humidity = (TextView) findViewById(R.id.EMHumidity);
        tv_WindPower = (TextView) findViewById(R.id.EMWindPower);
        tv_PM = (TextView) findViewById(R.id.EMPM2_5a);
        tv_Noise = (TextView) findViewById(R.id.EMNoise);

        tv_Company = (TextView) findViewById(R.id.EMCompany);
        tv_Company.setText("°C");

        Bundle extras = getIntent().getExtras();
        tv_Temperature.setText(StringUtils.doubleFormat(extras.getString("in_temp")));
        tv_Humidity.setText( StringUtils.doubleFormat(extras.getString("hum")));
        tv_WindPower.setText(StringUtils.doubleFormat(extras.getString("speed")));
        tv_PM.setText(StringUtils.doubleFormat(extras.getString("pm25")));
        tv_Noise.setText( StringUtils.doubleFormat(extras.getString("noise")));

        //获取传感器数据
//        handler.post(task);
        Intent service = new Intent(getApplicationContext(), MQTTService.class);
        startService(service);
        bindService( service,conn,BIND_ABOVE_CLIENT);
        ////初始化表格
        chartHelper = new ChartHelper();
        chartHelper.initLineChart(lineChart, chartHelper.dateTemp, chartHelper.temps, "°");

    }

    private void setPageValue(LampStatus lampStatus) {
        LampStatus.DataBean data = lampStatus.getData();
        String in_temp = data.getIn_temp();
        if (NotNull.isNotNull(in_temp))
            tv_Temperature.setText(StringUtils.doubleFormat(in_temp));
        else
            tv_Temperature.setText("0");
        tv_Humidity.setText(StringUtils.doubleFormat(data.getHum()));
        tv_WindPower.setText(StringUtils.doubleFormat(data.getSpeed()));
        tv_PM.setText(StringUtils.doubleFormat(data.getPm25()));
        tv_Noise.setText(StringUtils.doubleFormat(data.getNoise()));
    }

    int temp = 0;

    //设置UI参数和按钮响应事件
    private void EventHandle() {
        bt_EMTem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBtnNormal();
                bt_EMTem.setTextColor(ContextCompat.getColor(emact, R.color.colorSelect));
                TransAnima(bt_EMTem);
                tv_Company.setText("°C");
                chartHelper = new ChartHelper();
                chartHelper.initLineChart(lineChart, chartHelper.dateTemp, chartHelper.temps, "°");
            }
        });
        bt_EMHum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBtnNormal();
                bt_EMHum.setTextColor(ContextCompat.getColor(emact, R.color.colorSelect));
                TransAnima(bt_EMHum);
                tv_Company.setText("%");
                chartHelper = new ChartHelper();
                chartHelper.initLineChart(lineChart, chartHelper.dateTemp, chartHelper.humis, "%");
            }
        });
        bt_EMWinp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBtnNormal();
                bt_EMWinp.setTextColor(ContextCompat.getColor(emact, R.color.colorSelect));
                TransAnima(bt_EMWinp);
                tv_Company.setText("级");
                chartHelper = new ChartHelper();
                chartHelper.initLineChart(lineChart, chartHelper.dateTemp, chartHelper.winds, "级");
            }
        });
        bt_EMPm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBtnNormal();
                bt_EMPm.setTextColor(ContextCompat.getColor(emact, R.color.colorSelect));
                TransAnima(bt_EMPm);
                tv_Company.setText("μg/m³");
                chartHelper = new ChartHelper();
                chartHelper.initLineChart(lineChart, chartHelper.datePm, chartHelper.pms, "");
            }
        });
        bt_Noi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBtnNormal();
                bt_Noi.setTextColor(ContextCompat.getColor(emact, R.color.colorSelect));
                TransAnima(bt_Noi);
                tv_Company.setText("dB");
                chartHelper = new ChartHelper();
                chartHelper.initLineChart(lineChart, chartHelper.dateTemp, chartHelper.nioses, "");
            }
        });


        bt_DateLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (temp > -6)
                    EMTempTimes.setText(TimeFormat.getDate(--temp));
            }
        });
        bt_DateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (temp < 0)
                    EMTempTimes.setText(TimeFormat.getDate(++temp));
            }
        });
        bt_Today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EMTempTimes.setText(TimeFormat.getDate(0));
            }
        });

    }

    //滑块动画
    private void TransAnima(Button bt_animate) {
        int nextX = bt_animate.getLeft();
        int nextY = curY;
        TranslateAnimation animate = new TranslateAnimation(curX, nextX, curY, nextY);
        animate.setDuration(100);
        iv_EMSli.startAnimation(animate);
        animate.setFillAfter(true);
        curX = nextX;
    }

    private void setBtnNormal() {
        bt_EMTem.setTextColor(ContextCompat.getColor(this, R.color.colorNormal));
        bt_EMHum.setTextColor(ContextCompat.getColor(this, R.color.colorNormal));
        bt_EMWinp.setTextColor(ContextCompat.getColor(this, R.color.colorNormal));
        bt_EMPm.setTextColor(ContextCompat.getColor(this, R.color.colorNormal));
        bt_Noi.setTextColor(ContextCompat.getColor(this, R.color.colorNormal));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出循环
        handler.removeCallbacks(task);
    }

    @Override
    public void update(String succeedStr) {
        LampStatus lampStatus = new Gson().fromJson(succeedStr, LampStatus.class);
        if (NotNull.isNotNull(lampStatus)) {
            setPageValue(lampStatus);
        }
    }
}
