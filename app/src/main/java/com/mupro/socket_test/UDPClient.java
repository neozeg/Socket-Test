package com.mupro.socket_test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 2016/6/14.
 */
public class UDPClient {

    private static final int SERVER_PORT = 6666;
    private static final String SERVER_IP = "192.168.191.1";

    private DatagramSocket dSocket = null;

    private String msg;

    /**
     * @param msg
     */
    public UDPClient(String msg) {
        super();
        this.msg = msg;
    }

    /**
     * 发送信息到服务器
     */
    public String send() {
        StringBuilder sb = new StringBuilder();
        InetAddress IP = null;
        try {
            //local = InetAddress.getByName("localhost"); // 本机测试
            IP = InetAddress.getByName(SERVER_IP);
            sb.append("已找到服务器,连接中...").append("/n");
        } catch (UnknownHostException e) {
            sb.append("未找到服务器.").append("/n");
            e.printStackTrace();
        }
        try {
            dSocket = new DatagramSocket(); // 注意此处要先在配置文件里设置权限,否则会抛权限不足的异常
            sb.append("正在连接服务器...").append("/n");
        } catch (SocketException e) {
            e.printStackTrace();
            sb.append("服务器连接失败.").append("/n");
        }
        int msg_len = msg == null ? 0 : msg.length();
        DatagramPacket dPacket = new DatagramPacket(msg.getBytes(), msg_len,
                IP, SERVER_PORT);
        try {
            dSocket.send(dPacket);
            sb.append("消息发送成功!").append("/n");
        } catch (IOException e) {
            e.printStackTrace();
            sb.append("消息发送失败.").append("/n");
        }
        dSocket.close();
        return sb.toString();
    }

}
