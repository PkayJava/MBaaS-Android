package com.angkorteam.mbaas.sdk.android.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.angkorteam.mbaas.sdk.android.library.request.device.DeviceRegisterRequest;
import com.angkorteam.mbaas.sdk.android.library.response.device.DeviceRegisterResponse;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.apache.commons.configuration.XMLPropertiesConfiguration;

import java.io.IOException;
import java.util.Arrays;

import retrofit2.Call;

/**
 * Created by Khauv Socheat on 4/17/2016.
 */
public class MBaaSUtils {

    public static String requestGcm(SharedPreferences sharedPreferences, Context context, XMLPropertiesConfiguration configuration) throws IOException {
        InstanceID instanceID = InstanceID.getInstance(context);
        String token = instanceID.getToken(configuration.getString(MBaaS.SENDER_ID), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        // [END get_token]
        Log.i("MBaaS", "GCM Registration Token: " + token);

        MBaaSClient client = MBaaS.getInstance().getClient();

        DeviceRegisterRequest request = new DeviceRegisterRequest();
        request.setClientId(configuration.getString(MBaaS.CLIENT_ID));
        request.setClientSecret(configuration.getString(MBaaS.CLIENT_SECRET));
        if (configuration.getString(MBaaS.ALIAS) != null && !"".equals(configuration.getString(MBaaS.ALIAS))) {
            request.setAlias(configuration.getString(MBaaS.ALIAS));
        }
        if (configuration.getString(MBaaS.CATEGORY) != null && !"".equals(configuration.getString(MBaaS.CATEGORY))) {
            request.setCategories(Arrays.asList(configuration.getString(MBaaS.CATEGORY)));
        }
        if (configuration.getString(MBaaS.DEVICE_TYPE) != null && !"".equals(configuration.getString(MBaaS.DEVICE_TYPE))) {
            request.setDeviceType(configuration.getString(MBaaS.DEVICE_TYPE));
        }
        if (configuration.getString(MBaaS.OPERATING_SYSTEM) != null && !"".equals(configuration.getString(MBaaS.OPERATING_SYSTEM))) {
            request.setOperatingSystem(configuration.getString(MBaaS.OPERATING_SYSTEM));
        }
        if (configuration.getString(MBaaS.OS_SYSTEM) != null && !"".equals(configuration.getString(MBaaS.OS_SYSTEM))) {
            request.setOsVersion(configuration.getString(MBaaS.OS_SYSTEM));
        }
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
