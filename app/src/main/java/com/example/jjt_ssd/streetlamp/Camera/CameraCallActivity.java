package com.example.jjt_ssd.streetlamp.Camera;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import com.example.jjt_ssd.streetlamp.AppData;
import com.example.jjt_ssd.streetlamp.MainActivity;
import com.example.jjt_ssd.streetlamp.R;
import com.example.jjt_ssd.streetlamp.Tools.TimeFormat;

import java.io.IOException;



public class CameraCallActivity extends AppCompatActivity {

    AppData app;
    // 获取手机分辨率
    DisplayMetrics dm;
    private TextView tv_Loading;
    private TextView tv_Times;
    private TextView tv_Address;
    private TextView tv_Coordinate;
    private SurfaceView sf_VideoMonitor;
    MediaPlayer mediaPlayer;

    boolean isOpenAlarm =false;

    private final StartRenderingReceiver receiver = new StartRenderingReceiver();
    CameraManager manager=new CameraManager();
    /**
     * 返回标记
     */
    private boolean backflag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_call);

        // 设置用于发广播的上下文
        manager.setContext(getApplicationContext());
        setupUI();
        eventHandle();

    }

    private void setupUI()
    {
        tv_Loading = (TextView) findViewById(R.id.CALL_Loading);
        sf_VideoMonitor = (SurfaceView) findViewById(R.id.CALL_VideoMonitor);
        tv_Times = (TextView) findViewById(R.id.CALLTimes);
        tv_Address = (TextView) findViewById(R.id.CALLAddress);
        tv_Coordinate = (TextView) findViewById(R.id.CALLCoordinate);
    }

    private void eventHandle()
    {
        //获取数据初始设置
        app=(AppData) getApplication();
        isOpenAlarm=app.preferences.getBoolean("isOpenAlarm",Boolean.FALSE);
        app.isOpenCamera=true;
        app.isOpenCall=false;
        tv_Address.setText(app.preferences.getString("callAddress",null));
        String longitude= app.preferences.getString("callLongitude",null);//经度
        String latitude= app.preferences.getString("callLatitude",null);//纬度
        tv_Coordinate.setText("经度"+longitude+"°"+","+"纬度"+latitude+"°");
        tv_Times.setText(TimeFormat.getStrTimes());
        IntentFilter filter = new IntentFilter(MainActivity.action);
        registerReceiver(broadcastReceiver, filter);

        setTitle("摄像头");
        // 获取手机分辨率
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        ViewGroup.LayoutParams lp = sf_VideoMonitor.getLayoutParams();
        //视频窗口尺寸
        lp.width = dm.widthPixels;
        lp.height = lp.width / 16 * 9;

        sf_VideoMonitor.setLayoutParams(lp);
        tv_Loading.setLayoutParams(lp);

        sf_VideoMonitor.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d("DEBUG", getLocalClassName() + " surfaceDestroyed");
                sf_VideoMonitor.destroyDrawingCache();
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d("DEBUG", getLocalClassName() + " surfaceCreated");
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
                Log.d("DEBUG", getLocalClassName() + " surfaceChanged");
            }
        });

        //开始预览
        startPlay();
        playAlarmSound();
    }

    private void playAlarmSound()
    {
        mediaPlayer = MediaPlayer.create(CameraCallActivity.this,R.raw.alarmsound);
        mediaPlayer.stop();
        mediaPlayer.setLooping(true);//循环播放
        if(isOpenAlarm)
        {
            try {
                mediaPlayer.prepare();
                mediaPlayer.start();
                app.isPlaying=true;
            } catch (IOException e) {}

        }
    }

    protected void  startPlay() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(manager.ACTION_START_RENDERING);
        filter.addAction(manager.ACTION_DVR_OUTLINE);
        registerReceiver(receiver, filter);

        tv_Loading.setVisibility(View.VISIBLE);
        tv_Loading.setText("正在连接摄像头……");

        if (backflag) {
            backflag = false;
            new Thread() {
                @Override
                public void run() {

                    manager.setSurfaceHolder( sf_VideoMonitor.getHolder());
                    manager.realPlay(2);
                }
            }.start();
        } else {
            new Thread() {
                @Override
                public void run() {
                    manager.setCameraDevice(app.device);
                    manager.setSurfaceHolder(sf_VideoMonitor.getHolder());
                    manager.initSDK();
                    manager.loginDevice();
                    manager.realPlay(2);
                }
            }.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new Thread() {
                @Override
                public void run() {
                    manager.stopPlay();
                }
            }.start();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer=null;
        unregisterReceiver(broadcastReceiver);
        new Thread() {
            @Override
            public void run() {
                manager.logoutDevice();
                manager.freeSDK();
                manager=null;
                Log.i("DEBUG", "CameraCallActivity释放成功！");
            }
        }.start();

    }

    // 广播接收器
    private class StartRenderingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (CameraManager.ACTION_START_RENDERING.equals(intent.getAction())) {
                tv_Loading.setVisibility(View.GONE);
            }
            if (CameraManager.ACTION_DVR_OUTLINE.equals(intent.getAction())) {
                tv_Loading.setText("无法连接，请确认摄像头是否开启。");
            }
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String  data=  intent.getExtras().getString("data");
            if (data.equals("stopCall"))
            {
                app.isOpenCamera=false;
                manager.gotoPreSetPoint(1);
                finish();
            }
        }
    };


}
