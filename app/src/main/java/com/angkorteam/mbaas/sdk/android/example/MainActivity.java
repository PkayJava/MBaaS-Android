package com.angkorteam.mbaas.sdk.android.example;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.angkorteam.mbaas.sdk.android.library.MBaaSAdapter;
import com.angkorteam.mbaas.sdk.android.library.MBaaSCallback;
import com.angkorteam.mbaas.sdk.android.library.NetworkBroadcastReceiver;
import com.angkorteam.mbaas.sdk.android.library.response.javascript.JavaScriptExecuteResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Module.Data;
import adapter.DataAdapter;
import interfaces.OnLoadMoreListener;
import retrofit2.Call;
import utils.DividerItemDecoration;

public class MainActivity extends AppCompatActivity implements NetworkBroadcastReceiver.NetworkReceiver {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String TAG = "MBaaS";

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    //    private DataAdapter adapter;
    private Data data;
    private List<Data> dataList = new ArrayList<Data>();
    private int requestCounter = 0;

    private NetworkBroadcastReceiver broadcastReceiver = null;
    private NetworkBroadcastReceiver mbaasAdapterBroadcastReceiver = null;

    private MBaaSAdapter mbaasAdapter = null;

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

        mbaasAdapter = new MBaaSAdapter(this, this.mbaasAdapterBroadcastReceiver, "js_khmer_today", recyclerView) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                RecyclerView.ViewHolder vh;

                if (viewType == MBaaSAdapter.VIEW_ITEM) {
                    View v = LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.data_row, parent, false);
                    vh = new DataViewHolder(v);
                } else {
                    View v = LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.recycler_progress, parent, false);
                    vh = new ProgressViewHolder(v);
                }
                return vh;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                if (holder instanceof DataViewHolder) {
                    ((DataViewHolder) holder).tvTitle.setText(dataList.get(position).getTitle());

                } else {
                    ((ProgressViewHolder) holder).progressBar.getIndeterminateDrawable().setColorFilter(MainActivity.this.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                    ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
                }
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

    public class DataViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;

        public DataViewHolder(View v) {
            super(v);
            tvTitle = (TextView) v.findViewById(R.id.tvTitle);
            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, DataDetailActivity.class);
                    intent.putExtra("title", "" + dataList.get(getPosition()).getTitle());
                    intent.putExtra("description", "" + dataList.get(getPosition()).getDescription());
                    intent.putExtra("mp3", "" + dataList.get(getPosition()).getMediaMp3());
                    intent.putExtra("image", "" + dataList.get(getPosition()).getMediaImage());
                    intent.putExtra("video", "" + dataList.get(getPosition()).getMediaVideo());
                    MainActivity.this.startActivity(intent);
                }
            });
        }
    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }
    }


    @Override
    public void onResponse(int eventId, String json) {
    }

    @Override
    public void onFailure(int eventId, String message) {
        Log.i("MBaaS", "onFailure " + eventId);
    }

    @Override
    public void onUnauthorized(int eventId) {
        Log.i("MBaaS", "onUnauthorized " + eventId);
    }
}