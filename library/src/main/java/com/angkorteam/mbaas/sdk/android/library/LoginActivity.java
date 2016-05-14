package com.angkorteam.mbaas.sdk.android.library;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by socheat on 4/13/16.
 */
public class LoginActivity extends AppCompatActivity {

    private WebView webView;

    private String link;

    private String activity;

    private Integer eventId;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(HttpBroadcastReceiver.EVENT_ACTIVITY, this.activity);
        outState.putInt(HttpBroadcastReceiver.EVENT_ID, this.eventId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.activity = savedInstanceState.getString(HttpBroadcastReceiver.EVENT_ACTIVITY);
        this.eventId = savedInstanceState.getInt(HttpBroadcastReceiver.EVENT_ID);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.activity = getIntent().getStringExtra(HttpBroadcastReceiver.EVENT_ACTIVITY);
        this.eventId = getIntent().getIntExtra(HttpBroadcastReceiver.EVENT_ID, -1);

        MBaaS mbaas = MBaaS.getInstance();
        XMLPropertiesConfiguration configuration = mbaas.getConfiguration();

        String serverAddress = configuration.getString(MBaaS.SERVER_ADDRESS);
        String serviceAuthorize = serverAddress + "web/oauth2/authorize";
        this.link = serverAddress + "web/oauth2/response";
        List<String> params = new ArrayList<>();
        params.add("client_id=" + configuration.getString(MBaaS.CLIENT_ID));
        params.add("state=" + UUID.randomUUID().toString());
        params.add("response_type=code");

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && !"".equals(url) && url.startsWith(link)) {
                    Map<String, String> oauth2 = new HashMap<>();
                    String query = url.substring(link.length() + 1);
                    String[] queryParams = StringUtils.split(query, '&');
                    for (String queryParam : queryParams) {
                        String[] param = StringUtils.split(queryParam, '=');
                        String name = param.length >= 1 ? param[0] : null;
                        String value = param.length >= 2 ? param[1] : null;
                        oauth2.put(name, value);
                    }
                    if (oauth2.get("error") != null && !"".equals(oauth2.get("error"))) {
                        try {
                            Class<Activity> clazz = (Class<Activity>) Class.forName(LoginActivity.this.activity);
                            Intent intentActivity = new Intent(view.getContext(), clazz);
                            intentActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intentActivity.putExtra(HttpBroadcastReceiver.EVENT, HttpBroadcastReceiver.EVENT_UNAUTHORIZED);
                            intentActivity.putExtra(HttpBroadcastReceiver.EVENT_ID, eventId);
                            view.getContext().startActivity(intentActivity);
                        } catch (ClassNotFoundException e) {
                        }
                    } else {
                        Intent serviceIntent = new Intent(view.getContext(), MBaaSIntentService.class);
                        serviceIntent.putExtra(MBaaSIntentService.SERVICE, MBaaSIntentService.SERVICE_ACCESS_TOKEN);
                        serviceIntent.putExtra(MBaaSIntentService.OAUTH2_CODE, oauth2.get(MBaaSIntentService.OAUTH2_CODE));
                        serviceIntent.putExtra(MBaaSIntentService.OAUTH2_STATE, oauth2.get(MBaaSIntentService.OAUTH2_STATE));
                        serviceIntent.putExtra(HttpBroadcastReceiver.EVENT_ACTIVITY, activity);
                        serviceIntent.putExtra(HttpBroadcastReceiver.EVENT_ID, eventId);
                        startService(serviceIntent);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
        webView.loadUrl(serviceAuthorize + "?" + StringUtils.join(params, "&"));
    }

}
