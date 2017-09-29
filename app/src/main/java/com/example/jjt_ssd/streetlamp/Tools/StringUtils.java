package com.example.jjt_ssd.streetlamp.Tools;

/**
 * Created by LJN on 2017/9/29.
 */

public class StringUtils {
    public static String doubleFormat(String numStr) {
        try {
            double aDouble = Double.valueOf(numStr);
            String s ;
            if (aDouble!=(int)aDouble) {
                s = String.format("%.1f", aDouble);
            } else {
                s = String.valueOf((int) aDouble);
            }
            return s;
        } catch (Exception ex) {

        }
        return "0";
    }
}
