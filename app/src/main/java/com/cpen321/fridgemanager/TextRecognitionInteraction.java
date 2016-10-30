package com.cpen321.fridgemanager;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kazuki Fukushima on 2016/10/22.
 */

public class TextRecognitionInteraction {
    DatabaseInteraction di;
    JSONArray library;

    /*
      Default constructor
     */
    public TextRecognitionInteraction(Context context) {
        di = new DatabaseInteraction(context);
        library = di.readLibrary();
        if(library == null) {
            di.importLibrary();
            library = di.readLibrary();
        }
    }

    /*
      Add individual food to storage if it is a valid food.
      @param string to try to add
     */
    public void addFoodToStorage(String name, double quantity, int unit) {
        String real_name;
        int unit_int;
        if (isFood(name)) {
            // TODO FIX THIS
            di.writeToStorage(name, quantity, unit);
        }
        //else {
        //    real_name = isAbb(name);
        //    if (real_name != null) {
        //        di.writeToStorage(real_name);
        //    }
        //}
    }


    /*
      Checks if Food with given name is valid and returns unit of the food.
      @param name of the food
      @returns unit in String if valid
               null otherwise
     */
    public int getUnit(String name) {
        assert(library != null);
        try {
            for (int i = 0; i < library.length(); i++) {
                JSONObject jsonObject = library.getJSONObject(i);
                String library_name = jsonObject.optString("name").toString();
                if (library_name.toLowerCase().equals(name.toLowerCase()))
                    return Integer.parseInt(jsonObject.optString("unit").toString());
            }
        } catch (JSONException e) {}
        return -1;
    }

    /*
      Checks if Food with given name is valid.
      @param name of the food
      @returns true if valid food
               false otherwise
     */
    public boolean isFood(String name) {
        // Iterate the jsonArray and print the info of JSONObjects
        if(library == null) return false;
        try {
            for (int i = 0; i < library.length(); i++) {
                JSONObject jsonObject = library.getJSONObject(i);
                String library_name = jsonObject.optString("name").toString();
                if (library_name.toLowerCase().equals(name.toLowerCase()))
                    return true;
            }
        } catch (JSONException e) {}
        return false;
    }

    /*
      Checks if Food with given name is valid.
      @param name of the food
      @returns true if valid food
               false otherwise
     */
    public String isAbb(String name) {
        // Iterate the jsonArray and print the info of JSONObjects
        if(library == null) return null;
        try {
            for (int i = 0; i < library.length(); i++) {
                JSONObject jsonObject = library.getJSONObject(i);
                String library_abb = jsonObject.optString("abb").toString();
                if (library_abb != null) {
                    if (library_abb.toLowerCase().equals(name.toLowerCase()))
                        return jsonObject.optString("name").toString();;
                }
            }
        } catch (JSONException e) {}
        return null;
    }

    /*
      Gets the current instance of database interaction.
      @returns DatabaseInteraction Object
     */
    public DatabaseInteraction getDatabaseInteraction() {
        return this.di;
    }
}

