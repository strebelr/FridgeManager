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
    public void addFoodToStorage(String name) {
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

                int levDis = computeLevenshteinDistance(library_name, name);
                if (levDis < 10)    // TODO: decide the range for levDis
                    return true;

                /*
                if (library_name.toLowerCase().equals(name.toLowerCase()))
                    return true;
                */
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


    /*
        Compute the Levenshtein Distance between two character strings to check how similar
        the two strings are.
     */
    public static int computeLevenshteinDistance(String lhs, String rhs) {
        int[][] distance = new int[lhs.length() + 1][rhs.length() + 1];

        for (int i = 0; i <= lhs.length(); i++)
            distance[i][0] = i;
        for (int j = 1; j <= rhs.length(); j++)
            distance[0][j] = j;

        for (int i = 1; i <= lhs.length(); i++)
            for (int j = 1; j <= rhs.length(); j++)
                distance[i][j] = minimum(
                        distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1,
                        distance[i - 1][j - 1] + ((lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1));

        return distance[lhs.length()][rhs.length()];
    }

    private static int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }
}

