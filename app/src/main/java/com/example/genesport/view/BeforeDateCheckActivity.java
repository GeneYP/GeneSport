package com.example.genesport.view;

import android.os.Bundle;
import android.os.SystemClock;

import com.example.genesport.R;
import com.example.genesport.utils.Constants;
import com.example.genesport.view.base.BaseActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import okhttp3.Call;

//打卡前的过渡页面（学习百词斩那种样式
public class BeforeDateCheckActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_before_date_check);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {

    }

    @Override
    protected void initView() {
        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(300);
                getCheckedList();
            }
        }.start();
    }

    //获取用户打卡的列表
    private void getCheckedList() {
        String url = Constants.BASE_URL + "DailyCheck?method=getCheckedList";
        OkHttpUtils
                .post()
                .url(url)
                .id(1)
                .addParams("userId", Constants.USER.getUserId() + "")
                .build()
                .execute(new MyStringCallback());
    }

    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {
            SystemClock.sleep(300);
            switch (id) {
                case 1:
                    if (response.contains("error")) {
                        DisplayToast("暂时无法获取数据");
                        finish();
                    } else {
                        if (response.length() == 0) {
                            Constants.DAILYCHECKEDLIST = new ArrayList<>();
                            Constants.DAILYCHECKEDLIST.add("2000-1-1");
                        } else {
                            String[] dates = response.split(",");
                            if (Constants.DAILYCHECKEDLIST == null) {
                                Constants.DAILYCHECKEDLIST = new ArrayList<String>();
                            } else {
                                Constants.DAILYCHECKEDLIST.clear();
                            }
                            for (String s : dates) {
                                String[] split = s.split("-");
                                //要把日期前面的0去掉
                                s = split[0] + "-" + removeHeadingZero(split[1]) + "-" + removeHeadingZero(split[2]);
                                Constants.DAILYCHECKEDLIST.add(s);
                            }
                        }
                        openActivity(DateCheckActivity.class);
                        finish();
                    }
                    break;
            }
        }

        //去掉0，如果有0就去掉
        public String removeHeadingZero(String str) {
            if (str.startsWith("0")) {
                return str.substring(1);
            } else {
                return str;
            }
        }

        @Override
        public void onError(Call arg0, Exception arg1, int arg2) {
            DisplayToast("网络链接出错！");
        }
    }
}
