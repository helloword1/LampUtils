package com.example.jjt_ssd.streetlamp.SettingPage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jjt_ssd.streetlamp.AppData;
import com.example.jjt_ssd.streetlamp.BaseActivity;
import com.example.jjt_ssd.streetlamp.R;
import com.example.jjt_ssd.streetlamp.Tools.GetExternalDevice;
import com.example.jjt_ssd.streetlamp.Tools.WifiHelper;


//设置主界面
public class SettingActivity extends BaseActivity {
    //参数存储
    AppData app;

    //参数设置
    ListView listOne;
    BaseAdapter listOneAdapter;

    ListView listTwo;
    BaseAdapter listTwoAdapter;

    //设置网络界面参数
    TextView tv_NetWorkIP;
    TextView tv_NetWorkGateway;
    TextView tv_NetWorkMAC;

    //设置密码界面参数
    TextView tv_REPWTextOld;
    TextView tv_REPWTextNew;
    TextView tv_REPWTextCon;
    EditText et_REPWOldPS;
    EditText et_REPWNewPS;
    EditText et_REPWConfirmPS;
    TextView tv_REPWTip;
    Button btn_RSPWSure;
    Button btn_RSPWCamcel;

    //设置音量界面参数
    SeekBar sb_VolBar;
    Button bt_VolSure;
    Button bt_VolCancel;

    //设置供电模式界面
    RadioButton rb_POWAuto;
    RadioButton rb_POW220V;
    RadioButton rb_POWBattery;
    Button btn_POWSure;
    Button btn_POWCamcel;

    //设置报警界面参数
    Button btn_ALASure;
    Button btn_ALACamcel;
    EditText et_ALAtextAdd;//地址
    EditText et_ALALongitudeNum;//经度
    EditText et_ALALatitudeNum;//纬度
    TextView tv_ALAtipAdd;
    TextView tv_ALAtipCoor;
    TextView tv_ALAtipLongitude;
    TextView tv_ALAtipLatitude;

    //设置客户信息界面
    Button btn_CUSSure;
    Button btn_CUSCamcel;
    EditText et_CUStextZH;//客户中文信息
    EditText et_CUStextEN;//客胡英文信息
    TextView tv_CUStipZH;
    TextView tv_CUStipEN;
    //报警语音开关
    boolean isOpenAlarm = false;

    String[] listOneDataArr = {"网络", "修改密码", "储存设备", "音量", "供电模式", "报警地址与报警坐标", "修改客户信息"};
    String[] listTwoDataArr = {"广播报警语音"};

