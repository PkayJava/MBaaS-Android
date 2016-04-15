package com.angkorteam.mbaas.sdk.android.example;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

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

    private TextView mInformationTextView;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DataAdapter adapter;
    private Data data;
    private List<Data> dataList = new ArrayList<Data>();
    private int requestCounter = 0;

    private NetworkBroadcastReceiver broadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        broadcastReceiver = new NetworkBroadcastReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(broadcastReceiver.getUuid()));

        setContentView(R.layout.activity_main);
        mInformationTextView = (TextView) findViewById(R.id.informationTextView);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


        if (NetworkBroadcastReceiver.EVENT_UNAUTHORIZED == getIntent().getIntExtra(NetworkBroadcastReceiver.EVENT, -1)) {
            onUnauthorized(getIntent().getIntExtra(NetworkBroadcastReceiver.EVENT_ID, -1));
        } else if (NetworkBroadcastReceiver.EVENT_RESPONSE == getIntent().getIntExtra(NetworkBroadcastReceiver.EVENT, -1)) {
            onResponse(getIntent().getIntExtra(NetworkBroadcastReceiver.EVENT_ID, -1), getIntent().getStringExtra(NetworkBroadcastReceiver.EVENT_JSON));
        } else {
            Application application = (Application) getApplication();
            Map<String, Object> params = new HashMap<>();
            params.put("offset", 0);
            Call<JavaScriptExecuteResponse> responseCall = application.getMBaaSClient().javascriptExecutePost("js_khmer_today", params);
            responseCall.enqueue(new MBaaSCallback<JavaScriptExecuteResponse>(100, this, broadcastReceiver));
            Call<JavaScriptExecuteResponse> responseCall1 = application.getMBaaSClient().javascriptExecuteGet("query_khmer_today_total");
            responseCall1.enqueue(new MBaaSCallback<JavaScriptExecuteResponse>(101, this, broadcastReceiver));
        }
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
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

    @Override
    public void onResponse(int operationId, String json) {
        if (operationId == 100) {
            Gson gson = new Gson();
            JavaScriptExecuteResponse response = gson.fromJson(json, JavaScriptExecuteResponse.class);
            if (response.getHttpCode() == 200) {
                List<Map<String, Object>> test = (List<Map<String, Object>>) response.getData();
                List<String> titles = new ArrayList<>();

                if (requestCounter == 0) {
                    for (Map<String, Object> t : test) {
                        data = new Data();
                        data.setTitle("" + t.get("title"));
                        data.setDescription("" + t.get("description"));
                        data.setMediaImage("" + t.get("media_image"));
                        data.setMediaMp3("" + t.get("media_mp3"));
                        data.setMediaVideo("" + t.get("media_video"));
                        dataList.add(data);
                    }

                    adapter = new DataAdapter(MainActivity.this, dataList, recyclerView);
                    recyclerView.setAdapter(adapter);
                } else {
                    dataList.remove(dataList.size() - 1);
                    adapter.notifyItemRemoved(dataList.size());

                    for (Map<String, Object> t : test) {
                        data = new Data();
                        data.setTitle("" + t.get("title"));
                        data.setDescription("" + t.get("description"));
                        data.setMediaImage("" + t.get("media_image"));
                        data.setMediaMp3("" + t.get("media_mp3"));
                        data.setMediaVideo("" + t.get("media_video"));
                        dataList.add(data);
                    }
                    adapter.notifyDataSetChanged();
                    adapter.setLoaded();
                }
                //mInformationTextView.setText((String) test.get(0).get("title"));
                adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {

                        dataList.add(null);
                        adapter.notifyItemInserted(dataList.size() - 1);

                        requestCounter++;
                        Map<String, Object> params = new HashMap<>();
                        params.put("offset", 10 * requestCounter);

                        Application application = (Application) getApplication();
                        Call<JavaScriptExecuteResponse> responseCall = application.getMBaaSClient().javascriptExecutePost("js_khmer_today", params);
                        responseCall.enqueue(new MBaaSCallback<JavaScriptExecuteResponse>(100, MainActivity.this, broadcastReceiver));
                    }
                });

            } else {
                mInformationTextView.setText(response.getResult());
            }
        }
    }

    @Override
    public void onFailure(int operationId, String message) {
        Log.i("MBaaS", "onFailure " + operationId);
    }

    @Override
    public void onUnauthorized(int operationId) {
        Log.i("MBaaS", "onUnauthorized " + operationId);
    }
}