package com.angkorteam.mbaas.sdk.android.library;

import com.angkorteam.mbaas.sdk.android.library.request.device.DeviceRegisterRequest;
import com.angkorteam.mbaas.sdk.android.library.request.javascript.JavaScriptExecuteRequest;
import com.angkorteam.mbaas.sdk.android.library.response.device.DeviceMetricsResponse;
import com.angkorteam.mbaas.sdk.android.library.response.device.DeviceRegisterResponse;
import com.angkorteam.mbaas.sdk.android.library.response.device.DeviceUnregisterResponse;
import com.angkorteam.mbaas.sdk.android.library.response.javascript.JavaScriptExecuteResponse;
import com.angkorteam.mbaas.sdk.android.library.response.monitor.MonitorTimeResponse;

import java.util.Map;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by socheat on 4/6/16.
 */
public interface IService {

    @POST("api/rest/registry/device")
    public Call<DeviceRegisterResponse> deviceRegister(@Body DeviceRegisterRequest request);

    @DELETE("api/rest/registry/device/{deviceToken}")
    public Call<DeviceUnregisterResponse> deviceUnregister(@Path("deviceToken") String deviceToken);

    @PUT("api/rest/registry/device/pushMessage/{clientId}/{messageId}")
    public Call<DeviceMetricsResponse> sendMetrics(@Path("clientId") String clientId, @Path("messageId") String messageId);

    @POST("api/javascript/execute/{script}")
    public Call<JavaScriptExecuteResponse> javascriptExecutePost(@Path("script") String script);

    @POST("api/javascript/execute/{script}")
    @FormUrlEncoded
    public Call<JavaScriptExecuteResponse> javascriptExecutePost(@Path("script") String script, @FieldMap Map<String, Object> params);

    @POST("api/javascript/execute/{script}")
    public Call<JavaScriptExecuteResponse> javascriptExecutePost(@Path("script") String script, @Body JavaScriptExecuteRequest request);

    @GET("api/javascript/execute/{script}")
    public Call<JavaScriptExecuteResponse> javascriptExecuteGet(@Path("script") String script);

    @GET("api/javascript/execute/{script}")
    public Call<JavaScriptExecuteResponse> javascriptExecuteGet(@Path("script") String script, @QueryMap Map<String, Object> params);

    @PUT("api/javascript/execute/{script}")
    public Call<JavaScriptExecuteResponse> javascriptExecutePut(@Path("script") String script);

    @PUT("api/javascript/execute/{script}")
    public Call<JavaScriptExecuteResponse> javascriptExecutePut(@Path("script") String script, @Body JavaScriptExecuteRequest request);

    @GET("api/monitor/time")
    public Call<MonitorTimeResponse> monitorTime();

}
