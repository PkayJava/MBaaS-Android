package com.angkorteam.mbaas.sdk.android.library.gcm;

/**
 * Created by socheat on 4/9/16.
 */
public interface PushMessage {

    /**
     * Constant for the "push payload id" key
     */
    String PUSH_MESSAGE_ID = "aerogear-push-id";

    /**
     * Constant for the name of the push message alert key
     */
    String ALERT_KEY = "alert";

    String BADGE = "badge";

    String SOUND = "sound";

    String COLLAPSE_KEY = "collapse_key";

}
