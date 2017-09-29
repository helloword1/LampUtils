package com.example.jjt_ssd.streetlamp.SerialPortLib;


import java.io.IOException;

/**
 * Created by JJT-ssd on 2016/9/2.
 */
public class SerialPort {

    public int dataType;
    private static final String TAG = "SerialPort";
    /*
   *返回温度值 */
    public native int getTemperature();
    /*
     *返回湿度值 */
    public native int getHumidity();
    /*
    *返回风力值 */
    public native int getWindPower();
    /*
     *返回噪声值 */
    public native int getNoise();
    /*
     *返回PM2.5值 */
    public native int getPm();
///////////照明灯数据///////////////
   /*
   *返回蓄电池电量 */
  public native int getElectricity();
   /*
   *返回灯的状态：00灭；01开 */
   public native int getLampState();
    /*
     *返回灯的供电模式：01自动；02手动市电，03手动市电 */
    public native int getPowerMode();
    /*
    *返回蓄电池使用的天数*/
    public native int getBatteryDate();
    /*
     *返回蓄电池使用的天数剩下的小时*/
    public native int getBatteryHour();
    /*
    *返回市电使用的天数*/
    public native int getElectricDate();
    /*
     *返回市电使用的天数剩下的小时*/
    public native int getElectricHour();
    /*
     *返回灯柱内温度；第一bit是正负温度区分；如0xF5=-75；0X35=53度 */
    public native int getLamppostTemp();
    /*
       *返回散热风扇状态：01：开风扇；02：关风扇 */
    public native int getFanState();

    public native int getPowerModeChange();


    public int fd =0;
    public SerialPort(String device, int baudrate) throws SecurityException, IOException {
       fd = open_dev(device,baudrate);
    }

    public native int open_dev(String dev, int baudrate);
    public native int send_data(int fd, char[] data, int dataLen);
    public native void receive_data(int fd, int len);
    public native int close_dev(int fd);

    char[] dataBytes = new char[32]; //Accessing Fields，用于装载JNI中的返回数据
     //Accessing Fields，用于装载JNI中的返回数据长度

    //方法回调，JNI中一有数据此方法就被回调一次，
    // dataType = 0 没有正确数据，dataType=1 收到三合一传感器数据，
    // dataType =2 噪声传感器数据，dataType = 3 收到风速传感器数据
    //dataType =4照明灯数据处理，dataType = 5 收到供电模式设置返回数据
    public void callback(int dataType) {
        this.dataType = dataType;
    }

   static {
       System.loadLibrary("serial_port");
   }

}
