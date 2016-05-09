package com.angkorteam.mbaas.sdk.android.library;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.angkorteam.mbaas.sdk.android.library.netty.ClientInitializer;
import com.angkorteam.mbaas.sdk.android.library.request.asset.AssetCreateRequest;
import com.angkorteam.mbaas.sdk.android.library.request.device.DeviceRegisterRequest;
import com.angkorteam.mbaas.sdk.android.library.request.file.FileCreateRequest;
import com.angkorteam.mbaas.sdk.android.library.request.oauth2.OAuth2RefreshRequest;
import com.angkorteam.mbaas.sdk.android.library.request.security.SecurityLoginRequest;
import com.angkorteam.mbaas.sdk.android.library.request.security.SecuritySignUpRequest;
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
import com.angkorteam.mbaas.sdk.android.library.response.security.SecurityLoginResponse;
import com.angkorteam.mbaas.sdk.android.library.response.security.SecuritySignUpResponse;
import com.angkorteam.mbaas.sdk.android.library.retrofit.NetworkInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import bolts.Task;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by socheat on 4/6/16.
 */
public final class MBaaSClient {

    private static final String SDK_VERSION = "1.0.0";

    private final SharedPreferences sharedPreferences;

    private final Gson gson;

    private final IService service;

    private final XMLPropertiesConfiguration configuration;

    private XMPPTCPConnection connection;

    private boolean xmpp = false;

    MBaaSClient(final Application application, final XMLPropertiesConfiguration configuration) {
        this.configuration = configuration;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
        this.xmpp = false;

        this.gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ").create();
        Cache cache = new Cache(application.getCacheDir(), configuration.getLong(MBaaS.CACHE_SIZE));
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new NetworkInterceptor(this.sharedPreferences, configuration.getString(MBaaS.CLIENT_ID), configuration.getString(MBaaS.CLIENT_SECRET), configuration.getString(MBaaS.APP_VERSION), SDK_VERSION))
                .cache(cache)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(configuration.getString(MBaaS.SERVER_ADDRESS))
                .callbackExecutor(Task.BACKGROUND_EXECUTOR)
                .addConverterFactory(GsonConverterFactory.create(this.gson))
                .client(httpClient)
                .callbackExecutor(Executors.newFixedThreadPool(5))
                .build();
        this.service = retrofit.create(IService.class);

        Task.callInBackground(new Callable<String>() {
            @Override
            public String call() throws Exception {
                synchronized (NetworkInterceptor.LOCK) {
                    return MBaaSUtils.requestGcm(sharedPreferences, application, configuration);
                }
            }
        });
        if (!"".equals(sharedPreferences.getString(MBaaSIntentService.LOGIN, "")) && !"".equals(sharedPreferences.getString(MBaaSIntentService.ACCESS_TOKEN, ""))) {
            Task.callInBackground(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    synchronized (NetworkInterceptor.LOCK) {
                        String login = sharedPreferences.getString(MBaaSIntentService.LOGIN, "");
                        String accessToken = sharedPreferences.getString(MBaaSIntentService.ACCESS_TOKEN, "");
                        initXMPP(login, accessToken);
                    }
                    return null;
                }
            });
        }
    }

    public Gson getGson() {
        return this.gson;
    }

    public Call<OAuth2AuthorizeResponse> oauth2Authorize(String state, String code) {
        String clientId = configuration.getString(MBaaS.CLIENT_ID);
        String clientSecret = configuration.getString(MBaaS.CLIENT_SECRET);
        String grantType = MBaaSIntentService.OAUTH2_GRANT_TYPE_AUTHORIZATION_CODE;
        String redirectUri = null;
        return this.service.oauth2Authorize(clientId, clientSecret, grantType, redirectUri, state, code);
    }

    public boolean initXMPP(String login, String accessToken) {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap clientBootstrap = new Bootstrap();
        clientBootstrap.group(group);
        clientBootstrap.channel(NioSocketChannel.class);
        clientBootstrap.handler(new ClientInitializer());
        clientBootstrap.connect("192.168.1.115", 5222);
        return this.xmpp;
    }

    public boolean hasXMPP() {
        return this.xmpp;
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
        return this.service.assetDelete(assetId);
    }

    public Call<SecurityLoginResponse> securityLogin(String username, String password) {
        SecurityLoginRequest request = new SecurityLoginRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setSecret(this.configuration.getString(MBaaS.CLIENT_SECRET));
        request.setDeviceType(this.configuration.getString(MBaaS.DEVICE_TYPE));
        request.setDeviceToken(this.sharedPreferences.getString(MBaaSIntentService.GCM_TOKEN, ""));
        return this.service.securityLogin(request);
    }

    public Call<SecuritySignUpResponse> securitySignUp(String username,
                                                       String password,
                                                       Map<String, Object> byTheUser,
                                                       Map<String, Object> byFriends,
                                                       Map<String, Object> byAnonymousUsers,
                                                       Map<String, Object> byRegisteredUsers) {
        SecuritySignUpRequest request = new SecuritySignUpRequest();
        request.setVisibleByTheUser(byTheUser);
        request.setVisibleByFriends(byFriends);
        request.setVisibleByAnonymousUsers(byAnonymousUsers);
        request.setVisibleByRegisteredUsers(byRegisteredUsers);
        request.setUsername(username);
        request.setPassword(password);
        request.setSecret(this.configuration.getString(MBaaS.CLIENT_SECRET));
        request.setDeviceType(this.configuration.getString(MBaaS.DEVICE_TYPE));
        request.setDeviceToken(this.sharedPreferences.getString(MBaaSIntentService.ACCESS_TOKEN, ""));
        return this.service.securitySignup(request);
    }

    public Call<MonitorTimeResponse> monitorTime() {
        return this.service.monitorTime();
    }

}
