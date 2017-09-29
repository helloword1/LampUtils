package com.example.jjt_ssd.streetlamp.Tools;

import android.annotation.SuppressLint;
import android.os.Environment;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 获取sd卡数据
 * Created by JJT-ssd on 2016/8/19.
 */
public class  GetExternalDevice {

////////////////////SD卡资源读取///////////////////////////////////
    /**
     * 从sd卡获取图片资源
     * @return
     */
    public static List<String> getImagePathFromSD(String fileName) {

        // 得到sd卡内map文件夹的路径   File.separator(/)
        String filePath =  getExtSDCardPath().get(0)+File.separator
                + fileName;
        // 得到该路径文件夹下所有的文件
        File fileAll = new File(filePath);
        File[] files = fileAll.listFiles();
        int [] filesNumber = new int[files.length];

        try{
            // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
            // 图片列表
            String[] imagePathList = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String fileNam1e = file.getName();
                filesNumber[i] = Integer.parseInt(getNumber(fileNam1e));
                if (checkIsImageFile(file.getPath())) {
                    imagePathList[i] = file.getPath();
                }
            }
            //冒泡排序
            int temp;
            String tempStr;
            for(int i=0;i<filesNumber.length;i++){//趟数
                for(int j=0;j<filesNumber.length-i-1;j++){//比较次数
                    if(filesNumber[j]>filesNumber[j+1]){
                        temp=filesNumber[j];
                        filesNumber[j]=filesNumber[j+1];
                        filesNumber[j+1]=temp;

                        tempStr =imagePathList[j];
                        imagePathList[j]=imagePathList[j+1];
                        imagePathList[j+1]=tempStr;
                    }
                }
            }
            List<String> listPath = Arrays.asList(imagePathList);
            // 返回得到的图片列表
            return listPath;
        }catch (Exception e)
        {
            // 图片列表
            String[] imagePathList = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (checkIsImageFile(file.getPath())) {
                    imagePathList[i] = file.getPath();
                }
            }
            List<String> listPath = Arrays.asList(imagePathList);
            // 返回得到的图片列表
            return listPath;
        }
    }
    //截取字符的方法
    private static String getNumber(String str)
    {
        String a = str.split("\\.")[0];
        return  a;
    }

    /**
     * 检查扩展名，得到图片格式的文件
     * @param fName  文件名
     * @return
     */
    @SuppressLint("DefaultLocale")
    private static boolean checkIsImageFile(String fName) {
        boolean isImageFile = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals("jpg") || FileEnd.equals("png") || FileEnd.equals("gif")
                || FileEnd.equals("jpeg")|| FileEnd.equals("bmp") ) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }

    /**
     * 获取内置SD卡路径
     * @return
     */
    public static String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 获取外置SD卡路径
     * @return  应该就一条记录或空
     */
    private static List<String> getExtSDCardPath()
    {
        List<String> lResult = new ArrayList<String>();
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("extsd"))
                {
                    String [] arr = line.split(" ");
                    String path = arr[1];
                    File file = new File(path);
                    if (file.isDirectory())
                    {
                        lResult.add(path);
                    }
                }
            }
            isr.close();
        } catch (Exception e) {
        }
        return lResult;
    }

    ///////////////////////USB资源读取/////////////////////////////////

    /**
     * 获取外置U盘路径
     * @return  应该就一条记录或空
     */
    public static List<String> getExtUSBPath()
    {
        List<String> lResult = new ArrayList<String>();
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("usbhost1"))
                {
                    String [] arr = line.split(" ");
                    String path = arr[1];
                    File file = new File(path);
                    if (file.isDirectory())
                    {
                        lResult.add(path);
                    }
                }
            }
            isr.close();
        } catch (Exception e) {
        }
        return lResult;
    }

    /**
     * 从USB资源
     * @return
     */
    public static MyDictionary<String,String> getFileFromUSB(String fileName) {

        MyDictionary<String,String> fileDic = new MyDictionary<String,String>();

        // 得到USB内文件夹的fileName路径   File.separator(/)
        String filePath =  getExtUSBPath().get(0)+File.separator
                + fileName;
        // 得到该路径文件夹下所有的文件
        File fileAll = new File(filePath);
        File[] files = fileAll.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
           String fileKey = file.getName();
           String fileValue= file.getPath();
            fileDic.put(fileKey,fileValue);
        }
        // 返回得到的文件列表
        return fileDic;
    }
}



