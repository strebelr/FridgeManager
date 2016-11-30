package com.cpen321.fridgemanager.Notification;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.cpen321.fridgemanager.Activity.ScanResults;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class Alarm extends Fragment {

    // TODO:  MSG. WHAT HAPPENS IF JANUARY 31 AND DAY IS ADDED
    public void setAlarm(Context context, View view, int daysTillExpire, int notifID, int alarmType, int amount) {
        android.util.Log.i("Notification ID ", " Set ID: "+notifID);

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.SECOND, 10);
        //calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.add(Calendar.DAY_OF_YEAR, daysTillExpire);

        android.util.Log.i("AFTER ",": " +calendar);

        // Issues a new notification to be sent
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("NOTIF_TYPE", alarmType);
        intent.putExtra("ID", notifID);
        intent.putExtra("AMOUNT", amount);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notifID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public void cancelAlarm(Context context, String expiry, int amount) {
        int EXPIRY_ID = Alert.convertToID(expiry);
        int PRE_EXPIRY_ID = Alert.convertToID(expiry) + 50000;
        //android.util.Log.i("Notification ID", " IDs are set: "+EXPIRY_ID + " and " + PRE_EXPIRY_ID);

        if (ScanResults.counterID[EXPIRY_ID] == amount || ScanResults.counterID[PRE_EXPIRY_ID] == amount) {
            Intent myIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, EXPIRY_ID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, PRE_EXPIRY_ID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager1 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            AlarmManager alarmManager2 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            // cancel the alarms
            alarmManager1.cancel(pendingIntent1);
            alarmManager2.cancel(pendingIntent2);
            // delete the PendingIntents
            pendingIntent1.cancel();
            pendingIntent2.cancel();

            ScanResults.counterID[EXPIRY_ID] -= amount;
            ScanResults.counterID[PRE_EXPIRY_ID] -= amount;

            android.util.Log.i("Notification ID", " Cancelled ID: " + EXPIRY_ID + " and " + PRE_EXPIRY_ID);
            android.util.Log.i("Notification ID", " ID Remaining: " + ScanResults.counterID[EXPIRY_ID] + " and " + ScanResults.counterID[PRE_EXPIRY_ID]);

        } else {
            if (ScanResults.counterID[EXPIRY_ID] > 0 || ScanResults.counterID[PRE_EXPIRY_ID] > 0) {
                ScanResults.counterID[EXPIRY_ID] -= amount;
                ScanResults.counterID[PRE_EXPIRY_ID] -= amount;
                android.util.Log.i("Notification ID", " Decrease from counter ID: " + EXPIRY_ID + " and " + PRE_EXPIRY_ID);
            }

            android.util.Log.i("Notification ID", " ID Remaining: " + ScanResults.counterID[EXPIRY_ID] + " and " + ScanResults.counterID[PRE_EXPIRY_ID]);
        }
    }

    public String concatenate(String name, String quantity, String bought, String expiry) {
        String cat = expiry;    // decides encoding
        return cat;
    }


    public int convertToID(String cat) {
        int ID = 0;
        for(int i = 0; i < cat.length(); i++) {
            ID += (int)cat.charAt(i);
        }
        return ID;
    }

}