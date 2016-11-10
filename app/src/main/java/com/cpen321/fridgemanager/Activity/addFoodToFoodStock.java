package com.cpen321.fridgemanager.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.cpen321.fridgemanager.Database.DatabaseInteraction;
import com.cpen321.fridgemanager.Fragment.Expenditures;
import com.cpen321.fridgemanager.Fragment.FoodStock;
import com.cpen321.fridgemanager.Fragment.FoodToExpire;
import com.cpen321.fridgemanager.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.R.id.message;
import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class addFoodToFoodStock extends AppCompatActivity {

    AutoCompleteTextView text;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.ic_food_stock,
            R.drawable.ic_food_to_expire,
            R.drawable.ic_expenditures
    };

    addFoodToFoodStockDatePicker newFragment;

    public addFoodToFoodStock() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
        Button btnAddFoodToFoodStock = (Button) findViewById(R.id.button_add_to_food_stock);

        btnAddFoodToFoodStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedback(v);
            }
        });

        text = (AutoCompleteTextView) findViewById(R.id.addFoodName);
        DatabaseInteraction di = new DatabaseInteraction(getApplicationContext());
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

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_item, list);
        text.setAdapter(adapter);
        text.setThreshold(2);

        /*
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();*/

    }

    /*private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FoodStock(), "Stock");
        adapter.addFrag(new FoodToExpire(), "Alert");
        adapter.addFrag(new Expenditures(), "Spent");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }*/

    public void sendFeedback(View view) {

        EditText foodItem =  (EditText) findViewById(R.id.addFoodName);
        String name = foodItem.getText().toString();

        final EditText amountEditText = (EditText) findViewById(R.id.amounttext);
        String amountValue = amountEditText.getText().toString();
        double amount = 0;

        try {
            amount = Double.parseDouble(amountValue);
        } catch (NumberFormatException e) {}

        Spinner amountSpinner = (Spinner) findViewById(R.id.amountspinner);
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

        Spinner locationSpinner = (Spinner) findViewById(R.id.spinner1_for_location);
        String location = locationSpinner.getSelectedItem().toString();

        TextView expiryField =  (TextView) findViewById(R.id.expiry_date);
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

        DatabaseInteraction di = new DatabaseInteraction(getApplicationContext());
        di.writeToStorage(name, amount, int_unit, location, difference);

        Intent intent = new Intent(this, MainMenu.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void showDatePickerDialog(View v) {
        newFragment = new addFoodToFoodStockDatePicker();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

}
