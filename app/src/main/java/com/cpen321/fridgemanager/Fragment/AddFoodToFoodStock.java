package com.cpen321.fridgemanager.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.cpen321.fridgemanager.Database.DatabaseInteraction;
import com.cpen321.fridgemanager.Notification.Alarm;
import com.cpen321.fridgemanager.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AddFoodToFoodStock extends Fragment {

    AutoCompleteTextView text;
    private FragmentActivity myContext;
    private ViewPager viewPager;
    private View view;

    AddFoodToFoodStockDatePicker newFragment;
    private Alarm myAlarm;

    private DatabaseInteraction di;

    public AddFoodToFoodStock() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState ){
        this.view = inflater.inflate(R.layout.activity_add_food, container, false);

        di = new DatabaseInteraction(view.getContext());

        Button btnAddFoodToFoodStock = (Button) view.findViewById(R.id.button_add_to_food_stock);
        btnAddFoodToFoodStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedback();
            }
        });

        Spinner addTo = (Spinner) view.findViewById(R.id.spinner1_for_library);
        addTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            public void onItemSelected(AdapterView<?> arg0, View v, int position, long id)
            {
                hideFields(view);
            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        text = (AutoCompleteTextView) view.findViewById(R.id.addFoodName);
        DatabaseInteraction di = new DatabaseInteraction(view.getContext());
        List<String>  list = new ArrayList<>();
        JSONArray jsonArray = di.getArray("Library");
        if (jsonArray != null) {
            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    list.add(jsonArray.getJSONObject(i).optString("name").toString());
                }
            } catch (JSONException e) {
            }
        }

        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.list_item, list);
        text.setAdapter(adapter);
        text.setThreshold(2);

        ImageButton button = (ImageButton) view.findViewById(R.id.btn_image);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);

        myAlarm = new Alarm();

        return view;
    }

    private void hideFields(View view) {
        Spinner addTo = (Spinner) view.findViewById(R.id.spinner1_for_library);
        String addToValue = addTo.getSelectedItem().toString();

        final EditText foodAbbr = (EditText) view.findViewById(R.id.addFoodAbbr);
        final EditText amountEditText = (EditText) view.findViewById(R.id.amounttext);
        Spinner amountSpinner = (Spinner) view.findViewById(R.id.amountspinner);

        if(addToValue.equals("Food Stock"))
        {
            foodAbbr.setEnabled(false);
            foodAbbr.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
            amountEditText.setEnabled(true);
            amountEditText.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            amountSpinner.setEnabled(true);
        }
        else if(addToValue.equals("Library")) {
            amountEditText.setEnabled(false);
            amountEditText.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
            amountSpinner.setEnabled(false);
            foodAbbr.setEnabled(true);
            foodAbbr.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            myContext=(FragmentActivity) context;
        }
    }


    public void sendFeedback() {

        // Get All Fields
        final EditText foodItem =  (EditText) view.findViewById(R.id.addFoodName);
        final EditText amountEditText = (EditText) view.findViewById(R.id.amounttext);
        Spinner amountSpinner = (Spinner) view.findViewById(R.id.amountspinner);
        String amountUnitsValue = amountSpinner.getSelectedItem().toString();
        Spinner locationSpinner = (Spinner) view.findViewById(R.id.spinner1_for_location);
        TextView expiryField =  (TextView) view.findViewById(R.id.expiry_date);
        Spinner addTo = (Spinner) view.findViewById(R.id.spinner1_for_library);

        // Food Variables
        String name = foodItem.getText().toString();
        double amount = 0;
        try {
            amount = Double.parseDouble(amountEditText.getText().toString());
        } catch (NumberFormatException e) {}

        int int_unit;
        if (amountUnitsValue.equals("units"))
            int_unit = DatabaseInteraction.UNIT;
        else if (amountUnitsValue.equals("grams"))
            int_unit = DatabaseInteraction.GRAM;
        else if (amountUnitsValue.equals("kilograms"))
            int_unit = DatabaseInteraction.KG;
        else if (amountUnitsValue.equals("liters"))
            int_unit = DatabaseInteraction.L;
        else
            int_unit = DatabaseInteraction.CUP;

        String location = locationSpinner.getSelectedItem().toString();
        String expiryDate = expiryField.getText().toString(); // This contains the expiry date value that has to be formatted correctly.


        String[] strings;
        Calendar expiry = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        
        // TODO: CHECK VALID EXPIRY
        int difference = 0;
        if (expiryDate.length() != 0) {
            strings = expiryDate.split("-");
            int year = Integer.parseInt(strings[0]);

            int month = Integer.parseInt(strings[1]);
            int day = Integer.parseInt(strings[2]);

            expiry.clear();
            expiry.set(year, month - 1, day);
            difference = (int) TimeUnit.DAYS.convert(expiry.getTime().getTime() - today.getTime().getTime(), TimeUnit.MILLISECONDS) + 1;
        }

        String addToValue = addTo.getSelectedItem().toString();

        boolean alert = false;

        if(name != null) {
            if(name.length() == 0) {
                // Pop warning
                popDialog("Error", "Food name needs to be entered");
                alert = true;
            }
        }
        else {
            // Pop warning
            popDialog("Error", "Food name needs to be entered");
            alert = true;
        }

        if(amount == 0 && !alert) {
            // Pop warning
            popDialog("Error", "Amount needs to be entered");
            alert = true;
        }


        // Add Food to Selected Destination
        if (!alert) {
            if (addToValue.equals("Food Stock")) {
                // Write to database interaction.
                di.writeToStorage(name, amount, int_unit, location, difference);
                viewPager.setCurrentItem(0);

                // Set alarms
                myAlarm.prepAlarm(myContext, view, myAlarm, di, difference, amount);

            } else if (addToValue.equals("Library")) {
                // Check if the food item in the library with the same name contains the
                // abbreviation that is entered. Then add to the library. Amount is disabled.
                final EditText foodAbbr = (EditText) view.findViewById(R.id.addFoodAbbr);
                String abbr = foodAbbr.getText().toString();

                if (abbr != null) {
                    if (abbr.length() == 0) {
                        // Pop warning
                        popDialog("Error", "Abbreviation needs to be entered");
                    } else {
                        // If all fields are entered
                        // TODO: ADD TO LIBRARY
                        viewPager.setCurrentItem(0);
                    }
                } else {
                    // Pop warning
                    popDialog("Error", "Abbreviation needs to be entered");
                }

            } else if (addToValue.equals("Both")) {
                // Add to library if does not exist.
                di.writeToStorage(name, amount, int_unit, location, difference);
                // TODO: ADD TO LIBRARY
                viewPager.setCurrentItem(0);

                // Set alarms
                myAlarm.prepAlarm(myContext, view, myAlarm, di, difference, amount);

            } else {
                // Pop Warning
                popDialog("Error", "Add to desination must be specified");
            }
        }

    }

    /*
      Show a dialogue with provided message.
      @param message to show in the dialogue
     */
    private void popDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showDatePickerDialog() {
        newFragment = new AddFoodToFoodStockDatePicker();
        newFragment.show(myContext.getSupportFragmentManager(), "datePicker");
    }

    /**Notifications**/
    private static final int EXPIRY = 0;        // expired
    private static final int PRE_EXPIRY = 1;    // soon to expire

}
