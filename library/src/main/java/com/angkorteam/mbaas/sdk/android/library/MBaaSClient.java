package com.angkorteam.mbaas.sdk.android.library;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.angkorteam.mbaas.sdk.android.library.request.asset.AssetCreateRequest;
import com.angkorteam.mbaas.sdk.android.library.request.device.DeviceRegisterRequest;
import com.angkorteam.mbaas.sdk.android.library.request.file.FileCreateRequest;
import com.angkorteam.mbaas.sdk.android.library.request.oauth2.OAuth2RefreshRequest;
import com.angkorteam.mbaas.sdk.android.library.response.asset.AssetCreateResponse;
import com.angkorteam.mbaas.sdk.android.library.response.asset.AssetDeleteResponse;
import com.angkorteam.mbaas.sdk.android.library.response.device.DeviceMetricsResponse;
import com.angkorteam.mbaas.sdk.android.library.response.device.DeviceRegisterResponse;
import com.angkorteam.mbaas.sdk.android.library.response.device.DeviceUnregisterResponse;
import com.angkorteam.mbaas.sdk.android.library.response.file.FileCreateResponse;
import com.angkorteam.mbaas.sdk.android.library.response.file.FileDeleteResponse;
import com.angkorteam.mbaas.sdk.android.library.response.javascript.JavaScriptExecuteResponse;
import com.angkorteam.mbaas.sdk.android.library.response.monitor.MonitorTimeResponse;
import com.angkorteam.mbaas.sdk.android.library.response.oauth2.OAuth2AuthorizeResponse;
import com.angkorteam.mbaas.sdk.android.library.response.oauth2.OAuth2RefreshResponse;
import com.angkorteam.mbaas.sdk.android.library.retrofit.NetworkInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import bolts.Task;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by socheat on 4/6/16.
 */
public class MBaaSClient {

    private static final String SDK_VERSION = "1.0.0";

    private final Context context;

    private final SharedPreferences sharedPreferences;

    private final Gson gson;

    private final IService service;

    private MBaaSApplication application;

    public MBaaSClient(MBaaSApplication application, Context context) {
        this.application = application;
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        this.gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ").create();
        Cache cache = new Cache(application.getCacheDir(), application.getCacheSize());
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new NetworkInterceptor(this.sharedPreferences, this.application.getMBaaSClientId(), this.application.getMBaaSClientSecret(), this.application.getMBaaSAppVersion(), SDK_VERSION))
                .cache(cache)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(this.application.getMBaaSAddress())
                .callbackExecutor(Task.BACKGROUND_EXECUTOR)
                .addConverterFactory(GsonConverterFactory.create(this.gson))
                .client(httpClient)
                .callbackExecutor(Executors.newFixedThreadPool(5))
                .build();
        this.service = retrofit.create(IService.class);

        Intent intent = new Intent(context, MBaaSIntentService.class);
        intent.putExtra(MBaaSIntentService.SERVICE, MBaaSIntentService.SERVICE_GCM_TOKEN);
        intent.putExtra(MBaaSIntentService.SENDER_ID, application.getSenderId());
        context.startService(intent);
    }

    public Call<OAuth2AuthorizeResponse> oauth2Authorize(String state, String code) {
        String clientId = application.getMBaaSClientId();
        String clientSecret = application.getMBaaSClientSecret();
        String grantType = MBaaSIntentService.OAUTH2_GRANT_TYPE_AUTHORIZATION_CODE;
        String redirectUri = null;
        return this.service.oauth2Authorize(clientId, clientSecret, grantType, redirectUri, state, code);
    }

    public Call<DeviceRegisterResponse> deviceRegister(DeviceRegisterRequest request) {
        return this.service.deviceRegister(request);
    }

    public Call<OAuth2RefreshResponse> oauth2Refresh(String refreshToken) {
        OAuth2RefreshRequest request = new OAuth2RefreshRequest();
        request.setRefreshToken(refreshToken);
        return this.service.oauth2Refresh(request);
    }

    public Call<DeviceUnregisterResponse> deviceUnregister(String deviceToken) {
        return this.service.deviceUnregister(deviceToken);
    }

    public Call<DeviceMetricsResponse> sendMetrics(String messageId) {
        return this.service.sendMetrics(messageId);
    }

    public Call<JavaScriptExecuteResponse> javascriptExecutePost(String script) {
        return this.service.javascriptExecutePost(script, new HashMap<String, Object>());
    }

    public Call<JavaScriptExecuteResponse> javascriptExecutePost(String script, Map<String, Object> request) {
        return this.service.javascriptExecutePost(script, request);
    }

    public Call<JavaScriptExecuteResponse> javascriptExecuteGet(String script) {
        return this.service.javascriptExecuteGet(script);
    }

    public Call<JavaScriptExecuteResponse> javascriptExecuteGet(String script, Map<String, Object> params) {
        return this.service.javascriptExecuteGet(script, params);
    }

    public Call<JavaScriptExecuteResponse> javascriptExecutePut(String script) {
        return this.service.javascriptExecutePut(script);
    }

    public Call<JavaScriptExecuteResponse> javascriptExecutePut(String script, Map<String, Object> request) {
        return this.service.javascriptExecutePut(script, request);
    }

    public Call<FileCreateResponse> fileCreate(String filename, FileCreateRequest request) {
        return this.service.fileCreate(filename, request);
    }

    public Call<FileDeleteResponse> fileDelete(String fileId) {
        return this.fileDelete(fileId);
    }

    public Call<AssetCreateResponse> assetCreate(String filename, AssetCreateRequest request) {
        return this.service.assetCreate(filename, request);
    }

    public Call<AssetDeleteResponse> assetDelete(String assetId) {
        return this.assetDelete(assetId);
    }

    public Call<MonitorTimeResponse> monitorTime() {
        return this.service.monitorTime();
    }

}
