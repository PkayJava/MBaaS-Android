package com.angkorteam.mbaas.sdk.android.example;

import android.content.pm.PackageManager;
import android.util.Log;

import com.angkorteam.mbaas.sdk.android.library.MBaaSApplication;
import com.angkorteam.mbaas.sdk.android.library.MBaaSClient;
import com.angkorteam.mbaas.sdk.android.library.MBaaSIntentService;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
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
        return "5ec7c501-27a6-4480-bda5-b53b37e32e12";
//        return "912a0778-7664-4761-ba0d-ab0de2436307";
    }

    @Override
    public String getMBaaSClientSecret() {
        return "7af0e814-fd9b-48b0-bac7-f55b1e45ac0a";
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

    @Override
    public long getCacheSize() {
        return 1024 * 1024 * 100;
    }
}
