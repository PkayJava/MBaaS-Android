package com.angkorteam.mbaas.sdk.android.example.gcm;

import android.content.Intent;
import android.util.Log;

import com.angkorteam.mbaas.sdk.android.example.MainActivity;

/**
 * Created by socheat on 4/8/16.
 */
public class InstanceIDListenerService extends com.google.android.gms.iid.InstanceIDListenerService {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        Log.i(MainActivity.TAG, "InstanceIDListenerService.onTokenRefresh");
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
    // [END refresh_token]
}