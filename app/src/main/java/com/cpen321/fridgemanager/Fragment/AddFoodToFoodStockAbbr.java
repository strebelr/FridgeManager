package com.cpen321.fridgemanager.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cpen321.fridgemanager.R;

public class AddFoodToFoodStockAbbr extends Fragment {

    public AddFoodToFoodStockAbbr() {

    }

    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.activity_add_food_enter_abbrv, container, false);

        return view;
    }



}
