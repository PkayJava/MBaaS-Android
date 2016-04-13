package com.angkorteam.mbaas.sdk.android.library;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.angkorteam.mbaas.sdk.android.library.request.device.DeviceRegisterRequest;
import com.angkorteam.mbaas.sdk.android.library.response.device.DeviceRegisterResponse;
import com.angkorteam.mbaas.sdk.android.library.response.oauth2.OAuth2AuthorizeResponse;
import com.angkorteam.mbaas.sdk.android.library.response.oauth2.OAuth2RefreshResponse;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import retrofit2.Call;

/**
 * Created by socheat on 4/13/16.
 */
public class MBaaSIntentService extends IntentService {

    private static final String TAG = MBaaSIntentService.class.getName();

    public static final String SERVICE = "service";
    public static final String SERVICE_ACCESS_TOKEN = "accessToken";
    public static final String SERVICE_REFRESH_TOKEN = "refreshToken";
    public static final String SERVICE_GCM_TOKEN = "gcmToken";

    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";

    public static final String OAUTH2_RESULT = "result";

    public static final String OAUTH2_STATE = "state";
    public static final String OAUTH2_CODE = "code";
    public static final String OAUTH2_CLIENT_ID = "client_id";
    public static final String OAUTH2_CLIENT_SECRET = "client_secret";
    public static final String OAUTH2_GRANT_TYPE = "grant_type";
    public static final String OAUTH2_GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
    public static final String OAUTH2_REDIRECT_URI = "redirect_uri";

    //    public static final String TOKEN = "token";
    public static final String SENDER_ID = "senderId";

    public static final String RECEIVER = "receiver";

    private static final String[] TOPICS = {"global"};

    public MBaaSIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MBaaSApplication application = null;
        if (getApplication() instanceof MBaaSApplication) {
            application = (MBaaSApplication) getApplication();
        }
        if (application == null) {
            return;
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (SERVICE_ACCESS_TOKEN.equals(intent.getStringExtra(MBaaSIntentService.SERVICE))) {
            String oauth2Code = intent.getStringExtra(MBaaSIntentService.OAUTH2_CODE);
            String oauth2State = intent.getStringExtra(MBaaSIntentService.OAUTH2_STATE);
            MBaaSClient client = application.getMBaaSClient();
            Call<OAuth2AuthorizeResponse> responseCall = client.oauth2Authorize(oauth2State, oauth2Code);
            retrofit2.Response<OAuth2AuthorizeResponse> response = null;
            try {
                response = responseCall.execute();
            } catch (IOException e) {
            }
            if (response != null) {
                OAuth2AuthorizeResponse responseBody = response.body();
                sharedPreferences.edit().putString(MBaaSIntentService.ACCESS_TOKEN, responseBody.getAccessToken()).apply();
                sharedPreferences.edit().putString(MBaaSIntentService.REFRESH_TOKEN, responseBody.getRefreshToken()).apply();
            }
            if (intent.hasExtra(MBaaSIntentService.RECEIVER) && intent.getStringExtra(MBaaSIntentService.RECEIVER) != null && !"".equals(intent.getStringExtra(MBaaSIntentService.RECEIVER))) {
                String receiver = intent.getStringExtra(MBaaSIntentService.RECEIVER);
                Intent receiverIntent = new Intent(receiver);
                if (response != null && response.body() != null && response.body().getHttpCode() == 200) {
                    receiverIntent.putExtra(MBaaSIntentService.OAUTH2_RESULT, Activity.RESULT_OK);
                } else {
                    receiverIntent.putExtra(MBaaSIntentService.OAUTH2_RESULT, Activity.RESULT_CANCELED);
                }
                LocalBroadcastManager.getInstance(this).sendBroadcast(receiverIntent);
                return;
            }
        } else if (SERVICE_REFRESH_TOKEN.equals(intent.getStringExtra(MBaaSIntentService.SERVICE))) {
            MBaaSClient client = application.getMBaaSClient();
            String refreshToken = sharedPreferences.getString(MBaaSIntentService.REFRESH_TOKEN, "");
            Call<OAuth2RefreshResponse> responseCall = client.oauth2Refresh(refreshToken);
            retrofit2.Response<OAuth2RefreshResponse> response = null;
            try {
                response = responseCall.execute();
            } catch (IOException e) {
            }
            if (response != null) {
                OAuth2RefreshResponse responseBody = response.body();
                sharedPreferences.edit().putString(MBaaSIntentService.ACCESS_TOKEN, responseBody.getAccessToken()).apply();
            }
        } else if (SERVICE_GCM_TOKEN.equals(intent.getStringExtra(MBaaSIntentService.SERVICE))) {
            try {
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(intent.getStringExtra(MBaaSIntentService.SENDER_ID), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                // [END get_token]
                Log.i(TAG, "GCM Registration Token: " + token);

                MBaaSClient client = application.getMBaaSClient();

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
                retrofit2.Response<DeviceRegisterResponse> response = responseCall.execute();
                DeviceRegisterResponse responseBody = response.body();
                if (response.code() != 200 || responseBody.getHttpCode() != 200) {
                    sharedPreferences.edit().putString(MBaaSIntentService.ACCESS_TOKEN, "").apply();
                    throw new IOException(response.body().getResult());
                }
                sharedPreferences.edit().putString(MBaaSIntentService.ACCESS_TOKEN, responseBody.getData().getAccessToken()).apply();
                // Subscribe to topic channels
                subscribeTopics(token);
                Log.d("sender", "Broadcasting message");
            } catch (Throwable e) {
                sharedPreferences.edit().putString(MBaaSIntentService.ACCESS_TOKEN, "").apply();
            }
        }
        if (intent.hasExtra(MBaaSIntentService.RECEIVER) && intent.getStringExtra(MBaaSIntentService.RECEIVER) != null && !"".equals(intent.getStringExtra(MBaaSIntentService.RECEIVER))) {
            String receiver = intent.getStringExtra(MBaaSIntentService.RECEIVER);
            Intent receiverIntent = new Intent(receiver);
            LocalBroadcastManager.getInstance(this).sendBroadcast(receiverIntent);
        }
    }

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
