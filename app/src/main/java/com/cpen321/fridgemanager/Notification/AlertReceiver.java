package com.cpen321.fridgemanager.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.cpen321.fridgemanager.Activity.MainMenu;
import com.cpen321.fridgemanager.Activity.ScanResults;
import com.cpen321.fridgemanager.R;

public class AlertReceiver extends BroadcastReceiver {

    ScanResults number = new ScanResults();
    int uniqueID = number.getNumber();

    @Override
    public void onReceive(Context context, Intent intent) {
        int msgType = intent.getIntExtra("NOTIF_TYPE", 0);

        if(msgType == 0)
            createNotification(context, "Fridge Manager","Your food has expired","Alert", uniqueID);
        else
            createNotification(context, "Fridge Manager","Your food is expiring soon!","Alert", uniqueID);
    }

    public void createNotification(Context context, String msg, String msgText, String msgAlert, int notifID) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Intent repeating_intent = new Intent(context, Alert_Activity.class);
        Intent repeating_intent = new Intent(context, MainMenu.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, notifID, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_fridge_filled)
                .setContentTitle(msg)
                .setContentText(msgText)
                .setTicker(msgAlert)
                .setAutoCancel(true);

        //Vibration
        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000 });

        //LED
        builder.setLights( 0xFFC125, 100, 50);

        notificationManager.notify(notifID, builder.build());
        android.util.Log.i("Notification ID ", " ID: "+notifID);


    }



}
