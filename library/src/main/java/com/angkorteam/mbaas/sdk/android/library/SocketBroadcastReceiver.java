package com.angkorteam.mbaas.sdk.android.library;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by socheat on 5/14/16.
 */
public class SocketBroadcastReceiver extends BroadcastReceiver {

    public static final String UUID = "uuid";
    public static final String USER_ID = "userId";
    public static final String MESSAGE = "message";

    private SocketReceiver receiver;

    public SocketBroadcastReceiver(SocketReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (this.receiver != null) {
            String uuid = intent.getStringExtra(UUID);
            String userId = intent.getStringExtra(USER_ID);
            String message = intent.getStringExtra(MESSAGE);
            this.receiver.onMessage(uuid, userId, message);
        }
    }

    /**
     * Created by socheat on 5/14/16.
     */
    public interface SocketReceiver {

        SocketBroadcastReceiver getSocketBroadcastReceiver();

        void onMessage(String uuid, String userId, String message);

    }
}
