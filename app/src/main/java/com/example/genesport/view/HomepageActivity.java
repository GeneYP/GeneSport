package com.example.genesport.view;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.example.genesport.R;
import com.example.genesport.entity.User;
import com.example.genesport.utils.Constants;
import com.example.genesport.utils.SharedPreferencesUtils;
import com.example.genesport.view.base.BaseActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

//用户信息那块的Activity
public class HomepageActivity extends BaseActivity implements View.OnClickListener {

    private String TITLE_NAME = "关于我";
    private View title_back;
    private TextView titleText;

    private Context mContext;
    private TextView username;
    private RadioGroup sexGroup;
    private RadioButton maleRadio;
    private RadioButton femaleRadio;

    private EditText height;
    private EditText weight;

    private Button update;

    private String heightStr;
    private String weightStr;
    private String sex;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_homepage);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        this.title_back = $(R.id.title_back);
        this.titleText = $(R.id.titleText);
        sexGroup = $(R.id.homepage_radio_sex);
        update = $(R.id.homepage_btn_update);
        height = $(R.id.homepage_et_height);
        weight = $(R.id.homepage_et_weight);
        username = $(R.id.homepage_tv_username);

        maleRadio = $(R.id.homepage_rd_male);
        femaleRadio = $(R.id.homepage_rd_female);
    }

    @Override
    protected void initView() {
        mContext = this;
        this.titleText.setText(TITLE_NAME);
        this.title_back.setOnClickListener(this);
        update.setOnClickListener(this);

        echo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back: {
                this.finish();
            }
            break;
            case R.id.homepage_btn_update:
                checkInfo();
                break;
        }
    }

    //先获取数据库里的数据给你改，总不能给你一个空的，那哪叫修改
    private void echo() {
        username.setText(Constants.USER.getUsername());
        height.setText(Constants.USER.getHeight() + "");
        weight.setText(Constants.USER.getWeight() + "");
        if (Constants.USER.getSex().equals("女")) {
            femaleRadio.setChecked(true);
        } else {
            maleRadio.setChecked(true);
        }
    }

    private void checkInfo() {
        heightStr = height.getText().toString().trim();
        weightStr = weight.getText().toString().trim();
        sex = "男";
        if (sexGroup.getCheckedRadioButtonId() == R.id.homepage_rd_female) {
            sex = "女";
        }
        if (TextUtils.isEmpty(heightStr) || TextUtils.isEmpty(weightStr)) {
            DisplayToast("不可留空！");
            return;
        }

        update();
    }

    //去服务器执行update方法
    private void update() {
        String url = Constants.BASE_URL + "User?method=update";
        OkHttpUtils
                .post()
                .url(url)
                .addParams("username", Constants.USER.getUsername())
                .addParams("height", heightStr)
                .addParams("weight", weightStr)
                .addParams("sex", sex)
                .id(1)
                .build()
                .execute(new MyStringCallback());
    }

    //去服务器用JSON格式存用户
    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {
            Gson gson = new Gson();
            switch (id) {
                case 1:
                    User user = null;
                    try {
                        user = gson.fromJson(response, User.class);
                    } catch (JsonSyntaxException e) {
                        user = null;
                    }
                    if (user == null) {
                        DisplayToast(response);
                        return;
                    } else {
                        // 存储用户
                        Constants.USER.setHeight(user.getHeight());
                        Constants.USER.setWeight(user.getWeight());
                        Constants.USER.setSex(user.getSex());
                        user.setPassword(Constants.USER.getPassword());
                        user.setUserId(Constants.USER.getUserId());
                        boolean result = SharedPreferencesUtils.saveUserInfo(mContext, user);
                        if (result) {
                            Toast.makeText(mContext, "更新成功！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "用户信息存储失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    finish();
                    break;
                default:
                    DisplayToast("what?");
                    break;
            }
        }

        @Override
        public void onError(Call arg0, Exception arg1, int arg2) {
            DisplayToast("网络链接出错！");
        }
    }
}
