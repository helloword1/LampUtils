package com.example.jjt_ssd.streetlamp.Tools;

import com.example.jjt_ssd.streetlamp.SerialPortLib.SerialPort;


/**
 * Created by JJT-ssd on 2016/9/3.
 */

public class SeraliPortHelper {

    private SerialPort serialPort;
    private ReadThread mReadThread;
    private SendingThread mSendingThread;

    public int getDataType()
    {
        int temp = serialPort.dataType;
        serialPort.dataType=0;
        return temp;
    }

    private class SendingThread extends Thread {
        char [] buffer;
        public SendingThread(char [] buffers)
         {
           this.buffer=buffers;
         }
        @Override
        public void run() {
            super.run();
                serialPort.send_data(serialPort.fd,buffer,buffer.length);
         }
    }

    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (true)
            {
                serialPort.receive_data(serialPort.fd, 32);
            }

        }
    }


    public SeraliPortHelper()
    {
        initPort();
    }
     /*
     *串口初始化方法 */
     private void initPort() {
        //配置串口
//        try {
////            serialPort = new SerialPort("/dev/ttyS3", 9600);
//            mReadThread = new ReadThread();
//            mReadThread.setName("ReadThread");
//            mReadThread.start();
//        } catch (IOException e) { }
    }

    /*
     *数据发送的方法
     * buffer为char[]类型 */
    public void onDataSend(final char[] buffer)
    {
        //开启发送数据
        mSendingThread = new SendingThread(buffer);
        mSendingThread.setName("SendingThread");
        mSendingThread.start();
    }



    /*
   *返回温度值 */
    public  String getTemperature()
    {
        float temp = (float)serialPort.getTemperature();
        float integerTemp = temp/10;
        return String.valueOf(integerTemp);
    }
    /*
     *返回湿度值 */
    public  String getHumidity()
    {
        float temp = (float)serialPort.getHumidity();
        float integerTemp = temp/10;
        return String.valueOf(integerTemp);
    }
    /*
    *返回风力值 */
    public  String getWindPower()
    {
        float temp = (float)serialPort.getWindPower();
        float integerTemp = temp/10;
        return String.valueOf(integerTemp);
    }
    /*
     *返回噪声值 */
    public  String getNoise()
    {
        float temp = (float)serialPort.getNoise();
        float integerTemp = temp/10;
        return String.valueOf(integerTemp);
    }
    /*
     *返回PM2.5值 */
    public  String getPm()
    {
        return String.valueOf(serialPort.getPm());
    }


    /*
       *返回蓄电池电量*/
    public  String getElectricity() {return String.valueOf(serialPort.getElectricity());}
    public  int getIntElectricity() {return serialPort.getElectricity();}
    /*
   *返回灯的状态：00灭；01开 */
    public  String getLampState() {return String.valueOf(serialPort.getLampState());}
    /*
     *返回灯的供电模式：01自动；02手动市电，03手动市电 */
    public  int getPowerMode() {return serialPort.getPowerMode();}
    /*
    *返回蓄电池使用的天数*/
    public  String getBatteryDate() {return String.valueOf(serialPort.getBatteryDate());}
    /*
     *返回蓄电池使用的天数剩下的小时*/
    public  String getBatteryHour() {return String.valueOf(serialPort.getBatteryHour());}
    /*
    *返回市电使用的天数*/
    public  String getElectricDate() {return String.valueOf(serialPort.getElectricDate());}
    /*
     *返回市电使用的天数剩下的小时*/
    public  String getElectricHour() {return String.valueOf(serialPort.getElectricHour());}
    /*
     *返回灯柱内温度；第一bit是正负温度区分；如0xF5=-75；0X35=53度 */
    public  String getLamppostTemp() {return String.valueOf(serialPort.getLamppostTemp());}
    /*
       *返回散热风扇状态：01：开风扇；02：关风扇 */
    public  String getFanState() {return String.valueOf(serialPort.getFanState());}

    public int getPowerModeChange(){return serialPort.getPowerModeChange();}

}
