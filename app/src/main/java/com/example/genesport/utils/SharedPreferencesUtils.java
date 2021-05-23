package com.example.genesport.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.example.genesport.entity.User;

import java.util.HashMap;
import java.util.Map;

//这个是个轻量级的保存用的工具类，用来保存用户个人信息
public class SharedPreferencesUtils {

    //保存用户密码
    public static boolean saveUserInfo(Context context, User user) {
        try {
            //1.通过Context对象创建一个SharedPreference对象
            //name:sharedpreference文件的名称    mode:文件的操作模式
            SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
            //2.通过sharedPreferences对象获取一个Editor对象
            Editor editor = sharedPreferences.edit();
            //3.往Editor中添加数据
            editor.putInt("userId", user.getUserId());
            editor.putString("username", user.getUsername());
            editor.putString("password", user.getPassword());
            editor.putString("sex", user.getSex());
            editor.putString("height", user.getHeight() + "");
            editor.putString("weight", user.getWeight() + "");
            //4.提交Editor对象
            editor.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //获取用户名密码
    public static Map<String, String> getUserInfo(Context context) {
        HashMap<String, String> hashMap;
        try {
            //1.通过Context对象创建一个SharedPreference对象
            SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
            //2.通过sharedPreference获取存放的数据
            //key:存放数据时的key   defValue: 默认值,根据业务需求来写
            int userId = sharedPreferences.getInt("userId", 0);
            String password = sharedPreferences.getString("password", "");
            String username = sharedPreferences.getString("username", "");
            String sex = sharedPreferences.getString("sex", "男");
            String height = sharedPreferences.getString("height", "");
            String weight = sharedPreferences.getString("weight", "");

            hashMap = new HashMap<String, String>();
            hashMap.put("userId", userId + "");
            hashMap.put("password", password);
            hashMap.put("username", username);
            hashMap.put("sex", sex);
            hashMap.put("height", height);
            hashMap.put("weight", weight);
            return hashMap;

        } catch (Exception e) {
            e.printStackTrace();
            hashMap = null;
        }
        return hashMap;
    }
}
