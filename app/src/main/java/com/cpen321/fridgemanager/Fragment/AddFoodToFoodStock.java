package com.cpen321.fridgemanager.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
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

import static com.cpen321.fridgemanager.Activity.ScanResults.EXPIRY_ID;
import static com.cpen321.fridgemanager.Activity.ScanResults.PRE_EXPIRY_ID;
import static com.cpen321.fridgemanager.Activity.ScanResults.counterID;

public class AddFoodToFoodStock extends Fragment {

    AutoCompleteTextView text;
    private FragmentActivity myContext;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private View view;
    private int[] tabIcons = {
            R.drawable.ic_food_stock,
            R.drawable.ic_food_to_expire,
            R.drawable.ic_plus
    };

    AddFoodToFoodStockDatePicker newFragment;
    private Alarm myAlarm;

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
            String text = "";
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
            amountEditText.setEnabled(true);
            amountSpinner.setEnabled(true);

        }
        else if(addToValue.equals("Library")) {
            amountEditText.setEnabled(false);
            amountSpinner.setEnabled(false);
            foodAbbr.setEnabled(true);
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

        final EditText foodItem =  (EditText) view.findViewById(R.id.addFoodName);
        String name = foodItem.getText().toString();

        final EditText amountEditText = (EditText) view.findViewById(R.id.amounttext);
        String amountValue = amountEditText.getText().toString();
        double amount = 0;

        try {
            amount = Double.parseDouble(amountValue);
        } catch (NumberFormatException e) {}

        Spinner amountSpinner = (Spinner) view.findViewById(R.id.amountspinner);
        String amountUnitsValue = amountSpinner.getSelectedItem().toString();
        int int_unit;
        if (amountUnitsValue.equals("units"))
            int_unit = 0;
        else if (amountUnitsValue.equals("grams"))
            int_unit = 1;
        else if (amountUnitsValue.equals("kilograms"))
            int_unit = 2;
        else if (amountUnitsValue.equals("liters"))
            int_unit = 3;
        else
            int_unit = 4;

        Spinner locationSpinner = (Spinner) view.findViewById(R.id.spinner1_for_location);
        String location = locationSpinner.getSelectedItem().toString();

        TextView expiryField =  (TextView) view.findViewById(R.id.expiry_date);
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

        DatabaseInteraction di = new DatabaseInteraction(view.getContext());
        di.writeToStorage(name, amount, int_unit, location, difference);

        //Set alarms
        //String catted = myAlarm.concatenate(name, String.valueOf(amount), di.getCurrentDate(), di.getFutureDate(difference));
        EXPIRY_ID = myAlarm.convertToID(di.getFutureDate(difference));
        PRE_EXPIRY_ID = myAlarm.convertToID(di.getFutureDate(difference)) + 50000;

        if(counterID[EXPIRY_ID] == 0.0 || counterID[PRE_EXPIRY_ID] == 0.0) {
            //set alarm with cases
            if(difference > 4) {
                myAlarm.setAlarm(myContext, view, difference - 3, PRE_EXPIRY_ID, PRE_EXPIRY, amount);     // sends notification 3 days before expiry
                myAlarm.setAlarm(myContext, view, difference, EXPIRY_ID, EXPIRY, amount);
            } else if (difference <= 3 && difference > 1) {
                myAlarm.setAlarm(myContext, view, 1, PRE_EXPIRY_ID, PRE_EXPIRY, amount);              // sends notification the next day
                myAlarm.setAlarm(myContext, view, difference, EXPIRY_ID, EXPIRY, amount);
            } else {
                myAlarm.setAlarm(myContext, view, difference, EXPIRY_ID, EXPIRY, amount);         // only send notification on the day of expiry
            }
            counterID[EXPIRY_ID]+= amount;
            counterID[PRE_EXPIRY_ID]+= amount;
            android.util.Log.i("Notification ID", " ID Remaining: "+counterID[EXPIRY_ID] +" and "+counterID[PRE_EXPIRY_ID]);
        }
        else {
            counterID[EXPIRY_ID]+= amount;
            counterID[PRE_EXPIRY_ID]+= amount;
            android.util.Log.i("Notification ID", " ID Remaining: "+counterID[EXPIRY_ID] +" and "+counterID[PRE_EXPIRY_ID]);

        }

        Spinner addTo = (Spinner) view.findViewById(R.id.spinner1_for_library);
        String addToValue = addTo.getSelectedItem().toString();

        if(addToValue.equals("Food Stock"))
        {
            //Write to database interaction.
        }
        else if(addToValue.equals("Library")) {
            //
            final EditText foodAbbr = (EditText) view.findViewById(R.id.addFoodAbbr);
            String foodAbbrValue = foodAbbr.getText().toString();
            //Check if the food item in the library with the same name contains the
            //abbreviation that is entered. Then add to the library.
        }
        else if(addToValue.equals("Both")) {
            //Add to library if does not exist.
        }



        viewPager.setCurrentItem(0);
    }

    public void showDatePickerDialog() {
        newFragment = new AddFoodToFoodStockDatePicker();
        newFragment.show(myContext.getSupportFragmentManager(), "datePicker");
    }

    /**Notifications**/
    private static final int EXPIRY = 0;        // expired
    private static final int PRE_EXPIRY = 1;    // soon to expire

    /*public void setAlarm(View view, int daysTillExpire, int notifID, int alarmType, int amount) {
        android.util.Log.i("Notification ID ", " Set ID: "+notifID);

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.SECOND, 10);
        //calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.add(Calendar.DAY_OF_YEAR, daysTillExpire);

        android.util.Log.i("AFTER ",": " +calendar);

        // Issues a new notification to be sent
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        intent.putExtra("NOTIF_TYPE", alarmType);
        intent.putExtra("ID", notifID);
        intent.putExtra("AMOUNT", amount);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), notifID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }*/

}
