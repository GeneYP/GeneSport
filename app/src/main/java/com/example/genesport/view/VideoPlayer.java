package com.example.genesport.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.genesport.R;
import com.example.genesport.utils.Constants;
import com.example.genesport.view.base.BaseActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * Created by djzhao on 17/04/29.
 */

public class VideoPlayer extends BaseActivity {

    private VideoView videoView;

    private MediaController mediaController;

    private Context mContext;

    private int tag;

    private boolean videoStop;
    private int currentPosition;
    private String duration = "0";

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        //现在把它get出来
        tag = getIntent().getIntExtra("tag", 1);
        setContentView(R.layout.activity_test_video_player);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        this.videoView = $(R.id.player_test_vv);
    }

    @Override
    protected void initView() {
        mContext = this;
        loadVideo();
    }

    private void loadVideo() {
        String uri = "android.resource://" + getPackageName() + "/";
        switch (tag) {
            case 1:
                duration = "8";
                uri += R.raw.base;
                break;
            case 2:
                duration = "9";
                uri += R.raw.enhance;
                break;
            case 3:
                duration = "11";
                uri += R.raw.acme;
                break;
        }
        // 本地视频
        // videoView.setVideoPath(path);
        // 网络视频
        // videoView.setVideoURI(Uri.parse("http://www.runoob.com/try/demo_source/movie.mp4"));
        videoView.setVideoURI(Uri.parse(uri));
        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setMediaPlayer(videoView);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoStop = true;
                saveTrainRecord();
            }
        });
        videoView.start();
    }

    //看完视频要记录训练过了
    private void saveTrainRecord() {
        String url = Constants.BASE_URL + "Train?method=addNewTrainRecord";
        OkHttpUtils
                .post()
                .url(url)
                .id(1)
                .addParams("userId", Constants.USER.getUserId() + "")
                .addParams("duration", duration)
                .build()
                .execute(new MyStringCallback());
    }

    private void showInfo() {
        String message = "";
        switch (tag) {
            case 1:
                message = "“肌撕裂者-初级”";
                break;
            case 2:
                message = "“肌撕裂者-中级”";
                break;
            case 3:
                message = "“肌撕裂者-极致”";
                break;
        }
        SystemClock.sleep(300);
        AlertDialog.Builder builder = new AlertDialog.Builder(com.example.genesport.view.VideoPlayer.this);
        builder.setTitle("锻炼结束")
                .setMessage("恭喜你，" + message + "锻炼结束！")
                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .create()
                .show();
    }

    //获取服务器响应
    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {
            switch (id) {
                case 1:
                    if (response.contains("error")) {
                        DisplayToast("锻炼记录同步失败");
                    }
                    showInfo();
                    break;
                default:
                    DisplayToast("what?");
                    break;
            }
        }

        @Override
        public void onError(Call arg0, Exception arg1, int arg2) {
            Toast.makeText(mContext, "网络链接出错！", Toast.LENGTH_SHORT).show();
        }
    }

    //看一半退出的提示
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!videoStop) {
                videoView.pause();
                currentPosition = videoView.getCurrentPosition();
                AlertDialog.Builder builder = new AlertDialog.Builder(com.example.genesport.view.VideoPlayer.this);
                builder.setTitle("懦夫")
                        .setMessage("你就这么放弃了？")
                        .setPositiveButton("是，我是懦夫，我爬", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .setNegativeButton("否，我不小心点错了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                videoView.seekTo(currentPosition);
                                videoView.start();
                            }
                        })
                        .create()
                        .show();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
