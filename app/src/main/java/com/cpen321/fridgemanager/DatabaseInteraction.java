package com.cpen321.fridgemanager;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatabaseInteraction {
    Context context;

    private final String storage = "storage.json";
    private final String library = "library.json";
    private final String defaultlib = "default_library.json";
    private final String log = "log.txt"; // Output from test

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
        String jsonRoot = readFile(0);
        try {
            // If root object exists
            if (jsonRoot == "") {
                // Make a new JSON Root Object
                JSONObject jsonRootObject = new JSONObject();
                // Make a new JSON Array
                JSONArray freshArray = new JSONArray();
                JSONArray pantryArray = new JSONArray();
                JSONArray fridgeArray = new JSONArray();
                // Add the JSON Array to JSON Root Object
                jsonRootObject.put("Fresh", freshArray);
                jsonRootObject.put("Pantry", pantryArray);
                jsonRootObject.put("Fridge", fridgeArray);

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
        String jsonRoot = readFile(0);
        try {
            // If root object exists
            if (jsonRoot != "") {
                // Make a JSON Object from root String
                JSONObject jsonRootObject = new JSONObject(jsonRoot);
                // Get the JSON Array containing "Foods"
                JSONArray jsonArray = jsonRootObject.optJSONArray(location);
                // Make new JSONArray and Root to push back
                JSONObject jsonNewRoot = new JSONObject();
                JSONArray otherArray;
                JSONArray otherArray2;
                if(location.equals("Pantry")) {
                    otherArray = jsonRootObject.optJSONArray("Fresh");
                    otherArray2 = jsonRootObject.optJSONArray("Fridge");
                    jsonNewRoot.put("Fresh", otherArray);
                    jsonNewRoot.put("Fridge", otherArray2);
                }
                else if(location.equals("Fridge")) {
                    otherArray = jsonRootObject.optJSONArray("Pantry");
                    otherArray2 = jsonRootObject.optJSONArray("Fresh");
                    jsonNewRoot.put("Pantry", otherArray);
                    jsonNewRoot.put("Fresh", otherArray2);
                }
                else {
                    otherArray = jsonRootObject.optJSONArray("Pantry");
                    otherArray2 = jsonRootObject.optJSONArray("Fridge");
                    jsonNewRoot.put("Pantry", otherArray);
                    jsonNewRoot.put("Fridge", otherArray2);
                }
                JSONArray newArray = new JSONArray();

                for(int i = 0; i < jsonArray.length(); i++) {
                    // TODO: COMPARE ALL PARAMETERS
                    if(!(food.optString("name").toString().equals(jsonArray.getJSONObject(i).optString("name").toString()))) {
                        newArray.put(jsonArray.get(i));
                    }
                }

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
        String jsonRoot = readFile(0);
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
      Writes a String to File. Use this method for testing File IO.
    */
    public void plainWrite(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(log, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {}
    }

    /*
      Clears the content of Storage.
     */
    public void clear() {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(storage, Context.MODE_PRIVATE));
            outputStreamWriter.write("");
            outputStreamWriter.close();
        } catch (IOException e) {}
    }

    /*
      TODO: THIS METHOD SHOULD CHANGE WITH RESPECT TO UI
      Reads the Storage JSON file and returns it as formatted string
      @return raw JSON formatted string
     */
    public String readStorage() {
        String jsonRoot = readFile(0);

        String data = "";

        try {
            JSONObject  jsonRootObject = new JSONObject(jsonRoot);

            // Get the food array from root object
            JSONArray jsonArray = jsonRootObject.optJSONArray("Foods");

            // Iterate the jsonArray and print the info of JSONObjects
            for(int i=0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String name = jsonObject.optString("name").toString();
                String date = jsonObject.optString("date").toString();

                data += name + "\n" + date + "\n";
            }
        } catch (JSONException e) {}

        return data;
    }

    /*
      Reads the Library JSON file and returns an JSONArray
      @return raw JSON formatted string
     */
    public JSONArray readLibrary() {
        String jsonRoot = readFile(1);

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
      Reads the Library JSON file and returns an JSONArray
      @return raw JSON formatted string
     */
    public JSONArray getFreshArray() {
        String jsonRoot = readFile(0);

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
      Reads the Library JSON file and returns an JSONArray
      @return raw JSON formatted string
     */
    public JSONArray getPantryArray() {
        String jsonRoot = readFile(0);

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
      Reads the Library JSON file and returns an JSONArray
      @return raw JSON formatted string
     */
    public JSONArray getFridgeArray() {
        String jsonRoot = readFile(0);

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
      Reads the JSON file and returns it as string
      @param destination to read
             0 for storage
             1 for library
      @return raw JSON formatted string
    */
    private String readFile(int destination) {
        String dest;
        boolean local = true; // Checks if we are reading local app storage or assets folder
        if (destination == 0)
            dest = storage;
        else if (destination == 1)
            dest = library;
        else if (destination == 2) {
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
                inputStream = context.getResources().getAssets().open(defaultlib);

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
        String library = readFile(2);
        String jsonRoot = readFile(1);
        try {
            // Make a new JSON Root Object
            JSONObject jsonRootObject = new JSONObject();
            // JSON Root Object of Library
            JSONObject libraryroot = new JSONObject(library);
            // Get the JSON Array containing "Foods"
            JSONArray jsonArray = libraryroot.optJSONArray("Foods");
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