package com.angkorteam.mbaas.sdk.android.example;

import com.angkorteam.mbaas.sdk.android.library.MBaaSApplication;
import com.angkorteam.mbaas.sdk.android.library.MBaaSClient;

import java.util.Arrays;
import java.util.List;

/**
 * Created by socheat on 4/8/16.
 */
public class Application extends android.app.Application implements MBaaSApplication {

    private MBaaSClient mbaasClient;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mbaasClient = new MBaaSClient(this, this);
    }

    @Override
    public String getSenderId() {
        return "799876230570";
    }

    @Override
    public String getMBaaSClientId() {
        return "58e9c7d4-f256-4cb6-be00-ec7d4a08caef";
    }

    @Override
    public String getMBaaSClientSecret() {
        return "66f47af6-7149-4f91-9f06-a9aefe67cf23";
    }

    @Override
    public String getMBaaSAddress() {
        return "http://pkayjava.ddns.net:9080/mbaas-server/";
//        return "http://192.168.1.114:7080/";
    }

    @Override
    public String getMBaaSDeviceType() {
        return "Android";
    }

    @Override
    public String getMBaaSAlias() {
        return null;
    }

    @Override
    public String getMBaaSOperatingSystem() {
        return "Android KitKat";
    }

    @Override
    public String getMBaaSOsVersion() {
        return "kitcat";
    }

    @Override
    public List<String> getMBaaSCategories() {
        return Arrays.asList("Test");
    }

    @Override
    public MBaaSClient getMBaaSClient() {
        return this.mbaasClient;
    }
}
