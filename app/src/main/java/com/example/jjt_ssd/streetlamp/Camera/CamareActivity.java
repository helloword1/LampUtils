package com.example.jjt_ssd.streetlamp.Camera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jjt_ssd.streetlamp.AppData;
import com.example.jjt_ssd.streetlamp.BaseActivity;
import com.example.jjt_ssd.streetlamp.R;


//摄像头页面
public class CamareActivity extends BaseActivity implements View.OnTouchListener,View.OnClickListener {

    AppData app;
    // 获取手机分辨率
    DisplayMetrics dm;

    private TextView tv_Loading;
    private SurfaceView sf_VideoMonitor;

    private Button btn_Up, btn_Down, btn_Left, btn_Right;
    private Button btn_ZoomIn, btn_ZoomOut;
    private Button btn_Finish;

    private Button bt_CameraMove;
    private Button bt_PWSure;
    private Button bt_PWCancel;
    private EditText et_PassWord;
    private RelativeLayout ralatLayout;


    private final StartRenderingReceiver receiver = new StartRenderingReceiver();
    CameraManager h1;
    /**
     * 返回标记
     */
    private boolean backflag;

    @Override
    protected int getLoyoutId() {
        return R.layout.activity_camare;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startPlay();
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        h1.logoutDevice();
        h1.freeSDK();
        Log.i("DEBUG", "CamareActivity释放成功！");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        h1=new CameraManager();
        // 设置用于发广播的上下文
        Log.e("ss", "ss");
        h1.setContext(getApplicationContext());
        setupUI();
        eventHandle();

    }

    private void setupUI()
    {

        //获取数据初始设置
        app=(AppData) getApplication();

        setTitle("摄像头");
        // 获取手机分辨率
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        bt_CameraMove=(Button)findViewById(R.id.CAMRomve);
        bt_CameraMove.setOnClickListener(this);

        tv_Loading = (TextView) findViewById(R.id.tv_Loading);
        sf_VideoMonitor = (SurfaceView) findViewById(R.id.sf_VideoMonitor);

        btn_Up = (Button) findViewById(R.id.btn_Up);
        btn_Up.setOnTouchListener(this);

        btn_Left = (Button) findViewById(R.id.btn_Left);
        btn_Left.setOnTouchListener(this);

        btn_Right = (Button) findViewById(R.id.btn_Right);
        btn_Right.setOnTouchListener(this);

        btn_Down = (Button) findViewById(R.id.btn_Down);
        btn_Down.setOnTouchListener(this);

        btn_ZoomIn = (Button) findViewById(R.id.btn_ZoomIn);
        btn_ZoomIn.setOnTouchListener(this);

        btn_ZoomOut = (Button) findViewById(R.id.btn_ZoomOut);
        btn_ZoomOut.setOnTouchListener(this);

        btn_Finish = (Button) findViewById(R.id.btn_Finish);
        btn_Finish.setOnClickListener(this);

       ralatLayout=(RelativeLayout)findViewById(R.id.CAMlinear);
    }

    private void eventHandle()
    {
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
    }


    protected void  startPlay() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(h1.ACTION_START_RENDERING);
        filter.addAction(h1.ACTION_DVR_OUTLINE);
        registerReceiver(receiver, filter);

        tv_Loading.setVisibility(View.VISIBLE);
        tv_Loading.setText("正在连接摄像头……");

        if (backflag) {
            backflag = false;
            new Thread() {
                @Override
                public void run() {

                    h1.setSurfaceHolder( sf_VideoMonitor.getHolder());
                    h1.realPlay(1);
                }
            }.start();
        } else {
            new Thread() {
                @Override
                public void run() {
                    h1.setCameraDevice(app.device);
                    h1.setSurfaceHolder(sf_VideoMonitor.getHolder());
                    h1.initSDK();
                    h1.loginDevice();
                    h1.realPlay(1);
                }
            }.start();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new Thread() {
                @Override
                public void run() {
                    h1.stopPlay();
                }
            }.start();
        }
        return super.onKeyDown(keyCode, event);
    }

    //修改密码UI参数
    private void PasswordPage( LinearLayout linearLayout)
    {
        //Password Page
        et_PassWord  =(EditText)linearLayout.findViewById(R.id.INPUTPPS);
        bt_PWSure=(Button) linearLayout.findViewById(R.id.INPUTSure);
        bt_PWCancel=(Button)linearLayout.findViewById(R.id.INPUTCancel);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.CAMRomve:
                LinearLayout linearLayout=(LinearLayout)getLayoutInflater().inflate(R.layout.inputpassword,null);
                PasswordPage(linearLayout);
                final AlertDialog dialog = getDialongView(linearLayout);
                bt_PWSure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //点击确定按钮校验密码
                       String passWord = app.preferences.getString("passWord",null);
                        if (passWord.equals(et_PassWord.getText().toString()))
                        {
                            dialog.dismiss();
                            ralatLayout.setVisibility(View.VISIBLE);
                            bt_CameraMove.setVisibility(View.INVISIBLE);
                        }else
                        {
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
                break;
            case R.id.btn_Finish:
                ralatLayout.setVisibility(View.INVISIBLE);
                bt_CameraMove.setVisibility(View.VISIBLE);
                break;
        }
    }

    private AlertDialog getDialongView(View view) {
        final AlertDialog.Builder builder6 = new AlertDialog.Builder(CamareActivity.this);
        builder6.setView(view);
        builder6.create();
        AlertDialog dialog = builder6.show();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        return dialog;
    }
    public boolean onTouch(final View v, final MotionEvent event) {

        new Thread() {
            @Override
            public void run() {

                switch (v.getId()) {
                    case R.id.btn_Up:
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            h1.startMove(8);
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            h1.stopMove(8);
                        }
                        break;
                    case R.id.btn_Left:
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            h1.startMove(4);
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            h1.stopMove(4);
                        }
                        break;
                    case R.id.btn_Right:
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            h1.startMove(6);
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            h1.stopMove(6);
                        }
                        break;
                    case R.id.btn_Down:
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            h1.startMove(2);
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            h1.stopMove(2);
                        }
                        break;
                    case R.id.btn_ZoomIn:
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            h1.startZoom(1);
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            h1.stopZoom(1);
                        }
                        break;
                    case R.id.btn_ZoomOut:
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            h1.startZoom(-1);
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            h1.stopZoom(-1);
                        }
                        break;
                    default:
                        break;
                }
            }
        }.start();
        return false;
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
}
