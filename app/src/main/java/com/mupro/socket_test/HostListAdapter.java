package com.mupro.socket_test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;
import android.view.LayoutInflater;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/6/16.
 */
public class HostListAdapter extends BaseAdapter {
    private final static String TAG = "HostListAdapter";
    private List<HostObject> mList;
    private Context mContext;
    private LayoutInflater mInflater;

    public HostListAdapter(Context context,List<HostObject> list){
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mList = list;
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
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final HostObject host = mList.get(position);
        ViewHolder holder = null;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.hosts_list,null);
            holder = new ViewHolder();
            holder.tvHostName = (TextView) convertView.findViewById(R.id.textViewHostname);
            holder.tvHostAddress = (TextView) convertView.findViewById(R.id.textViewHostaddress);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        holder.tvHostName.setText(host.getHostName().toString());
        holder.tvHostAddress.setText(host.getHostAddress().toString());
        return convertView;
    }

    static class ViewHolder {
        public TextView tvHostName;
        public TextView tvHostAddress;
    }
}
