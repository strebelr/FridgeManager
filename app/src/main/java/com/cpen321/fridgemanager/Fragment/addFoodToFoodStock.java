package com.cpen321.fridgemanager.Fragment;

import android.app.Activity;
import android.content.Context;
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
import com.cpen321.fridgemanager.Database.DatabaseInteraction;
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
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private View view;
    private int[] tabIcons = {
            R.drawable.ic_food_stock,
            R.drawable.ic_food_to_expire,
            R.drawable.ic_expenditures
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

        viewPager.setCurrentItem(0);
    }

    public void showDatePickerDialog() {
        newFragment = new AddFoodToFoodStockDatePicker();
        newFragment.show(myContext.getSupportFragmentManager(), "datePicker");
    }

}
