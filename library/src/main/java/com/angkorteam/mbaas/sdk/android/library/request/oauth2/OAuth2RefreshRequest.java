package com.angkorteam.mbaas.sdk.android.library.request.oauth2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OAuth2RefreshRequest {

    @Expose
    @SerializedName("refreshToken")
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}