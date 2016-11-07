package com.cpen321.fridgemanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.cpen321.fridgemanager.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static java.util.logging.Logger.global;


public class foodStock extends Fragment{

    // Initialize ArrayLists  that store food data. Index corresponds between these three lists.
    private JSONArray fridge;
    private JSONArray fresh;
    private JSONArray pantry;

    TableLayout mTlayout;
    private ArrayList<TableRow> trs = new ArrayList<TableRow>();
    private JSONObject food;
    DatabaseInteraction di;

    public foodStock() {
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

        TableRow titleFridge = new TableRow(getActivity());
        TextView titleFridgeText = new TextView(getActivity());
        TableRow titleFresh = new TableRow(getActivity());
        TextView titleFreshText = new TextView(getActivity());
        TableRow titlePantry = new TableRow(getActivity());
        TextView titlePantryText = new TextView(getActivity());

        trs.clear();
        fridge = di.getFridgeArray();
        createTitle(titleFridge,titleFridgeText, R.string.foodStock_Fridge, fridge.length());
        createTable(0);
        fresh = di.getFreshArray();
        createTitle(titleFresh,titleFreshText, R.string.foodStock_Fresh, fresh.length());
        createTable(1);
        pantry = di.getPantryArray();
        createTitle(titlePantry,titlePantryText, R.string.foodStock_Pantry, pantry.length());
        createTable(2);
        // Inflate the layout for this fragment
        return view;
    }

    public void createTable(int location){

        // Selects food location
        JSONArray foodList;
        int index;
        if (location == 0 ) {
            foodList = fridge;
            index = 0;
        }
        else if(location == 1) {
            foodList = fresh;
            index = fridge.length();
        }
        else {
            foodList = pantry;
            index = fridge.length() + fresh.length();
        }

        for (int i = 0; i < foodList.length(); i++) {
            try {
                food = foodList.getJSONObject(i);

                trs.add(new TableRow(getActivity()));

                // Add table row to layout
                if(trs.get(i + index).getParent() != null)
                    ((ViewGroup)trs.get(i + index).getParent()).removeView(trs.get(i + index));
                mTlayout.addView(trs.get(i + index));
                ImageButton btn_del = new ImageButton(getActivity());
                TextView food_name = new TextView(getActivity());
                TextView unit_name = new TextView(getActivity());
                TextView amount = new TextView(getActivity());

                // Create unit text
                switch (Integer.parseInt(food.optString("unit").toString())) {
                    // TODO: CHANGE UNIT STRING IF NECESSARY
                    case 0:
                        unit_name.setText("");
                        break;
                    case 1:
                        unit_name.setText(" g");
                        break;
                    case 2:
                        unit_name.setText(" kg");
                        break;
                    case 3:
                        unit_name.setText(" l");
                        break;
                    case 4:
                        unit_name.setText(" cups");
                        break;
                }

                // Create unit text view
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
                        else {
                            new_index = index - fridge.length() - fresh.length();
                            location = "Pantry";
                        }

                        // Remove table row from table layout
                        mTlayout.removeView(trs.get(index));
                        try {
                            if (location == "Fridge")
                                di.removeFood(fridge.getJSONObject(new_index), location);
                            else if (location == "Fresh")
                                di.removeFood(fresh.getJSONObject(new_index), location);
                            else
                                di.removeFood(pantry.getJSONObject(new_index), location);
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
                trs.get(i + index).addView(food_name, 0);
            } catch (JSONException e) {
            }

        }
    }
    private void createTitle(TableRow title, TextView textTitle, int nameTitle, int length ){
        if (length > 0) {
            if (title.getParent() != null)
                ((ViewGroup) title.getParent()).removeView(title);
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
