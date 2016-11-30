package com.cpen321.fridgemanager.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

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

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch(checkedId) {
                    case R.id.radioOption1:
                        // switch to fragment 1
                        Fragment fragmentFoodAbbr = new AddFoodToFoodStockAbbr();
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction transaction = fm.beginTransaction();
                        transaction.replace(R.id.fragment_container, fragmentFoodAbbr);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;

                    case R.id.radioOption2:
                        // Fragment 2
                        Fragment fragmentFoodForm = new AddFoodToFoodStock();
                        FragmentManager fmFoodForm = getFragmentManager();
                        FragmentTransaction transaction1 = fmFoodForm.beginTransaction();
                        transaction1.replace(R.id.fragment_container, fragmentFoodForm);
                        transaction1.addToBackStack(null);
                        transaction1.commit();
                        break;
                }
            }
        });


        return view;
    }

}



