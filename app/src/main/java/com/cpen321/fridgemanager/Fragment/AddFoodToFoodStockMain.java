package com.cpen321.fridgemanager.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.cpen321.fridgemanager.R;

public class AddFoodToFoodStockMain extends Fragment{

    private View view;


    public AddFoodToFoodStockMain() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.activity_add_food_main, container, false);

        return view;
    }

}



