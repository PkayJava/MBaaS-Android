package com.angkorteam.mbaas.sdk.android.library.retrofit;

import android.content.SharedPreferences;

import com.angkorteam.mbaas.sdk.android.library.MBaaSClient;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by socheat on 4/6/16.
 */
public class NetworkInterceptor implements Interceptor {

    private final String userAgent;

    private final SharedPreferences preferences;

    private final String clientId;

    private final String clientSecret;

    private final String appVersion;

    private final String sdkVersion;

    public NetworkInterceptor(SharedPreferences preferences, String clientId, String clientSecret, String appVersion, String sdkVersion, String userAgent) {
        this.userAgent = userAgent;
        this.preferences = preferences;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.appVersion = appVersion;
        this.sdkVersion = sdkVersion;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Request.Builder builder = originalRequest.newBuilder();

        String accessToken = preferences.getString(MBaaSClient.ACCESS_TOKEN, "");
        if (!"".equals(accessToken)) {
            builder.header("Authorization", "Bearer " + accessToken);
        } else {
            if (this.clientSecret != null && !"".equals(this.clientSecret)) {
                builder.header("client_secret", this.clientSecret);
            }
        }

        if (this.clientId != null && !"".equals(this.clientId)) {
            builder.header("client_id", this.clientId);
        }

        if (this.appVersion != null && !"".equals(this.appVersion)) {
            builder.header("app_version", this.appVersion);
        }

        if (this.sdkVersion != null && !"".equals(this.sdkVersion)) {
            builder.header("sdk_version", this.sdkVersion);
        }

        builder.header("User-Agent", userAgent);

        Request newRequest = builder.build();

        return chain.proceed(newRequest);
    }
}
