package com.angkorteam.mbaas.sdk.android.library.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.angkorteam.mbaas.sdk.android.library.MBaaSApplication;
import com.angkorteam.mbaas.sdk.android.library.MBaaSClient;
import com.angkorteam.mbaas.sdk.android.library.request.device.DeviceRegisterRequest;
import com.angkorteam.mbaas.sdk.android.library.response.device.DeviceRegisterResponse;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by socheat on 4/8/16.
 */
public abstract class MBaaSRegistrationIntentService extends IntentService {

    private static final String[] TOPICS = {"global"};

    private static final String TAG = MBaaSRegistrationIntentService.class.getName();

    public MBaaSRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        MBaaSApplication application = null;
        if (getApplication() instanceof MBaaSApplication) {
            application = (MBaaSApplication) getApplication();
        }

        if (application != null && application.getSenderId() != null && !"".equals(application.getSenderId())) {
            try {
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(application.getSenderId(), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                // [END get_token]
                Log.i(TAG, "GCM Registration Token: " + token);

                MBaaSClient client = ((MBaaSApplication) getApplication()).getMBaaSClient();
                DeviceRegisterRequest request = new DeviceRegisterRequest();
                request.setClientId(application.getMBaaSClientId());
                request.setClientSecret(application.getMBaaSClientSecret());
                request.setAlias(application.getMBaaSAlias());
                request.setCategories(application.getMBaaSCategories());
                request.setDeviceType(application.getMBaaSDeviceType());
                request.setOperatingSystem(application.getMBaaSOperatingSystem());
                request.setOsVersion(application.getMBaaSOsVersion());
                request.setDeviceToken(token);
                Call<DeviceRegisterResponse> responseCall = client.deviceRegister(request);
                Response<DeviceRegisterResponse> response = responseCall.execute();
                DeviceRegisterResponse responseBody = response.body();
                if (responseBody.getHttpCode() != 200) {
                    throw new IOException(response.body().getResult());
                }
                sharedPreferences.edit().putString(MBaaSClient.ACCESS_TOKEN, responseBody.getData().getAccessToken()).apply();
                // Subscribe to topic channels
                subscribeTopics(token);

                onRegistrationCompleted();
            } catch (Exception e) {
                onRegistrationFailed(e.getMessage());
            }
        }
    }

    protected abstract void onRegistrationCompleted();

    protected abstract void onRegistrationFailed(String reason);

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

}