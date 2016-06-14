package com.mupro.socket_test;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by Administrator on 2016/6/14.
 */
public class UDPServer implements Runnable {

    private static final int PORT = 6666    ;

    private byte[] msg = new byte[1024];

    private boolean life = true;

    public UDPServer() {
    }

    /**
     * @return the life
     */
    public boolean isLife() {
        return life;
    }

    /**
     * @param life
     *            the life to set
     */
    public void setLife(boolean life) {
        this.life = life;
    }

    @Override
    public void run() {

        DatagramSocket dSocket = null;
        DatagramPacket dPacket = new DatagramPacket(msg, msg.length);
        try {
            dSocket = new DatagramSocket(PORT);
            while (life) {
                try {
                    dSocket.receive(dPacket);
                    Log.i("msg sever received", new String(dPacket.getData()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
