package com.angkorteam.mbaas.sdk.android.example.gcm;

import android.util.Log;

/**
 * Created by socheat on 4/8/16.
 */
public class RegistrationIntentService extends com.angkorteam.mbaas.sdk.android.library.gcm.MBaaSRegistrationIntentService {

    @Override
    protected void onRegistrationCompleted() {
        // Notify UI that registration has completed, so the progress indicator can be hidden.
//        Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    @Override
    protected void onRegistrationFailed(String reason) {
        Log.i("MBaaS", reason);
    }

}