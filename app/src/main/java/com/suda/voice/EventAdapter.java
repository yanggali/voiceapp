package com.suda.voice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Edited by YangJiali on 2016/9/11 0011.
 */
public class EventAdapter extends BaseAdapter {
    private Context mContext;
    private List<Event> mData;

    public EventAdapter(Context mContext, List<Event> mData) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.event_item,parent,false);
            holder = new ViewHolder();
            holder.txt_eTitle = (TextView) convertView.findViewById(R.id.item_event_title);
            holder.txt_eType = (TextView) convertView.findViewById(R.id.item_event_type);
            convertView.setTag(holder);   //将Holder存储到convertView中
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txt_eTitle.setText(mData.get(position).getTitle());
        holder.txt_eType.setText(mData.get(position).getType());

        return convertView;
    }
    static class ViewHolder{
        TextView txt_eTitle;
        TextView txt_eType;
    }
}
