package com.qrcode.raspberrytvcontrol;
import android.app.Application;

public class GlobalApplication extends Application {

    private String ipAddress;

    public String getIP() {
        return ipAddress;
    }

    public void setIP(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}