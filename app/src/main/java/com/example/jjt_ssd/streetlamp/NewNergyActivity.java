package com.example.jjt_ssd.streetlamp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.TextView;

import com.example.jjt_ssd.streetlamp.Tools.NotNull;
import com.example.jjt_ssd.streetlamp.Tools.RoundProgressBar;
import com.example.jjt_ssd.streetlamp.Tools.bean.LampStatus;
import com.example.jjt_ssd.streetlamp.mqtt.MQTTService;
import com.example.jjt_ssd.streetlamp.mqtt.mqttobserver.MqttObserver;
import com.google.gson.Gson;

//新能源界面
public class NewNergyActivity extends BaseActivity implements MqttObserver {

    //参数存储
    AppData app;

    private RoundProgressBar mRoundProgressBar1;//进度圆

    TextView electricity;//蓄电池电量
    TextView money;//节省的钱
    TextView batteryDay;//蓄电池供电天数
    TextView batteryTime;//蓄电池供电小时
    TextView electricDay;//市电供电天数
    TextView electricTime;//市电池供电天数
    private static final String TAG = "NewNergyActivity";
    //////////////////////传感器数据获取/////////////////////////////////
    public Handler handler = new Handler();
    private Runnable task = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            //对界面数据进行赋值
//            setPageValue();
            //设置延迟时间，此处是2分钟
            handler.postDelayed(this, 2 * 1000);
        }
    };
    private MQTTService service;
    ///////////////////////////////////////////////////////////////////////////////
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MQTTService.MyBinder myBinder = (MQTTService.MyBinder) iBinder;
            service = myBinder.getService();
            service.registerObserver(NewNergyActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected int getLoyoutId() {
        return R.layout.activity_new_nergy;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetupUI();
        // EventHandle();
    }

    //获取UI相关参数
    private void SetupUI() {
        setTitle("绿色新能源");
        electricity = (TextView) findViewById(R.id.NNElectricity);
        money = (TextView) findViewById(R.id.NNMoney);
        batteryDay = (TextView) findViewById(R.id.NNDate1);
        batteryTime = (TextView) findViewById(R.id.NNTime1);
        electricDay = (TextView) findViewById(R.id.NNDate2);
        electricTime = (TextView) findViewById(R.id.NNTime2);
        mRoundProgressBar1 = (RoundProgressBar) findViewById(R.id.roundProgressBar1);
        Bundle extras = getIntent().getExtras();
        String battery = extras.getString("battery");
        electricity.setText(battery);
        double aDouble = Double.valueOf(battery);
        mRoundProgressBar1.setProgress((int) aDouble);
        //获取数据初始设置
        app = (AppData) getApplication();

        //获取传感器数据
//        handler.post(task);
        Intent service = new Intent(getApplicationContext(), MQTTService.class);
        startService(service);
        bindService( service,conn,BIND_ABOVE_CLIENT);
    }

    private void setPageValue(LampStatus lampStatus) {
        LampStatus.DataBean data = lampStatus.getData();
        String battery = data.getBattery();
        electricity.setText(battery);
        double aDouble = Double.valueOf(battery);
        mRoundProgressBar1.setProgress((int) aDouble);
        money.setText("1314.45");
        batteryDay.setText(app.preferences.getString("BatteryDate", "0"));
        batteryTime.setText(app.preferences.getString("BatteryHour", "0"));
        electricDay.setText(app.preferences.getString("ElectricDate", "0"));
        electricTime.setText(app.preferences.getString("ElectricHour", "0"));
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
