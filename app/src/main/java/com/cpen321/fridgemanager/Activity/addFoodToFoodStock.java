package com.cpen321.fridgemanager.Activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.cpen321.fridgemanager.Database.DatabaseInteraction;
import com.cpen321.fridgemanager.Fragment.Expenditures;
import com.cpen321.fridgemanager.Fragment.FoodStock;
import com.cpen321.fridgemanager.Fragment.FoodToExpire;
import com.cpen321.fridgemanager.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

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

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.activity_add_food, list );
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

        final EditText foodItem =  (EditText) findViewById(R.id.addFoodName);
        String name = foodItem.getText().toString();

        final EditText amountEditText = (EditText) findViewById(R.id.amounttext);
        String amountValue = amountEditText.getText().toString();
        double amount = Double.parseDouble(amountValue);

        //final Spinner amountSpinner = (Spinner) findViewById(R.id.amountspinner);
        //String amountUnitsValue = amountSpinner.getSelectedItem().toString();
        //int amountUnit = Integer.parseInt(amountUnitsValue);


        final Spinner locationSpinner = (Spinner) findViewById(R.id.spinner1_for_location);
        String location = locationSpinner.getSelectedItem().toString();


        DatabaseInteraction di = new DatabaseInteraction(getApplicationContext());
        //di.writeToStorage(name, amount, amountUnit, location, expiry);
        di.writeToStorage(name, amount, 1, location, 2-22-2016);

    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new addFoodToFoodStockDatePicker();
        newFragment.show(getSupportFragmentManager(), "datePicker");


    }

}
