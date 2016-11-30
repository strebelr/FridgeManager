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

    /*
      Sets up the alarm
      @param context
      @param view
      @param daysTillExpire
      @param notifID
      @param alarmType
      @param amount
    */
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

    /*
      Cancels the existing alarm or decrease the count of food expiring on the same day
      @param context
      @param expiry
      @param amount
    */
    public void cancelAlarm(Context context, String expiry, double amount) {
        int EXPIRY_ID = convertToID(expiry);
        int PRE_EXPIRY_ID = convertToID(expiry) + 50000;
        //android.util.Log.i("Notification ID", " IDs are set: "+EXPIRY_ID + " and " + PRE_EXPIRY_ID);

        // checks if remaining ID is smaller than the amount being deleted
        if (ScanResults.counterID[EXPIRY_ID] <= amount || ScanResults.counterID[PRE_EXPIRY_ID] <= amount) {
            Intent myIntent = new Intent(context, AlarmReceiver.class);

            // EXPIRY
            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, EXPIRY_ID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager1 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager1.cancel(pendingIntent1);   // cancels the alarm
            pendingIntent1.cancel();                // deletes the PendingIntents

            // PRE_EXPIRY
            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, PRE_EXPIRY_ID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager2 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager2.cancel(pendingIntent2);
            pendingIntent2.cancel();

            // resets number of ID on same day back to 0
            ScanResults.counterID[EXPIRY_ID] = 0.0;
            ScanResults.counterID[PRE_EXPIRY_ID] = 0.0;

            android.util.Log.i("Notification ID", " Cancelled ID: " + EXPIRY_ID + " and " + PRE_EXPIRY_ID);
            android.util.Log.i("Notification ID", " ID Remaining: " + ScanResults.counterID[EXPIRY_ID] + " and " + ScanResults.counterID[PRE_EXPIRY_ID]);

        // delete the given quantity/amount if there are IDs still remaining
        } else {
            if (ScanResults.counterID[EXPIRY_ID] > 0.0 || ScanResults.counterID[PRE_EXPIRY_ID] > 0.0) {
                ScanResults.counterID[EXPIRY_ID] -= amount;
                ScanResults.counterID[PRE_EXPIRY_ID] -= amount;
                android.util.Log.i("Notification ID", " Decrease from counter ID: " + EXPIRY_ID + " and " + PRE_EXPIRY_ID);
            }

            android.util.Log.i("Notification ID", " ID Remaining: " + ScanResults.counterID[EXPIRY_ID] + " and " + ScanResults.counterID[PRE_EXPIRY_ID]);
        }
    }

    /*
      Algorithm to generate unique IDs for notifications
      @param cat
    */
    public int convertToID(String cat) {
        int ID;
        String toBeConverted = "";
        for(int i = 0; i < cat.length()-4; i++) {
            if(cat.charAt(i) != '-')
                toBeConverted += cat.charAt(i);
        }

        toBeConverted += cat.charAt(cat.length()-2);
        toBeConverted += cat.charAt(cat.length()-1);

        // should return the date of expiry concatenated without '-' and the first two digits of the year
        ID = Integer.valueOf(toBeConverted);
        return ID;
    }

}