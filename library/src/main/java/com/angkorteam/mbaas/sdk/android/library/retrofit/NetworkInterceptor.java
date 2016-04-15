package com.angkorteam.mbaas.sdk.android.library.retrofit;

import android.content.SharedPreferences;
import android.util.Log;

import com.angkorteam.mbaas.sdk.android.library.MBaaSClient;
import com.angkorteam.mbaas.sdk.android.library.MBaaSIntentService;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.IOException;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.RealResponseBody;
import okio.BufferedSource;

/**
 * Created by socheat on 4/6/16.
 */
public class NetworkInterceptor implements Interceptor {

    private final SharedPreferences preferences;

    private final String clientId;

    private final String clientSecret;

    private final String appVersion;

    private final String sdkVersion;

    private Object lock = new Object();

    public NetworkInterceptor(SharedPreferences preferences, String clientId, String clientSecret, String appVersion, String sdkVersion) {
        this.preferences = preferences;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.appVersion = appVersion;
        this.sdkVersion = sdkVersion;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        synchronized (lock) {
            long now = System.currentTimeMillis();
            Log.i("MBaaS", "Locked " + DateFormatUtils.ISO_DATE_TIME_ZONE_FORMAT.format(new Date()));
            Request originalRequest = chain.request();
            Request.Builder requestBuilder = originalRequest.newBuilder();
            String accessToken = preferences.getString(MBaaSIntentService.ACCESS_TOKEN, "");
            Log.i("MBaaS", "AccessToken " + accessToken);
            if (!"".equals(accessToken)) {
                requestBuilder.header("Authorization", "Bearer " + accessToken);
            } else {
                if (this.clientSecret != null && !"".equals(this.clientSecret)) {
                    requestBuilder.header("client_secret", this.clientSecret);
                }
            }
            if (this.clientId != null && !"".equals(this.clientId)) {
                requestBuilder.header("client_id", this.clientId);
            }
            if (this.appVersion != null && !"".equals(this.appVersion)) {
                requestBuilder.header("app_version", this.appVersion);
            }
            if (this.sdkVersion != null && !"".equals(this.sdkVersion)) {
                requestBuilder.header("sdk_version", this.sdkVersion);
            }
            requestBuilder.header("User-Agent", System.getProperty("http.agent"));
            Request newRequest = requestBuilder.build();
            Response response = chain.proceed(newRequest);
            Log.i("MBaaS", "Unlocked " + DateFormatUtils.ISO_DATE_TIME_ZONE_FORMAT.format(new Date()));
            float consumeSecond = (System.currentTimeMillis() - now) / 1000f;
            Log.i("MBaaS", "Consume " + consumeSecond + " ss");
            return response;
        }
    }
}
