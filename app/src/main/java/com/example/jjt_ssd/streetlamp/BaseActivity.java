package com.example.jjt_ssd.streetlamp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.jjt_ssd.streetlamp.Tools.TimeFormat;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
/**
 * 基类
 * Created by JJT-ssd on 2016/8/31.
 */
public abstract class BaseActivity extends AppCompatActivity {

    //返回按钮
    private Button bt_Back;
    //时期显示
    private TextView dateTimes;
    private String timeStr;
    //标题
    private TextView titleText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Init();
        super.onCreate(savedInstanceState);
        setContentView(getLoyoutId());
        setupView();
    }

    protected abstract int getLoyoutId();
    public void Init()
    {
        //注册刷新时间广播（系统默认一分钟发一次广播）
        registerReceiver(mTimeRefreshReceiver,new IntentFilter(Intent.ACTION_TIME_TICK));
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/Roboto-Regular.ttf").setFontAttrId(R.attr.fontPath).build());
    }

    public void setupView()
    {
        //获取当前时间
        timeStr = TimeFormat.StringData();
        dateTimes=(TextView)findViewById(R.id.baseTimes);
        dateTimes.setText(timeStr);

        titleText=(TextView)findViewById(R.id.baseTitle);

        bt_Back=(Button) findViewById(R.id.backButton);
        bt_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //设置标题
    public void setTitle(String titleString){
        titleText.setText(titleString);
    }

    private BroadcastReceiver mTimeRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                //获取当前时间
                timeStr = TimeFormat.StringData();
                dateTimes.setText (timeStr);
            }
        }
    };
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消注册
        unregisterReceiver(mTimeRefreshReceiver);
    }
}
