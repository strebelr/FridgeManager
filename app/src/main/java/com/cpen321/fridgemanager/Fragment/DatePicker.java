package com.cpen321.fridgemanager.Fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.cpen321.fridgemanager.Activity.MainMenu;
import com.cpen321.fridgemanager.Database.DatabaseInteraction;
import com.cpen321.fridgemanager.Notification.Alarm;
import com.cpen321.fridgemanager.R;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static com.cpen321.fridgemanager.Activity.ScanResults.EXPIRY_ID;
import static com.cpen321.fridgemanager.Activity.ScanResults.PRE_EXPIRY_ID;
import static com.cpen321.fridgemanager.Activity.ScanResults.counterID;

public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    int year;
    int month;
    int day;
    public static String newExpiry = "";

    private JSONObject obj;
    private Alarm a = new Alarm();

    FragmentActivity mContext;

    private DatabaseInteraction di;

    private static final int EXPIRY = 0;        // expired
    private static final int PRE_EXPIRY = 1;    // soon to expire

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            mContext=(FragmentActivity) context;
            di = new DatabaseInteraction(mContext);
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int i, int i1, int i2) {
        int month = view.getMonth() + 1;
        year = view.getYear();
        this.month = month;
        day = view.getDayOfMonth();

        // TODO: FIX THIS

        Calendar c = Calendar.getInstance();
        Calendar expiry = Calendar.getInstance();
        expiry.set(year, month - 1, day);

        newExpiry = (view.getYear() + "-" + month + "-" + view.getDayOfMonth());
        android.util.Log.i("Expiry ", " New Expiry: "+newExpiry);

        int daysTillExpiry =  (int) TimeUnit.DAYS.convert(expiry.getTime().getTime() - c.getTime().getTime(), TimeUnit.MILLISECONDS);

        EXPIRY_ID = a.convertToID(di.getFutureDate(daysTillExpiry));
        PRE_EXPIRY_ID = a.convertToID(di.getFutureDate(daysTillExpiry)) + 50000;

        String oldExpiry = obj.optString("expiry").toString();
        double amount = Double.parseDouble(obj.optString("quantity").toString());
        String name = obj.optString("name");
        String location = obj.optString("location");
        int unit = Integer.parseInt(obj.optString("unit").toString());


        a.cancelAlarm(getActivity(), oldExpiry, amount);
        prepAlarm(daysTillExpiry, view, amount);

        //TODO: UPDATE FOOD FIELDS HERE
        di.removeFood(obj, location);
        di.writeToStorage(name, amount, unit, location, daysTillExpiry);
        ((MainMenu)getActivity()).refresh();
    }

    private void prepAlarm(int expiry, View view, double amount) {
        EXPIRY_ID =  a.convertToID(di.getFutureDate(expiry));
        PRE_EXPIRY_ID =  a.convertToID(di.getFutureDate(expiry)) + 50000;

        if (counterID[EXPIRY_ID] == 0 || counterID[PRE_EXPIRY_ID] == 0) {

            //sets alarm with cases
            if (expiry > 4) {
                a.setAlarm(mContext, view, expiry - 3, PRE_EXPIRY_ID, PRE_EXPIRY, amount);  // sends notification 3 days before expiry
                a.setAlarm(mContext, view, expiry, EXPIRY_ID, EXPIRY, amount);
            } else if (expiry <= 3 && expiry > 1) {
                a.setAlarm(mContext, view, 1, PRE_EXPIRY_ID, PRE_EXPIRY, amount);           // sends notification the next day
                a.setAlarm(mContext, view, expiry, EXPIRY_ID, EXPIRY, amount);
            } else {
                a.setAlarm(mContext, view, expiry, EXPIRY_ID, EXPIRY, amount);              // only sends notification on the day of expiry
            }
            counterID[EXPIRY_ID] += amount;
            counterID[PRE_EXPIRY_ID]+= amount;
            android.util.Log.i("Notification ID", " ID Remaining: " + counterID[EXPIRY_ID] + " and " + counterID[PRE_EXPIRY_ID]);
        } else {

            counterID[EXPIRY_ID]+= amount;
            counterID[PRE_EXPIRY_ID]+= amount;
            android.util.Log.i("Notification ID", " ID Remaining: " + counterID[EXPIRY_ID] + " and " + counterID[PRE_EXPIRY_ID]);
        }
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public String getExpiry() {
        return newExpiry;
    }

    public void setJSONObject(JSONObject obj) {
        this.obj = obj;
    }

}