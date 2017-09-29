package com.example.jjt_ssd.streetlamp.mqtt.mqttobserver;

/**
 * Created by LJN on 2017/9/29.
 */

public interface MqttSubject {
    /**
     * 注册观察者
     */
    void registerObserver(MqttObserver observer);

    /**
     * 移除观察者
     */
    void removeObserver(MqttObserver observer);

    /**
     * 通知观察者
     */
    void notifyObservers(String succeedStr);

}
