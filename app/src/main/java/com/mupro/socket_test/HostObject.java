package com.mupro.socket_test;

/**
 * Created by Administrator on 2016/6/16.
 */
public class HostObject {
    private String HostName;
    private String HostAddress;
    public HostObject(){
        HostName = "";
        HostAddress = "0.0.0.0";
    }

    public void setHostName(String name){
        HostName = name;
    }
    public void setHostAddress(String address){
        if(NetTool.isValidIp4Address(address)){
          HostAddress = address;
        }
    }

    public String getHostName(){
        return HostName;
    }
    public String getHostAddress(){
        return HostAddress;
    }

}
