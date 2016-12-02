package com.cpen321.fridgemanager.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.cpen321.fridgemanager.Activity.MainMenu;
import com.cpen321.fridgemanager.R;

public class AlarmReceiver extends BroadcastReceiver {

    /*
      Activities occur when an intent is received
      @param context
      @param intent
    */
    @Override
    public void onReceive(Context context, Intent intent) {
        int msgType = intent.getIntExtra("NOTIF_TYPE", 0);  // grabs the notification message type
        int uniqueID = intent.getIntExtra("ID", 0);         // grabs the ID

        String msgExpire = "Your food has expired!";
        String msgSoonExpire = "Your food is about to expire!";

        if (msgType == 0) {
            createNotification(context, "Fridge Manager", msgExpire, "Alarm", uniqueID);
        }
        else {
            createNotification(context, "Fridge Manager", msgSoonExpire, "Alarm", uniqueID);
        }
    }

    /*
      Creates and format the notification settings
      @param context
      @param msg
      @param msgtext
      @param msgAlert
      @param notifID
    */
    public void createNotification(Context context, String msg, String msgText, String msgAlert, int notifID) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent repeating_intent = new Intent(context, MainMenu.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, notifID, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_fridge_empty)
                .setContentTitle(msg)
                .setContentText(msgText)
                .setTicker(msgAlert)
                .setColor(0xFFC107) // colour of the circle backgroud
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000}) // Vibration
                .setLights(0xFFFF00, 100, 50) // LED
                .setAutoCancel(true);

        notificationManager.notify(notifID, builder.build());
    }


}
