package com.angkorteam.mbaas.sdk.android.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.angkorteam.mbaas.sdk.android.library.MBaaSCallback;
import com.angkorteam.mbaas.sdk.android.library.response.javascript.JavaScriptExecuteResponse;
import com.angkorteam.mbaas.sdk.android.library.response.monitor.MonitorTimeResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements Callback<JavaScriptExecuteResponse> {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String TAG = "MBaaS";

    private TextView mInformationTextView;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {
                Application application = (Application) getApplication();
                Call<MonitorTimeResponse> responseCall = application.getMBaaSClient().monitorTime();
                responseCall.enqueue(new MBaaSCallback<MonitorTimeResponse>(100, this) {

                    @Override
                    protected void doResponse(Call<MonitorTimeResponse> call, Response<MonitorTimeResponse> response) {
                        Log.i("MBaaS", "Server Time : " + response.body().getData());
                        mInformationTextView.setText(response.body().getData());
                    }

                    @Override
                    protected void doFailure(Call<MonitorTimeResponse> call, Throwable t) {
                    }

                });
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            mInformationTextView.setText("User Denied");
        }
        Log.i("MBaaS", "requestCode " + requestCode + ", resultCode " + resultCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mInformationTextView = (TextView) findViewById(R.id.informationTextView);

        Application application = (Application) getApplication();
        Call<MonitorTimeResponse> responseCall = application.getMBaaSClient().monitorTime();
        responseCall.enqueue(new MBaaSCallback<MonitorTimeResponse>(100, this) {

            @Override
            protected void doResponse(Call<MonitorTimeResponse> call, Response<MonitorTimeResponse> response) {
                Log.i("MBaaS", "Server Time : " + response.body().getData());
                mInformationTextView.setText(response.body().getData());
            }

            @Override
            protected void doFailure(Call<MonitorTimeResponse> call, Throwable t) {
            }

        });
    }

    @Override
    public void onResponse(Call<JavaScriptExecuteResponse> call, Response<JavaScriptExecuteResponse> response) {
        JavaScriptExecuteResponse responseBody = response.body();
        Log.i("MBaaS", responseBody.getMethod());
    }

    @Override
    public void onFailure(Call<JavaScriptExecuteResponse> call, Throwable t) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(MainActivity.TAG, "MainActivity.onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}