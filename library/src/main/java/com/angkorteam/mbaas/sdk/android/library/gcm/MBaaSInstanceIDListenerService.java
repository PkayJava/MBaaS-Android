package com.angkorteam.mbaas.sdk.android.library.gcm;

import android.content.Intent;

import com.angkorteam.mbaas.sdk.android.library.MBaaS;
import com.angkorteam.mbaas.sdk.android.library.MBaaSIntentService;

import org.apache.commons.configuration.XMLPropertiesConfiguration;

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
        MBaaS mbaas = MBaaS.getInstance();
        XMLPropertiesConfiguration configuration = mbaas.getConfiguration();
        String senderId = configuration.getString(MBaaS.SENDER_ID);
        Intent intent = new Intent(this, MBaaSIntentService.class);
        intent.putExtra(MBaaSIntentService.SERVICE, MBaaSIntentService.SERVICE_GCM_TOKEN);
        intent.putExtra(MBaaSIntentService.SENDER_ID, senderId);
        startService(intent);
    }
    // [END refresh_token]
}