    @Override
    protected int getLoyoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //1.初始化全局UI
        setupUI();
        //2.初始化listOne UI
        listOneAdapater();
        //3.初始化listTwo UI
        listTwoAdapater();
    }

    private void setupUI() {
        app = (AppData) getApplication();
        isOpenAlarm = app.preferences.getBoolean("isOpenAlarm", Boolean.FALSE);
        setTitle("设置");
        listOne = (ListView) findViewById(R.id.ListOne);
        listTwo = (ListView) findViewById(R.id.ListTwo);
    }

    //网络UI参数
    private void netWorkPageSetting(LinearLayout linearLayout) {
        //NetWork Page
        tv_NetWorkIP = (TextView) linearLayout.findViewById(R.id.NETWorkIP);
        tv_NetWorkGateway = (TextView) linearLayout.findViewById(R.id.NETWorkGateway);
        tv_NetWorkMAC = (TextView) linearLayout.findViewById(R.id.NETWorkMAC);

        @SuppressLint("WifiManagerLeak") WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        String IP = WifiHelper.getIP(wifiManager);
        String gateWay = WifiHelper.getGateway(wifiManager);
        //获取路由器的MAC地址
        String MAC = WifiHelper.getMac(wifiManager);
        //获取本机的MAC地址
        //WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        //String MAC  = wifiInfo.getMacAddress();
        tv_NetWorkIP.setText("IP:" + IP);
        tv_NetWorkGateway.setText("网关:" + gateWay);
        tv_NetWorkMAC.setText("MAC:" + MAC);
    }

    //修改密码UI参数
    private void rSPasswordPageSetting(LinearLayout linearLayout) {
        //RSPassword Page
        et_REPWOldPS = (EditText) linearLayout.findViewById(R.id.RSOldPS);
        et_REPWNewPS = (EditText) linearLayout.findViewById(R.id.RSNewPS);
        et_REPWConfirmPS = (EditText) linearLayout.findViewById(R.id.RSConfirm);
        tv_REPWTip = (TextView) linearLayout.findViewById(R.id.RSTip);
        tv_REPWTextOld = (TextView) linearLayout.findViewById(R.id.RStextOldPS);
        tv_REPWTextNew = (TextView) linearLayout.findViewById(R.id.RStextNewPS);
        tv_REPWTextCon = (TextView) linearLayout.findViewById(R.id.RStextConPS);

        tv_REPWTip.setVisibility(View.INVISIBLE);

        et_REPWOldPS.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    tv_REPWTextOld.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.popup_inputbox2));

                } else {
                    // 此处为失去焦点时的处理内容
                    tv_REPWTextOld.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.popup_inputbox1));
                }
            }
        });

        et_REPWNewPS.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    tv_REPWTextNew.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.popup_inputbox2));

                } else {
                    // 此处为失去焦点时的处理内容
                    tv_REPWTextNew.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.popup_inputbox1));
                }
            }
        });

        et_REPWConfirmPS.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    tv_REPWTextCon.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.popup_inputbox2));

                } else {
                    // 此处为失去焦点时的处理内容
                    tv_REPWTextCon.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.popup_inputbox1));
                }
            }
        });

        btn_RSPWSure = (Button) linearLayout.findViewById(R.id.RSSure);
        btn_RSPWCamcel = (Button) linearLayout.findViewById(R.id.RSCancel);
    }

    //存储设备UI参数
    private void storageDevicePageSetting(LinearLayout linearLayout) {

    }

    //音量UI参数
    private void volumePageSetting(LinearLayout linearLayout) {
        //Volume Page
        //获取音量条
        sb_VolBar = (SeekBar) linearLayout.findViewById(R.id.VOLLine);
        //设置音量条的最大值（根据系统音量的最大决定）
        sb_VolBar.setMax(15);
        int volumeValue = app.preferences.getInt("volume", 7);
        sb_VolBar.setProgress(volumeValue);
        bt_VolSure = (Button) linearLayout.findViewById(R.id.VOLSure);
        bt_VolCancel = (Button) linearLayout.findViewById(R.id.VOLCancel);
    }

    //供电模式UI参数
    private void powerPageSetting(LinearLayout linearLayout) {
        //Power Page
        btn_POWSure = (Button) linearLayout.findViewById(R.id.POWSure);
        btn_POWCamcel = (Button) linearLayout.findViewById(R.id.POWCancel);
        rb_POWAuto = (RadioButton) linearLayout.findViewById(R.id.POWRadio1);
        rb_POW220V = (RadioButton) linearLayout.findViewById(R.id.POWRadio2);
        rb_POWBattery = (RadioButton) linearLayout.findViewById(R.id.POWRadio3);
        int powerMode = app.preferences.getInt("powerMode", 0);

        switch (powerMode) {
            case 2:
                rb_POW220V.setChecked(true);
                break;
            case 3:
                rb_POWBattery.setChecked(true);
                break;
            default:
                rb_POWAuto.setChecked(true);
                break;
        }


    }

    //报警地址与报警坐标UI参数
    private void alarmPageSetting(LinearLayout linearLayout) {
        //Alarm Page
        btn_ALASure = (Button) linearLayout.findViewById(R.id.ALASure);
        btn_ALACamcel = (Button) linearLayout.findViewById(R.id.ALACancel);
        et_ALAtextAdd = (EditText) linearLayout.findViewById(R.id.ALAtextAdd);
        et_ALALongitudeNum = (EditText) linearLayout.findViewById(R.id.ALALongitudeNum);
        et_ALALatitudeNum = (EditText) linearLayout.findViewById(R.id.ALALatitudeNum);
        tv_ALAtipAdd = (TextView) linearLayout.findViewById(R.id.ALAtipAdd);
        tv_ALAtipCoor = (TextView) linearLayout.findViewById(R.id.ALAtipCoor);
        tv_ALAtipLongitude = (TextView) linearLayout.findViewById(R.id.ALAtipLongitude);
        tv_ALAtipLatitude = (TextView) linearLayout.findViewById(R.id.ALAtipLatitude);
        et_ALAtextAdd.setText(app.preferences.getString("callAddress", null));
        et_ALALongitudeNum.setText(app.preferences.getString("callLongitude", null));
        et_ALALatitudeNum.setText(app.preferences.getString("callLatitude", null));

        et_ALAtextAdd.requestFocus();
        if (et_ALAtextAdd.getText().length() > 0) {
            tv_ALAtipAdd.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.popup_inputbox2));
        }
        et_ALAtextAdd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    tv_ALAtipAdd.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.popup_inputbox2));

                } else {
                    // 此处为失去焦点时的处理内容
                    tv_ALAtipAdd.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.popup_inputbox1));
                }
            }
        });
        et_ALALongitudeNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    tv_ALAtipCoor.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.popup_inputbox2));
                    tv_ALAtipLongitude.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.popup_inputbox2));

                } else {
                    // 此处为失去焦点时的处理内容
                    tv_ALAtipCoor.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.popup_inputbox1));
                    tv_ALAtipLongitude.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.popup_inputbox1));
                }
            }
        });
        et_ALALatitudeNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    tv_ALAtipLatitude.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.popup_inputbox2));

                } else {
                    // 此处为失去焦点时的处理内容
                    tv_ALAtipLatitude.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.popup_inputbox1));
                }
            }
        });

    }

    //修改客户信息UI参数
    private void customerPageSetting(LinearLayout linearLayout) {
        //Customer Page
        btn_CUSSure = (Button) linearLayout.findViewById(R.id.CUSSure);
        btn_CUSCamcel = (Button) linearLayout.findViewById(R.id.CUSCancel);
        et_CUStextZH = (EditText) linearLayout.findViewById(R.id.CUStextZH);
        et_CUStextEN = (EditText) linearLayout.findViewById(R.id.CUStextEN);
        tv_CUStipZH = (TextView) linearLayout.findViewById(R.id.CUStipZH);
        tv_CUStipEN = (TextView) linearLayout.findViewById(R.id.CUStipEN);

        et_CUStextZH.setText(app.preferences.getString("cusZHName", null));
        et_CUStextEN.setText(app.preferences.getString("cusENName", null));
        et_CUStextZH.requestFocus();
        if (et_CUStextZH.getText().length() > 0) {
            tv_CUStipZH.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.popup_inputbox2));
        }

        et_CUStextZH.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    tv_CUStipZH.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.popup_inputbox2));

                } else {
                    // 此处为失去焦点时的处理内容
                    tv_CUStipZH.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.popup_inputbox1));
                }
            }
        });
        et_CUStextEN.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    tv_CUStipEN.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.popup_inputbox2));
                } else {
                    // 此处为失去焦点时的处理内容
                    tv_CUStipEN.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.popup_inputbox1));
                }
            }
        });
    }

    //初始化listOne适配器
    private void listOneAdapater() {
        listOneAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return listOneDataArr.length;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            //初始化界面
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                RelativeLayout relat = new RelativeLayout(SettingActivity.this);
                relat.setMinimumHeight(100);
                TextView nameText = new TextView(SettingActivity.this);
                nameText.setTextColor(Color.parseColor("#ffffff"));
                ImageView backImage = new ImageView(SettingActivity.this);

                TextView textLine = new TextView(SettingActivity.this);
                textLine.setBackgroundColor(Color.parseColor("#4e565b"));
                textLine.setHeight(1);

                RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 35);
                lp1.setMargins(35, 0, 0, 0);
                lp1.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                relat.addView(nameText, lp1);

                RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 45);
                lp2.setMargins(0, 0, 45, 0);
                lp2.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                relat.addView(backImage, lp2);

                RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                lp3.setMargins(35, 100, 45, 0);
                lp3.addRule(RelativeLayout.BELOW, RelativeLayout.TRUE);
                relat.addView(textLine, lp3);

                nameText.setText(listOneDataArr[position]);
                backImage.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.mipmap.setting_goin));
                return relat;
            }
        };
        listOne.setAdapter(listOneAdapter);

        //ListViewOne前面7个Cell的点击事件逻辑处理
        listOne.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://网络设置
                        LinearLayout linearLayout0 = (LinearLayout) getLayoutInflater().inflate(R.layout.networkalert, null);
                        netWorkPageSetting(linearLayout0);
                        AlertDialog dialog = getDialongView(linearLayout0);
                        setWindowCenter(dialog);
                        break;
                    case 1://修改密码
                        LinearLayout linearLayout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.rspassword, null);
                        rSPasswordPageSetting(linearLayout1);
                        final AlertDialog dialog1 = getDialongView(linearLayout1);
                        btn_RSPWSure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (et_REPWOldPS.getText().length() <= 0 || et_REPWNewPS.getText().length() <= 0 || et_REPWConfirmPS.getText().length() <= 0) {
                                    tv_REPWTip.setText("输入不能为空！");
                                    tv_REPWTip.setVisibility(View.VISIBLE);
                                }
                                //校验旧密码
                                if (!et_REPWOldPS.getText().toString().equals(app.preferences.getString("passWord", null))) {
                                    tv_REPWTip.setText("旧密码输入错误！");
                                    tv_REPWTip.setVisibility(View.VISIBLE);
                                    et_REPWOldPS.setText("");
                                    et_REPWOldPS.requestFocus();
                                    return;
                                }
                                //新密码与确认密码是否一致
                                if (!et_REPWNewPS.getText().toString().equals(et_REPWConfirmPS.getText().toString())) {
                                    tv_REPWTip.setText("新密码与确认密码不一致！");
                                    tv_REPWTip.setVisibility(View.VISIBLE);
                                    et_REPWConfirmPS.setText("");
                                    et_REPWNewPS.setText("");
                                    et_REPWNewPS.requestFocus();
                                } else {
                                    dialog1.dismiss();
                                    app.editor.putString("passWord", et_REPWNewPS.getText().toString());
                                    app.editor.commit();
                                    Toast toast = Toast.makeText(getApplicationContext(),
                                            "密码修改成功", Toast.LENGTH_SHORT);
                                    toast.getView().getBackground().setAlpha(200);//设置透明度
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            }

                        });
                        btn_RSPWCamcel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //点击取消按钮什么都不做
                                dialog1.dismiss();
                            }
                        });
                        setWindowCenter(dialog1);
                        break;
                    case 2://存储设备
                        //没有插入U盘时，弹提示框
                        if (GetExternalDevice.getExtUSBPath().size() <= 0) {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "请接入存储设备(U盘)", Toast.LENGTH_SHORT);
                            toast.getView().getBackground().setAlpha(200);//设置透明度
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        } else//插入U盘时的处理
                        {
                            //页面跳转
                            Intent intent = new Intent();
                            intent.setClass(SettingActivity.this, ExternalDeviceActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case 3://音量
                        LinearLayout linearLayout3 = (LinearLayout) getLayoutInflater().inflate(R.layout.setvolume, null);
                        volumePageSetting(linearLayout3);
                        final AlertDialog dialog3 = getDialongView(linearLayout3);
                        bt_VolSure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //点击确定按钮保存修改的音量
                                //获取进度条的值
                                int progress = sb_VolBar.getProgress();
                                //设置系统音量值
                                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_PLAY_SOUND);
                                progress = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                                //存储当前的音量值
                                app.editor.putInt("volume", progress);
                                app.editor.commit();
                                dialog3.dismiss();
                            }
                        });
                        bt_VolCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //点击取消按钮什么都不做
                                dialog3.dismiss();
                            }
                        });
                        setWindowCenter(dialog3);
                        break;
                    case 4://供电模式
                        LinearLayout linearLayout4 = (LinearLayout) getLayoutInflater().inflate(R.layout.setpowermode, null);
                        powerPageSetting(linearLayout4);
                        final AlertDialog dialog4 = getDialongView(linearLayout4);
                        btn_POWSure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int temp = 0;
                                //点击确定按钮保存修改的供电模式
                                if (rb_POWAuto.isChecked()) {
                                    temp = 17;
                                    char[] autoMode = {0x7D, 0x03, 0x01, 0x70, 0x7F};
//                                    app.portHelper.onDataSend(autoMode);
                                }
                                if (rb_POW220V.isChecked()) {
                                    temp = 18;
                                    char[] elecMode = {0x7D, 0x03, 0x02, 0x70, 0x7F};
//                                    app.portHelper.onDataSend(elecMode);
                                }
                                if (rb_POWBattery.isChecked()) {
                                    temp = 19;
                                    char[] battMode = {0x7D, 0x03, 0x03, 0x70, 0x7F};
//                                    app.portHelper.onDataSend(battMode);
                                }
                                SystemClock.sleep(1000);
                                int powerMode = 1/*app.portHelper.getPowerModeChange()*/;
                                if (powerMode == temp) {
                                    Toast toast = Toast.makeText(getApplicationContext(),
                                            "修改成功！", Toast.LENGTH_SHORT);
                                    toast.getView().getBackground().setAlpha(200);//设置透明度
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    switch (powerMode) {
                                        case 17:
                                            app.editor.putInt("powerMode", 1);
                                            app.editor.commit();
                                            break;
                                        case 18:
                                            app.editor.putInt("powerMode", 2);
                                            app.editor.commit();
                                            break;
                                        case 19:
                                            app.editor.putInt("powerMode", 3);
                                            app.editor.commit();
                                            break;
                                    }

                                    dialog4.dismiss();
                                } else {
                                    Toast toast = Toast.makeText(getApplicationContext(),
                                            "修改失败！", Toast.LENGTH_SHORT);
                                    toast.getView().getBackground().setAlpha(200);//设置透明度
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }

                            }
                        });
                        btn_POWCamcel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //点击取消按钮什么都不做
                                dialog4.dismiss();
                            }
                        });
                        setWindowCenter(dialog4);
                        break;
                    case 5://报警地址与报警坐标
                        LinearLayout linearLayout5 = (LinearLayout) getLayoutInflater().inflate(R.layout.setalarmcoordinates, null);
                        alarmPageSetting(linearLayout5);
                        final AlertDialog dialog5 = getDialongView(linearLayout5);
                        btn_ALASure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //点击确定按钮保存修改的密码
                                //写入数据
                                app.editor.putString("callAddress", et_ALAtextAdd.getText().toString());
                                app.editor.putString("callLatitude", et_ALALatitudeNum.getText().toString());
                                app.editor.putString("callLongitude", et_ALALongitudeNum.getText().toString());
                                //提交存储的数据
                                app.editor.commit();
                                dialog5.dismiss();
                            }
                        });
                        btn_ALACamcel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //点击取消按钮什么都不做
                                dialog5.dismiss();
                            }
                        });
                        setWindowCenter(dialog5);
                        break;
                    case 6://修改客户信息
                        LinearLayout linearLayout6 = (LinearLayout) getLayoutInflater().inflate(R.layout.setcustomerinfo, null);
                        customerPageSetting(linearLayout6);
                        final AlertDialog dialog6 = getDialongView(linearLayout6);
                        btn_CUSSure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //点击确定按钮保存修改
                                //写入数据
                                app.editor.putString("cusZHName", et_CUStextZH.getText().toString());
                                app.editor.putString("cusENName", et_CUStextEN.getText().toString());
                                //提交存储的数据
                                app.editor.commit();
                                dialog6.dismiss();
                            }
                        });
                        btn_CUSCamcel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //点击取消按钮什么都不做
                                dialog6.dismiss();
                            }
                        });
                        setWindowCenter(dialog6);
                        break;
                }

            }
        });
    }

    //初始化listTwo适配器
    private void listTwoAdapater() {
        listTwoAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                RelativeLayout relat = new RelativeLayout(SettingActivity.this);
                relat.setMinimumHeight(100);
                TextView nameText = new TextView(SettingActivity.this);
                nameText.setTextColor(Color.parseColor("#ffffff"));
                Switch alartSwitch = new Switch(SettingActivity.this);
                alartSwitch.setChecked(isOpenAlarm);
                TextView textLine = new TextView(SettingActivity.this);
                textLine.setBackgroundColor(Color.parseColor("#4e565b"));
                textLine.setHeight(1);

                RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 35);
                lp1.setMargins(35, 0, 0, 0);
                lp1.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                relat.addView(nameText, lp1);

                RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 45);
                lp2.setMargins(0, 0, 45, 0);
                lp2.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                relat.addView(alartSwitch, lp2);

                RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                lp3.setMargins(35, 100, 45, 0);
                lp3.addRule(RelativeLayout.BELOW, RelativeLayout.TRUE);
                relat.addView(textLine, lp3);

                nameText.setText(listTwoDataArr[position]);
//                alartSwitch.setThumbDrawable();
                //             alartSwitch.setThumbDrawable(ContextCompat.getDrawable(SettingActivity.this,R.mipmap.setting_switch_on_btn));
                //             alartSwitch.setTrackResource(R.style);
//                alartSwitch.setBackRadius(10);
//                alartSwitch.setHeight(20);
//                alartSwitch.setWidth(36);
//                alartSwitch.setDrawDebugRect(true);
                //  alartSwitch.setPadding(10,4,10,4);
                alartSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) app.editor.putBoolean("isOpenAlarm", true);
                        else app.editor.putBoolean("isOpenAlarm", false);
                        app.editor.commit();
                    }
                });

                return relat;
            }
        };
        listTwo.setAdapter(listTwoAdapter);
    }

    private void setWindowCenter(AlertDialog dialog) {
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
    }

    private AlertDialog getDialongView(View view) {
        final AlertDialog.Builder builder6 = new AlertDialog.Builder(SettingActivity.this);
        builder6.setView(view);
        builder6.create();
        return builder6.show();
    }
}
