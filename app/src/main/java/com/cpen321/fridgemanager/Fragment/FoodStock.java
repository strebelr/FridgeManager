package com.cpen321.fridgemanager.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.cpen321.fridgemanager.Database.DatabaseInteraction;
import com.cpen321.fridgemanager.R;
import com.cpen321.fridgemanager.Algorithm.TextRecognitionInteraction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class FoodStock extends Fragment{

    // Initialize ArrayLists  that store food data. Index corresponds between these three lists.
    private JSONArray fridge;
    private JSONArray fresh;
    private JSONArray pantry;
    private JSONArray freezer;

    TableLayout mTlayout;
    private ArrayList<TableRow> trs = new ArrayList<TableRow>();
    private JSONObject food;
    DatabaseInteraction di;

    // Titles
    TableRow titleFridge;
    TableRow titleFresh;
    TableRow titlePantry;
    TableRow titleFreezer;

    public FoodStock() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_stock, container, false);

        TextRecognitionInteraction ti = new TextRecognitionInteraction(getContext());
        mTlayout = (TableLayout) view.findViewById(R.id.mTlayoutF);

        di = new DatabaseInteraction(getContext());

        titleFridge = new TableRow(getActivity());
        titleFresh = new TableRow(getActivity());
        titlePantry = new TableRow(getActivity());
        titleFreezer = new TableRow(getActivity());
        TextView titleFridgeText = new TextView(getActivity());
        TextView titleFreshText = new TextView(getActivity());
        TextView titlePantryText = new TextView(getActivity());
        TextView titleFreezerText = new TextView(getActivity());

        try {
            trs.clear();
            fridge = di.getArray("Fridge");
            createTitle(titleFridge,titleFridgeText, R.string.foodStock_Fridge, fridge.length());
            createTable(0);
            fresh = di.getArray("Fresh");
            createTitle(titleFresh,titleFreshText, R.string.foodStock_Fresh, fresh.length());
            createTable(1);
            pantry = di.getArray("Pantry");
            createTitle(titlePantry,titlePantryText, R.string.foodStock_Pantry, pantry.length());
            createTable(2);
            freezer = di.getArray("Freezer");
            createTitle(titleFreezer,titleFreezerText, R.string.foodStock_Freezer, freezer.length());
            createTable(3);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Inflate the layout for this fragment
        return view;
    }

    public void createTable(int location) throws ParseException {
        int DaysToExpiryDate = 3;
        Calendar actDate = Calendar.getInstance();
        actDate.set(Calendar.DAY_OF_YEAR, DaysToExpiryDate + actDate.get(Calendar.DAY_OF_YEAR));

        // Selects food location
        JSONArray foodList;
        int index;

        switch(location) {
            case 0:
                foodList = fridge;
                index = 0;
                break;
            case 1:
                foodList = fresh;
                index = fridge.length();
                break;
            case 2:
                foodList = pantry;
                index = fridge.length() + fresh.length();
                break;
            default: // and case 3
                foodList = freezer;
                index = fridge.length() + fresh.length() + pantry.length();
                break;
        }

        for (int i = 0; i < foodList.length(); i++) {
            try {
                food = foodList.getJSONObject(i);

                Calendar expDate = Calendar.getInstance();
                String expiryDate = food.optString("expiry");
                DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                Date date = formatter.parse(expiryDate);
                expDate.setTime(date);
                int time_diff = actDate.compareTo(expDate);

                trs.add(new TableRow(getActivity()));

                // Add table row to layout
                if(trs.get(i + index).getParent() != null)
                    ((ViewGroup)trs.get(i + index).getParent()).removeView(trs.get(i + index));
                mTlayout.addView(trs.get(i + index));
                ImageButton btn_decr = new ImageButton(getActivity());
                ImageButton btn_del = new ImageButton(getActivity());
                final TextView food_name = new TextView(getActivity());
                TextView unit_name = new TextView(getActivity());
                TextView amount = new TextView(getActivity());

                // Create unit text
                switch (Integer.parseInt(food.optString("unit").toString())) {
                    // CHANGE UNIT STRING IF NECESSARY
                    case DatabaseInteraction.UNIT:
                        unit_name.setText("");
                        break;
                    case DatabaseInteraction.GRAM:
                        unit_name.setText(" g");
                        break;
                    case DatabaseInteraction.KG:
                        unit_name.setText(" kg");
                        break;
                    case DatabaseInteraction.L:
                        unit_name.setText(" L");
                        break;
                    case DatabaseInteraction.CUP:
                        unit_name.setText(" cups");
                        break;
                }


                // Create amount text view
                amount.setId(i + index);
                amount.setText(food.optString("quantity").toString());
                amount.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
                TableRow.LayoutParams trLayoutParams_amount = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                amount.setLayoutParams(trLayoutParams_amount);
                trs.get(i + index).addView(amount);

                // Create unit text view
                unit_name.setId(i + index);
                unit_name.setGravity(Gravity.CENTER_VERTICAL);
                TableRow.LayoutParams trLayoutParams_unit = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                float measure = unit_name.getPaint().measureText("9999"); // Set width
                unit_name.setWidth(unit_name.getPaddingLeft() + unit_name.getPaddingRight() + (int) measure);
                unit_name.setLayoutParams(trLayoutParams_unit);
                trs.get(i + index).addView(unit_name);

                // Create decrease button
                btn_decr.setImageResource(R.drawable.ic_minus);
                btn_decr.setId(i + index);
                // Set on click listener
                btn_decr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                trs.get(i + index).addView(btn_decr);

                // Create delete button
                btn_del.setImageResource(R.drawable.ic_trash);
                btn_del.setId(i + index);
                // Set on click listener
                btn_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = v.getId();
                        int new_index;
                        String location;
                        if (index < fridge.length()) {
                            new_index = index;
                            location = "Fridge";
                        }
                        else if (index >= fridge.length() && index < fridge.length() + fresh.length()) {
                            new_index = index - fridge.length();
                            location = "Fresh";
                        }
                        else if (index >= fridge.length() + fresh.length() && index < fridge.length() + fresh.length() + pantry.length()) {
                            new_index = index - fridge.length() - fresh.length();
                            location = "Pantry";
                        }
                        else {
                            new_index = index - fridge.length() - fresh.length() - pantry.length();
                            location = "Freezer";
                        }

                        // Remove table row from table layout
                        mTlayout.removeView(trs.get(index));
                        try {
                            if (location == "Fridge") {
                                di.removeFood(fridge.getJSONObject(new_index), location);
                                if (di.getArray("Fridge").length() == 0) {
                                    mTlayout.removeView(titleFridge);
                                }
                            }
                            else if (location == "Fresh") {
                                di.removeFood(fresh.getJSONObject(new_index), location);
                                if (di.getArray("Fresh").length() == 0) {
                                    mTlayout.removeView(titleFresh);
                                }
                            }
                            else if (location == "Pantry"){
                                di.removeFood(pantry.getJSONObject(new_index), location);
                                if (di.getArray("Pantry").length() == 0) {
                                    mTlayout.removeView(titlePantry);
                                }
                            }
                            else if (location == "Freezer") {
                                di.removeFood(freezer.getJSONObject(new_index), location);
                                if (di.getArray("Freezer").length() == 0) {
                                    mTlayout.removeView(titleFreezer);
                                }
                            }
                        } catch(JSONException e) {}

                    }
                });
                trs.get(i + index).addView(btn_del);

                // Create food text
                food_name.setText(food.optString("name").toString());
                food_name.setId(i + index);
                food_name.setGravity(Gravity.CENTER_VERTICAL);
                food_name.setPadding((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.foodStock_paddingLeft), getResources().getDisplayMetrics()),0,0,0);
                TableRow.LayoutParams trLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                trLayoutParams.weight = 1;
                food_name.setLayoutParams(trLayoutParams);
                if (time_diff >= 0){
                    food_name.setTextColor(ContextCompat.getColor(getContext(),R.color.red));
                }
                trs.get(i + index).addView(food_name, 0);

                /*food_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = v.getId();
                        int new_index;
                        //food_name.setText(food.optString("name").toString());
                        food_name.setText(food.optString("expiry").toString());

                        //if ("Boiling Point K".equals(boilingpointK.getText().toString()))
                        //    boilingpointK.setText("2792");
                        //else if ("2792".equals(boilingpointK.getText().toString()))
                        //    boilingpointK.setText("Boiling Point K");
                    }
                });*/



            } catch (JSONException e) {
            }

        }
    }
    private void createTitle(TableRow title, TextView textTitle, int nameTitle, int length ){
        if (title.getParent() != null)
            ((ViewGroup) title.getParent()).removeView(title);
        if (length > 0) {
            mTlayout.addView(title);
            textTitle.setText(nameTitle);
            textTitle.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            TableRow.LayoutParams trLayoutParams_amount = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            textTitle.setLayoutParams(trLayoutParams_amount);
            textTitle.setTextSize(25);
            title.addView(textTitle);
        }

    }
}
