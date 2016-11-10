package com.cpen321.fridgemanager.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cpen321.fridgemanager.R;


public class FoodToExpire extends Fragment{

    public FoodToExpire() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // set title of the page
        ((MainMenu) getActivity()).getSupportActionBar().setTitle("Expiring");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_food_to_expire, container, false);
    }

}
