package com.angkorteam.mbaas.sdk.android.library;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.angkorteam.mbaas.sdk.android.library.response.oauth2.OAuth2RefreshResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by socheat on 4/14/16.
 */
public class MBaaSCallback<T extends Response> implements Callback<T> {

    private final Activity activity;

    private final MBaaSOperation operation;

    private final int operationId;

    public MBaaSCallback(int operationId, Activity activity, MBaaSOperation operation) {
        this.activity = activity;
        this.operationId = operationId;
        this.operation = operation;
    }

    @Override
    public final void onResponse(final Call<T> call, final retrofit2.Response<T> response) {
        if (response.body() != null) {
            final T body = response.body();
            if (body != null) {
                if (body.getHttpCode() == 403 || body.getHttpCode() == 401) {
                    MBaaSApplication application = null;
                    if (this.activity.getApplication() instanceof MBaaSApplication) {
                        application = (MBaaSApplication) this.activity.getApplication();
                    }
                    if (application != null) {
                        Intent intentActivity = new Intent(this.activity, LoginActivity.class);
                        this.activity.startActivityForResult(intentActivity, operationId);
                    }
                } else if (body.getHttpCode() == 423) {
                    MBaaSApplication application = null;
                    if (this.activity.getApplication() instanceof MBaaSApplication) {
                        application = (MBaaSApplication) this.activity.getApplication();
                    }
                    if (application != null) {
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
                        String refreshToken = sharedPreferences.getString(MBaaSIntentService.REFRESH_TOKEN, "");
                        Call<OAuth2RefreshResponse> responseCall = application.getMBaaSClient().oauth2Refresh(refreshToken);
                        retrofit2.Response<OAuth2RefreshResponse> responseBody = null;
                        try {
                            responseBody = responseCall.execute();
                        } catch (IOException e) {
                        }
                        if (responseBody != null) {
                            if (responseBody.body().getHttpCode() == 200) {
                                sharedPreferences.edit().putString(MBaaSIntentService.ACCESS_TOKEN, responseBody.body().getAccessToken()).apply();
                                if (operation != null) {
                                    operation.operationRetry(operationId);
                                }
                            }
                        }
                    }
                } else {
                    this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (operation != null) {
                                operation.operationResponse(operationId, body);
                            }
                        }
                    });
                }
            }
        } else {
            this.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (operation != null) {
                        operation.operationResponse(operationId, null);
                    }
                }
            });
        }
    }

    @Override
    public final void onFailure(final Call<T> call, final Throwable t) {
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (operation != null) {
                    operation.operationResponse(operationId, null);
                }
            }
        });
    }
}
