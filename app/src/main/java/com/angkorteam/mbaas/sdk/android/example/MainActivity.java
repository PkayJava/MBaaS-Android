package com.angkorteam.mbaas.sdk.android.example;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.angkorteam.mbaas.sdk.android.library.SocketBroadcastReceiver;
import com.angkorteam.mbaas.sdk.android.library.MBaaS;
import com.angkorteam.mbaas.sdk.android.library.MBaaSAdapter;
import com.angkorteam.mbaas.sdk.android.library.HttpBroadcastReceiver;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements HttpBroadcastReceiver.HttpReceiver {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String TAG = "MBaaS";

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private HttpBroadcastReceiver httpBroadcastReceiver = null;

    private MBaaSAdapter<DataViewHolder> mbaasAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);

        this.httpBroadcastReceiver = new HttpBroadcastReceiver(this);
        manager.registerReceiver(this.httpBroadcastReceiver, new IntentFilter(this.httpBroadcastReceiver.getUuid()));

        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        if (HttpBroadcastReceiver.EVENT_UNAUTHORIZED == getIntent().getIntExtra(HttpBroadcastReceiver.EVENT, -1)) {
            onUnauthorized(getIntent().getIntExtra(HttpBroadcastReceiver.EVENT_ID, -1));
        } else if (HttpBroadcastReceiver.EVENT_RESPONSE == getIntent().getIntExtra(HttpBroadcastReceiver.EVENT, -1)) {
            onResponse(getIntent().getIntExtra(HttpBroadcastReceiver.EVENT_ID, -1), getIntent().getStringExtra(HttpBroadcastReceiver.EVENT_JSON));
        } else {

        }

        // client.securityLogin("admin", "admin").enqueue(new MBaaSCallback<SecurityLoginResponse>(1002, this, httpBroadcastReceiver));

        this.mbaasAdapter = new MBaaSAdapter<DataViewHolder>(this, R.layout.data_row, "khmer_now", recyclerView) {
            @Override
            protected void onPopulateItem(DataViewHolder holder, int position, Map<String, Object> item) {
                String title = (String) item.get("headline");
                String text = position + " : " + title;
                holder.tvTitle.setText(text);
            }

            @Override
            protected DataViewHolder onCreateViewHolder(View view) {
                return new DataViewHolder(view);
            }
        };
        manager.registerReceiver(this.mbaasAdapter.getHttpBroadcastReceiver(), new IntentFilter(this.mbaasAdapter.getHttpBroadcastReceiver().getUuid()));
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
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.unregisterReceiver(this.httpBroadcastReceiver);
        manager.unregisterReceiver(this.mbaasAdapter.getHttpBroadcastReceiver());
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

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;

        public DataViewHolder(View v) {
            super(v);
            tvTitle = (TextView) v.findViewById(R.id.tvTitle);
            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(MainActivity.this, DataDetailActivity.class);
//                    intent.putExtra("title", "" + dataList.get(getPosition()).getTitle());
//                    intent.putExtra("description", "" + dataList.get(getPosition()).getDescription());
//                    intent.putExtra("mp3", "" + dataList.get(getPosition()).getMediaMp3());
//                    intent.putExtra("image", "" + dataList.get(getPosition()).getMediaImage());
//                    intent.putExtra("video", "" + dataList.get(getPosition()).getMediaVideo());
//                    MainActivity.this.startActivity(intent);
                }
            });
        }
    }


    @Override
    public void onResponse(int eventId, String json) {
        if (eventId == 112) {
            Log.i("MBaaS", json);
        } else if (eventId == 1002) {
            Log.i("MBaaS", json);
        }
    }

    @Override
    public void onFailure(int eventId, String message) {
        Log.i("MBaaS", "onFailure " + eventId + " " + message);
    }

    @Override
    public BroadcastReceiver getHttpBroadcastReceiver() {
        return this.httpBroadcastReceiver;
    }

    @Override
    public void onUnauthorized(int eventId) {
        Log.i("MBaaS", "onUnauthorized " + eventId);
    }
}