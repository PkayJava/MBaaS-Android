package com.angkorteam.mbaas.sdk.android.library;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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

    private final static String RECEIVER = "oauth2";

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter(LoginActivity.RECEIVER));

        MBaaSApplication application = null;
        if (getApplication() instanceof MBaaSApplication) {
            application = (MBaaSApplication) getApplication();
        }

        if (application == null) {
            Intent message = new Intent(LoginActivity.RECEIVER);
            LocalBroadcastManager.getInstance(this).sendBroadcast(message);
            return;
        }

        String serviceAuthorize = application.getMBaaSAddress().endsWith("/") ? application.getMBaaSAddress() + "web/oauth2/authorize" : application.getMBaaSAddress() + "/web/oauth2/authorize";
        this.link = application.getMBaaSAddress().endsWith("/") ? application.getMBaaSAddress() + "web/oauth2/response" : application.getMBaaSAddress() + "/web/oauth2/response";
        List<String> params = new ArrayList<>();
        params.add("client_id=" + application.getMBaaSClientId());
        params.add("state=" + UUID.randomUUID().toString());
        params.add("response_type=code");

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                MBaaSApplication application = null;
                if (getApplication() instanceof MBaaSApplication) {
                    application = (MBaaSApplication) getApplication();
                }
                if (application == null) {
                    Intent message = new Intent(LoginActivity.RECEIVER);
                    LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(message);
                    return true;
                }
                if (url != null && !"".equals(url) && url.startsWith(link)) {
                    Map<String, String> oauth2 = new HashMap<>();
                    String query = url.substring(link.length() + 1);
                    String[] queryParams = StringUtils.split(query, '&');
                    for (String queryParam : queryParams) {
                        String[] param = StringUtils.split(queryParam, '=');
                        String name = param[0];
                        String value = param[1];
                        oauth2.put(name, value);

                    }
                    Intent serviceIntent = new Intent(view.getContext(), MBaaSIntentService.class);
                    serviceIntent.putExtra(MBaaSIntentService.SERVICE, MBaaSIntentService.SERVICE_ACCESS_TOKEN);
                    serviceIntent.putExtra(MBaaSIntentService.OAUTH2_CODE, oauth2.get(MBaaSIntentService.OAUTH2_CODE));
                    serviceIntent.putExtra(MBaaSIntentService.OAUTH2_STATE, oauth2.get(MBaaSIntentService.OAUTH2_STATE));
                    serviceIntent.putExtra(MBaaSIntentService.RECEIVER, LoginActivity.RECEIVER);
                    startService(serviceIntent);
                    return true;
                } else {
                    return false;
                }
            }
        });
        webView.loadUrl(serviceAuthorize + "?" + StringUtils.join(params, "&"));
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onDestroy();
    }

}
