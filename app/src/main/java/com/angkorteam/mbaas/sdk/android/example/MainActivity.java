package com.angkorteam.mbaas.sdk.android.example;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.angkorteam.mbaas.sdk.android.library.MBaaS;
import com.angkorteam.mbaas.sdk.android.library.MBaaSAdapter;
import com.angkorteam.mbaas.sdk.android.library.MBaaSCallback;
import com.angkorteam.mbaas.sdk.android.library.MBaaSClient;
import com.angkorteam.mbaas.sdk.android.library.NetworkBroadcastReceiver;
import com.angkorteam.mbaas.sdk.android.library.request.file.FileCreateRequest;
import com.angkorteam.mbaas.sdk.android.library.response.file.FileCreateResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements NetworkBroadcastReceiver.NetworkReceiver {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String TAG = "MBaaS";

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private NetworkBroadcastReceiver broadcastReceiver = null;
    private NetworkBroadcastReceiver mbaasAdapterBroadcastReceiver = null;

    private MBaaSAdapter<DataViewHolder> mbaasAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        broadcastReceiver = new NetworkBroadcastReceiver(this);
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(broadcastReceiver, new IntentFilter(broadcastReceiver.getUuid()));
        mbaasAdapterBroadcastReceiver = new NetworkBroadcastReceiver();
        manager.registerReceiver(mbaasAdapterBroadcastReceiver, new IntentFilter(mbaasAdapterBroadcastReceiver.getUuid()));

        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


        if (NetworkBroadcastReceiver.EVENT_UNAUTHORIZED == getIntent().getIntExtra(NetworkBroadcastReceiver.EVENT, -1)) {
            onUnauthorized(getIntent().getIntExtra(NetworkBroadcastReceiver.EVENT_ID, -1));
        } else if (NetworkBroadcastReceiver.EVENT_RESPONSE == getIntent().getIntExtra(NetworkBroadcastReceiver.EVENT, -1)) {
            onResponse(getIntent().getIntExtra(NetworkBroadcastReceiver.EVENT_ID, -1), getIntent().getStringExtra(NetworkBroadcastReceiver.EVENT_JSON));
        } else {

        }

        MBaaS mbaas = MBaaS.getInstance();
        MBaaSClient client = mbaas.getClient();

        FileCreateRequest fileCreateRequest = new FileCreateRequest();
        fileCreateRequest.setContentType("text/plain");
        fileCreateRequest.setContent("hello".getBytes());

        String gson = client.getGson().toJson(fileCreateRequest);

        client.fileCreate("test.txt", fileCreateRequest).enqueue(new MBaaSCallback<FileCreateResponse>(112, this, this.broadcastReceiver));

        mbaasAdapter = new MBaaSAdapter<DataViewHolder>(this, R.layout.data_row, this.mbaasAdapterBroadcastReceiver, "khmer_now", recyclerView) {
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
        manager.unregisterReceiver(broadcastReceiver);
        manager.unregisterReceiver(mbaasAdapterBroadcastReceiver);
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
        }
    }

    @Override
    public void onFailure(int eventId, String message) {
        Log.i("MBaaS", "onFailure " + eventId + " " + message);
    }

    @Override
    public void onUnauthorized(int eventId) {
        Log.i("MBaaS", "onUnauthorized " + eventId);
    }
}