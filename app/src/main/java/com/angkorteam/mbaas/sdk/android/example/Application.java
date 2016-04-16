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
        return "b8a185e5-cf3e-4ba0-a101-764f6b418157";
//        return "912a0778-7664-4761-ba0d-ab0de2436307";
    }

    @Override
    public String getMBaaSClientSecret() {
        return "c6754e0a-9188-4a1a-bdc1-e9711daa6020";
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
