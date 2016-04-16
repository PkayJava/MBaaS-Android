package com.angkorteam.mbaas.sdk.android.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.angkorteam.mbaas.sdk.android.library.request.device.DeviceRegisterRequest;
import com.angkorteam.mbaas.sdk.android.library.response.device.DeviceRegisterResponse;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import retrofit2.Call;

/**
 * Created by Khauv Socheat on 4/17/2016.
 */
public class MBaaSUtils {

    public static String requestGcm(SharedPreferences sharedPreferences, Context context, MBaaSApplication application) throws IOException {
        InstanceID instanceID = InstanceID.getInstance(context);
        String token = instanceID.getToken(application.getSenderId(), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        // [END get_token]
        Log.i("MBaaS", "GCM Registration Token: " + token);

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
        GcmPubSub pubSub = GcmPubSub.getInstance(context);
        pubSub.subscribe(token, "/topics/global", null);
        return token;
    }
}
