package com.cpen321.fridgemanager.Notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Calendar;
//This class is currently unused
public class Alert extends AppCompatActivity {

    public void setAlarm(View view, int dayToExpire, int notifID) {

        Calendar calendar = Calendar.getInstance();     // possible redundancy here
        //Calendar c = new GregorianCalendar();

        calendar.add(Calendar.SECOND, 10);
        //calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.add(Calendar.DAY_OF_YEAR, dayToExpire);

        android.util.Log.i("AFTER ",": " +calendar);

        Long alertTime = System.currentTimeMillis() + 5000;

        android.util.Log.i("Time Class ", " Time value in milliseconds "+alertTime);

        Intent intent = new Intent(getApplicationContext(), AlertReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notifID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime, pendingIntent);

        /* Comment above line and uncomment this line once expiry date is ready) */
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);

    }

}