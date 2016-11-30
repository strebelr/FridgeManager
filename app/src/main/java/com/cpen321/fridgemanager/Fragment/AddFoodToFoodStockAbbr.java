package com.cpen321.fridgemanager.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.cpen321.fridgemanager.Algorithm.Levenshtein;
import com.cpen321.fridgemanager.Database.DatabaseInteraction;
import com.cpen321.fridgemanager.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddFoodToFoodStockAbbr extends Fragment {

    public AddFoodToFoodStockAbbr() {

    }

    private View view;
    DatabaseInteraction di;
    JSONArray library;
    AutoCompleteTextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.activity_add_food_enter_abbrv, container, false);


        textView = (AutoCompleteTextView) view.findViewById(R.id.addFoodAbbr);

        di = new DatabaseInteraction(view.getContext());
        library = di.getArray("Library");
        if(library == null) {
            di.importLibrary();
            library = di.getArray("Library");
        }
        List<String> list = new ArrayList<>();
        if(library != null) {
            try {
                for(int i = 0; i < library.length(); i++) {
                    JSONArray jsonArray = library.getJSONObject(i).getJSONArray("abb");
                    for(int ii = 0; ii < jsonArray.length(); ii++) {
                        list.add(jsonArray.get(i).toString());

                    }
                }
            } catch(JSONException e) {

            }
        }

        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.list_item, list);
        textView.setAdapter(adapter);
        textView.setThreshold(2);

        Fragment fragmentFoodForm = new AddFoodToFoodStock();
        FragmentManager fmFoodForm = getFragmentManager();
        FragmentTransaction transaction1 = fmFoodForm.beginTransaction();
        transaction1.replace(R.id.fragment_container_food_form, fragmentFoodForm);
        transaction1.addToBackStack(null);
        transaction1.commit();

        fillFields(view, fragmentFoodForm.getView());


        return view;
    }


    public void fillFields(View viewAbbr, View viewFoodForm) {

        final EditText foodAbbr = (EditText) viewAbbr.findViewById(R.id.addFoodAbbr);
        String abbr = foodAbbr.getText().toString();

        JSONObject foodObject = retrieveObject(abbr);

        if(foodObject != null) {
            try {
                final EditText foodItem = (EditText) viewFoodForm.findViewById(R.id.addFoodName);
                foodItem.setText(foodObject.getString("name").toString());


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }



    public JSONObject retrieveObject(String abbr) {

        assert(library != null);

        try {
            int margin = 1;

            for(int i = 0; i < library.length(); i++) {
                JSONObject jsonObject = library.getJSONObject(i);
                JSONArray jsonArray = jsonObject.getJSONArray("abb");

                for(int ii=0; ii < jsonArray.length(); ii++) {
                    String library_name = jsonArray.getString(ii);
                    if(abbr.length() >= library_name.length()) {
                        int dist = Levenshtein.distance(abbr, library_name);
                        int abs_dist = dist - Math.abs(library_name.length() - abbr.length());
                        if(abs_dist < margin) {
                            return jsonObject;
                        }
                    }
                }
            }
        }catch (JSONException e) {};

        return null;
    }

}
