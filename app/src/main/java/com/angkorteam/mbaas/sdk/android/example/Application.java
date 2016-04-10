package com.angkorteam.mbaas.sdk.android.example;

import android.content.pm.PackageManager;

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
        return "8d3bdc45-a9e3-4fa4-a126-6d0a70532ca5";
//        return "912a0778-7664-4761-ba0d-ab0de2436307";
    }

    @Override
    public String getMBaaSClientSecret() {
        return "52a0c53a-d10e-41bd-8584-07c631127cb3";
//        return "fd821e9b-86f3-4c23-8084-3a236c390113";
    }

    @Override
    public String getMBaaSAddress() {
        return "http://pkayjava.ddns.net:9080/mbaas-server/";
//        return "http://192.168.1.114:7080/";
    }

    @Override
    public String getMBaaSAppVersion() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "N/A";
        }
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
