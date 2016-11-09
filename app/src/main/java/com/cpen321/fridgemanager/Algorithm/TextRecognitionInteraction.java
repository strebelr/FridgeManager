package com.cpen321.fridgemanager.Algorithm;

import android.content.Context;
import android.util.Log;

import com.cpen321.fridgemanager.Database.DatabaseInteraction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kazuki Fukushima on 2016/10/22.
 */

public class TextRecognitionInteraction {

    private static final String TAG = "TRI";

    DatabaseInteraction di;
    JSONArray library;

    /*
      Default constructor
     */
    public TextRecognitionInteraction(Context context) {
        di = new DatabaseInteraction(context);
        library = di.getArray("Library");
        if(library == null) {
            di.importLibrary();
            library = di.getArray("Library");
        }
    }

    /*
      Add individual food to storage if it is a valid food.
      @param string to try to add
     */
    public void addFoodToStorage(String name, double quantity, int unit, String location, int expiry) {
        di.writeToStorage(name, quantity, unit, location, expiry);
    }

    /*
      Checks if Food with given name is valid.
      @param name of the food
      @returns jsonobject for food is valid
               null otherwise
     */
    public JSONObject isFood(String name) {
        // Iterate the jsonArray and print the info of JSONObjects
        assert(library != null);

        Log.d(TAG, "name is" + name);

        try {
            //maximum allowable difference
            int margin = 1;

            for (int i = 0; i < library.length(); i++) {
                JSONObject jsonObject = library.getJSONObject(i);
                String library_name = jsonObject.optString("name").toString();

                if(name.length() >= library_name.length()) {
                    //get the distance between the two strings
                    int dist = Levenshtein.distance(name, library_name);
                    //get the absolute distance (accounts for difference in string length
                    int abs_dist = dist - Math.abs(library_name.length()-name.length());
                    //if absolute distance is less than error, assume found and add to database
                    if(abs_dist < margin){
                        return jsonObject;
                    }
                }
            }

        } catch (JSONException e) {}
        return null;
    }

//    /*
//      Checks if Food with given name is valid.
//      @param name of the food
//      @returns true if valid food
//               false otherwise
//     */
//    public String isAbb(String name) {
//        // Iterate the jsonArray and print the info of JSONObjects
//        if(library == null) return null;
//        try {
//            for (int i = 0; i < library.length(); i++) {
//                JSONObject jsonObject = library.getJSONObject(i);
//                String library_abb = jsonObject.optString("abb").toString();
//                if (library_abb != null) {
//                    if (library_abb.toLowerCase().equals(name.toLowerCase()))
//                        return jsonObject.optString("name").toString();;
//                }
//            }
//        } catch (JSONException e) {}
//        return null;
//    }

}

