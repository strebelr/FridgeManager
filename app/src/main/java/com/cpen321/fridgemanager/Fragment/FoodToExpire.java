package com.cpen321.fridgemanager.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.cpen321.fridgemanager.Activity.MainMenu;
import com.cpen321.fridgemanager.Algorithm.TextRecognitionInteraction;
import com.cpen321.fridgemanager.Database.DatabaseInteraction;
import com.cpen321.fridgemanager.R;

import java.util.ArrayList;

import static com.cpen321.fridgemanager.R.id.mTlayout;


public class FoodToExpire extends Fragment{

    DatabaseInteraction di;
    private ArrayList<TableRow> trs = new ArrayList<TableRow>();

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

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_food_to_expire, container, false);

        //mTlayout = (TableLayout) view.findViewById(R.id.TableToExpire);

        di = new DatabaseInteraction(getContext());


        return view;
    }

}
