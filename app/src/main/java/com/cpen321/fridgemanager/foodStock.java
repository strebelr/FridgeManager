package com.cpen321.fridgemanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
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


public class foodStock extends Fragment{

    // Initialize ArrayLists  that store food data. Index corresponds between these three lists.
    private JSONArray fridge;
    private JSONArray fresh;
    private JSONArray pantry;

    TableLayout mTlayout;
    private ArrayList<TableRow> trsf = new ArrayList<TableRow>();
    private ArrayList<TableRow> trsp = new ArrayList<TableRow>();
    private ArrayList<TableRow> trs = new ArrayList<TableRow>();

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

        fridge = di.getFridgeArray();

        JSONObject food;

        trsf.clear();

        for (int i = 0; i < fridge.length(); i++) {
            try {
                food = fridge.getJSONObject(i);

                trsf.add(new TableRow(getActivity()));

                // Add table row to layout
                if(trsf.get(i).getParent() != null)
                    ((ViewGroup)trsf.get(i).getParent()).removeView(trsf.get(i));
                mTlayout.addView(trsf.get(i));
                ImageButton btn_del = new ImageButton(getActivity());
                TextView food_name = new TextView(getActivity());
                TextView unit_name = new TextView(getActivity());
                TextView amount = new TextView(getActivity());

                // Create unit text
                switch (Integer.parseInt(food.optString("unit").toString())) {
                    // TODO: CHANGE UNIT STRING IF NECESSARY
                    case 0:
                        unit_name.setText("unit");
                        break;
                    case 1:
                        unit_name.setText("grams");
                        break;
                    case 2:
                        unit_name.setText("kgs");
                        break;
                    case 3:
                        unit_name.setText("litres");
                        break;
                    case 4:
                        unit_name.setText("cups");
                        break;
                }

                // Create unit text view
                amount.setId(i);
                amount.setText(food.optString("quantity").toString());
                amount.setGravity(Gravity.CENTER_VERTICAL);
                TableRow.LayoutParams trLayoutParams_amount = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                amount.setLayoutParams(trLayoutParams_amount);
                trsf.get(i).addView(amount);

                // Create unit text view
                unit_name.setId(i);
                unit_name.setGravity(Gravity.CENTER_VERTICAL);
                TableRow.LayoutParams trLayoutParams_unit = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                unit_name.setLayoutParams(trLayoutParams_unit);
                trsf.get(i).addView(unit_name);

                // Create delete button
                btn_del.setImageResource(R.drawable.ic_trash);
                btn_del.setId(i);
                // Set on click listener
                btn_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = v.getId();
                        // TODO: CALL REMOVE
                        // Remove table row from table layout
                        mTlayout.removeView(trsf.get(index));
                        try {
                            di.removeFood(fridge.getJSONObject(index), "Fridge");
                        } catch(JSONException e) {}
                    }
                });
                trsf.get(i).addView(btn_del);

                // Create food text
                food_name.setText(food.optString("name").toString());
                food_name.setId(i);
                food_name.setGravity(Gravity.CENTER_VERTICAL);
                TableRow.LayoutParams trLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                trLayoutParams.weight = 1;
                food_name.setLayoutParams(trLayoutParams);
                trsf.get(i).addView(food_name, 0);
            } catch (JSONException e) {
            }

        }

        fresh = di.getFreshArray();
        trs.clear();

        for (int i = 0; i < fresh.length(); i++) {
            try {
                food = fresh.getJSONObject(i);

                trs.add(new TableRow(getActivity()));

                // Add table row to layout
                if(trs.get(i).getParent() != null)
                    ((ViewGroup)trs.get(i).getParent()).removeView(trs.get(i));
                mTlayout.addView(trs.get(i));
                ImageButton btn_del = new ImageButton(getActivity());
                TextView food_name = new TextView(getActivity());
                TextView unit_name = new TextView(getActivity());
                TextView amount = new TextView(getActivity());

                // Create unit text
                switch (Integer.parseInt(food.optString("unit").toString())) {
                    // TODO: CHANGE UNIT STRING IF NECESSARY
                    case 0:
                        unit_name.setText("unit");
                        break;
                    case 1:
                        unit_name.setText("grams");
                        break;
                    case 2:
                        unit_name.setText("kgs");
                        break;
                    case 3:
                        unit_name.setText("litres");
                        break;
                    case 4:
                        unit_name.setText("cups");
                        break;
                }

                // Create unit text view
                amount.setId(i);
                amount.setText(food.optString("quantity").toString());
                amount.setGravity(Gravity.CENTER_VERTICAL);
                TableRow.LayoutParams trLayoutParams_amount = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                amount.setLayoutParams(trLayoutParams_amount);
                trs.get(i).addView(amount);

                // Create unit text view
                unit_name.setId(i);
                unit_name.setGravity(Gravity.CENTER_VERTICAL);
                TableRow.LayoutParams trLayoutParams_unit = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
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
                        // TODO: CALL REMOVE
                        // Remove table row from table layout
                        mTlayout.removeView(trs.get(index));
                        try {
                            di.removeFood(fresh.getJSONObject(index), "Fresh");
                        } catch(JSONException e) {}
                    }
                });
                trs.get(i).addView(btn_del);

                // Create food text
                food_name.setText(food.optString("name").toString());
                food_name.setId(i);
                food_name.setGravity(Gravity.CENTER_VERTICAL);
                TableRow.LayoutParams trLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                trLayoutParams.weight = 1;
                food_name.setLayoutParams(trLayoutParams);
                trs.get(i).addView(food_name, 0);
            } catch (JSONException e) {
            }

        }

        pantry = di.getPantryArray();
        trsp.clear();

        for (int i = 0; i < pantry.length(); i++) {
            try {
                food = pantry.getJSONObject(i);

                trsp.add(new TableRow(getActivity()));

                // Add table row to layout
                if(trsp.get(i).getParent() != null)
                    ((ViewGroup)trsp.get(i).getParent()).removeView(trs.get(i));
                mTlayout.addView(trsp.get(i));
                ImageButton btn_del = new ImageButton(getActivity());
                TextView food_name = new TextView(getActivity());
                TextView unit_name = new TextView(getActivity());
                TextView amount = new TextView(getActivity());

                // Create unit text
                switch (Integer.parseInt(food.optString("unit").toString())) {
                    // TODO: CHANGE UNIT STRING IF NECESSARY
                    case 0:
                        unit_name.setText("unit");
                        break;
                    case 1:
                        unit_name.setText("grams");
                        break;
                    case 2:
                        unit_name.setText("kgs");
                        break;
                    case 3:
                        unit_name.setText("litres");
                        break;
                    case 4:
                        unit_name.setText("cups");
                        break;
                }

                // Create unit text view
                amount.setId(i);
                amount.setText(food.optString("quantity").toString());
                amount.setGravity(Gravity.CENTER_VERTICAL);
                TableRow.LayoutParams trLayoutParams_amount = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                amount.setLayoutParams(trLayoutParams_amount);
                trsp.get(i).addView(amount);

                // Create unit text view
                unit_name.setId(i);
                unit_name.setGravity(Gravity.CENTER_VERTICAL);
                TableRow.LayoutParams trLayoutParams_unit = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                unit_name.setLayoutParams(trLayoutParams_unit);
                trsp.get(i).addView(unit_name);

                // Create delete button
                btn_del.setImageResource(R.drawable.ic_trash);
                btn_del.setId(i);
                // Set on click listener
                btn_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = v.getId();
                        // TODO: CALL REMOVE
                        // Remove table row from table layout
                        mTlayout.removeView(trsp.get(index));
                        try {
                            di.removeFood(pantry.getJSONObject(index), "Pantry");
                        } catch(JSONException e) {}
                    }
                });
                trs.get(i).addView(btn_del);

                // Create food text
                food_name.setText(food.optString("name").toString());
                food_name.setId(i);
                food_name.setGravity(Gravity.CENTER_VERTICAL);
                TableRow.LayoutParams trLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                trLayoutParams.weight = 1;
                food_name.setLayoutParams(trLayoutParams);
                trsp.get(i).addView(food_name, 0);
            } catch (JSONException e) {
            }

        }
        // Inflate the layout for this fragment
        return view;
    }

}
