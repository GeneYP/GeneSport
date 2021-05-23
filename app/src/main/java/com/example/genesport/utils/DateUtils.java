package com.example.genesport.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

//获取当前日期的类
public class DateUtils {

    public static String getCurrentDatetime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
}
