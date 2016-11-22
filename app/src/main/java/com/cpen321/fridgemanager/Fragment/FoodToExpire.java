package com.cpen321.fridgemanager.Fragment;

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

import com.cpen321.fridgemanager.Activity.MainMenu;
import com.cpen321.fridgemanager.Algorithm.TextRecognitionInteraction;
import com.cpen321.fridgemanager.Database.DatabaseInteraction;
import com.cpen321.fridgemanager.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

import static com.cpen321.fridgemanager.R.id.mTlayout;


public class FoodToExpire extends Fragment{

    private JSONArray fridge;
    private JSONArray fresh;
    private JSONArray pantry;
    private JSONArray freezer;

    DatabaseInteraction di;
    private ArrayList<TableRow> trs = new ArrayList<TableRow>();
    TableLayout mTlayout;
    private JSONArray toExpire;
    TableRow titleToExpire;

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

        mTlayout = (TableLayout) view.findViewById(R.id.TableToExpire);

        di = new DatabaseInteraction(getContext());

        refresh();

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if(getActivity() != null) {
                refresh();
            }
        }else{

        }
    }

    private void clearScreen() {
        for (int i = 0; i < trs.size(); i++) {
            mTlayout.removeView(trs.get(i));
        }
        if(titleToExpire != null) {
            mTlayout.removeView(titleToExpire);
        }
    }


    private void refresh() {

        clearScreen();

        fridge = di.getArray("Fridge");
        fresh = di.getArray("Fresh");
        pantry = di.getArray("Pantry");
        freezer = di.getArray("Freezer");

        titleToExpire = new TableRow(getActivity());


        TextView foodToExpire = new TextView(getActivity());

        try {
            trs.clear();
            toExpire = di.getSortedExpiryArray();
            createTitle(titleToExpire,foodToExpire, R.string.toExpire, toExpire.length());
            createTable();

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createTable() throws ParseException {

        for (int i = 0; i < toExpire.length(); i++) {
            try {
                JSONObject food = toExpire.getJSONObject(i);

                trs.add(new TableRow(getActivity()));

                // Add table row to layout
                if(trs.get(i).getParent() != null)
                    ((ViewGroup)trs.get(i).getParent()).removeView(trs.get(i));
                mTlayout.addView(trs.get(i));
                ImageButton btn_del = new ImageButton(getActivity());
                final TextView food_name = new TextView(getActivity());
                TextView unit_name = new TextView(getActivity());
                TextView amount = new TextView(getActivity());
                TextView expiry = new TextView(getActivity());

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

                // Create exipry date text view
                expiry.setId(i);
                expiry.setText(food.optString("expiry").toString());
                expiry.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
                TableRow.LayoutParams trLayoutParams_expiry = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                expiry.setLayoutParams(trLayoutParams_expiry);
                int padding_right = 50;
                expiry.setPadding(0,0,padding_right,0);
                trs.get(i).addView(expiry);


                // Create amount text view
                amount.setId(i);
                amount.setText(food.optString("quantity").toString());
                amount.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
                TableRow.LayoutParams trLayoutParams_amount = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                amount.setLayoutParams(trLayoutParams_amount);
                trs.get(i).addView(amount);

                // Create unit text view
                unit_name.setId(i);
                unit_name.setGravity(Gravity.CENTER_VERTICAL);
                TableRow.LayoutParams trLayoutParams_unit = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                float measure = unit_name.getPaint().measureText("9999"); // Set width
                unit_name.setWidth(unit_name.getPaddingLeft() + unit_name.getPaddingRight() + (int) measure);
                unit_name.setLayoutParams(trLayoutParams_unit);
                trs.get(i).addView(unit_name);

                // Create delete button
                btn_del.setImageResource(R.drawable.ic_trash);
                btn_del.setId(i);
                // Set on click listener
                btn_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = v.getId();
                        try {
                            di.removeFood((JSONObject) toExpire.get(index), ((JSONObject) toExpire.get(index)).optString("location").toString());
                            refresh();
                        } catch(JSONException e) {}

                    }
                });
                trs.get(i).addView(btn_del);

                // Create food text
                food_name.setText(food.optString("name").toString());
                food_name.setId(i);
                food_name.setGravity(Gravity.CENTER_VERTICAL);
                food_name.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.foodStock_paddingLeft), getResources().getDisplayMetrics()),0,0,0);
                TableRow.LayoutParams trLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                trLayoutParams.weight = 1;
                food_name.setLayoutParams(trLayoutParams);
                trs.get(i).addView(food_name, 0);

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
