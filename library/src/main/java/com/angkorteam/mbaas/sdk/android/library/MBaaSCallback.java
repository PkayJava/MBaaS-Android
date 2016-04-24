package com.angkorteam.mbaas.sdk.android.library;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.angkorteam.mbaas.sdk.android.library.response.oauth2.OAuth2RefreshResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.WeakHashMap;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by socheat on 4/14/16.
 */
public class MBaaSCallback<T extends Response> implements Callback<T> {

    private final int eventId;

    private final NetworkBroadcastReceiver broadcastReceiver;

    private final Activity activity;

    private final Gson gson;

    public MBaaSCallback(int eventId, Activity activity, NetworkBroadcastReceiver broadcastReceiver) {
        this.eventId = eventId;
        this.gson = new Gson();
        this.broadcastReceiver = broadcastReceiver;
        this.activity = activity;
    }

    @Override
    public final void onResponse(final Call<T> call, final retrofit2.Response<T> response) {
        final T body = response.body();
        if (body != null) {
            if (body.getHttpCode() == 403 || body.getHttpCode() == 401) {
                Intent intent = new Intent(broadcastReceiver.getUuid());
                intent.putExtra(NetworkBroadcastReceiver.EVENT, NetworkBroadcastReceiver.EVENT_LOGIN);
                intent.putExtra(NetworkBroadcastReceiver.EVENT_ID, this.eventId);
                intent.putExtra(NetworkBroadcastReceiver.EVENT_ACTIVITY, this.activity.getClass().getName());
                MBaaSIntentService.REVOKED.put(eventId, call.clone());
                LocalBroadcastManager.getInstance(this.activity).sendBroadcast(intent);
            } else if (body.getHttpCode() == 423) {
                MBaaSApplication application = null;
                if (this.activity.getApplicationContext() instanceof MBaaSApplication) {
                    application = (MBaaSApplication) this.activity.getApplicationContext();
                }
                if (application != null) {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.activity);
                    String refreshToken = sharedPreferences.getString(MBaaSIntentService.REFRESH_TOKEN, "");
                    Call<OAuth2RefreshResponse> responseCall = application.getMBaaSClient().oauth2Refresh(refreshToken);
                    try {
                        retrofit2.Response<OAuth2RefreshResponse> responseBody = responseCall.execute();
                        sharedPreferences.edit().putString(MBaaSIntentService.ACCESS_TOKEN, responseBody.body().getAccessToken()).apply();
                        call.clone().enqueue(this);
                    } catch (IOException e) {
                        Intent intent = new Intent(broadcastReceiver.getUuid());
                        intent.putExtra(NetworkBroadcastReceiver.EVENT_ID, this.eventId);
                        intent.putExtra(NetworkBroadcastReceiver.EVENT, NetworkBroadcastReceiver.EVENT_FAILURE);
                        intent.putExtra(NetworkBroadcastReceiver.EVENT_MESSAGE, e.getMessage());
                        LocalBroadcastManager.getInstance(this.activity).sendBroadcast(intent);
                    }
                }
            } else if (body.getHttpCode() == 500) {
                Intent intent = new Intent(broadcastReceiver.getUuid());
                intent.putExtra(NetworkBroadcastReceiver.EVENT, NetworkBroadcastReceiver.EVENT_FAILURE);
                intent.putExtra(NetworkBroadcastReceiver.EVENT_MESSAGE, body.getResult());
                intent.putExtra(NetworkBroadcastReceiver.EVENT_ID, this.eventId);
                LocalBroadcastManager.getInstance(this.activity).sendBroadcast(intent);
            } else {
                Intent intent = new Intent(broadcastReceiver.getUuid());
                intent.putExtra(NetworkBroadcastReceiver.EVENT_ID, this.eventId);
                intent.putExtra(NetworkBroadcastReceiver.EVENT, NetworkBroadcastReceiver.EVENT_RESPONSE);
                intent.putExtra(NetworkBroadcastReceiver.EVENT_JSON, gson.toJson(body));
                LocalBroadcastManager.getInstance(this.activity).sendBroadcast(intent);
            }
        } else {
            if (response.code() == 200) {
                Intent intent = new Intent(broadcastReceiver.getUuid());
                intent.putExtra(NetworkBroadcastReceiver.EVENT, NetworkBroadcastReceiver.EVENT_RESPONSE);
                intent.putExtra(NetworkBroadcastReceiver.EVENT_ID, this.eventId);
                LocalBroadcastManager.getInstance(this.activity).sendBroadcast(intent);
            } else {
                Intent intent = new Intent(broadcastReceiver.getUuid());
                intent.putExtra(NetworkBroadcastReceiver.EVENT, NetworkBroadcastReceiver.EVENT_FAILURE);
                intent.putExtra(NetworkBroadcastReceiver.EVENT_MESSAGE, response.message());
                intent.putExtra(NetworkBroadcastReceiver.EVENT_ID, this.eventId);
                LocalBroadcastManager.getInstance(this.activity).sendBroadcast(intent);
            }
        }
    }

    @Override
    public final void onFailure(final Call<T> call, final Throwable t) {
        Intent intent = new Intent(broadcastReceiver.getUuid());
        intent.putExtra(NetworkBroadcastReceiver.EVENT_ID, this.eventId);
        intent.putExtra(NetworkBroadcastReceiver.EVENT, NetworkBroadcastReceiver.EVENT_FAILURE);
        intent.putExtra(NetworkBroadcastReceiver.EVENT_MESSAGE, t.getMessage());
        LocalBroadcastManager.getInstance(this.activity).sendBroadcast(intent);
    }
}
