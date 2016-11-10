package com.cpen321.fridgemanager.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import com.cpen321.fridgemanager.R;

import java.util.Calendar;

public class addFoodToFoodStockDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    int year;
    int month;
    int day;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int i, int i1, int i2) {
        TextView tv1 = (TextView) getActivity().findViewById(R.id.expiry_date);
        int month = view.getMonth() + 1;
        year = view.getYear();
        this.month = month;
        day = view.getDayOfMonth();
        tv1.setText(view.getYear() + "-" + month + "-" + view.getDayOfMonth());
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

}
