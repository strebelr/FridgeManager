package com.cpen321.fridgemanager.Notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Calendar;
//This class is currently unused
public class Alert extends AppCompatActivity {

    public void cancelAlarm(int notifID) {

        Intent myIntent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, notifID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        // cancel the alarm
        alarmManager.cancel(pendingIntent);

        // delete the PendingIntent

        pendingIntent.cancel();
        android.util.Log.i("Notification ID"," Cancelled ID: "+notifID);
    }

    // TODO:  MSG. WHAT HAPPENS IF JANUARY 31 AND DAY IS ADDED
    public void setAlarm(View view, int daysTillExpire, int notifID, int alarmType) {
        android.util.Log.i("Notification ID ", " Set ID: "+notifID);

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.SECOND, 10);
        //calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.add(Calendar.DAY_OF_YEAR, daysTillExpire);

        android.util.Log.i("AFTER ",": " +calendar);

        // Issues a new notification to be sent
        Intent intent = new Intent(getApplicationContext(), AlertReceiver.class);
        intent.putExtra("NOTIF_TYPE", alarmType);
        intent.putExtra("ID", notifID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notifID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public static String concatenate(String name, String quantity, String bought, String expiry) {
        String cat = expiry;    // decides encoding
        return cat;
    }


    public static int convertToID(String cat) {
        int ID = 0;
        for(int i = 0; i < cat.length(); i++) {
            ID += (int)cat.charAt(i);
        }
        return ID;
    }

}