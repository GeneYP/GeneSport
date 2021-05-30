package com.example.genesport.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.genesport.R;
import com.example.genesport.entity.NewsListForFound;

import java.util.List;

//动态页的Adapter
public class FoundNewsAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private List<NewsListForFound> mList;

    public FoundNewsAdapter(Context mContext, List<NewsListForFound> mList) {
        this.inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_found_news, null);
            holder = new ViewHolder();
            holder.bg = (ImageView) convertView.findViewById(R.id.found_list_icon);
            holder.title = (TextView) convertView.findViewById(R.id.found_list_item_title);
            holder.username = (TextView) convertView.findViewById(R.id.found_list_item_username);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //把获取到的用户名和标题赋值给控件
        NewsListForFound news = mList.get(position);
        holder.title.setText(news.getTitle());
        holder.username.setText(news.getUsername());
        //0用女生图，1是男生图
        if (news.getSex() == 0) {
            holder.bg.setImageResource(R.drawable.topic_female);
        } else {
            holder.bg.setImageResource(R.drawable.topic_male);
        }

        return convertView;
    }

    private class ViewHolder {
        public ImageView bg;
        public TextView title;
        public TextView username;
    }
}
