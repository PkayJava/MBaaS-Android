package com.angkorteam.mbaas.sdk.android.example;

import android.content.pm.PackageManager;

import com.angkorteam.mbaas.sdk.android.library.MBaaS;
import com.angkorteam.mbaas.sdk.android.library.MBaaSClient;

import java.util.Arrays;
import java.util.List;

/**
 * Created by socheat on 4/8/16.
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MBaaS.initialize(this);
    }

}
