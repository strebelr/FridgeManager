package com.cpen321.fridgemanager.Fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import com.cpen321.fridgemanager.Activity.MainMenu;
import com.cpen321.fridgemanager.Database.DatabaseInteraction;
import com.cpen321.fridgemanager.Notification.Alarm;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static com.cpen321.fridgemanager.Notification.Alarm.EXPIRY_ID;
import static com.cpen321.fridgemanager.Notification.Alarm.PRE_EXPIRY_ID;

public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    int year;
    int month;
    int day;
    public static String newExpiry = "";

    private JSONObject obj;
    private Alarm myAlarm = new Alarm();

    FragmentActivity mContext;

    private DatabaseInteraction di;

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

        EXPIRY_ID = myAlarm.convertToID(di.getFutureDate(daysTillExpiry));
        PRE_EXPIRY_ID = myAlarm.convertToID(di.getFutureDate(daysTillExpiry)) + 50000;

        String oldExpiry = obj.optString("expiry").toString();
        double amount = Double.parseDouble(obj.optString("quantity").toString());
        String name = obj.optString("name");
        String location = obj.optString("location");
        int unit = Integer.parseInt(obj.optString("unit").toString());

        myAlarm.cancelAlarm(getActivity(), oldExpiry, amount);  // cancel alarms for old expiry date
        myAlarm.prepAlarm(mContext, view, myAlarm, di, daysTillExpiry, amount); // set new alarms for new expiry dates

        //TODO: UPDATE FOOD FIELDS HERE
        di.removeFood(obj, location);
        di.writeToStorage(name, amount, unit, location, daysTillExpiry);
        ((MainMenu)getActivity()).refresh();
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