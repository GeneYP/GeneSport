package com.example.genesport.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.genesport.R;
import com.example.genesport.utils.Constants;
import com.example.genesport.view.base.BaseActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Calendar;

import cn.aigestudio.datepicker.bizs.calendars.DPCManager;
import cn.aigestudio.datepicker.bizs.decors.DPDecor;
import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;
import okhttp3.Call;

//写死我了这个类，真的累，写了一堆没用全注释掉了
//用来打卡的，有用到一个DatePicker的东西
public class DateCheckActivity extends BaseActivity implements View.OnClickListener {

    private String TITLE_NAME = "每日打卡";
    private View title_back;
    private TextView titleText;

    private Context mContext;
    private DatePicker picker;
    private Button btnPick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_check);
        findViewById();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDailyCheck();
        echoChecked();
    }

    //从服务器获取打卡记录
    private void getDailyCheck() {
        String url = Constants.BASE_URL + "DailyCheck?method=getCheckedList";
        OkHttpUtils
                .post()
                .url(url)
                .id(2)
                .addParams("userId", Constants.USER.getUserId() + "")
                .build()
                .execute(new MyStringCallback());
    }

    @Override
    protected void findViewById() {
        //相当于this.xxx = findViewById(R.id.xxx);
        this.title_back = $(R.id.title_back);
        this.titleText = $(R.id.titleText);

        picker = (DatePicker) findViewById(R.id.date_date_picker);
        btnPick = (Button) findViewById(R.id.date_btn_check);
    }

    @Override
    protected void initView() {
        mContext = this;
        this.title_back.setOnClickListener(this);
        this.titleText.setText(TITLE_NAME);
        btnPick.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.date_btn_check:
                todayCheck();
                break;
        }
    }

    //打卡功能，写到服务器里面去
    private void todayCheck() {
        String url = Constants.BASE_URL + "DailyCheck?method=check";
        OkHttpUtils
                .post()
                .url(url)
                .id(1)
                .addParams("userId", Constants.USER.getUserId() + "")
                .build()
                .execute(new MyStringCallback());
    }

    //获取响应然后完成打卡
    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {
            switch (id) {
                case 1:
                    if (response.contains("success")) {
                        DisplayToast("今日打卡成功");
                    } else {
                        DisplayToast(response);
                    }
                    break;
                case 2:
                    if (response.contains("error")) {
                        DisplayToast("暂时无法获取数据");
                    } else {
                        String[] dates = response.split(",");
                        for (String s: dates) {
//                            dailyCheckedList.add(s);
                        }
                    }
                    break;
            }
        }

        @Override
        public void onError(Call arg0, Exception arg1, int arg2) {
            DisplayToast("网络链接出错！");
        }
    }

    //展示打过的卡
    public void echoChecked() {
        DPCManager.getInstance().setDecorTR(Constants.DAILYCHECKEDLIST);
        Calendar today = Calendar.getInstance();
        picker.setDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1);
        //展示节假日和农历的那些乱七八糟的东西
//        picker.setFestivalDisplay(false);
        picker.setTodayDisplay(true);
//        picker.setHolidayDisplay(false);
        picker.setDeferredDisplay(false);
        picker.setMode(DPMode.NONE);
        //打卡的用绿色点上去
        picker.setDPDecor(new DPDecor() {
//            @Override
//            public void drawDecorTL(Canvas canvas, Rect rect, Paint paint, String data) {
//                super.drawDecorTL(canvas, rect, paint, data);
//                switch (data) {
//                    case "2021-05-22":
//                    case "2021-10-1":
//                    case "2015-10-9":
//                    case "2015-10-11":
//                        paint.setColor(Color.GREEN);
//                        // canvas.drawRect(rect, paint);
//                        BitmapDrawable bmpDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_location_checked);
//                        Bitmap bmp = bmpDraw.getBitmap();
//                        canvas.drawBitmap(bmp, 10, 10, paint);
//                        break;
//                    default:
//                        paint.setColor(Color.RED);
//                        canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2, paint);
//                        break;
//                }
//            }

            @Override
            public void drawDecorTR(Canvas canvas, Rect rect, Paint paint, String data) {
                super.drawDecorTR(canvas, rect, paint, data);
                //这个绿是AndroidStudio那个build图标的绿色，用GREEN太丑了
                paint.setColor(Color.rgb(80,160,90));
                canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 3, paint);
            }
        });
    }
}
