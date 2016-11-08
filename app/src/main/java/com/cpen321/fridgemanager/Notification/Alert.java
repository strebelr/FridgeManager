package com.cpen321.fridgemanager.Notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Calendar;

public class Alert extends AppCompatActivity {

    public void setAlarm(View view, int day) {

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_MONTH,day);
        calendar.set(Calendar.HOUR_OF_DAY,18);
        calendar.set(Calendar.MINUTE,00);
        calendar.set(Calendar.SECOND,00);

        Long alertTime = System.currentTimeMillis() + 5000;

        android.util.Log.i("Time Class ", " Time value in milliseconds "+alertTime);

        Intent intent = new Intent(getApplicationContext(), AlertReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime, pendingIntent);

        /* Comment above line and uncomment this line once expiry date is ready) */
        //alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);

    }

}