package com.example.genesport.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.example.genesport.R;
import com.example.genesport.adapter.NewsDetailCommentsAdapter;
import com.example.genesport.entity.Comment;
import com.example.genesport.entity.NewsDetail;
import com.example.genesport.utils.Constants;
import com.example.genesport.utils.DateUtils;
import com.example.genesport.view.base.BaseActivity;
import com.example.genesport.widgets.ListViewWithScrollView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

//动态类的Activity
public class NewsDetailActivity extends BaseActivity implements View.OnClickListener {

    private NewsDetailCommentsAdapter adapter;
    private NewsDetail newsDetail;
    private List<Comment> mList;

    private String TITLE_NAME = "新鲜事详情";
    private View title_back;
    private TextView titleText;

    private TextView usernameTV;
    private TextView titleTV;
    private TextView releaseTimeTV;
    private ImageView imageIV;
    private TextView contentTV;

    private LinearLayout commentPane;
    private EditText addCommentET;
    private ImageView addCommentIV;
    private boolean isShowCommentPane;


    private ListViewWithScrollView commentsLV;
    private LinearLayout commentLL;
    private LinearLayout favorLL;

    private Context mContext;

    private int newsId;
    private String replyUsername;


    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        newsId = getIntent().getIntExtra("newsId", 0);
        setContentView(R.layout.activity_news_detail);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        this.title_back = $(R.id.title_back);
        this.titleText = $(R.id.titleText);

        usernameTV = $(R.id.news_detail_username);
        releaseTimeTV = $(R.id.news_detail_time);
        imageIV = $(R.id.news_detail_image);
        contentTV = $(R.id.news_detail_content);
        titleTV = $(R.id.detail_title);

        commentLL = $(R.id.news_detail_add_comment);
        favorLL = $(R.id.news_detail_add_favor);

        commentPane = $(R.id.news_detail_add_commment_pane);
        addCommentET = $(R.id.news_detail_add_commment_text);
        addCommentIV = $(R.id.news_detail_add_commment_btn);

        commentsLV = $(R.id.news_detail_comment);
    }

    @Override
    protected void initView() {
        mContext = this;
        this.titleText.setText(TITLE_NAME);

        this.title_back.setOnClickListener(this);
        commentLL.setOnClickListener(this);
        favorLL.setOnClickListener(this);
        addCommentIV.setOnClickListener(this);

        refreshData();
    }

    //初始化数据用的
    private void refreshData() {
        String url = Constants.BASE_URL + "News?method=getNewsDetail";
        OkHttpUtils
                .post()
                .url(url)
                .id(1)
                .addParams("newsId", newsId + "")
                .build()
                .execute(new MyStringCallback());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.news_detail_add_comment:
                showCommentBar();
                break;
            case R.id.news_detail_add_favor:
                addNewFavor();
                break;
            case R.id.news_detail_add_commment_btn:
                addNewComment();
                break;
        }
    }

    private void showCommentBar() {
        isShowCommentPane = !isShowCommentPane;
        if (isShowCommentPane) {
            //显示发表新评论的框框
            commentPane.setVisibility(View.VISIBLE);
            addCommentET.setHint("发表新评论");
            replyUsername = "";
            addCommentET.requestFocus();
        } else {
            commentPane.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            //发表完评论收起输入框，免得占屏幕
            imm.hideSoftInputFromWindow(addCommentET.getWindowToken(), 0);
            // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    // 添加新评论
    private void addNewComment() {
        String commentText = addCommentET.getText().toString().trim();
        if (TextUtils.isEmpty(commentText)) {
            DisplayToast("请先输入内容");
            return;
        }
        //去服务器加入
        String url = Constants.BASE_URL + "Comment?method=addNewComment";
        OkHttpUtils
                .post()
                .url(url)
                .id(3)
                .addParams("newsId", newsId + "")
                .addParams("userId", Constants.USER.getUserId() + "")
                .addParams("comment", commentText)
                .addParams("replyUser", replyUsername)
                .build()
                .execute(new MyStringCallback());
    }

    //收藏，这个也没写完
    private void addNewFavor() {
        String url = Constants.BASE_URL + "Favor?method=addNewFavor";
        OkHttpUtils
                .post()
                .url(url)
                .id(2)
                .addParams("newsId", newsId + "")
                .addParams("userId", Constants.USER.getUserId() + "")
                .build()
                .execute(new MyStringCallback());
    }

    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {
            //去服务器获取响应回来的动态
            switch (id) {
                case 1://获取动态和评论
                    Gson gson = new Gson();
                    try {
                        NewsDetail newsDetail = gson.fromJson(response, NewsDetail.class);
                        if (newsDetail != null) {
                            usernameTV.setText(newsDetail.getUsername());
                            releaseTimeTV.setText(newsDetail.getReleaseTime());
                            titleTV.setText(newsDetail.getTitle());
                            contentTV.setText(newsDetail.getContent());
                            // 加载图片，没写完
                            if (!TextUtils.isEmpty(newsDetail.getImage())) {
                                imageIV.setVisibility(View.VISIBLE);
                                imageIV.setImageResource(R.drawable.default_image);
//                                getNewsImage(newsDetail.getImage());
                            } else {
                                imageIV.setVisibility(View.GONE);
                            }
                        }
                        mList = newsDetail.getComments();
                    } catch (Exception e) {
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        mList = null;
                    }
                    if (mList != null && mList.size() > 0) {
                        adapter = new NewsDetailCommentsAdapter(mContext, mList);
                        adapter.setOnCommentButtonClickListener(new NewsDetailCommentsAdapter.OnCommentButtonClickListener() {

                            @Override
                            public void OnCommentButtonClicked(String replyUser) {
                                commentPane.setVisibility(View.VISIBLE);
                                addCommentET.setHint("回复 " + replyUser + " 的评论");
                                replyUsername = replyUser;
                            }
                        });
                        commentsLV.setAdapter(adapter);
                    }
                    break;
                case 2://收藏
                    DisplayToast(response);
                    break;
                case 3://写评论
                    if (response.contains("error")) {
                        DisplayToast("请稍后再试..");
                    } else {
                        DisplayToast(response);
                        Comment comment = new Comment();
                        comment.setCommentTime(DateUtils.getCurrentDatetime());
                        comment.setComment(addCommentET.getText().toString());
                        comment.setReplyUser(replyUsername);
                        comment.setUsername(Constants.USER.getUsername());
                        if (mList == null) {
                            mList = new ArrayList<Comment>();
                        }
                        mList.add(0, comment);
                        if (adapter == null) {
                            adapter = new NewsDetailCommentsAdapter(mContext, mList);
                            commentsLV.setAdapter(adapter);
                        }
                        adapter.notifyDataSetChanged();
                        replyUsername = "";
                        addCommentET.setText("");
                        commentPane.setVisibility(View.GONE);
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

//    private void getNewsImage(String imageName) {
//        String url = Constants.BASE_URL + "Download?method=getNewsImage";
//        //写图片来着，这个没实现
//        OkHttpUtils
//                .get()//
//                .url(url)//
//                .addParams("imageName", imageName)
//                .build()//
//                .execute(new BitmapCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int i) {
//                        DisplayToast("无法获取图片");
//                    }
//
//                    @Override
//                    public void onResponse(Bitmap bitmap, int i) {
//                        imageIV.setImageBitmap(bitmap);
//                    }
//                });
//    }
}
