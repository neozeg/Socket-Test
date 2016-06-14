package com.mupro.socket_test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/14.
 */

public class NetworkServer{
        private static int ID = 1;

        /**TCP端口 */
        private static final int TCP_PORT = 8000;
        /**UDP端口 */
        private static final int UDP_PORT = 9999;


        /**与服务器已经建立链接的客户端数量 */
        private ArrayList<Client> clients = new ArrayList<Client>();

        public static void main(String args[]) {
            new NetworkServer().start();
        }

        private void start() {
            new UDPThread().start();
            ServerSocket serverSocket = null;

            try {
                serverSocket = new ServerSocket(TCP_PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (true) {
                Socket socket = null;
                try {
                    socket = serverSocket.accept();
                    System.out.println("socket="+socket);


                    String IP = socket.getInetAddress().getHostAddress();
                    Client c = new Client(IP);
                    clients.add(c);

                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    out.writeInt(ID++);

                    System.out.println("一个新的客户端已连接，IP:"+IP+";port="+socket.getPort()+"; ID="+(ID-1));
                } catch (IOException e) {
                    e.printStackTrace();
                }finally{
                    if(socket != null) {
                        try {
                            socket.close();
                            socket = null;
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }

        }

        /**
         * 客户端：ip地址和udp端口
         * @author
         * zhoujianghai
         * 2011-5-15
         * 下午04:41:09
         */
        private class Client {
            String IP;
            int udpPort;

            public Client(String IP) {
                this.IP = IP;
            }
            public void setUdpPort(int udpPort) {
                this.udpPort = udpPort;
            }
        }

        /**
         * 接收客户端发送的数据，并把接收到的数据发送给所有客户端,使用UDP协议
         * @author
         * zhoujianghai
         * 2011-5-15
         * 下午04:54:03
         */
        private class UDPThread extends Thread {
            //服务器每次收发数据的缓冲区，大小是1024个字节
            byte[] buf = new byte[1024];

            public void run() {
                DatagramSocket ds = null;
                try {
                    ds = new DatagramSocket(UDP_PORT);
                } catch (SocketException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                while(ds != null) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);

                    try {
                        //接收数据包
                        ds.receive(packet);
                        System.out.println("地址:"+packet.getAddress()+"  端口:"+packet.getPort()+"数据："+new String(packet.getData()));
                        String clientIp =  (packet.getAddress().toString().split("/")[1]);
                        for(Client c:clients) {

                            if(clientIp.trim().equals(c.IP) && c.udpPort == 0) {
                                c.setUdpPort(packet.getPort());
                            }
                        }
                        //把接收到的数据包发送给所有已连接的客户端
                        System.out.println("clients.size="+clients.size());
                        for(Client c:clients) {
                            System.out.println("server send:IP="+c.IP+"; udpPort="+c.udpPort);
                            //设置数据包要发送的客户端地址
                            packet.setSocketAddress(new InetSocketAddress(c.IP, c.udpPort));
                            ds.send(packet);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

}
