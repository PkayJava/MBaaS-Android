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

    public NetworkInterceptor(SharedPreferences preferences, String clientId, String clientSecret, String userAgent) {
        this.userAgent = userAgent;
        this.preferences = preferences;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Request.Builder builder = originalRequest.newBuilder();

        String accessToken = preferences.getString(MBaaSClient.ACCESS_TOKEN, "");
        if (!"".equals(accessToken)) {
            builder.header("Authorization", "Bearer " + accessToken);
        }

        if (this.clientId != null && !"".equals(this.clientId)) {
            builder.header("client_id", this.clientId);
        }

        if (this.clientSecret != null && !"".equals(this.clientSecret)) {
            builder.header("client_secret", this.clientSecret);
        }

        builder.header("User-Agent", userAgent);

        Request newRequest = builder.build();

        return chain.proceed(newRequest);
    }
}
