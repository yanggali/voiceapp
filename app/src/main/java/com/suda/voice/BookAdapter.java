package com.suda.voice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Edited by YangJiali on 2016/8/19 0019.
 */
public class BookAdapter extends BaseAdapter{
    private Context mContext;
    private List<Book> mData;

    public BookAdapter(Context mContext, List<Book> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }


    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.book_item,parent,false);
            holder = new ViewHolder();
            holder.txt_bName = (TextView) convertView.findViewById(R.id.item_book_name);
            holder.txt_bAuthor = (TextView) convertView.findViewById(R.id.item_book_author);
            holder.txt_bPublisher = (TextView) convertView.findViewById(R.id.item_book_publisher);
            convertView.setTag(holder);   //将Holder存储到convertView中
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txt_bName.setText(mData.get(position).getName());
        holder.txt_bAuthor.setText(mData.get(position).getbAuthor());
        holder.txt_bPublisher.setText(mData.get(position).getbPublisher());

        return convertView;
    }
    static class ViewHolder{
        TextView txt_bName;
        TextView txt_bAuthor;
        TextView txt_bPublisher;
    }
}
