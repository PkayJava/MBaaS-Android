package com.angkorteam.mbaas.sdk.android.library.gcm;

import android.content.Intent;

/**
 * Created by socheat on 4/8/16.
 */
public class MBaaSInstanceIDListenerService extends com.google.android.gms.iid.InstanceIDListenerService {

    private Class<? extends MBaaSRegistrationIntentService> clazz;

    public MBaaSInstanceIDListenerService(Class<? extends MBaaSRegistrationIntentService> clazz) {
        this.clazz = clazz;
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    // [START refresh_token]
    @Override
    public final void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, this.clazz);
        startService(intent);
    }
    // [END refresh_token]
}