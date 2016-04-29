package com.angkorteam.mbaas.sdk.android.example;

import com.angkorteam.mbaas.sdk.android.library.MBaaS;

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
