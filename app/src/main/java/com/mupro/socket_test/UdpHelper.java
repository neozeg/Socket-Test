package com.mupro.socket_test;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.LogRecord;

/**
 * Created by Administrator on 2016/6/14.
 */
public class UdpHelper implements Runnable{

    private static final String TAG = "UdpHelper";

    public  final static  String ACTION_UDP_MSG_RECEIVE = "action.udp_msg_receive";
    public  final static  String EXTRA_UDP_MSG_RECEIVE = "message.udp_msg_receive";
    public  final static  int MSG_UDP_MSG_RECEIVE = 1;

    private static final String SERVER_IP = "192.168.191.1";
    public    Boolean IsThreadDisable = false;//指示监听线程是否终止
    private static WifiManager.MulticastLock lock;
    InetAddress mInetAddress;

    private Context mContext;

    public UdpHelper(Context context){
        mContext = context;
    }
    public UdpHelper(WifiManager manager) {
        this.lock= manager.createMulticastLock("UDPwifi");
    }

    public UdpHelper(WifiManager manager,Context context) {
        this.lock= manager.createMulticastLock("UDPwifi");
        mContext = context;
    }

    public void UdpProcessing(){

    }

    public void StartListen()  {
        // UDP服务器监听的端口
        Integer port = 6667;
        // 接收的字节大小，客户端发送的数据不能超过这个大小
        byte[] message = new byte[100];
        try {
            // 建立Socket连接
            DatagramSocket datagramSocket = new DatagramSocket(port);
            datagramSocket.setBroadcast(true);
            DatagramPacket datagramPacket = new DatagramPacket(message,
                    message.length);
            try {
                while (!IsThreadDisable) {
                    // 准备接收数据
                    Log.d(TAG, "准备接受");
                    this.lock.acquire();

                    datagramSocket.receive(datagramPacket);
                    //broadcastUDPMsg(ACTION_UDP_MSG_RECEIVE,datagramPacket.getData());
                    Message msg = new Message();
                    msg.what = MSG_UDP_MSG_RECEIVE;
                    Bundle data = new Bundle();
                    byte[] bytes = new byte[datagramPacket.getLength()];
                    System.arraycopy(datagramPacket.getData(),0,bytes,0,bytes.length);
                    data.putByteArray(EXTRA_UDP_MSG_RECEIVE,bytes);
                    msg.setData(data);
                    msgHandler.sendMessage(msg);
                    String strMsg=new String(datagramPacket.getData()).trim();
                    Log.d(TAG, datagramPacket.getAddress()
                            .getHostAddress().toString()
                            + ":" +strMsg );
                    this.lock.release();
                }
            } catch (IOException e) {//IOException
                e.printStackTrace();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    public static void send(String message) {
        message = (message == null ? "Hello IdeasAndroid!" : message);
        int server_port = 6666;
        int client_port = 6668;
        Log.d(TAG, "UDP发送数据:"+message);
        DatagramSocket s = null;
        try {
            s = new DatagramSocket(client_port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        InetAddress local = null;
        try {
            local = InetAddress.getByName(SERVER_IP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        int msg_length = message.length();
        byte[] messageByte = message.getBytes();
        DatagramPacket p = new DatagramPacket(messageByte, msg_length, local,
                server_port);
        try {
            s.send(p);
            s.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        StartListen();
    }

    private Handler msgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == MSG_UDP_MSG_RECEIVE){
                byte[] data = msg.getData().getByteArray(EXTRA_UDP_MSG_RECEIVE);
                broadcastUDPMsg(ACTION_UDP_MSG_RECEIVE,data);
            }else{
                super.handleMessage(msg);
            }
        }
    };

    private void broadcastUDPMsg(final String action,byte[] data){
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(EXTRA_UDP_MSG_RECEIVE,data);
        mContext.sendBroadcast(intent);
    }
}
