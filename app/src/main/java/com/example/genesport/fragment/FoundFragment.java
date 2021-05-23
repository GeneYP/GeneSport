package com.example.genesport.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.genesport.R;
import com.example.genesport.adapter.FoundNewsAdapter;
import com.example.genesport.entity.NewsListForFound;
import com.example.genesport.utils.Constants;
import com.example.genesport.view.NewsDetailActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

//动态页的fragment
public class FoundFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView foundList;
    private FoundNewsAdapter adapter;
    private Context mContext;

    private List<NewsListForFound> mList;

    //初始化view对象
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_found, null);
        findViewById(v);
        initView();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        //在离开这个view的时候去数据库刷新一份动态
        reLoadNews();
    }

    private void reLoadNews() {
        String url = Constants.BASE_URL + "News?method=getNewsList";
        //利用OkHttpUtils的url访问数据库
        OkHttpUtils
                .post()                     //提交
                .url(url)                   //访问的服务器地址
                .id(1)
                .build()                    //忘了啥来着
                .execute(new MyStringCallback());   //执行回写类
    }

    public void findViewById(View v) {
        foundList = (ListView) v.findViewById(R.id.found_list);
    }

    public void initView() {
        mContext = getActivity();
        foundList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), NewsDetailActivity.class);
        intent.putExtra("newsId", mList.get(position).getNewsId());
        startActivity(intent);
    }

    //扩展类重写onResponse方法，传一个Json，里面是 {"newsId":3,……"sex":1}这种键值对。
    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {
            Gson gson = new Gson();
            try {
                Type type = new TypeToken<ArrayList<NewsListForFound>>() {
                }.getType();
                mList = gson.fromJson(response, type);
            } catch (Exception e) {
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                mList = null;
            }
            switch (id) {
                case 1:
                    if (mList != null && mList.size() > 0) {
                        adapter = new FoundNewsAdapter(mContext, mList);
                        foundList.setAdapter(adapter);
                    }
                    break;
                default:
                    Toast.makeText(mContext, "what！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onError(Call arg0, Exception arg1, int arg2) {
            Toast.makeText(mContext, "网络链接出错！", Toast.LENGTH_SHORT).show();
        }
    }
}
