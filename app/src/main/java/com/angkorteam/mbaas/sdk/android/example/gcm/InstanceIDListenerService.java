package com.angkorteam.mbaas.sdk.android.example.gcm;

import com.angkorteam.mbaas.sdk.android.library.gcm.MBaaSInstanceIDListenerService;

/**
 * Created by socheat on 4/8/16.
 */
public class InstanceIDListenerService extends MBaaSInstanceIDListenerService {

    public InstanceIDListenerService() {
        super(RegistrationIntentService.class);
    }

}