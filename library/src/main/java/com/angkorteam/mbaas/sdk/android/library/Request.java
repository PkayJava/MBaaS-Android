package com.angkorteam.mbaas.sdk.android.library;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by socheat on 4/6/16.
 */
public abstract class Request implements Serializable {

    @Expose
    @SerializedName("version")
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
