package com.example.jjt_ssd.streetlamp.mqtt;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.example.jjt_ssd.streetlamp.mqtt.mqttobserver.MqttObserver;
import com.example.jjt_ssd.streetlamp.mqtt.mqttobserver.MqttSubject;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.jjt_ssd.streetlamp.AppData.TOPIC_PUBLIC;
import static com.example.jjt_ssd.streetlamp.AppData.TOPIC_SUBCRIBE;

public class MQTTService extends Service implements MqttSubject {
    public static final String TAG = MQTTService.class.getSimpleName();
    private static String[] topic = new String[]{"gk/light/4/521"};
    private static int[] qos = new int[]{0};
    private List<MqttObserver> observers;
    private MQTTService service;
    private Thread thread;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isConnectIsNomarl()) {
            observers = new ArrayList<>();
            init();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void init() {
        if (thread == null || !thread.isAlive()) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            MqttUtils.publish(TOPIC_PUBLIC, String2Char(senProtocol2Net("msg001", 1)), new MqttHandleListener() {
                                @Override
                                public void MqttSuccedListener(String succeed) {
                                    Log.d(TAG, "MqttSuccedListener: ");

                                }
                            });
                            try {
                                MqttUtils.subscribe(new String[]{TOPIC_SUBCRIBE}, new int[]{0}, new MqttHandleListener() {
                                    @Override
                                    public void MqttSuccedListener(final String succeed) {
                                        Log.d(TAG, "MqttSuccedListener: " + succeed);
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                notifyObservers(succeed);
                                            }
                                        });
                                    }
                                });
                                SystemClock.sleep(10000);
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
            thread.start();
        }
    }

    private String String2Char(String str) {
        return str + "\0";
    }

    @Override
    public void onDestroy() {
        MqttUtils.disconnect();
        super.onDestroy();
    }


    /**
     * 判断网络是否连接
     */
    private boolean isConnectIsNomarl() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            Log.i(TAG, "MQTT当前网络名称：" + name);
            return true;
        } else {
            Log.i(TAG, "MQTT 没有可用网络");
            return false;
        }
    }

    @Override
    public void registerObserver(MqttObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public void removeObserver(MqttObserver observer) {
        this.observers.remove(observer);
    }

    @Override
    public void notifyObservers(String succeedStr) {
        for (MqttObserver observer : observers) {
            observer.update(succeedStr);
        }
    }


    public class MyBinder extends Binder {
        //提供获取当前服务实例的方法
        public MQTTService getService() {
            return MQTTService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return new MyBinder();
    }

    private String senProtocol2Net(String id, int type) {
        JSONObject object = new JSONObject();
        try {
            object.put("msg_id", id);
            object.put("time", getTime());
            object.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    private String getTime() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(new Date());
        return date;
    }
}
