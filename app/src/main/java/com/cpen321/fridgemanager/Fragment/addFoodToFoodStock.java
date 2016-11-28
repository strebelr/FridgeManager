package com.cpen321.fridgemanager.Fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.cpen321.fridgemanager.Activity.MainMenu;
import com.cpen321.fridgemanager.Activity.ScanResults;
import com.cpen321.fridgemanager.Database.DatabaseInteraction;
import com.cpen321.fridgemanager.Notification.Alert;
import com.cpen321.fridgemanager.Notification.AlertReceiver;
import com.cpen321.fridgemanager.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.Context.ALARM_SERVICE;
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

        return view;
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
        String catted = Alert.concatenate(name, String.valueOf(amount), di.getCurrentDate(), di.getFutureDate(difference));
        EXPIRY_ID = Alert.convertToID(catted);
        PRE_EXPIRY_ID = Alert.convertToID(catted) + 50000;

        if(counterID[EXPIRY_ID] == 0 || counterID[PRE_EXPIRY_ID] == 0) {
            //set alarm with cases
            if(difference > 4) {
                setAlarm(view, difference - 3, PRE_EXPIRY_ID, PRE_EXPIRY);     // sends notification 3 days before expiry
                setAlarm(view, difference, EXPIRY_ID, EXPIRY);
            } else if (difference <= 3 && difference > 1) {
                setAlarm(view, 1, PRE_EXPIRY_ID, PRE_EXPIRY);              // sends notification the next day
                setAlarm(view, difference, EXPIRY_ID, EXPIRY);
            } else {
                setAlarm(view, difference, EXPIRY_ID, EXPIRY);         // only send notification on the day of expiry
            }
            counterID[EXPIRY_ID]++;
            counterID[PRE_EXPIRY_ID]++;
            android.util.Log.i("Notification ID", " ID Remaining: "+counterID[EXPIRY_ID] +" and "+counterID[PRE_EXPIRY_ID]);
        }
        else {
            counterID[EXPIRY_ID]++;
            counterID[PRE_EXPIRY_ID]++;
            android.util.Log.i("Notification ID", " ID Remaining: "+counterID[EXPIRY_ID] +" and "+counterID[PRE_EXPIRY_ID]);

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
    //public static int EXPIRY_ID; // random number to generate unique ID
    //public static int PRE_EXPIRY_ID;

    public void setAlarm(View view, int daysTillExpire, int notifID, int alarmType) {
        android.util.Log.i("Notification ID ", " Set ID: "+notifID);

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.SECOND, 10);
        //calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.add(Calendar.DAY_OF_YEAR, daysTillExpire);

        android.util.Log.i("AFTER ",": " +calendar);

        // Issues a new notification to be sent
        Intent intent = new Intent(getActivity(), AlertReceiver.class);
        intent.putExtra("NOTIF_TYPE", alarmType);
        intent.putExtra("ID", notifID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), notifID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

}
