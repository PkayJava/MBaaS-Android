package com.angkorteam.mbaas.sdk.android.library;

import android.app.Application;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.util.AndroidRuntimeException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.im.SmackImInitializer;
import org.jivesoftware.smack.initializer.SmackInitializer;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by socheat on 4/29/16.
 */
public class MBaaS {

    private static MBaaS INSTANCE;
    private static final String RESOURCE;

    public static final String SENDER_ID = "senderId";
    public static final String SERVER_ADDRESS = "serverAddress";
    public static final String CLIENT_ID = "clientId";
    public static final String CLIENT_SECRET = "clientSecret";
    public static final String CATEGORY = "category";
    public static final String ALIAS = "alias";
    public static final String DEVICE_TYPE = "deviceType";
    public static final String CACHE_SIZE = "cacheSize";
    public static final String OPERATING_SYSTEM = "operatingSystem";
    public static final String OS_SYSTEM = "osSystem";
    public static final String APP_VERSION = "appVersion";

    private final Application application;
    private final XMLPropertiesConfiguration configuration;
    private final MBaaSClient mbaasClient;

    static {
        RESOURCE = MBaaS.class.getSimpleName().toLowerCase();
    }

    public static void initialize(Application application) {
        if (INSTANCE != null) {
            throw new InstantiationError("mbaas is initialized");
        }
        INSTANCE = new MBaaS(application);
    }

    public static MBaaS getInstance() {
        if (INSTANCE == null) {
            throw new InstantiationError("mbaas hasn't initialized");
        }
        return INSTANCE;
    }

    private MBaaS(Application application) {
        this.application = application;
        this.configuration = new XMLPropertiesConfiguration();
        String appVersion = "N/A";
        try {
            appVersion = application.getPackageManager().getPackageInfo(application.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        this.configuration.addProperty(APP_VERSION, appVersion);
        this.configuration.addProperty(OS_SYSTEM, Build.VERSION.CODENAME);
        this.configuration.addProperty(OPERATING_SYSTEM, Build.VERSION.RELEASE);
        InputStream inputStream = null;
        try {
            inputStream = application.getResources().openRawResource(application.getResources().getIdentifier(RESOURCE, "raw", application.getPackageName()));
        } catch (Resources.NotFoundException e) {
            throw new AndroidRuntimeException("row/mbaas.xml is not found");
        }
        Properties properties = null;
        if (inputStream != null) {
            properties = new Properties();
            try {
                properties.loadFromXML(inputStream);
            } catch (IOException e) {
            }
            IOUtils.closeQuietly(inputStream);
        }
        if (properties != null) {
            String serverAddress = (String) properties.get(MBaaS.SERVER_ADDRESS);
            if (!serverAddress.endsWith("/")) {
                serverAddress = serverAddress + "/";
            }
            this.configuration.addProperty(MBaaS.SERVER_ADDRESS, serverAddress);
            Enumeration<Object> keys = properties.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                Object value = properties.get(key);
                this.configuration.addProperty(key, value);
            }
        }
        this.mbaasClient = new MBaaSClient(application, this.configuration);
    }

    public final MBaaSClient getClient() {
        return this.mbaasClient;
    }

    public final XMLPropertiesConfiguration getConfiguration() {
        return this.configuration;
    }
}
