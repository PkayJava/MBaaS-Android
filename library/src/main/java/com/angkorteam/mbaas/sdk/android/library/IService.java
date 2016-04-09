package com.angkorteam.mbaas.sdk.android.library;

import com.angkorteam.mbaas.sdk.android.library.request.device.DeviceRegisterRequest;
import com.angkorteam.mbaas.sdk.android.library.response.device.DeviceRegisterResponse;
import com.angkorteam.mbaas.sdk.android.library.response.monitor.MonitorTimeResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by socheat on 4/6/16.
 */
public interface IService {

    @GET("api/monitor/time")
    public Call<MonitorTimeResponse> monitorTime();

    @POST("api/rest/registry/device")
    public Call<DeviceRegisterResponse> deviceRegister(@Body DeviceRegisterRequest request);

    @DELETE("api/rest/registry/device/{id}")
    public Call<ResponseBody> deviceUnregister(@Path("id") String id);

}
