package com.example.jjt_ssd.streetlamp.Tools;

import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by JJT-ssd on 2016/8/31.
 */
public class WifiHelper {

    //获取IP地址
    public static String getIP(WifiManager wifiManager)
    {
        //获取wifi服务
        if (wifiManager.isWifiEnabled()==true)
        {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            return intToIp(ipAddress);
        }
        return "";
    }

    public static  String getGateway(WifiManager wifiManager)
    {
        //获取wifi服务
        if (wifiManager.isWifiEnabled()==true)
        {
            DhcpInfo dhcpInfo =  wifiManager.getDhcpInfo();
            int gateway =dhcpInfo.gateway;
            return intToIp(gateway);
        }
        return "";
    }

    //获取MAC地址
    public static String getMac(WifiManager wifiManager)
    {
        //获取wifi服务
        if (wifiManager.isWifiEnabled()==true)
        {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            return wifiInfo.getMacAddress();
        }
        return "";
    }
    //IP地址转换
    private static String intToIp(int i) {

        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }
}
