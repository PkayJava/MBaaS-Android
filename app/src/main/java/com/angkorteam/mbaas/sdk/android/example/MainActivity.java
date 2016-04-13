package com.angkorteam.mbaas.sdk.android.example;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.angkorteam.mbaas.sdk.android.library.MBaaSIntentService;
import com.angkorteam.mbaas.sdk.android.library.response.javascript.JavaScriptExecuteResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements Callback<JavaScriptExecuteResponse> {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String TAG = "MBaaS";

    private TextView mInformationTextView;
    private boolean isReceiverRegistered;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent intentActivity = new Intent(context, WebViewActivity.class);
            startActivity(intentActivity);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Application application = (Application) getApplication();

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter("token"));

        setContentView(R.layout.activity_main);
        mInformationTextView = (TextView) findViewById(R.id.informationTextView);

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, MBaaSIntentService.class);
            intent.putExtra(MBaaSIntentService.SERVICE, MBaaSIntentService.SERVICE_GCM_TOKEN);
            intent.putExtra(MBaaSIntentService.SENDER_ID, application.getSenderId());
            intent.putExtra(MBaaSIntentService.RECEIVER, "token");
            startService(intent);
        }
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
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