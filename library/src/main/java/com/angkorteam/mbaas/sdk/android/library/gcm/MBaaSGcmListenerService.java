package com.angkorteam.mbaas.sdk.android.library.gcm;

import android.os.Bundle;

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
        String message = data.getString("message");

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
        onMessage(message);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    protected abstract void onMessage(String message);
}