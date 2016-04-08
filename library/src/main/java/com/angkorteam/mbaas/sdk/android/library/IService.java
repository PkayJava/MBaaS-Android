package com.angkorteam.mbaas.sdk.android.library;

import com.angkorteam.mbaas.sdk.android.library.request.device.DeviceRegisterRequest;
import com.angkorteam.mbaas.sdk.android.library.response.device.DeviceRegisterResponse;
import com.angkorteam.mbaas.sdk.android.library.response.monitor.MonitorTimeResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by socheat on 4/6/16.
 */
public interface IService {

    @GET("api/monitor/time")
    public Call<MonitorTimeResponse> monitorTime();

    @POST("api/rest/registry/device")
    public Call<DeviceRegisterResponse> deviceRegister(@Body DeviceRegisterRequest request);

}
