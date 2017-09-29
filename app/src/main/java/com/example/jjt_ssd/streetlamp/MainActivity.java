package com.example.jjt_ssd.streetlamp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jjt_ssd.streetlamp.Camera.CamareActivity;
import com.example.jjt_ssd.streetlamp.SettingPage.SettingActivity;
import com.example.jjt_ssd.streetlamp.Tools.HudHelper;
import com.example.jjt_ssd.streetlamp.Tools.NotNull;
import com.example.jjt_ssd.streetlamp.Tools.StringUtils;
import com.example.jjt_ssd.streetlamp.Tools.TimeFormat;
import com.example.jjt_ssd.streetlamp.Tools.bean.LampStatus;
import com.example.jjt_ssd.streetlamp.mqtt.MQTTService;
import com.example.jjt_ssd.streetlamp.mqtt.mqttobserver.MqttObserver;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

//主界面
public class MainActivity extends AppCompatActivity implements MqttObserver {

    //参数存储
    AppData app;
    ///////////////////////////////////
    public MQTTService mqttService;
    public static final String action = "jason.broadcast.action";

    //主页跳转环境监测
    private Button bt_EM;
    //主页跳转摄像头
    private Button bt_Camera;
    //主页跳转客户介绍
    private Button bt_Customer;
    //主页跳转周边地图
    private Button bt_MAP;
    //主页跳转绿色新能源
    private Button bt_GreenEnergy;
    //主页跳转介绍
    private Button bt_Introduce;
    //页面跳转设置
    private Button bt_Setting;

    private Button bt_PWSure;
    private Button bt_PWCancel;
    private EditText et_PassWord;

    private TextView tv_Text;
    private String timeStr;

    private TextView tv_TemperatureText;//温度值
    private TextView tv_HumidityText;//湿度值
    private TextView tv_WindPowerText;//风力值
    private TextView tv_PMText;//颗粒密度值

    private TextView tv_CusZH;//客户介绍中文
    private TextView tv_CusEN;//客户介绍英文

    private String passWord;

    //////////////////////传感器数据获取/////////////////////////////////
    public Handler handler = new Handler();

    //协议
    char[] agreementThree = {(char) 0x01, (char) 0x04, (char) 0x00, (char) 0x00, (char) 0x00, (char) 0x06, (char) 0x70, (char) 0x08};
    char[] agreementWind = {(char) 0x02, (char) 0x03, (char) 0x06, (char) 0x00, (char) 0x00, (char) 0x02, (char) 0xC4, (char) 0xB0};
    char[] agreementNoise = {(char) 0x0A, (char) 0x04, (char) 0x00, (char) 0x00, (char) 0x00, (char) 0x02, (char) 0x70, (char) 0xB0};
    char[] agreementLamp = {(char) 0x7e, (char) 0x04, (char) 0x06, (char) 0x01, (char) 0x00, (char) 0x7f};

