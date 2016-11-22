package com.cpen321.fridgemanager.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.cpen321.fridgemanager.Database.DatabaseInteraction;
import com.cpen321.fridgemanager.Fragment.Expenditures;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        new DatabaseInteraction(getApplicationContext()).setUp();
    }

    @Override
    public void onBackPressed(){

    }

    private void setupTabIcons() {
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
    }

    public void OcrCaptureActivity(View view){
        Intent intent = new Intent(this, OcrCaptureActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
    

}
