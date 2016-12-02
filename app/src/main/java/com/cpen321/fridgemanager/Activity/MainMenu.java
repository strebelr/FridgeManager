package com.cpen321.fridgemanager.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.cpen321.fridgemanager.Database.DatabaseInteraction;
import com.cpen321.fridgemanager.Fragment.AddFoodToFoodStock;
import com.cpen321.fridgemanager.Fragment.FoodStock;
import com.cpen321.fridgemanager.Fragment.FoodToExpire;
import com.cpen321.fridgemanager.OcrReader.OcrCaptureActivity;
import com.cpen321.fridgemanager.R;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.message;
import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainMenu extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.ic_food_stock,
            R.drawable.ic_food_to_expire,
            R.drawable.ic_plus
    };

    // Fragments
    private FoodStock foodstock;
    private FoodToExpire foodtoexpire;

    // Settings Value
    private String decrement_percent;

    DatabaseInteraction di; // Database Interaction Initialization

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize global variables and set up screen.
        initialize();

        // Create a new database interaction object
        di = new DatabaseInteraction(getApplicationContext());
        di.setUp();

        // Checks to run first time instruction page
        checkFirstRun();

    }

    /*
      Check if first run. If so, move to instruction page.
     */
    private void checkFirstRun(){
        SharedPreferences settings = getSharedPreferences("prefs",0);
        boolean firstRun = settings.getBoolean("firstRun",false);
        if(firstRun == false)//if running for first time
        {
            SharedPreferences.Editor editor= settings.edit();
            editor.putBoolean("firstRun",true);
            editor.putString("decrement", "0.25");
            editor.putInt("expiryWarning", 3);
            editor.commit();
            Intent i = new Intent(this,Instruction.class);//Activity to be launched For the First time
            startActivity(i);
            finish();
        }
    }

    /*
      Initialize the screen.
     */
    private void initialize() {
        foodstock = new FoodStock();
        foodtoexpire = new FoodToExpire();
        setContentView(R.layout.activity_main_menu);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(0);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub

        MenuInflater menuINF = getMenuInflater();
        menuINF.inflate(R.menu.menu_toolbar, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_daysToExpire:
                    menu_daysToExpiry();
                break;
            case R.id.menu_decrease:
                    menu_decrease();
                break;
            case R.id.menu_help:
                    menu_showInstruction();
        }
        return true;
    }

    @Override
    public void onBackPressed(){

    }

    /*
      Set Tab Icons for each fragments.
     */
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(foodstock, "Stock");
        adapter.addFrag(foodtoexpire, "Alert");
        adapter.addFrag(new AddFoodToFoodStock(), "Add Food");
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
    }

    /*
      Move to OcrCaptureActivity.
      @param view
     */
    public void OcrCaptureActivity(View view){
        Intent intent = new Intent(this, OcrCaptureActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    /*
      Pop undo if there is some to pop.
      @param view
     */
    public void undo(View view) {
        if(di.popUndo()) {
            di.fixStack();
            foodstock.refresh();
            foodtoexpire.refresh();
            Toast toast = Toast.makeText(getApplicationContext(), "Undo Success!", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(), "No more to undo", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /*
      Show decrement percentage settings dialog.
     */
    public void menu_decrease() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(true);
        builder.setTitle(getString(R.string.title_decrement));

        final CharSequence[] items = { "10%", "20%", "25%", "50%" };

        builder.setSingleChoiceItems(items, -1,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch(item)
                        {
                            case 0:
                                decrement_percent = "0.1";
                                break;
                            case 1:
                                decrement_percent = "0.2";
                                break;
                            case 2:
                                decrement_percent = "0.25";
                                break;
                            case 3:
                                decrement_percent = "0.5";
                                break;
                        }
                    }
                });

        builder.setPositiveButton(
                "Confirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        SharedPreferences settings = getSharedPreferences("prefs",0);
                        SharedPreferences.Editor editor= settings.edit();
                        editor.putString("decrement", decrement_percent);
                        editor.commit();
                        refresh();
                    }
                });
        builder.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    /*
      Show days to expiry settings dialog.
     */
    private void menu_daysToExpiry() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(true);
        builder.setTitle(getString(R.string.title_expiry));

        final NumberPicker numberPicker = new NumberPicker(new ContextThemeWrapper(getApplicationContext(), R.style.numpicker_custom));
        numberPicker.setMaxValue(31);
        numberPicker.setMinValue(1);

        builder.setPositiveButton(
                "Confirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        SharedPreferences settings = getSharedPreferences("prefs",0);
                        SharedPreferences.Editor editor= settings.edit();
                        editor.putInt("expiryWarning", Integer.parseInt(String.valueOf(numberPicker.getValue())));
                        editor.commit();
                        refresh();
                    }
                });
        builder.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();


        alert.setView(numberPicker, 75, 30, 75, 10);
        alert.setCanceledOnTouchOutside(false);

        alert.show();
    }

    /*
      Show first time instructions.
     */
    private void menu_showInstruction() {
        Intent i = new Intent(this,Instruction.class);//Activity to be launched For the First time
        startActivity(i);
    }

    /*
      Refreshes the content of food stock and food to expire fragments
     */
    public void refresh() {
        foodstock.refresh();
        foodtoexpire.refresh();
    }

}
