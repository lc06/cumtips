package com.cumtips.android.BlueToothUtil;

public class BlueDevice {
    private String device_name;
    private String device_addr;
    private int device_rssi;
    public BlueDevice(String device_name,String device_addr,int device_rssi){
        this.device_name = device_name;
        this.device_addr = device_addr;
        this.device_rssi = device_rssi;
    }

    public String getDevice_name() {
        return device_name;
    }

    public String getDevice_addr() {
        return device_addr;
    }

    public int getDevice_rssi() {
        return device_rssi;
    }
}
