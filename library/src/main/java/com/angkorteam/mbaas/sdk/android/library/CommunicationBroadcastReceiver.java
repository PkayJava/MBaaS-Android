package com.angkorteam.mbaas.sdk.android.library;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by socheat on 5/14/16.
 */
public class CommunicationBroadcastReceiver extends BroadcastReceiver {

    public static final String FROM_USER_ID = "from-user-id";
    public static final String MESSAGE = "message";

    private CommunicationReceiver receiver;

    public CommunicationBroadcastReceiver(CommunicationReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (this.receiver != null) {
            String fromUserId = intent.getStringExtra(FROM_USER_ID);
            String message = intent.getStringExtra(MESSAGE);
            this.receiver.onMessage(fromUserId, message);
        }
    }

    /**
     * Created by socheat on 5/14/16.
     */
    public interface CommunicationReceiver {

        void onMessage(String fromUserId, String message);

    }
}
