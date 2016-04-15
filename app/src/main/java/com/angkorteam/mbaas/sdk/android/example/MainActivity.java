package com.angkorteam.mbaas.sdk.android.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.angkorteam.mbaas.sdk.android.library.MBaaSCallback;
import com.angkorteam.mbaas.sdk.android.library.MBaaSOperation;
import com.angkorteam.mbaas.sdk.android.library.response.javascript.JavaScriptExecuteResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Module.Data;
import adapter.DataAdapter;
import interfaces.OnLoadMoreListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.DividerItemDecoration;

public class MainActivity extends AppCompatActivity implements Callback<JavaScriptExecuteResponse>, MBaaSOperation {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String TAG = "MBaaS";

    private TextView mInformationTextView;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DataAdapter adapter;
    private Data data;
    private List<Data> dataList = new ArrayList<Data>();
    private int requestCounter = 0;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {
                Application application = (Application) getApplication();
                Call<JavaScriptExecuteResponse> responseCall = application.getMBaaSClient().javascriptExecutePost("js_khmer_today");
                responseCall.enqueue(new MBaaSCallback<JavaScriptExecuteResponse>(100, this, this));
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

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


        Map<String, Object> params = new HashMap<>();
        params.put("offset", 0);
        Application application = (Application) getApplication();
        Call<JavaScriptExecuteResponse> responseCall = application.getMBaaSClient().javascriptExecutePost("js_khmer_today", params);
        responseCall.enqueue(new MBaaSCallback<JavaScriptExecuteResponse>(100, this, this));
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

    @Override
    public void operationResponse(int operationId, Object object) {
        if (operationId == 100) {
            JavaScriptExecuteResponse response = (JavaScriptExecuteResponse) object;
            if (response.getHttpCode() == 200) {
                List<Map<String, Object>> test = (List<Map<String, Object>>) response.getData();
                List<String> titles = new ArrayList<>();

                if(requestCounter == 0){
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
                }
                else{
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
                        adapter.notifyItemInserted(dataList.size() -1);

                        requestCounter++;
                        Map<String, Object> params = new HashMap<>();
                        params.put("offset", 10*requestCounter);

                        Application application = (Application) getApplication();
                        Call<JavaScriptExecuteResponse> responseCall = application.getMBaaSClient().javascriptExecutePost("js_khmer_today", params);
                        responseCall.enqueue(new MBaaSCallback<JavaScriptExecuteResponse>(100, MainActivity.this, MainActivity.this));
                    }
                });

            } else {
                mInformationTextView.setText(response.getResult());
            }
        }
    }

    @Override
    public void operationRetry(int operationId) {
        if (operationId == 100) {
            Application application = (Application) getApplication();
            Call<JavaScriptExecuteResponse> responseCall = application.getMBaaSClient().javascriptExecutePost("js_khmer_today");
            responseCall.enqueue(new MBaaSCallback<JavaScriptExecuteResponse>(100, this, this));
        }
    }
}