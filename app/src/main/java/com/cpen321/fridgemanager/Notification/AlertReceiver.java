package com.cpen321.fridgemanager.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.cpen321.fridgemanager.Activity.MainMenu;
import com.cpen321.fridgemanager.R;

public class AlertReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        createNotification(context, "Expired Food","Your food has expired","Alert");

    }

    public void createNotification(Context context, String msg, String msgText, String msgAlert) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Intent repeating_intent = new Intent(context, Alert_Activity.class);
        Intent repeating_intent = new Intent(context, MainMenu.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

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

        notificationManager.notify(100,builder.build());

    }



}
