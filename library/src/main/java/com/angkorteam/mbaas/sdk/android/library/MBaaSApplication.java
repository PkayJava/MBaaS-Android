package com.angkorteam.mbaas.sdk.android.library;

import java.util.List;

/**
 * Created by socheat on 4/8/16.
 */
public interface MBaaSApplication {

    public MBaaSClient getMBaaSClient();

    public String getSenderId();

    public String getMBaaSAddress();

    public String getMBaaSClientId();

    public String getMBaaSClientSecret();

    public String getMBaaSDeviceType();

    public String getMBaaSAlias();

    public String getMBaaSOperatingSystem();

    public String getMBaaSOsVersion();

    public List<String> getMBaaSCategories();

}
