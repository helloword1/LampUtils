package com.example.jjt_ssd.streetlamp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Chronometer;
import android.widget.RelativeLayout;

import com.example.jjt_ssd.streetlamp.Camera.CameraCallActivity;


public class OneKeyCallActivity extends Activity {
    AppData app;
    RelativeLayout relativeLayout;
    Chronometer cm_Times;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_key_call);
        setupUI();
        eventHandle();
    }

    private void setupUI()
    {
        relativeLayout=(RelativeLayout)findViewById(R.id.OKCBackgroud);
        relativeLayout.getBackground().setAlpha(220);
        cm_Times =(Chronometer)findViewById(R.id.OKCTimes);
    }

    private void eventHandle()
    {
        IntentFilter filter = new IntentFilter(MainActivity.action);
        registerReceiver(broadcastReceiver, filter);
        //获取数据初始设置
        app=(AppData) getApplication();
        app.isOpenCall=true;

        cm_Times.setBase(SystemClock.elapsedRealtime());
        cm_Times.start();
        cm_Times.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if(SystemClock.elapsedRealtime()- cm_Times.getBase()>8*1000)
                {
                    cm_Times.stop();
                    if (app.isOpenCamera==false)
                    {
                        //页面跳转
                        Intent intent = new Intent();
                        intent.setClass(OneKeyCallActivity.this,CameraCallActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          String  data=  intent.getExtras().getString("data");
            if (data.equals("stopCall"))
            {
                cm_Times.stop();
                app.isOpenCall=false;
                finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
