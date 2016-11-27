package com.cpen321.fridgemanager.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.cpen321.fridgemanager.Activity.MainMenu;
import com.cpen321.fridgemanager.Activity.ScanResults;
import com.cpen321.fridgemanager.R;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int msgType = intent.getIntExtra("NOTIF_TYPE", 0);
        int uniqueID = intent.getIntExtra("ID", 0);

        String msgExpire = "Your food has expired!"; //foodName + " has expired";
        String msgSoonExpire = "Your food is expiring!"; //foodName + " is expiring soon!";

        if (msgType == 0) {
            createNotification(context, "Fridge Manager", msgExpire, "Alert", uniqueID);
        }
        else {
            createNotification(context, "Fridge Manager", msgSoonExpire, "Alert", uniqueID);
        }
    }

    public void createNotification(Context context, String msg, String msgText, String msgAlert, int notifID) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent repeating_intent = new Intent(context, MainMenu.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, notifID, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_fridge_filled)
                .setContentTitle(msg)
                .setContentText(msgText)
                .setTicker(msgAlert)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000}) // Vibration
                .setLights(0xFFFF00, 100, 50) // LED
                .setAutoCancel(true);

        notificationManager.notify(notifID, builder.build());
    }


}
