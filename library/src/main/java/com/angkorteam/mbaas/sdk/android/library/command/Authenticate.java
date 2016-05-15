package com.angkorteam.mbaas.sdk.android.library.command;

import com.angkorteam.mbaas.sdk.android.library.netty.Command;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socheat on 5/14/16.
 */
public class Authenticate extends Command {

    @Expose
    @SerializedName("accessToken")
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
