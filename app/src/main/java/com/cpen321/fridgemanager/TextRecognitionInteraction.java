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
      Add foods to storage. The method will split the string if it is a sentence containing multiple food items.
      @param string to try to add
     */
    public void addFoodToStorage(String name) {
        String[] words = name.replaceAll("[^a-zA-Z ]",
                "").split("\\s+");
        for (int i = 0; i < words.length; i++) {
            // You may want to check for a non-word character before blindly
            // performing a replacement
            // It may also be necessary to adjust the character class
            words[i] = words[i].replaceAll("[^\\w]", "");
            addIndividualFoods(words[i]);
        }
    }

    /*
      Add individual food to storage if it is a valid
food.
      @param string must not be a sentence containing
multiple food items
     */
    public void addIndividualFoods(String name) {
        String real_name;
        if (isFood(name)) {
            di.writeToStorage(name);
        }
        //else {
        //    real_name = isAbb(name);
        //    if (real_name != null) {
        //        di.writeToStorage(real_name);
        //    }
        //}
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

