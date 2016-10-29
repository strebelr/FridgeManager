package com.cpen321.fridgemanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cpen321.fridgemanager.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class foodStock extends Fragment{

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
        TextView textViewToChange = (TextView)view.findViewById(R.id.foodstock);
        DatabaseInteraction di = new DatabaseInteraction(getContext());
        JSONArray jsonArray = di.getStorageArray();
        if (jsonArray != null) {
            String text = "";
            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String name = jsonObject.optString("name").toString();
                    //String date = jsonObject.optString("date").toString();

                    text = text + name + "\n";
                }
            } catch (JSONException e) {
            }
            textViewToChange.setText(text);
        }

        ((MainMenu) getActivity()).getSupportActionBar().setTitle("Stock");

        // Inflate the layout for this fragment
        return view;

    }

}
