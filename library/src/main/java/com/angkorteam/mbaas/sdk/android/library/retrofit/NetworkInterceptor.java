package com.angkorteam.mbaas.sdk.android.library.retrofit;

import android.content.SharedPreferences;

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

    public NetworkInterceptor(SharedPreferences preferences, String userAgent) {
        this.userAgent = userAgent;
        this.preferences = preferences;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Request.Builder builder = originalRequest.newBuilder();

        String accessToken = preferences.getString("accessToken", "");
        if ("".equals(accessToken)) {
            builder.header("Authorization", "Bearer Anonymous");
        } else {
            builder.header("Authorization", "Bearer " + accessToken);
        }

        builder.header("User-Agent", userAgent);

        Request newRequest = builder.build();

        return chain.proceed(newRequest);
    }
}
