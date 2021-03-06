package com.angkorteam.mbaas.sdk.android.library;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.UUID;

/**
 * Created by socheat on 4/15/16.
 */
public final class HttpBroadcastReceiver extends BroadcastReceiver {

    public static final String EVENT = "event";

    public static final int EVENT_RESPONSE = 0;
    public static final int EVENT_FAILURE = 1;
    public static final int EVENT_UNAUTHORIZED = 2;
    public static final int EVENT_LOGIN = 3;

    public static final String EVENT_ID = "event_id";
    public static final String EVENT_JSON = "event_json";
    public static final String EVENT_MESSAGE = "event_message";
    public static final String EVENT_ACTIVITY = "event_activity";

    private final IntentFilter intentFilter;
    private final String uuid;

    private HttpReceiver receiver;

    public HttpBroadcastReceiver(HttpReceiver receiver) {
        this.receiver = receiver;
        this.uuid = UUID.randomUUID().toString();
        this.intentFilter = new IntentFilter(this.uuid);
    }

    public void setNetworkReceiver(HttpReceiver httpReceiver) {
        this.receiver = httpReceiver;
    }

    @Override
    public final void onReceive(Context context, Intent intent) {
        if (this.receiver != null) {
            int event = intent.getIntExtra(EVENT, -1);
            if (event == EVENT_RESPONSE) {
                int eventId = intent.getIntExtra(EVENT_ID, -1);
                String json = intent.getStringExtra(EVENT_JSON);
                this.receiver.onResponse(eventId, json);
            } else if (event == EVENT_FAILURE) {
                int eventId = intent.getIntExtra(EVENT_ID, -1);
                String message = intent.getStringExtra(EVENT_MESSAGE);
                this.receiver.onFailure(eventId, message);
            } else if (event == EVENT_LOGIN) {
                int eventId = intent.getIntExtra(EVENT_ID, -1);
                Intent intentActivity = new Intent(context, LoginActivity.class);
                intentActivity.putExtra(EVENT_ID, eventId);
                intentActivity.putExtra(EVENT_ACTIVITY, intent.getStringExtra(EVENT_ACTIVITY));
                intentActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentActivity);
            }
        }
    }

    public IntentFilter getIntentFilter() {
        return this.intentFilter;
    }

    public String getUuid() {
        return this.uuid;
    }

    public interface HttpReceiver {

        BroadcastReceiver getHttpBroadcastReceiver();

        void onResponse(int operationId, String json);

        void onFailure(int operationId, String message);

        void onUnauthorized(int operationId);

    }
}
