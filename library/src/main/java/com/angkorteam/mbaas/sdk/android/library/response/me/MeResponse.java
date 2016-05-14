package com.angkorteam.mbaas.sdk.android.library.response.me;

import com.angkorteam.mbaas.sdk.android.library.Response;

import java.util.HashMap;
import java.util.Map;

public class MeResponse extends Response<Map<String, Object>> {

    public MeResponse() {
        this.data = new HashMap<>();
    }

}
