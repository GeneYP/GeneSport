package com.example.genesport.utils;

import com.example.genesport.entity.User;

import java.util.List;

//放链接数据库的各种乱七八糟烦得要死的数据
public class Constants {

	// 用户对象
	public static User USER = new User();
	//打卡列表
	public static List<String> DAILYCHECKEDLIST;
	// 服务器地址，要一直改好麻烦
	public static String BASE_URL = "http://10.8.245.43:8080/MyServer/";

	//本来想写上传图片的，还没写完
}
