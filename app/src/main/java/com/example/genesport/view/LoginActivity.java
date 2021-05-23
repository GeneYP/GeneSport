package com.example.genesport.view;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.example.genesport.R;
import com.example.genesport.entity.User;
import com.example.genesport.utils.Constants;
import com.example.genesport.utils.SharedPreferencesUtils;
import com.example.genesport.view.base.BaseActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;

public class LoginActivity extends BaseActivity implements OnClickListener {
    private EditText et_username;
    private EditText et_password;

    private Button bt_login;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;

        findViewById();
        initView();
    }

    private void login() {
        String username = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        //判断是不是没输东西进去，要不然不给登
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(mContext, "不可留空", Toast.LENGTH_SHORT).show();
            return;
        }
        // 服务端验证
        checkUser();
        //直接打开
//        openActivity(MainActivity.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_bt_login:
                login();
                break;
                //注册还没写完
//            case R.id.login_bt_register:
//                openActivity(RegisterActivity.class);
//                break;
            default:
                break;
        }
    }

    //想写修改IP的页面，一直到IDEA里面改麻烦死了，上传图片还没写，先不做这个
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
//            openActivity(ConfigActivity.class);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void findViewById() {
        et_username = $(R.id.login_et_username);
        et_password = $(R.id.login_et_password);
        bt_login = $(R.id.login_bt_login);
    }

    @Override
    protected void initView() {
        bt_login.setOnClickListener(this);
        echo();
    }

    //输错了回显
    private void echo() {
        Map<String, String> map = SharedPreferencesUtils.getUserInfo(mContext);//获取用户名密码
        if (map != null) {
            String username = map.get("username");
            String password = map.get("password");
            et_username.setText(username);
            et_password.setText(password);
        }
    }

    //去服务器验证登录
    private void checkUser() {
        String url = Constants.BASE_URL + "User?method=login";
        OkHttpUtils
                .post()
                .url(url)
                .id(1)
                .addParams("username", et_username.getText().toString().trim())
                .addParams("password", et_password.getText().toString().trim())
                .build()
                .execute(new MyStringCallback());
    }

    //返回的JSON数据验证有没有登进去
    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {
            Gson gson = new Gson();
            switch (id) {
                case 1:

                    User user = gson.fromJson(response, User.class);
                    if (user.getUserId() == 0) {
                        DisplayToast("用户名或者密码错误");
                        return;
                    } else {
                        // 存储用户
                        Constants.USER = user;
                        boolean result = SharedPreferencesUtils.saveUserInfo(mContext, user);
                        if (result) {
                            Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "用户存储失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    openActivity(MainActivity.class);
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
