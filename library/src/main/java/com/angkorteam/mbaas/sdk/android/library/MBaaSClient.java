package com.angkorteam.mbaas.sdk.android.library;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.angkorteam.mbaas.sdk.android.library.request.asset.AssetCreateRequest;
import com.angkorteam.mbaas.sdk.android.library.request.device.DeviceRegisterRequest;
import com.angkorteam.mbaas.sdk.android.library.request.file.FileCreateRequest;
import com.angkorteam.mbaas.sdk.android.library.request.javascript.JavaScriptExecuteRequest;
import com.angkorteam.mbaas.sdk.android.library.response.asset.AssetCreateResponse;
import com.angkorteam.mbaas.sdk.android.library.response.asset.AssetDeleteResponse;
import com.angkorteam.mbaas.sdk.android.library.response.device.DeviceMetricsResponse;
import com.angkorteam.mbaas.sdk.android.library.response.device.DeviceRegisterResponse;
import com.angkorteam.mbaas.sdk.android.library.response.device.DeviceUnregisterResponse;
import com.angkorteam.mbaas.sdk.android.library.response.file.FileCreateResponse;
import com.angkorteam.mbaas.sdk.android.library.response.file.FileDeleteResponse;
import com.angkorteam.mbaas.sdk.android.library.response.javascript.JavaScriptExecuteResponse;
import com.angkorteam.mbaas.sdk.android.library.response.monitor.MonitorTimeResponse;
import com.angkorteam.mbaas.sdk.android.library.retrofit.NetworkInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by socheat on 4/6/16.
 */
public class MBaaSClient {

    private static final String SDK_VERSION = "1.0.0";

    public static final String ACCESS_TOKEN = "accessToken";

    private final Context context;

    private final SharedPreferences preferences;

    private final Gson gson;

    private final IService service;

    private MBaaSApplication application;

    public MBaaSClient(MBaaSApplication application, Context context) {
        this.application = application;
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);

        this.gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ").create();
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new NetworkInterceptor(this.preferences, this.application.getMBaaSClientId(), this.application.getMBaaSClientSecret(), this.application.getMBaaSAppVersion(), SDK_VERSION, System.getProperty("http.agent")))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(this.application.getMBaaSAddress())
                .addConverterFactory(GsonConverterFactory.create(this.gson))
                .client(httpClient)
                .callbackExecutor(Executors.newFixedThreadPool(5))
                .build();
        this.service = retrofit.create(IService.class);
    }

    public Call<DeviceRegisterResponse> deviceRegister(DeviceRegisterRequest request) {
        return this.service.deviceRegister(request);
    }

    public Call<DeviceUnregisterResponse> deviceUnregister(String deviceToken) {
        return this.service.deviceUnregister(deviceToken);
    }

    public Call<DeviceMetricsResponse> sendMetrics(String messageId) {
        return this.service.sendMetrics(messageId);
    }

    public Call<JavaScriptExecuteResponse> javascriptExecutePost(String script) {
        return this.service.javascriptExecutePost(script);
    }

    public Call<JavaScriptExecuteResponse> javascriptExecutePost(String script, Map<String, Object> params) {
        return this.service.javascriptExecutePost(script, params);
    }

    public Call<JavaScriptExecuteResponse> javascriptExecutePost(String script, JavaScriptExecuteRequest request) {
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

    public Call<JavaScriptExecuteResponse> javascriptExecutePut(String script, JavaScriptExecuteRequest request) {
        return this.service.javascriptExecutePut(script, request);
    }

    public Call<FileCreateResponse> fileCreate(String filename, FileCreateRequest request) {
        return this.service.fileCreate(filename, request);
    }

    public Call<FileDeleteResponse> fileDelete(String fileId) {
        return this.fileDelete(fileId);
    }

    public Call<AssetCreateResponse> assetCreate(String filename, AssetCreateRequest request) {
        return this.assetCreate(filename, request);
    }

    public Call<AssetDeleteResponse> assetDelete(String assetId) {
        return this.assetDelete(assetId);
    }

    public Call<MonitorTimeResponse> monitorTime() {
        return this.service.monitorTime();
    }

    public void login() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this.context);
        alert.setTitle("Mobile Backend as a Service");

        WebView wv = new WebView(context);
        wv.loadUrl("http:\\www.google.com");
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        alert.setView(wv);
        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

}
