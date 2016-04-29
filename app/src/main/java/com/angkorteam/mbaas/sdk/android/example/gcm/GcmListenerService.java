package com.angkorteam.mbaas.sdk.android.example.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.angkorteam.mbaas.sdk.android.example.Application;
import com.angkorteam.mbaas.sdk.android.example.MainActivity;
import com.angkorteam.mbaas.sdk.android.example.R;
import com.angkorteam.mbaas.sdk.android.library.MBaaS;

import java.io.IOException;
import java.util.Map;

/**
 * Created by socheat on 4/8/16.
 */
public class GcmListenerService extends com.angkorteam.mbaas.sdk.android.library.gcm.MBaaSGcmListenerService {

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param alert GCM message received.
     */
    @Override
    protected void onMessage(String messageId, String alert, String badge, String sound, String collapseKey, Map<String, Object> userData) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.common_ic_googleplayservices)
                .setContentTitle("GCM Message")
                .setContentText(alert)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        MBaaS mbaas = MBaaS.getInstance();
        try {
            mbaas.getClient().sendMetrics(messageId).execute();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
