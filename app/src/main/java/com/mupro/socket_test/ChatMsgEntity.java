package com.mupro.socket_test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by Administrator on 2016/6/15.
 */
public class ChatMsgEntity {
    private final static String DATA_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private Date mDate;
    private String mName;
    private String mMessage;
    private boolean mMsgType = false;

    public ChatMsgEntity(){
        mDate = Calendar.getInstance().getTime();
        mName = "";
        mMessage = "";
    }
    public void setDate(Date date){
        mDate = date;
    }
    public void setName(String name){
        mName = name;
    }
    public void setMessageContent(String msg){
        mMessage = msg;
    }
    public void setMsgType(boolean type){
        mMsgType = type;
    }

    public boolean getMsgType(){
       return mMsgType;
    }
    public String getDate(){
        DateFormat df = new SimpleDateFormat(DATA_FORMAT);
        return df.format(mDate);
    }
    public String getMessageContent(){
        return  mMessage;
    }
    public String getName(){
        return mName;
    }
}
