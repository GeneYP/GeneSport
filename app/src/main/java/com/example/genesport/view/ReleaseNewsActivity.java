package com.example.genesport.view;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.example.genesport.R;
import com.example.genesport.utils.Constants;
import com.example.genesport.view.base.BaseActivity;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.define.Define;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import okhttp3.Call;

//发布新动态的Activity
public class ReleaseNewsActivity extends BaseActivity implements View.OnClickListener {

    private String TITLE_NAME = "新鲜事";
    private View title_back;
    private TextView titleText;

    private Context mContext;

    private EditText title;
    private EditText content;
    private ImageView photo;
    private Button release;

    private ArrayList<Uri> path;

    private File imageFile;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_add_news);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        this.title_back = $(R.id.title_back);
        this.titleText = $(R.id.titleText);
        this.title = $(R.id.add_news_et_share_title);
        this.content = $(R.id.add_news_et_share_content);
        this.photo = $(R.id.add_news_iv_photo);
        this.release = $(R.id.add_news_btn_release);
    }

    @Override
    protected void initView() {
        mContext = this;
        this.titleText.setText(TITLE_NAME);

        this.title_back.setOnClickListener(this);
        this.photo.setOnClickListener(this);
        this.release.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back: {
                this.finish();
            }
            break;
            case R.id.add_news_iv_photo:
                getPhoto();
                break;
            case R.id.add_news_btn_release:
                checkInfo();
                break;
        }
    }

    //判断有没有输完整
    private void checkInfo() {
        String titleStr = title.getText().toString();
        String contentStr = content.getText().toString();
        if (TextUtils.isEmpty(titleStr)) {
            DisplayToast("请输入一个标题");
            title.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(contentStr)) {
            DisplayToast("请输入新鲜事");
            content.requestFocus();
            return;
        }
        releaseNews();
    }

    //发布新动态
    private void releaseNews() {
        String titleStr = title.getText().toString();
        String contentStr = content.getText().toString();
        String url;

        //图片这个没写完
//        if (imageFile != null && imageFile.exists()) {
//            url = Constants.BASE_URL + "News?method=releaseNewsWithImage";
//            OkHttpUtils
//                    .post()
//                    .addFile("image", imageFile.getName(), imageFile)
//                    .url(url)
//                    .id(1)
//                    .addHeader("content-Type", "multipart/form-data; boundary=" + UUID.randomUUID().toString())
//                    .addParams("title", titleStr)
//                    .addParams("content", contentStr)
//                    .addParams("userId", Constants.USER.getUserId() + "")
//                    .build()
//                    .execute(new MyStringCallback());
//        } else {
            url = Constants.BASE_URL + "News?method=releaseNewsWithoutImage";
            OkHttpUtils
                    .post()
                    .url(url)
                    .id(1)
                    .addParams("title", titleStr)
                    .addParams("content", contentStr)
                    .addParams("userId", Constants.USER.getUserId() + "")
                    .build()
                    .execute(new MyStringCallback());
//        }
    }
    //参考网上获取相册图片的方法，用的fishBun，这个好像新版gradle会报错，我还特意用了旧版本的
    private void getPhoto() {
        FishBun.with(com.example.genesport.view.ReleaseNewsActivity.this)
                .setPickerCount(5) //Deprecated
                .setMaxCount(1)
                .setMinCount(1)
                .setPickerSpanCount(4)
                .setActionBarColor(Color.parseColor("#DB4A37"), Color.parseColor("#DB4A37"), false)
                .setActionBarTitleColor(Color.parseColor("#ffffff"))
                .setAlbumSpanCount(2, 4)
                .setButtonInAlbumActivity(false)
                .setCamera(true)
                .setReachLimitAutomaticClose(true)
                .setAllViewTitle("所有图片")
                .setActionBarTitle("图片库")
                .textOnImagesSelectionLimitReached("已选数量受限！")
                .textOnNothingSelected("你还没有选择图片哟~")
                .startAlbum();
    }

    //发布后存图片的地方，这个跟图片一样，没写完
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);
        switch (requestCode) {
            case Define.ALBUM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
//                    path = imageData.getStringArrayListExtra(Define.INTENT_PATH);

                    //You can get image path(ArrayList<String>) Under version 0.6.2

                    path = imageData.getParcelableArrayListExtra(Define.INTENT_PATH);

                    Uri uri = path.get(0);

                    photo.setImageURI(uri);

                    String[] proj = {MediaStore.Images.Media.DATA};

                    Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);

                    int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                    actualimagecursor.moveToFirst();

                    String img_path = actualimagecursor.getString(actual_image_column_index);

                    imageFile = new File(img_path);

                    //You can get image path(ArrayList<Uri>) Version 0.6.2 or later
                    break;
                }
        }
    }

    //动态上传到服务器
    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {
            switch (id) {
                case 1:
                    if (response.contains("success")) {
                        DisplayToast("新鲜事发布成功");
                        finish();
                    } else {
                        DisplayToast("请稍后再试");
                    }
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