    private String temperature;
    private String humidity;
    private String windPower;
    private String noise;
    private String pm;
    private int poerModes;
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            //对界面数据进行赋值
            tv_TemperatureText.setText(temperature);
            tv_HumidityText.setText(humidity);
            tv_WindPowerText.setText(windPower);
            tv_PMText.setText(pm);
            //设置延迟时间，此处是1s
            handler.postDelayed(this, 1 * 1000);
        }
    };
    private HudHelper hudHelper;
    private String in_temp;
    private String hum;
    private String speed;
    private String pm25;
    private String noise1;
    private String battery;

    @Override
    public void update(String succeedStr) {
        try {
            LampStatus lampStatus = new Gson().fromJson(succeedStr, LampStatus.class);
            if (NotNull.isNotNull(lampStatus)) {
                initUI(lampStatus);
            }
        } catch (Exception ex) {

        }

    }

    ///////////监测IO输入///////////////////
    private class CallThread extends AsyncTask<Void, Integer, Integer> {
        Intent intent = new Intent();
        Process process = null;
        DataOutputStream dos = null;
        DataInputStream dis = null;

        CallThread() {
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {
                process = Runtime.getRuntime().exec("su");
                dos = new DataOutputStream(process.getOutputStream());
                dos.writeBytes("echo 177 > /sys/class/gpio/export" + "\n");
                dos.flush();
                //设置引脚功能为输出
                //dos.writeBytes("echo out > /sys/class/gpio/gpio177/direction" + "\n");
                //设置引脚功能为输入
                dos.writeBytes("echo in > /sys/class/gpio/gpio177/direction" + "\n");
                dos.flush();
                dos.close();
                while (true) {
                    try {
                        process = Runtime.getRuntime().exec("su");
                        dis = new DataInputStream(process.getInputStream());
                        dos = new DataOutputStream(process.getOutputStream());
                        dos.writeBytes("cd /sys/class/gpio/gpio177" + "\n");
                        dos.flush();
                        dos.writeBytes("cat value" + "\n");
                        int state = dis.read();
                        publishProgress(state);
                        dos.flush();
                        dis.close();
                        //设置延迟时间，此处是1秒
                        SystemClock.sleep(1000);
                    } catch (Exception e) {
                    }
                }
            } catch (IOException e) {
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int reValue = values[0];
            if (reValue == 48) {
                Intent intent = new Intent(action);
                intent.putExtra("data", "stopCall");
                sendBroadcast(intent);
            }
            if (reValue == 49 && app.isOpenCall == false && app.isOpenCamera == false) {
                intent.setClass(MainActivity.this, OneKeyCallActivity.class);
                startActivity(intent);
            }
        }
    }

//////////发送传感器协议///////////////////
//    boolean isSend=false;
//    private class AsynWork extends AsyncTask<Void, Integer, Integer>{
//        AsynWork(){}
//        @Override
//        protected Integer doInBackground(Void... params) {
//            // TODO Auto-generated method stub
//
//            while(true){
//                //发送风速协议
//                app.portHelper.onDataSend(agreementWind);
//                //设置延迟时间，此处是100毫秒
//                SystemClock.sleep(100);
//                //发送三合一协议
//                app.portHelper.onDataSend(agreementThree);
//                //设置延迟时间，此处是100毫秒
//                 SystemClock.sleep(100);
//                //发送噪声协议
//                app.portHelper.onDataSend(agreementNoise);
//                //设置延迟时间，此处是100毫秒
//                SystemClock.sleep(100);
//                //发送照明灯协议
//                app.portHelper.onDataSend(agreementLamp);
//
//                temperature = app.portHelper.getTemperature();
//                humidity = app.portHelper.getHumidity();
//                windPower = app.portHelper.getWindPower();
//                noise = app.portHelper.getNoise();
//                pm = app.portHelper.getPm();
//
//                poerModes=app.portHelper.getPowerMode();
//
//                //数据保存操作
//                app.editor.putString("Temperature", temperature);
//                app.editor.putString("Humidity", humidity);
//                app.editor.putString("WindPower", windPower);
//                app.editor.putString("Noise", noise);
//                app.editor.putString("Pm", pm);
//                if(poerModes==1)
//                    app.editor.putInt("powerMode",1);
//                if(poerModes==2)
//                    app.editor.putInt("powerMode",2);
//                if(poerModes==3)
//                    app.editor.putInt("powerMode",3);
//
//                app.editor.putString("Electricity",app.portHelper.getElectricity());
//                app.editor.putString("BatteryDate",app.portHelper.getBatteryDate());
//                app.editor.putString("BatteryHour",app.portHelper.getBatteryHour());
//                app.editor.putString("ElectricDate",app.portHelper.getElectricDate());
//                app.editor.putString("ElectricHour",app.portHelper.getElectricHour());
//                app.editor.commit();
//                //设置延迟时间，此处是1分钟
//                SystemClock.sleep(5*1000);
//
//            }
//        }
//}
///////////////////////////////////////////////////////////////////////////////

    private BroadcastReceiver mTimeRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                timeStr = TimeFormat.StringData();
                tv_Text.setText(timeStr);
            }
        }
    };
    private static final String TAG = "MainActivity";
    private MQTTService service;
    ServiceConnection conn = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MQTTService.MyBinder myBinder = (MQTTService.MyBinder) iBinder;
            service = myBinder.getService();
            service.registerObserver(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Init();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();
        EnventHandle();
    }

    //刷新界面
    @Override
    protected void onStart() {
        super.onStart();
        tv_CusZH.setText(app.preferences.getString("cusZHName", null));
        tv_CusEN.setText(app.preferences.getString("cusENName", null));
    }

    private void Init() {
        //获取数据初始设置
        app = (AppData) getApplication();
        //注册刷新时间广播（系统默认一分钟发一次广播）
        registerReceiver(mTimeRefreshReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/Roboto-Regular.ttf").setFontAttrId(R.attr.fontPath).build());

    }

    //UI控件获取
    private void setupUI() {
        //获取当前时间
        timeStr = TimeFormat.StringData();
        bt_EM = (Button) findViewById(R.id.EnvironmentalMonitoring);
        bt_Camera = (Button) findViewById(R.id.Camera);
        bt_Customer = (Button) findViewById(R.id.Customer);
        bt_MAP = (Button) findViewById(R.id.MAP);
        bt_GreenEnergy = (Button) findViewById(R.id.GreenEnergy);
        bt_Introduce = (Button) findViewById(R.id.Introduce);
        bt_Setting = (Button) findViewById(R.id.MAINSetting);
        tv_Text = (TextView) findViewById(R.id.DTimes);
        tv_Text.setText(timeStr);

        tv_TemperatureText = (TextView) findViewById(R.id.TemperatureText);
        tv_HumidityText = (TextView) findViewById(R.id.HumidityText);
        tv_WindPowerText = (TextView) findViewById(R.id.WindPowerText);
        tv_PMText = (TextView) findViewById(R.id.PM2_5a);

        tv_CusZH = (TextView) findViewById(R.id.MAINCusZH);
        tv_CusEN = (TextView) findViewById(R.id.MAINCusEN);

        //获取传感器数据
//        handler.post(task);

        //实例化该任务，调用方法启动
//        AsynWork task=new AsynWork();
//        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        //设置一键呼叫
        CallThread call = new CallThread();
//        call.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        //设置初始用户信息
        tv_CusZH.setText(app.preferences.getString("cusZHName", null));
        tv_CusEN.setText(app.preferences.getString("cusENName", null));
        passWord = app.preferences.getString("passWord", "goockr");
        if (!NotNull.isNotNull(hudHelper))
            hudHelper = new HudHelper();
        hudHelper.hudShow(this, "正在加载...");
        //开启MQTT通讯服务
        Intent service = new Intent(getApplicationContext(), MQTTService.class);
        startService(service);
        bindService(service, conn, BIND_ABOVE_CLIENT);
    }


    private void initUI(LampStatus lampStatus) {
        hudHelper.hudHide();
        LampStatus.DataBean data = lampStatus.getData();
        in_temp = data.getIn_temp();
        if (NotNull.isNotNull(in_temp))
            tv_TemperatureText.setText(StringUtils.doubleFormat(in_temp));
        else
            tv_TemperatureText.setText("0");
        hum = data.getHum();
        tv_HumidityText.setText(StringUtils.doubleFormat(hum));
        speed = data.getSpeed();
        tv_WindPowerText.setText(StringUtils.doubleFormat(speed));
        pm25 = data.getPm25();
        noise = data.getNoise();
        battery = data.getBattery();
        tv_PMText.setText(StringUtils.doubleFormat(StringUtils.doubleFormat(pm25)));

    }

    //修改密码UI参数
    private void PasswordPage(View linearLayout) {
        //Pard Page
        et_PassWord = (EditText) linearLayout.findViewById(R.id.INPUTPPS);
        bt_PWSure = (Button) linearLayout.findViewById(R.id.INPUTSure);
        bt_PWCancel = (Button) linearLayout.findViewById(R.id.INPUTCancel);
    }

    //UI控件的设置与响应事件
    private void EnventHandle() {

        bt_EM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //环境页面跳转
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("in_temp", in_temp);
                bundle.putString("hum", hum);
                bundle.putString("speed", speed);
                bundle.putString("pm25", pm25);
                bundle.putString("noise", noise);
                intent.putExtras(bundle);
                intent.setClass(MainActivity.this, EMActivity.class);
                startActivity(intent);
            }
        });
        bt_Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //摄像头页面跳转
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, CamareActivity.class);
                startActivity(intent);

            }
        });
        bt_Customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //果壳科技页面跳转
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, CMIntroduceActivity.class);
                startActivity(intent);
            }
        });
        bt_MAP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //地图页面跳转
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
        bt_GreenEnergy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //绿色能源页面跳转
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("battery", battery);
                intent.putExtras(bundle);
                intent.setClass(MainActivity.this, NewNergyActivity.class);
                startActivity(intent);
            }
        });
        bt_Introduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //页面跳转
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ZRIntroduceActivity.class);
                startActivity(intent);
            }
        });

        bt_Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = getLayoutInflater().inflate(R.layout.inputpassword, null);
                PasswordPage(view);
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(view);
                builder.create();
                final AlertDialog dialog = builder.show();
                Window window = dialog.getWindow();
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.gravity = Gravity.CENTER_VERTICAL;
                window.setAttributes(lp);

                bt_PWSure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //点击确定按钮校验密码
                        if (passWord.equals(et_PassWord.getText().toString())) {
                        dialog.dismiss();
                        //页面跳转
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, SettingActivity.class);
                        startActivity(intent);

                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "密码错误，请重新输入!", Toast.LENGTH_SHORT);
                            toast.getView().getBackground().setAlpha(200);//设置透明度
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                });
                bt_PWCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //点击取消按钮什么都不做
                        dialog.dismiss();
                    }
                });

            }
        });
    }


    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hudHelper.hudHide();
        //取消注册
        unregisterReceiver(mTimeRefreshReceiver);
        //退出循环
        handler.removeCallbacks(task);
    }


}
