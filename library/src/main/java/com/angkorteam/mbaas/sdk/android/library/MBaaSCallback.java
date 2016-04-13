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

    public MBaaSCallback(Activity activity) {
        this.activity = activity;
    }

    @Override
    public final void onResponse(Call<T> call, retrofit2.Response<T> response) {
        if (response.body() != null && (response.body().getHttpCode() == 403 || response.body().getHttpCode() == 401)) {
            MBaaSApplication application = null;
            if (this.activity.getApplication() instanceof MBaaSApplication) {
                application = (MBaaSApplication) this.activity.getApplication();
            }
            if (application != null) {
                Intent intentActivity = new Intent(this.activity, LoginActivity.class);
                this.activity.startActivityForResult(intentActivity, LoginActivity.REQUEST_CODE);
            }
        } else {
            doResponse(call, response);
        }
    }

    @Override
    public final void onFailure(Call<T> call, Throwable t) {
        doFailure(call, t);
    }

    protected abstract void doResponse(Call<T> call, retrofit2.Response<T> response);

    protected abstract void doFailure(Call<T> call, Throwable t);
}
