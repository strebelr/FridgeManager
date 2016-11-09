package com.cpen321.fridgemanager.Database;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class DatabaseInteraction {
    Context context;

    // Static list that contains the list of food locations. Modify this only to add more locations.
    private final static List<String> LOCATIONS_LIST = Arrays.asList("Fridge", "Fresh", "Pantry", "Freezer");

    // Defined variables to select file read locations
    private final static int STORAGE_DEST = 0;
    private final static int LIBRARY_DEST = 1;
    private final static int ASSETS_DEST = 2;

    // Defined variables to select unit encoded in integers
    public final static int UNIT = 0;
    public final static int GRAM = 1;
    public final static int KG = 2;
    public final static int L = 3;
    public final static int CUP = 4;

    // Defined file names for File IO
    private final static String storage = "storage.json";
    private final static String library = "library.json";
    private final static String default_lib = "default_library.json"; // Default library in assets
    private final static String log = "log.txt"; // Output from test

    /*
      Default constructor
     */
    public DatabaseInteraction(Context context) {
        this.context = context;
    }

    /*
      Sets up the storage json. Runs only once after installation.
     */
    public void setUp() {
        // Get the root JSON String from File
        String jsonRoot = readFile(STORAGE_DEST);
        try {
            // If root object exists
            if (jsonRoot == "") {
                // Make a new JSON Root Object
                JSONObject jsonRootObject = new JSONObject();

                // Put all arrays into object
                for(int i = 0; i < LOCATIONS_LIST.size(); i++) {
                    jsonRootObject.put(LOCATIONS_LIST.get(i), new JSONArray());
                }

                // Output the new JSON Root Object to File
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(storage, Context.MODE_PRIVATE));
                outputStreamWriter.write(jsonRootObject.toString());
                outputStreamWriter.close();
            }
        } catch (IOException e) {} catch (JSONException e) {}
    }

    /*
      Deletes a JSONObject from JSONArray.
      @param JSONObject to remove
      @param location/JSONArray from which object is removed
     */
    public void removeFood(JSONObject food, String location) {
        // Get the root JSON String from File
        String jsonRoot = readFile(STORAGE_DEST);
        try {
            // If root object exists
            if (jsonRoot != "") {
                // Make a JSON Object from root String
                JSONObject jsonRootObject = new JSONObject(jsonRoot);
                // Get the JSON Array containing "Foods"
                JSONArray jsonArray = jsonRootObject.optJSONArray(location);
                // Make new JSONArray and Root to push back
                JSONObject jsonNewRoot = new JSONObject();

                // Put unmodified arrays into new root
                for(int i = 0; i < LOCATIONS_LIST.size(); i++) {
                    if (location != LOCATIONS_LIST.get(i)) {
                        jsonNewRoot.put(LOCATIONS_LIST.get(i), jsonRootObject.optJSONArray(LOCATIONS_LIST.get(i)));
                    }
                }
                // Make new array to store the array without the deleted element
                JSONArray newArray = new JSONArray();

                boolean remove = true; // Keeps track of if item has been removed already
                for(int i = 0; i < jsonArray.length(); i++) {
                    if(!(food.optString("name").toString().equals(jsonArray.getJSONObject(i).optString("name").toString())) ||
                            !(food.optString("bought").toString().equals(jsonArray.getJSONObject(i).optString("bought").toString())) ||
                            !(food.optString("quantity").toString().equals(jsonArray.getJSONObject(i).optString("quantity").toString())) ||
                            !remove) { // If item doesn't match
                        newArray.put(jsonArray.get(i)); // Add item to new array
                    }
                    else { // Don't add. I.e: remove
                        remove = false;
                    }
                }

                // Put the new array
                jsonNewRoot.put(location, newArray);

                // Output the new JSON Root Object to File
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(storage, Context.MODE_PRIVATE));
                outputStreamWriter.write(jsonNewRoot.toString());
                outputStreamWriter.close();
            }

        } catch (IOException e) {} catch (JSONException e) {}
    }

    /*
      Appends a new JSONObject with String data to the JSON file.
      @param data The data to initialize the JSONObject with
      @param quantity
            @param unit 0 for unit
                  1 for gram
                  2 for kg
                  3 for L
                  4 for cup
     */
    public void writeToStorage(String data, double quantity, int unit, String location, int expiry) {
        // Create a new JSON Object
        JSONObject element = new JSONObject();
        String date = getCurrentDate();
        String expiry_date = getFutureDate(expiry);
        try {
            element.put("name", data);
            element.put("bought", date);
            element.put("expiry", expiry_date);
            element.put("quantity", quantity);
            element.put("unit", unit);
        } catch (JSONException e) {}

        // Get the root JSON String from File
        String jsonRoot = readFile(STORAGE_DEST);
        try {
            JSONObject jsonRootObject;
            JSONArray jsonArray;
            // If root object exists
            if (jsonRoot != "") {
                // Make a JSON Object from root String
                jsonRootObject = new JSONObject(jsonRoot);
                // Get the JSON Array containing "Foods"
                jsonArray = jsonRootObject.optJSONArray(location);
                if (jsonArray != null) {
                    // Append the new element
                    jsonArray.put(element);
                }
                else {
                    jsonArray = new JSONArray();
                    // Put the new element into the array
                    jsonArray.put(element);
                    // Add the JSON Array to JSON Root Object
                    jsonRootObject.put(location, jsonArray);
                }
            }
            // If root does not exist
            else {
                // Make a new JSON Root Object
                jsonRootObject = new JSONObject();
                // Make a new JSON Array
                jsonArray = new JSONArray();
                // Put the new element into the array
                jsonArray.put(element);
                // Add the JSON Array to JSON Root Object
                jsonRootObject.put(location, jsonArray);
            }

            // Output the new JSON Root Object to File
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(storage, Context.MODE_PRIVATE));
            outputStreamWriter.write(jsonRootObject.toString());
            outputStreamWriter.close();
        } catch (IOException e) {} catch (JSONException e) {}
    }

    /*
      Writes a String to Log. Use this method for testing File IO.
    */
    public void plainWrite(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(log, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {}
    }

    /*
      Reads the Library JSON file and returns an JSONArray
      @return raw JSON formatted string
     */
    public JSONArray readLibrary() {
        String jsonRoot = readFile(LIBRARY_DEST);

        String data = "";

        try {
            JSONObject  jsonRootObject = new JSONObject(jsonRoot);

            // Get the food array from root object
            JSONArray jsonArray = jsonRootObject.optJSONArray("Foods");

            return jsonArray;
        } catch (JSONException e) {}

        return null;
    }

    /*
      Reads the Storage JSON file and returns the Fresh JSONArray
      @return raw JSON formatted string
     */
    public JSONArray getFreshArray() {
        String jsonRoot = readFile(STORAGE_DEST);

        String data = "";

        try {
            JSONObject  jsonRootObject = new JSONObject(jsonRoot);

            // Get the food array from root object
            JSONArray jsonArray = jsonRootObject.optJSONArray("Fresh");

            return jsonArray;
        } catch (JSONException e) {}

        return null;
    }

    /*
      Reads the Storage JSON file and returns the Pantry JSONArray
      @return raw JSON formatted string
     */
    public JSONArray getPantryArray() {
        String jsonRoot = readFile(STORAGE_DEST);

        String data = "";

        try {
            JSONObject  jsonRootObject = new JSONObject(jsonRoot);

            // Get the food array from root object
            JSONArray jsonArray = jsonRootObject.optJSONArray("Pantry");

            return jsonArray;
        } catch (JSONException e) {}

        return null;
    }

    /*
      Reads the Storage JSON file and returns the Fridge JSONArray
      @return raw JSON formatted string
     */
    public JSONArray getFridgeArray() {
        String jsonRoot = readFile(STORAGE_DEST);

        String data = "";

        try {
            JSONObject  jsonRootObject = new JSONObject(jsonRoot);

            // Get the food array from root object
            JSONArray jsonArray = jsonRootObject.optJSONArray("Fridge");

            return jsonArray;
        } catch (JSONException e) {}

        return null;
    }

    /*
      Reads the Storage JSON file and returns the Freezer JSONArray
      @return raw JSON formatted string
     */
    public JSONArray getFreezereArray() {
        String jsonRoot = readFile(STORAGE_DEST);

        String data = "";

        try {
            JSONObject  jsonRootObject = new JSONObject(jsonRoot);

            // Get the food array from root object
            JSONArray jsonArray = jsonRootObject.optJSONArray("Freezer");

            return jsonArray;
        } catch (JSONException e) {}

        return null;
    }

    /*
      Reads the JSON file and returns it as string
      @param destination to read
             0 for storage
             1 for library
      @return raw JSON formatted string
    */
    private String readFile(int destination) {
        String dest;
        boolean local = true; // Checks if we are reading local app storage or assets folder
        if (destination == STORAGE_DEST)
            dest = storage;
        else if (destination == LIBRARY_DEST)
            dest = library;
        else if (destination == ASSETS_DEST) {
            local = false;
            dest = null;
        }
        else
            dest = null;

        String root = "";
        try {
            InputStream inputStream;
            if (local)
                inputStream = context.openFileInput(dest);
            else
                inputStream = context.getResources().getAssets().open(default_lib);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                root = stringBuilder.toString();
            }
        } catch (IOException e) {}
        return root;
    }

    /*
      Imports a default library to working library. Must only be called once.
     */
    public void importLibrary() {
        String library = readFile(ASSETS_DEST);
        try {
            // Make a new JSON Root Object
            JSONObject jsonRootObject = new JSONObject();
            // JSON Root Object of Library
            JSONObject library_root = new JSONObject(library);
            // Get the JSON Array containing "Foods"
            JSONArray jsonArray = library_root.optJSONArray("Foods");
            // Add the JSON Array to JSON Root Object
            jsonRootObject.put("Foods", jsonArray);

            // Output the new JSON Root Object to File
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(this.library, Context.MODE_PRIVATE));
            outputStreamWriter.write(jsonRootObject.toString());
            outputStreamWriter.close();
        } catch (IOException e) {} catch (JSONException e) {}
    }

    /*
      Gets the current date in DD-MM-YYYY format.
      @return current date in String
    */
    private String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    /*
      Gets the date that is x days away from today.
      @param days away from today
      @return date in String
     */
    private String getFutureDate(int days) {
        Calendar c = new GregorianCalendar();
        c.add(Calendar.DATE, days);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(c.getTime());
    }


}