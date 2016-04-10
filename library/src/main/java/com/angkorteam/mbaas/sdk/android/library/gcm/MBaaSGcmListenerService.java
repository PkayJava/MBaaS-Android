package com.angkorteam.mbaas.sdk.android.library.gcm;

import android.os.Bundle;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by socheat on 4/8/16.
 */
public abstract class MBaaSGcmListenerService extends com.google.android.gms.gcm.GcmListenerService {

    public MBaaSGcmListenerService() {
    }

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        String messageId = data.getString(PushMessage.PUSH_MESSAGE_ID);
        String payload = data.getString(PushMessage.ALERT_KEY);
        String badge = data.getString(PushMessage.BADGE);
        String sound = data.getString(PushMessage.SOUND);
        String collapseKey = data.getString(PushMessage.COLLAPSE_KEY);

        Map<String, Object> userData = new HashMap<>();
        for (String key : data.keySet()) {
            userData.put(key, data.get(key));
        }
        userData.remove(PushMessage.COLLAPSE_KEY);
        userData.remove(PushMessage.PUSH_MESSAGE_ID);
        userData.remove(PushMessage.SOUND);
        userData.remove(PushMessage.ALERT_KEY);
        userData.remove(PushMessage.BADGE);

        onMessage(messageId, payload, badge, sound, collapseKey, Collections.unmodifiableMap(userData));
        // [END_EXCLUDE]
    }
    // [END receive_message]

    protected abstract void onMessage(String messageId, String alert, String badge, String sound, String collapseKey, Map<String, Object> userData);
}