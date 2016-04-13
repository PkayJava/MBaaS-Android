package com.angkorteam.mbaas.sdk.android.library.gcm;

import android.content.Intent;

import com.angkorteam.mbaas.sdk.android.library.MBaaSApplication;
import com.angkorteam.mbaas.sdk.android.library.MBaaSIntentService;

/**
 * Created by socheat on 4/8/16.
 */
public class MBaaSInstanceIDListenerService extends com.google.android.gms.iid.InstanceIDListenerService {

    public MBaaSInstanceIDListenerService() {
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
        MBaaSApplication application = null;
        if (getApplication() instanceof MBaaSApplication) {
            application = (MBaaSApplication) getApplication();
        }
        if (application == null) {
            return;
        }
        Intent intent = new Intent(this, MBaaSIntentService.class);
        intent.putExtra(MBaaSIntentService.SERVICE, MBaaSIntentService.SERVICE_GCM_TOKEN);
        intent.putExtra(MBaaSIntentService.SENDER_ID, application.getSenderId());
        startService(intent);
    }
    // [END refresh_token]
}