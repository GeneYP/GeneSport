package com.example.genesport.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.genesport.R;
import com.example.genesport.view.VideoPlayer;

//训练首页的fragment
public class TrainingFragment extends Fragment implements View.OnClickListener {

    private LinearLayout baseTraining;
    private LinearLayout enhanceTraining;
    private LinearLayout acmeTraining;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_train, null);
        findViewById(v);
        initView();
        return v;
    }

    public void findViewById(View v) {
        baseTraining = (LinearLayout) v.findViewById(R.id.train_base);
        enhanceTraining = (LinearLayout) v.findViewById(R.id.train_enhance);
        acmeTraining = (LinearLayout) v.findViewById(R.id.train_acme);
    }

    public void initView() {
        baseTraining.setOnClickListener(this);
        enhanceTraining.setOnClickListener(this);
        acmeTraining.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), VideoPlayer.class);
        switch (v.getId()) {
            case R.id.train_base:
                //根据选择的等级把它的id给put进去，在videoPlayer里面get出来
                intent.putExtra("tag", 1);
                startActivity(intent);
                break;
            case R.id.train_enhance:
                intent.putExtra("tag", 2);
                startActivity(intent);
                break;
            case R.id.train_acme:
                intent.putExtra("tag", 3);
                startActivity(intent);
                break;
        }
    }
}
