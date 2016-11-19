package com.cpen321.fridgemanager.Algorithm;

import android.content.Context;
import android.util.Log;

import com.cpen321.fridgemanager.Database.DatabaseInteraction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Kazuki Fukushima on 2016/10/22.
 * Handles the recognition of food items from the text read by OCRCaptureActivity
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
      Constructor that assigns library to provided JSONArray. Useful for testing.
     */
    public TextRecognitionInteraction(Context context, JSONArray array) {
        di = new DatabaseInteraction(context);
        library = array;
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
        assert(library != null);
        // Iterate the jsonArray and print the info of JSONObjects
        try {
            //maximum allowable difference
            int margin = 1;

            for (int i = 0; i < library.length(); i++) {
                JSONObject jsonObject = library.getJSONObject(i);

                JSONArray jArray = jsonObject.getJSONArray("abb");

                for(int ii=0; ii < jArray.length(); ii++) {
                    String library_name = jArray.getString(ii);
                    Log.d(TAG, "line "+ii+ " is :" + library_name);
                    if (name.length() >= library_name.length()) {
                        //get the distance between the two strings
                        int dist = Levenshtein.distance(name, library_name);
                        //get the absolute distance (accounts for difference in string length
                        int abs_dist = dist - Math.abs (library_name.length() - name.length());
                        //if absolute distance is less than error, assume found and add to database
                        if (abs_dist < margin) {
                            return jsonObject;
                        }
                    }
                }
            }

        } catch (JSONException e) {}
        return null;
    }

    /*
      Checks if Food with given name is valid.
      @param name of the food
      @returns true if valid food
               false otherwise
     */
    public JSONObject isAbb(String abb) {
        assert(library != null);
        // Iterate the jsonArray and print the info of JSONObjects
        if(library == null) return null;
        try {
            //maximum allowable difference
            int margin = 1;

            for (int i = 0; i < library.length(); i++) {
                JSONObject jsonObject = library.getJSONObject(i);
                String library_abb = jsonObject.optString("abb").toString();

                if(abb.length() >= library_abb.length()) {
                    //get the distance between the two strings
                    int dist = Levenshtein.distance(abb, library_abb);
                    //get the absolute distance (accounts for difference in string length
                    int abs_dist = dist - Math.abs(library_abb.length()-abb.length());
                    //if absolute distance is less than error, assume found and add to database
                    if(abs_dist < margin){
                        return jsonObject;
                    }
                }
            }
        } catch (JSONException e) {}
        return null;
    }

}

