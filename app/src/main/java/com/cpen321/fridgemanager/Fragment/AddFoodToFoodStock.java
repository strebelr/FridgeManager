package com.cpen321.fridgemanager.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cpen321.fridgemanager.Database.DatabaseInteraction;
import com.cpen321.fridgemanager.Notification.Alarm;
import com.cpen321.fridgemanager.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AddFoodToFoodStock extends Fragment {

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
        viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
        myAlarm = new Alarm();
        di = new DatabaseInteraction(view.getContext());

        initializeAddButton();
        initializeAutoComplete();
        initializeExpiryButton();
        initializeResetButton();
        initializeSpinners();

        setWidth();

        SharedPreferences settings = getActivity().getSharedPreferences("prefs",0);
        boolean firstCamera = settings.getBoolean("firstScan",false);
        if (firstCamera == false) {
            SharedPreferences.Editor editor= settings.edit();
            editor.putBoolean("firstScan",true);
            editor.commit();

            popDialog("Manual Entry", getString(R.string.manualentry_explanation));
        }

        return view;
    }

    private void initializeSpinners() {
        Spinner amountSpinner = (Spinner) view.findViewById(R.id.amountspinner);

        String[] amounts = getContext().getResources().getStringArray(R.array.amount);
        ArrayAdapter<String> adap1 = new ArrayAdapter<String>(getContext(), R.layout.spinner_layout, amounts );
        amountSpinner.setAdapter(adap1);
        adap1.setDropDownViewResource(R.layout.spinner_content);

        Spinner locationSpinner = (Spinner) view.findViewById(R.id.spinner1_for_location);

        String[] locations = getContext().getResources().getStringArray(R.array.locations);
        ArrayAdapter<String> adap2 = new ArrayAdapter<String>(getContext(), R.layout.spinner_layout, locations );
        adap2.setDropDownViewResource(R.layout.spinner_content);
        locationSpinner.setAdapter(adap2);
    }

    private void setWidth() {
        AutoCompleteTextView text = (AutoCompleteTextView) view.findViewById(R.id.addFoodName);
        final EditText foodAbbr = (EditText) view.findViewById(R.id.addFoodAbbr);
        final EditText amountEditText = (EditText) view.findViewById(R.id.amounttext);

        float measure = text.getPaint().measureText("XXXXXXXXXX"); // Set width
        text.setWidth(text.getPaddingLeft() + text.getPaddingRight() + (int) measure);
        foodAbbr.setWidth(foodAbbr.getPaddingLeft() + foodAbbr.getPaddingRight() + (int) measure);
        amountEditText.setWidth(amountEditText.getPaddingLeft() + amountEditText.getPaddingRight() + (int) measure);
    }

    private void initializeAddButton() {
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
    }

    private void initializeExpiryButton() {
        TextView expiryField =  (TextView) view.findViewById(R.id.expiry_date);
        expiryField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    private void initializeResetButton() {
        Button btnReset = (Button) view.findViewById(R.id.button_reset);
        btnReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                reset();
            }
        });
    }

    private void initializeAutoComplete() {
        AutoCompleteTextView text = (AutoCompleteTextView) view.findViewById(R.id.addFoodName);

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
    }

    private void hideFields(View view) {
        Spinner addTo = (Spinner) view.findViewById(R.id.spinner1_for_library);
        String addToValue = addTo.getSelectedItem().toString();

        final EditText foodItem =  (EditText) view.findViewById(R.id.addFoodName);
        final EditText foodAbbr = (EditText) view.findViewById(R.id.addFoodAbbr);
        final EditText amountEditText = (EditText) view.findViewById(R.id.amounttext);

        if(addToValue.equals("Food Stock"))
        {
            foodItem.setImeOptions(EditorInfo.IME_ACTION_DONE);
            foodAbbr.setEnabled(false);
            foodAbbr.setText("XXXX");
            amountEditText.setEnabled(true);
            if (amountEditText.getText().toString().equals("XXXX"))
                amountEditText.setText("");
        }
        else if(addToValue.equals("Library")) {
            foodItem.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            foodAbbr.setEnabled(true);
            if (foodAbbr.getText().toString().equals("XXXX") || foodAbbr.getText().toString().equals("")) {
                foodAbbr.setText("");
                foodAbbr.setHint("Optional");
            }
            foodAbbr.setImeOptions(EditorInfo.IME_ACTION_DONE);
            amountEditText.setEnabled(false);
            amountEditText.setText("XXXX");
        }
        else if(addToValue.equals("Both")) {
            foodItem.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            foodAbbr.setEnabled(true);
            if (foodAbbr.getText().toString().equals("XXXX") || foodAbbr.getText().toString().equals("")) {
                foodAbbr.setText("");
                foodAbbr.setHint("Optional");
            }
            foodAbbr.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            amountEditText.setEnabled(true);
            if (amountEditText.getText().toString().equals("XXXX"))
                amountEditText.setText("");
        }
        else {
            foodItem.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            foodAbbr.setEnabled(true);
            if (foodAbbr.getText().toString().equals("XXXX"))
                foodAbbr.setText("");
            amountEditText.setEnabled(true);
            foodAbbr.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            if (amountEditText.getText().toString().equals("XXXX"))
                amountEditText.setText("");
        }
    }

    private void reset() {
        Spinner addTo = (Spinner) view.findViewById(R.id.spinner1_for_library);

        final EditText foodItem =  (EditText) view.findViewById(R.id.addFoodName);
        final EditText foodAbbr = (EditText) view.findViewById(R.id.addFoodAbbr);
        final EditText amountEditText = (EditText) view.findViewById(R.id.amounttext);
        Spinner amountSpinner = (Spinner) view.findViewById(R.id.amountspinner);
        Spinner locationSpinner = (Spinner) view.findViewById(R.id.spinner1_for_location);
        TextView expiryField =  (TextView) view.findViewById(R.id.expiry_date);

        addTo.setSelection(0);
        foodItem.setText("");
        foodAbbr.setEnabled(true);
        foodAbbr.setText("");
        amountEditText.setEnabled(true);
        amountEditText.setText("");
        amountSpinner.setSelection(0);
        locationSpinner.setSelection(0);
        expiryField.setText("");

        Toast toast = Toast.makeText(getContext(), "Reset Success", Toast.LENGTH_SHORT);
        toast.show();
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


        if(amount <= 0 && !alert && !(addToValue.equals("Library"))) {
            // Pop warning
            popDialog("Error", "A valid amount (non-zero) must be entered");
            alert = true;
        }

        if(expiryDate.equals("") && !alert) {
            popDialog("Error", "An expiry date must be entered");
            alert = true;
        }


        // Add Food to Selected Destination
        if (!alert) {
            if (addToValue.equals("Food Stock")) {
                // Write to database interaction.
                di.writeToStorage(name, amount, int_unit, location, difference);
                viewPager.setCurrentItem(0);

                Toast toast = Toast.makeText(getContext(), "Success: " + name + " added to food stock.", Toast.LENGTH_SHORT);
                toast.show();

                // Set alarms
                myAlarm.prepAlarm(myContext, view, myAlarm, di, difference, amount);
            } else if (addToValue.equals("Library")) {
                // Check if the food item in the library with the same name contains the
                // abbreviation that is entered. Then add to the library. Amount is disabled.
                final EditText foodAbbr = (EditText) view.findViewById(R.id.addFoodAbbr);
                String abbr = foodAbbr.getText().toString();

                // If all fields are entered
                di.addToLibrary(name, abbr, difference, int_unit, location);
                viewPager.setCurrentItem(0);

                Toast toast = Toast.makeText(getContext(), "Success: " + name + " added to library.", Toast.LENGTH_SHORT);
                toast.show();

            } else if (addToValue.equals("Both")) {
                // Add to library if does not exist.

                final EditText foodAbbr = (EditText) view.findViewById(R.id.addFoodAbbr);
                String abbr = foodAbbr.getText().toString();

                // If all fields are entered
                di.writeToStorage(name, amount, int_unit, location, difference);
                di.addToLibrary(name, abbr, difference, int_unit, location);
                viewPager.setCurrentItem(0);
                // Set alarms
                myAlarm.prepAlarm(myContext, view, myAlarm, di, difference, amount);

                Toast toast = Toast.makeText(getContext(), "Success: " + name + " added to food stock and library.", Toast.LENGTH_SHORT);
                toast.show();

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
