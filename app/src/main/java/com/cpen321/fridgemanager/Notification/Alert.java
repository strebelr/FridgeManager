package com.cpen321.fridgemanager.Notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Calendar;
//This class is currently unused
public class Alert extends AppCompatActivity {

    // TODO:  MSG. WHAT HAPPENS IF JANUARY 31 AND DAY IS ADDED
    public void setAlarm(View view, int daysTillExpire, int notifID, int alarmType) {

        Calendar calendar = Calendar.getInstance();     // possible redundancy here

        calendar.add(Calendar.SECOND, 10);
        //calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.add(Calendar.DAY_OF_YEAR, daysTillExpire);

        android.util.Log.i("AFTER ",": " +calendar);

        // Issues a new notification to be sent
        Intent intent = new Intent(getApplicationContext(), AlertReceiver.class);
        intent.putExtra("NOTIF_TYPE", alarmType);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notifID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

}