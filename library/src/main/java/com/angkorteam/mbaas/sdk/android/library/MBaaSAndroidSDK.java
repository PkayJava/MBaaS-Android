package com.angkorteam.mbaas.sdk.android.library;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.angkorteam.mbaas.sdk.android.library.response.monitor.MonitorTimeResponse;
import com.angkorteam.mbaas.sdk.android.library.retrofit.NetworkInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by socheat on 4/6/16.
 */
public class MBaaSAndroidSDK {

    private final String clientId;

    private final String clientSecret;

    private final Context context;

    private final SharedPreferences preferences;

    private final Gson gson;

    private final IService service;

    public MBaaSAndroidSDK(Context context, String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.context = context;
        this.preferences = context.getSharedPreferences(MBaaSAndroidSDK.class.getName(), Context.MODE_PRIVATE);

        this.gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ").create();
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new NetworkInterceptor(this.preferences, System.getProperty("http.agent")))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://pkayjava.ddns.net:9080/mbaas-server/")
                .addConverterFactory(GsonConverterFactory.create(this.gson))
                .client(httpClient)
                .callbackExecutor(Executors.newFixedThreadPool(5))
                .build();
        this.service = retrofit.create(IService.class);
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
