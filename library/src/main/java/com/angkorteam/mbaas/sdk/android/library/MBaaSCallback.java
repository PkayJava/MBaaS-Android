package com.angkorteam.mbaas.sdk.android.library;

import android.app.Activity;
import android.content.Intent;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by socheat on 4/14/16.
 */
public abstract class MBaaSCallback<T extends Response> implements Callback<T> {

    private final Activity activity;

    private final int operationId;

    public MBaaSCallback(int operationId, Activity activity) {
        this.activity = activity;
        this.operationId = operationId;
    }

    @Override
    public final void onResponse(final Call<T> call, final retrofit2.Response<T> response) {
        if (response.body() != null && (response.body().getHttpCode() == 403 || response.body().getHttpCode() == 401)) {
            MBaaSApplication application = null;
            if (this.activity.getApplication() instanceof MBaaSApplication) {
                application = (MBaaSApplication) this.activity.getApplication();
            }
            if (application != null) {
                Intent intentActivity = new Intent(this.activity, LoginActivity.class);
                this.activity.startActivityForResult(intentActivity, operationId);
            }
        } else {
            this.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    doResponse(call, response);
                }
            });
        }
    }

    @Override
    public final void onFailure(final Call<T> call, final Throwable t) {
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                doFailure(call, t);
            }
        });
    }

    protected abstract void doResponse(Call<T> call, retrofit2.Response<T> response);

    protected abstract void doFailure(Call<T> call, Throwable t);
}
