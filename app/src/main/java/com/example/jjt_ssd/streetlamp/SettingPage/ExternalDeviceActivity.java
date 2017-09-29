package com.example.jjt_ssd.streetlamp.SettingPage;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jjt_ssd.streetlamp.AppData;
import com.example.jjt_ssd.streetlamp.BaseActivity;
import com.example.jjt_ssd.streetlamp.Tools.GetExternalDevice;
import com.example.jjt_ssd.streetlamp.Tools.MyDictionary;
import com.example.jjt_ssd.streetlamp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExternalDeviceActivity extends BaseActivity {

    AppData app;
    ListView fileList;
    BaseAdapter fileAdapter;

    Button bt_ExtSure;
    Button bt_ExtCancel;
    TextView tv_ExtTips;

    //存储文件key-名字 value-路径 的字典
    MyDictionary<String, String> fileDict;
    //存储文件名字
    List<String> fileDataArr = new ArrayList<String>();
    //存储文件路径
    List<String> filePathArr = new ArrayList<String>();


    @Override
    protected int getLoyoutId() {
        return R.layout.activity_external_device;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUI();
        listAdapater();
    }

    private void setupUI() {
        app = (AppData)getApplication();
        setTitle("存储设备");

        fileList = (ListView) findViewById(R.id.EXTdevList);

        fileDict = GetExternalDevice.getFileFromUSB("file");
        fileDataArr = Collections.list(fileDict.keys());
        filePathArr = Collections.list(fileDict.elements());
    }

    private void playTipsPageSetting( LinearLayout linearLayout)
    {
        bt_ExtSure = (Button)linearLayout.findViewById(R.id.EXTdevSure);
        bt_ExtCancel= (Button)linearLayout.findViewById(R.id.EXTdevCancel);
        tv_ExtTips= (TextView)linearLayout.findViewById(R.id.EXTdevTips);
    }

    //初始化listOne适配器
    private void listAdapater() {
        fileAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return fileDataArr.size();
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

                RelativeLayout relat = new RelativeLayout(ExternalDeviceActivity.this);
                relat.setMinimumHeight(60);
                TextView nameText = new TextView(ExternalDeviceActivity.this);
                nameText.setTextColor(Color.parseColor("#ffffff"));
                nameText.setTextSize(16);

                final  Button bt_StopPlay = new Button(ExternalDeviceActivity.this);
                bt_StopPlay.setText("停止播放");
                bt_StopPlay.setTextSize(14);
                bt_StopPlay.setHeight(20);
                bt_StopPlay.setAllCaps(true);
                bt_StopPlay.setBackgroundColor(Color.parseColor("#00A483"));
                bt_StopPlay.setTextColor(Color.parseColor("#ffffff"));
                bt_StopPlay.setVisibility(View.INVISIBLE);
                if (app.isPlaying==true&&app.getplayingIndex()==position)
                    bt_StopPlay.setVisibility(View.VISIBLE);

                bt_StopPlay.setId(position);
                bt_StopPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bt_StopPlay.setVisibility(View.INVISIBLE);
                        app.isPlaying=false;
                        app.mediaPlayer.stop();
                        app.mediaPlayer.reset();
                    }
                });

                TextView textLine = new TextView(ExternalDeviceActivity.this);
                textLine.setBackgroundColor(Color.parseColor("#4e565b"));
                textLine.setHeight(1);

                RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 35);
                lp1.setMargins(35, 0, 0, 0);
                lp1.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                relat.addView(nameText, lp1);

                RelativeLayout.LayoutParams lp2 = new  RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,45);
                lp2.setMargins(0,0,45,0);
                lp2.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                relat.addView(bt_StopPlay, lp2 );

                RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                lp3.setMargins(35, 59, 45, 0);
                lp3.addRule(RelativeLayout.ALIGN_BOTTOM, RelativeLayout.TRUE);
                relat.addView(textLine, lp3);
                nameText.setText(fileDataArr.get(position));
                return relat;
            }
        };
        //监听播放完成
        app.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                //播放结束后的动作
                Button btnIndex = (Button)fileList.findViewById(app.getplayingIndex()).findViewById(app.getplayingIndex());
                btnIndex.setVisibility(View.INVISIBLE);
                app.isPlaying=false;
                app.mediaPlayer.stop();
                app.mediaPlayer.reset();
            }
        });

        fileList.setAdapter(fileAdapter);
        fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                //获取当前选中的文件类型
                final String filePath = filePathArr.get(position);

                String fileType = getFileType(fileDataArr.get(position));
                fileType = fileType.toLowerCase();
                //判断是否为音频文件(是的话直接播放)
                if (fileType.equals("mp3")||fileType.equals("mp4")||fileType.equals("wav")||fileType.equals("amr")||fileType.equals("awb")||fileType.equals("wma")||fileType.equals("ogg"))
                {
                    LinearLayout linearLayout1=(LinearLayout)getLayoutInflater().inflate(R.layout.setplaytip,null);
                    playTipsPageSetting(linearLayout1);
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ExternalDeviceActivity.this);
                    tv_ExtTips.setText("确定播放 "+ fileDataArr.get(position)+"吗?");
                    builder1.setView(linearLayout1);
                    builder1.create();
                    final AlertDialog dialog1 = builder1.show();
                    bt_ExtSure.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //点击确定按钮保存修改
                            try {
                                Button btnIndex = (Button)fileList.findViewById(app.getplayingIndex()).findViewById(app.getplayingIndex());
                                btnIndex.setVisibility(View.INVISIBLE);
                                Button button = (Button) view.findViewById(position);
                                app.mediaPlayer.stop();
                                app.mediaPlayer.reset();
                                app.mediaPlayer.setDataSource(filePath);
                                app.mediaPlayer.prepare();
                                app.mediaPlayer.start();
                                button.setVisibility(View.VISIBLE);
                                app.setPlayingIndex(position);
                                app.isPlaying=true;
                            } catch (IOException e) { }
                            dialog1.dismiss();
                        }
                    });
                    bt_ExtCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //点击取消按钮什么都不做
                            dialog1.dismiss();
                        }
                    });

                }
            }
        });
    }

    //截取字符的方法
    private static String getFileType(String str) {
        String a = str.split("\\.")[1];
        return a;
    }

}