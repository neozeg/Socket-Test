package com.mupro.socket_test;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


import java.io.DataInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;


/**
 * Created by Administrator on 2016/6/15.
 */
public class NetTool {
    private final static String TAG = "NetTool";

    public final static String ACTION_FOUND_HOST = "action.found.host";
    public final static String EXTRA_HOST_IP = "extra.host.ip";

    private final static int SERVER_PORT = 8888;
    private String locAddress;//local IP
    private Runtime runtime = Runtime.getRuntime();//获取运行环境来执行ping
    private Process proc = null;
    //private String ping = "ping -c 1 -w 0.5 "; //-c 1为发送的次数，-w 为发送后等待相应的时间
    private String ping = "ping -c 1 -w 0.5 "; //-n 1为发送的次数，-w 为发送后等待相应的时间
    private int j;//存放ip最后一位地址0-255
    private Context mContext;

    public NetTool(Context context){
        mContext = context;
    }

    private Handler handler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            //super.dispatchMessage(msg);
            switch (msg.what){
                case 111://ping successed
                    String ip = msg.getData().getString(EXTRA_HOST_IP);
                    Intent intent = new Intent();
                    intent.setAction(ACTION_FOUND_HOST);
                    intent.putExtra(EXTRA_HOST_IP,ip);
                    mContext.sendBroadcast(intent);
                    break;
                case 222://server msg
                    break;
                case 333://scan done
                    Toast.makeText(mContext,"scan host:"+((String)msg.obj).substring(6),Toast.LENGTH_SHORT).show();
                    break;
                case 444://scan failed
                    Toast.makeText(mContext,(String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public String sendMsg(String ip,String msg){
        String res = null;
        Socket socket = null;
        try{
            socket = new Socket(ip,SERVER_PORT);
            //send msg to server
            PrintWriter os = new PrintWriter(socket.getOutputStream());
            os.println(msg);
            os.flush();//refresh outputstream, make server receive immediately
            //get msg from server
            DataInputStream input = new DataInputStream(socket.getInputStream());
            res = input.readUTF();
            System.out.println("Server return msg: "+ res);
            Message.obtain(handler,222,res).sendToTarget();
        } catch (Exception e) {
            System.out.println("you are trying to connect to an unknown host!");
            e.printStackTrace();
        } finally {
            try{
                if(socket !=null){
                    socket.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return res;
    }
    public void scanHost(){
        locAddress = getLocAddrIndex();
        if(locAddress.equals("")){
            Toast.makeText(mContext,"Scan failed, please check your wifi network", Toast.LENGTH_LONG).show();
            return;
        }
        for(int i =0 ;i<256 ; i++){
            j=i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String p = NetTool.this.ping + locAddress + NetTool.this.j;
                    String current_ip = locAddress +NetTool.this.j;
                    //Log.i(TAG,p);
                    try {
                        proc = runtime.exec(p);
                        int result = proc.waitFor();
                        if(result == 0){
                            Log.i(TAG,"Connect sucessfully:" + current_ip);
                            //Message.obtain(handler,111).sendToTarget();
                            Message msg = new Message();
                            msg.what = 111;
                            Bundle data = new Bundle();
                            data.putString(EXTRA_HOST_IP,current_ip);
                            msg.setData(data);
                            handler.sendMessage(msg);
                        }else{
                            //Toast.makeText(mContext, "ping测试失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        proc.destroy();
                    }
                }
            }).start();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void scan(){
        locAddress = getLocAddrIndex();
        if(locAddress.equals("")){
            Toast.makeText(mContext,"Scan failed, please check your wifi network", Toast.LENGTH_LONG).show();
            return;
        }
        for(int i=0;i<256;i++){
            j=i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String p = NetTool.this.ping + locAddress + NetTool.this.j;
                    String current_ip = locAddress +NetTool.this.j;
                    try{
                        proc = runtime.exec(p);
                        int result = proc.waitFor();
                        if(result == 0){
                            //Toast.makeText(mContext, "ping连接成功", Toast.LENGTH_SHORT).show();
                            System.out.println("Connect sucessfully:" + current_ip);
                            String msg = sendMsg(current_ip,"scan"+getLocAddress()+" ( "+ Build.MODEL + " ) ");
                            if(msg != null){
                                if(msg.contains("OK")){
                                    System.out.println("Server IP: " + msg.substring(8,msg.length()));
                                    Message.obtain(handler,333,msg.substring(2,msg.length())).sendToTarget();
                                }
                            }
                        }else{
                            //Toast.makeText(mContext, "ping测试失败", Toast.LENGTH_SHORT).show();
                        }

                    }catch (IOException e1){
                      e1.printStackTrace();
                    }catch (InterruptedException e2){
                        e2.printStackTrace();
                    } finally {
                        proc.destroy();
                    }
                }
            }).start();
        }
    }


    public String getLocAddress(){
        String ipaddress = "";
        try{
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()){
                NetworkInterface networkInterface = en.nextElement();
                Enumeration<InetAddress> address = networkInterface.getInetAddresses();
                while (address.hasMoreElements()){
                    InetAddress ip = address.nextElement();
                    //if(!ip.isLoopbackAddress() && Inet4Address.class.isInstance(ip.getHostAddress()) && InetAddressUtils.isIPv4Address(ip.getHostAddress())){
                    if(!ip.isLoopbackAddress() && isValidIp4Address(ip.getHostAddress())){
                        ipaddress = ip.getHostAddress();
                    }
                }
            }
        }catch (SocketException e){
            Log.e(TAG,"获取本地ip失败");
            e.printStackTrace();
        }
        Log.i(TAG,"local addres: " +ipaddress);
        return ipaddress;

    }

    public String getLocAddrIndex(){
        String str = getLocAddress();
        if(!str.equals("")){
            return  str.substring(0,str.lastIndexOf(".")+1);
        }
        return null;
    }

    public static boolean isValidIp4Address(final String hostName) {
        try {
            return Inet4Address.getByName(hostName) != null;
        } catch (UnknownHostException ex) {
            return false;
        }
    }

    public static boolean isValidIp6Address(final String hostName) {
        try {
            return Inet6Address.getByName(hostName) != null;
        } catch (UnknownHostException ex) {
            return false;
        }
    }
}
