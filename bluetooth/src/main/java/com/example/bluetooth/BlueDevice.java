package com.example.bluetooth;

public class BlueDevice {
    private String deviceName;
    private String deviceMac;
    private int RSSI;

    public BlueDevice(String Name,String Mac,int Rssi){
        deviceName = Name;
        deviceMac = Mac;
        RSSI = Rssi;
    }
    public String getDeviceName(){
        return deviceName;
    }
    public String getDeviceMac(){ return deviceMac; }
    public int getRSSI() { return RSSI; }
}
