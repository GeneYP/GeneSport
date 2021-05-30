package com.example.genesport.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.genesport.R;
import com.example.genesport.utils.AppManager;
import com.example.genesport.utils.Constants;
import com.example.genesport.view.BeforeDateCheckActivity;
import com.example.genesport.view.FavorsListActivity;
import com.example.genesport.view.HomepageActivity;
import com.example.genesport.view.LoginActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

import static android.content.ContentValues.TAG;

//个人主页的fragment
public class MeFragment extends Fragment implements View.OnClickListener {

    private LinearLayout homepage;
    private LinearLayout record;
    private LinearLayout favor;

    private TextView usernameTV;
    private TextView exerciseTimeTextView;
    private TextView recordDaysTextView;
    private TextView exit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_me, null);
        findViewById(v);
        initView();

        return v;
    }

    public void findViewById(View v) {
        homepage = (LinearLayout) v.findViewById(R.id.me_homepage);
        record = (LinearLayout) v.findViewById(R.id.me_item_reord);
        favor = (LinearLayout) v.findViewById(R.id.me_item_favor);
        usernameTV = (TextView) v.findViewById(R.id.me_homepage_username);
        exerciseTimeTextView = (TextView) v.findViewById(R.id.me_exercise_time);
        recordDaysTextView = (TextView) v.findViewById(R.id.me_record_days);
        exit = (TextView) v.findViewById(R.id.me_item_exit);
    }

    public void initView() {
        homepage.setOnClickListener(this);
        record.setOnClickListener(this);
        favor.setOnClickListener(this);
        exit.setOnClickListener(this);
        
        echo();
        getRecords();
    }


    private void getRecords() {
        //在服务器计算锻炼总时间
        String url = Constants.BASE_URL + "DailyCheck?method=getHomepageTotalRecord";
        OkHttpUtils
                .post()
                .url(url)
                .addParams("userId", Constants.USER.getUserId() + "")
                .id(1)
                .build()
                .execute(new MyStringCallback());
    }

    //数据回显，TextView如果你输错了不用重新输
    private void echo() {
        usernameTV.setText(Constants.USER.getUsername());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.me_homepage:
                startActivity(new Intent(getActivity(), HomepageActivity.class));
                break;
            case R.id.me_item_reord:
                startActivity(new Intent(getActivity(), BeforeDateCheckActivity.class));
                break;
            case R.id.me_item_favor:
                startActivity(new Intent(getActivity(), FavorsListActivity.class));
                break;
            case R.id.me_item_exit:
                SystemClock.sleep(500);
                AppManager.getInstance().killAllActivity();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
        }
    }

    public class MyStringCallback extends StringCallback {

        @Override
        public void onResponse(String response, int id) {
//            Log.d(TAG, "onResponse: " + response);
//            Log.d(TAG, "onResId: " + id);
            //获取锻炼天数和时间
            switch (id) {
                case 1:
                    String[] items = response.split(":");
                    exerciseTimeTextView.setText(items[1]);
                    recordDaysTextView.setText(items[0]);
                    break;
                default:
                    Toast.makeText(getActivity(), "what?", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onError(Call arg0, Exception arg1, int arg2) {
            Toast.makeText(getActivity(), "网络链接出错！", Toast.LENGTH_SHORT).show();
        }
    }
}
