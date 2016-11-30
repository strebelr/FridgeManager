package com.cpen321.fridgemanager.Notification;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.cpen321.fridgemanager.Activity.ScanResults;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.R.attr.digits;
import static android.content.Context.ALARM_SERVICE;

public class Alarm extends Fragment {

    // TODO:  MSG. WHAT HAPPENS IF JANUARY 31 AND DAY IS ADDED
    public void setAlarm(Context context, View view, int daysTillExpire, int notifID, int alarmType, double amount) {
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

    public void cancelAlarm(Context context, String expiry, double amount) {
        int EXPIRY_ID = convertToID(expiry);
        int PRE_EXPIRY_ID = convertToID(expiry) + 50000;
        //android.util.Log.i("Notification ID", " IDs are set: "+EXPIRY_ID + " and " + PRE_EXPIRY_ID);

        if (ScanResults.counterID[EXPIRY_ID] <= amount || ScanResults.counterID[PRE_EXPIRY_ID] <= amount) {
            Intent myIntent = new Intent(context, AlarmReceiver.class);

            // EXPIRY
            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, EXPIRY_ID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager1 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager1.cancel(pendingIntent1);// cancel alarm
            pendingIntent1.cancel();            // delete the PendingIntents

            // PRE_EXPIRY
            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, PRE_EXPIRY_ID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager2 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager2.cancel(pendingIntent2);
            pendingIntent2.cancel();

            ScanResults.counterID[EXPIRY_ID] = 0.0;//amount;
            ScanResults.counterID[PRE_EXPIRY_ID] = 0.0;//amount;

            android.util.Log.i("Notification ID", " Cancelled ID: " + EXPIRY_ID + " and " + PRE_EXPIRY_ID);
            android.util.Log.i("Notification ID", " ID Remaining: " + ScanResults.counterID[EXPIRY_ID] + " and " + ScanResults.counterID[PRE_EXPIRY_ID]);

        } else {
            if (ScanResults.counterID[EXPIRY_ID] > 0.0 || ScanResults.counterID[PRE_EXPIRY_ID] > 0.0) {
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
        int ID;
        String toBeConverted = "";
        for(int i = 0; i < cat.length()-4; i++) {
            //ID += (int)cat.charAt(i);
            if(cat.charAt(i) != '-')
                toBeConverted += cat.charAt(i);
        }

        toBeConverted += cat.charAt(cat.length()-2);
        toBeConverted += cat.charAt(cat.length()-1);


        ID = Integer.valueOf(toBeConverted);
        return ID;

    }

}