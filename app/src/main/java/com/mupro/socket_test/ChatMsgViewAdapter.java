package com.mupro.socket_test;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Administrator on 2016/6/15.
 */
public class ChatMsgViewAdapter extends BaseAdapter {

    private final static String TAG = "ChatMsgViewAdapter";
    private List<ChatMsgEntity> mColl;
    private Context mContext;
    private LayoutInflater mInflater;


    public ChatMsgViewAdapter(Context context,List<ChatMsgEntity> coll){
        mContext = context;
        mColl = coll;
        mInflater = LayoutInflater.from(mContext);
    }

    public static interface IMsgViewType {
        int IMVT_COM_MSG = 0;
        int IMVT_TO_MSG = 1;
    }

    @Override
    public int getCount() {
        return mColl.size();
    }

    @Override
    public Object getItem(int position) {
        return mColl.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        ChatMsgEntity entity = mColl.get(position);

        if (entity.getMsgType()) {
            return IMsgViewType.IMVT_COM_MSG;
        } else {
            return IMsgViewType.IMVT_TO_MSG;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ChatMsgEntity entity = mColl.get(position);
        boolean isComMsg = entity.getMsgType();
        ViewHolder viewHolder = null;
        if(convertView == null){
            if(isComMsg){
                convertView = mInflater.inflate(R.layout.chatting_item_msg_text_left,null);
            }else{
                convertView = mInflater.inflate(R.layout.chatting_item_msg_text_right,null);
            }

            viewHolder = new ViewHolder();
            viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.textViewSendTime);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.textViewSendName);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.textViewSendMessage);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.tvSendTime.setText(entity.getDate());
        viewHolder.tvContent.setText(entity.getMessageContent());
        viewHolder.tvContent.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        viewHolder.tvUserName.setText(entity.getName());

        return convertView;
    }


    static class ViewHolder {
        public TextView tvSendTime;
        public TextView tvUserName;
        public TextView tvContent;
        public boolean isComMsg = true;
    }


}
