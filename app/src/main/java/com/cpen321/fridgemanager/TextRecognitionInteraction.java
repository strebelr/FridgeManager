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
      TODO: NEEDS TO HANDLE CASE WHEN TEXTBLOCK CONTAINS MORE THAN ONE FOOD.
      Adds the food to storage if the name is a valid food.
     */
    public void addFoodToStorage(String name) {
        if (isFood(name)) {
            di.writeToStorage(name);
        }
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
      Gets the current instance of database interaction.
      @returns DatabaseInteraction Object
     */
    public DatabaseInteraction getDatabaseInteraction() {
        return this.di;
    }
}

