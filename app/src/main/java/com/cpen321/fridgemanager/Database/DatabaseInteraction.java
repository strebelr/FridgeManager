package com.cpen321.fridgemanager.Database;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class DatabaseInteraction {
    Context context;

    // Static list that contains the list of food locations. Modify this only to add more locations.
    public final static List<String> LOCATIONS_LIST = Arrays.asList("Fridge", "Fresh", "Pantry", "Freezer");

    // Defined variables to select file read locations
    private final static int STORAGE_DEST = 0;
    private final static int LIBRARY_DEST = 1;
    private final static int ASSETS_DEST = 2;
    private final static int UNDO_DEST = 3;

    // Defined variables to select unit encoded in integers
    public final static int UNIT = 0;
    public final static int GRAM = 1;
    public final static int KG = 2;
    public final static int L = 3;
    public final static int CUP = 4;

    // Define default expiry date
    public final static int DEFAULT_EXPIRY = 7;

    // Remove or Decrement Select
    private final static String DEC_SEL = "dec";
    private final static String REM_SEL = "rem";

    // Defined file names for File IO
    private final static String storage = "storage.json";
    private final static String library = "library.json";
    private final static String undo_stack = "undo_stack.json";
    private final static String default_lib = "default_library.json"; // Default library in assets

    private SharedPreferences settings;

    /*
      Default constructor
     */
    public DatabaseInteraction(Context context) {
        this.context = context;
        this.settings = context.getSharedPreferences("prefs",0);
    }

    /*
      Sets up the storage json. Runs only once after installation.
     */
    public void setUp() {
        try { // Output the new JSON Root Object to File
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(storage, Context.MODE_PRIVATE));
            write(outputStreamWriter, makeRoot().toString());
        } catch (FileNotFoundException e) {}
    }

    /*
      Makes a new json root object with default location parameters.
      @returns default root object
     */
    private JSONObject makeRoot() {
        JSONObject root = new JSONObject();
        try {
            // Put all arrays into object
            for (int i = 0; i < LOCATIONS_LIST.size(); i++) {
                root.put(LOCATIONS_LIST.get(i), new JSONArray());
            }
        } catch (JSONException e) {}

        return root;
    }

    /*
      Add to Undo Stack.
      @param object to add to stack
      @param select whether it was added by remove or decrement
      @param location of where the object was
     */
    public void addStack(JSONObject food, String sel, String location) {
        String root = readFile(UNDO_DEST);
        try {  // Output the new JSON Root Object to File
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(undo_stack, Context.MODE_PRIVATE));
            write(outputStreamWriter, stack(root, food, sel, location).toString());
        } catch (FileNotFoundException e) {}
    }

    /*
      Update Undo Stack JSON.
     */
    private JSONObject stack(String root, JSONObject food, String sel, String location) {
        JSONObject rootObject;
        JSONObject writeObject = null;
        if (root != "") { // if stack does exist
            try {
                rootObject = new JSONObject(root);
                writeObject = new JSONObject();
                int size = Integer.parseInt(rootObject.optString("size").toString());
                int new_size;
                if (size < 9) new_size = size + 1;
                else new_size = 10;
                writeObject.put("size", new_size);
                writeObject.put("0", food);
                writeObject.put("0s", sel);
                writeObject.put("0sl", location);
                String attribute;
                String attribute_stack;
                int j;
                for (int i = 1; i < new_size; i++) {
                    j = i - 1;
                    attribute = "" + i;
                    attribute_stack = "" + j;
                    writeObject.put(attribute, new JSONObject(rootObject.optString(attribute_stack).toString()));
                    attribute = attribute + "s";
                    attribute_stack = attribute_stack + "s";
                    writeObject.put(attribute, rootObject.optString(attribute_stack).toString());
                    attribute = attribute + "l";
                    attribute_stack = attribute_stack + "l";
                    writeObject.put(attribute, rootObject.optString(attribute_stack).toString());
                }
            } catch (JSONException e) {}
        }
        else { // if stack does not exist
            writeObject = new JSONObject();
            try {
                writeObject.put("size", 1);
                writeObject.put("0", food);
                writeObject.put("0s", sel);
                writeObject.put("0sl", location);
            } catch (JSONException e) {}
        }
        return writeObject;
    }

    /*
      Decrement the index of stack to pop.
     */
    public void fixStack() {
        String root = readFile(UNDO_DEST);
        try{
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(undo_stack, Context.MODE_PRIVATE));
            if(decStack(root) != null)
                write(outputStreamWriter, decStack(root).toString());
        } catch (FileNotFoundException e) {}
    }

    private JSONObject decStack(String root) {
        try {
            JSONObject rootObject = new JSONObject(root);
            JSONObject writeObject = new JSONObject();
            int size = Integer.parseInt(rootObject.optString("size").toString()) - 1;
            writeObject.put("size", size);
            int j;
            String attribute;
            String attribute_stack;
            for (int i = 0; i < size; i++) {
                j = i + 1;
                attribute = "" + i;
                attribute_stack = "" + j;
                writeObject.put(attribute, new JSONObject(rootObject.optString(attribute_stack).toString()));
                attribute = attribute + "s";
                attribute_stack = attribute_stack + "s";
                writeObject.put(attribute, rootObject.optString(attribute_stack).toString());
                attribute = attribute + "l";
                attribute_stack = attribute_stack + "l";
                writeObject.put(attribute, rootObject.optString(attribute_stack).toString());
            }
            return writeObject;
        } catch (JSONException e) {
        }
        return null;
    }

    /*
      Undo Last Delete.
      @returns false if undo fails
               true if succeeds
     */
    public boolean popUndo() {
        String rootU = readFile(UNDO_DEST);
        String rootS = readFile(STORAGE_DEST);
        if (rootU == "" || rootS == "") return false;
        try {  // Output the new JSON Root Object to File
            if (undo(rootU, rootS) != null  && !(new JSONObject(rootU).optString("size").equals("0"))) {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(storage, Context.MODE_PRIVATE));
                write(outputStreamWriter, undo(rootU, rootS).toString());
                return true;
            }
        } catch (FileNotFoundException | JSONException e) {}

        return false;
    }

    /*
      Undo previous delete. Pop from JSON Stack and return root.
     */
    private JSONObject undo(String rootUndo, String rootStorage) {
        try {
            JSONObject stack = new JSONObject(rootUndo);
            String location = stack.optString("0sl").toString();
            JSONObject toAdd = new JSONObject(stack.optString("0").toString());
            if(stack.optString("0s").toString().equals(REM_SEL)) {
                return new JSONObject(add_to_root(toAdd, rootStorage, location));
            }
            else {
                JSONObject jsonRootObject = new JSONObject(rootStorage);
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
                    if(!(toAdd.optString("name").toString().equals(jsonArray.getJSONObject(i).optString("name").toString())) ||
                            !(toAdd.optString("unit").toString().equals(jsonArray.getJSONObject(i).optString("unit").toString())) ||
                            !(toAdd.optString("expiry").toString().equals(jsonArray.getJSONObject(i).optString("expiry").toString())) ||
                            !remove) { // If item doesn't match
                            newArray.put(jsonArray.get(i)); // Add item to new array
                    }
                    else { // Don't add. I.e: remove
                        remove = false;
                    }
                }
                newArray.put(toAdd);
                // Put the new array
                jsonNewRoot.put(location, newArray);
                return jsonNewRoot;
            }
        } catch (JSONException e) {}

        return null;
    }

    /*
      Add new food or abbreviation to the library.
      @param name of food
      @param abbreviation of food
      @param expected expiry for food
      @param unit of the food (refer to defined selects)
      @param location of food
     */
    public void addToLibrary(String name, String abb, int expiry, int unit, String location) {
        String root = readFile(LIBRARY_DEST);
        if (root == "") return;
        try {  // Output the new JSON Root Object to File
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(library, Context.MODE_PRIVATE));
            write(outputStreamWriter, addDefinition(root, name, abb, expiry, unit, location).toString());
        } catch (FileNotFoundException e) {}
    }

    /*
      Add new word to library.
     */
    private JSONObject addDefinition(String root, String name, String abbr, int expiry, int unit, String location) {
        try {
            JSONObject library = new JSONObject(root);
            JSONArray food_info = library.optJSONArray("Foods");
            JSONObject food;
            for (int i = 0; i < food_info.length(); i++ ) {
                food = food_info.getJSONObject(i);

                JSONArray abbr_list = food.optJSONArray("abb");
                boolean abbr_exists = false;
                for (int j = 0; j < abbr_list.length(); j++) {
                    if ((abbr_list.getString(j).toLowerCase()).equals(abbr.toLowerCase())) {
                        abbr_exists = true;
                        break;
                    }
                }

                if ((food.optString("name").toLowerCase()).equals(name.toLowerCase())
                        && Integer.parseInt(food.optString("expiry")) == expiry
                        && Integer.parseInt(food.optString("unit")) == unit
                        && food.optString("location").equals(location)) {

                    // Case 1: If food type exists in database

                    if (abbr_exists) {
                        // Case 1.1: Do nothing
                        return library;
                    }
                    else {
                        // Case 1.2: Add Abbreviation
                        abbr_list.put(abbr);
                        return library;
                    }
                }
                else if ((food.optString("name").toLowerCase()).equals(name.toLowerCase())) {
                    // Case 2: If food type exists but information differ, overwrite
                    if (!abbr_exists) {
                        abbr_list.put(abbr);
                    }
                    food.put("expiry", expiry);
                    food.put("unit", unit);
                    food.put("location", location);
                    return library;
                }
            }

            // Case 3: Food does not exist
            JSONObject newFood = new JSONObject();
            JSONArray newAbbr = new JSONArray();
            newFood.put("name", name);
            if(!abbr.equals(""))
                newAbbr.put(abbr);
            newAbbr.put(name);
            newAbbr.put(name + "s");
            newFood.put("abb", newAbbr);
            newFood.put("expiry", expiry);
            newFood.put("unit", unit);
            newFood.put("location", location);
            food_info.put(newFood);

            return library;

        } catch (JSONException e) {}
        return null;
    }




    /*
      Decrement food. If given in plain units, decrement by 1. Else, decrement by 25% of original amount.
      @returns 0 if decrements
               1 if removes
               -1 otherwise
     */
    public int decrementFood(JSONObject food, String location) {
        String root = readFile(STORAGE_DEST);

        if (root == "") return -1;
        try {  // Output the new JSON Root Object to File
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(storage, Context.MODE_PRIVATE));
            if(checkRemove(food)) {
                write(outputStreamWriter, remove(root, location, food).toString());
                return 1;
            }
            else {
                write(outputStreamWriter, decrement(root, location, food).toString());
                return 0;
            }
        } catch (FileNotFoundException e) { return -1;}
    }

    /*
      Check if decrement == remove.
      @returns true if a decrement removes the food
               false if a decrement still keeps food
     */
    private boolean checkRemove(JSONObject food) {
        if (Integer.parseInt(food.optString("unit").toString()) == UNIT ) {
            return Double.parseDouble(food.optString("quantity").toString()) == 1;
        }
        else {
            return Double.parseDouble(food.optString("quantity").toString()) <= new BigDecimal(settings.getString("decrement","0.25")).multiply(new BigDecimal(food.optString("original_qty").toString())).doubleValue();
        }
    }

    /*
      @param root object in string
      @param array to specific
      @param object to decrement from
      @return json object with element decremented
     */
    private JSONObject decrement(String root, String array, JSONObject element) {
        try {
            // If root object exists
            if (root != "") {
                // Make a JSON Object from root String
                JSONObject jsonRootObject = new JSONObject(root);
                // Get the JSON Array containing "Foods"
                JSONArray jsonArray = jsonRootObject.optJSONArray(array);
                // Make new JSONArray and Root to push back
                JSONObject jsonNewRoot = new JSONObject();

                // Put unmodified arrays into new root
                for(int i = 0; i < LOCATIONS_LIST.size(); i++) {
                    if (array != LOCATIONS_LIST.get(i)) {
                        jsonNewRoot.put(LOCATIONS_LIST.get(i), jsonRootObject.optJSONArray(LOCATIONS_LIST.get(i)));
                    }
                }
                // Make new array to store the array without the deleted element
                JSONArray newArray = new JSONArray();
                JSONObject decrement;
                JSONObject stack;
                double new_qty;

                boolean remove = true; // Keeps track of if item has been removed already
                for(int i = 0; i < jsonArray.length(); i++) {
                    if(!(element.optString("name").toString().equals(jsonArray.getJSONObject(i).optString("name").toString())) ||
                            !(element.optString("unit").toString().equals(jsonArray.getJSONObject(i).optString("unit").toString())) ||
                            !(element.optString("expiry").toString().equals(jsonArray.getJSONObject(i).optString("expiry").toString())) ||
                            !remove) { // If item doesn't match
                        newArray.put(jsonArray.get(i)); // Add item to new array
                    }
                    else { // Don't add. I.e: remove
                        decrement = new JSONObject();
                        stack = new JSONObject();
                        decrement.put("name", jsonArray.getJSONObject(i).optString("name").toString());
                        stack.put("name", jsonArray.getJSONObject(i).optString("name").toString());
                        decrement.put("expiry", jsonArray.getJSONObject(i).optString("expiry").toString());
                        stack.put("expiry", jsonArray.getJSONObject(i).optString("expiry").toString());
                        if (Integer.parseInt(jsonArray.getJSONObject(i).optString("unit").toString()) == UNIT)
                            new_qty = Integer.parseInt(jsonArray.getJSONObject(i).optString("quantity").toString()) - 1;
                        else
                            new_qty = new BigDecimal(jsonArray.getJSONObject(i).optString("quantity").toString()).subtract(new BigDecimal(settings.getString("decrement","0.25")).multiply(new BigDecimal(jsonArray.getJSONObject(i).optString("original_qty").toString()))).doubleValue();
                        decrement.put("quantity", new_qty);
                        stack.put("quantity", Double.parseDouble(jsonArray.getJSONObject(i).optString("quantity").toString()));
                        decrement.put("original_qty", Double.parseDouble(jsonArray.getJSONObject(i).optString("original_qty").toString()));
                        stack.put("original_qty", Double.parseDouble(jsonArray.getJSONObject(i).optString("original_qty").toString()));
                        decrement.put("unit", Integer.parseInt(jsonArray.getJSONObject(i).optString("unit").toString()));
                        stack.put("unit", Integer.parseInt(jsonArray.getJSONObject(i).optString("unit").toString()));
                        decrement.put("location", jsonArray.getJSONObject(i).optString("location").toString());
                        stack.put("location", jsonArray.getJSONObject(i).optString("location").toString());
                        newArray.put(decrement);
                        addStack(stack, DEC_SEL, array);
                        remove = false;

                    }
                }

                // Put the new array
                jsonNewRoot.put(array, newArray);

                return jsonNewRoot;
            }

        }  catch (JSONException e) {}

        return null;
    }

    /*
      Deletes a JSONObject from JSONArray.
      @param JSONObject to remove
      @param location/JSONArray from which object is removed
     */
    public void removeFood(JSONObject food, String location) {
        String root = readFile(STORAGE_DEST);
        if (root == "") return;
        try {  // Output the new JSON Root Object to File
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(storage, Context.MODE_PRIVATE));
                write(outputStreamWriter, remove(root, location, food).toString());
        } catch (FileNotFoundException e) {}

        addStack(food, REM_SEL, location);
    }

    /*
      Removes an object from specified array in given root.
      @param root object in string
      @param array to specific
      @param object to remove
      @return json object with element removed
     */
    private JSONObject remove(String root, String array, JSONObject element) {
        try {
            // If root object exists
            if (root != "") {
                // Make a JSON Object from root String
                JSONObject jsonRootObject = new JSONObject(root);
                // Get the JSON Array containing "Foods"
                JSONArray jsonArray = jsonRootObject.optJSONArray(array);
                // Make new JSONArray and Root to push back
                JSONObject jsonNewRoot = new JSONObject();

                // Put unmodified arrays into new root
                for(int i = 0; i < LOCATIONS_LIST.size(); i++) {
                    if (array != LOCATIONS_LIST.get(i)) {
                        jsonNewRoot.put(LOCATIONS_LIST.get(i), jsonRootObject.optJSONArray(LOCATIONS_LIST.get(i)));
                    }
                }
                // Make new array to store the array without the deleted element
                JSONArray newArray = new JSONArray();

                boolean remove = true; // Keeps track of if item has been removed already
                for(int i = 0; i < jsonArray.length(); i++) {
                    if(!(element.optString("name").toString().equals(jsonArray.getJSONObject(i).optString("name").toString())) ||
                            !(element.optString("unit").toString().equals(jsonArray.getJSONObject(i).optString("unit").toString())) ||
                            !(element.optString("expiry").toString().equals(jsonArray.getJSONObject(i).optString("expiry").toString())) ||
                            !remove) { // If item doesn't match
                        newArray.put(jsonArray.get(i)); // Add item to new array
                    }
                    else { // Don't add. I.e: remove
                        remove = false;

                    }
                }

                // Put the new array
                jsonNewRoot.put(array, newArray);

                return jsonNewRoot;
            }

        }  catch (JSONException e) {}

        return null;
    }

    /*
      Appends a new JSONObject with String data to the JSON file.
      @param name of the food
      @param quantity
      @param unit 0 for unit
                  1 for gram
                  2 for kg
                  3 for L
                  4 for cup
      @param location of the food to store
      @param expiry days until it expires
     */
    public void writeToStorage(String name, double quantity, int unit, String location, int expiry) {
        // Create the element
        JSONObject element = create(name, quantity, quantity, unit, expiry, location);

        // Get the root JSON String from File
        String jsonRoot = readFile(STORAGE_DEST);

        try {
            // Make a JSON Object from root String
            JSONObject jsonRootObject = new JSONObject(jsonRoot);
            // Get the JSON Array containing "Foods"
            JSONArray jsonArray = jsonRootObject.optJSONArray(location);

            // Remove duplicate
            for (int i = 0; i < jsonArray.length(); i++) {
                if ((element.optString("name").toString().equals(jsonArray.getJSONObject(i).optString("name").toString()))  &&
                (element.optString("unit").toString().equals(jsonArray.getJSONObject(i).optString("unit").toString())) &&
                (element.optString("expiry").toString().equals(jsonArray.getJSONObject(i).optString("expiry").toString())) )
                {
                    element = create(name, new BigDecimal(element.optString("quantity").toString()).add(new BigDecimal(jsonArray.getJSONObject(i).optString("quantity").toString())).doubleValue(),
                            new BigDecimal(element.optString("original_qty").toString()).add(new BigDecimal(jsonArray.getJSONObject(i).optString("original_qty").toString())).doubleValue(),
                            unit, expiry, location);
                    removeFood(jsonArray.getJSONObject(i), location);
                    jsonRoot = readFile(STORAGE_DEST);
                    break;
                }
            }
        } catch (JSONException e) {}

        try {
            // Output the new JSON Root Object to File
            write(new OutputStreamWriter(context.openFileOutput(storage, Context.MODE_PRIVATE)), add_to_root(element, jsonRoot, location));
        } catch (FileNotFoundException e) {}
    }

    /*
      Make a new JSON Object with provided attributes.
      @param name of the food
      @param quantity
      @param unit 0 for unit
                  1 for gram
                  2 for kg
                  3 for L
                  4 for cup
      @param expiry days until it expires
      @returns JSONObject with given attributes
     */
    private JSONObject create(String name, double quantity, double original_qty, int unit, int expiry, String location) {
        // Create a new JSON Object
        JSONObject element = new JSONObject();
        String date = getCurrentDate();
        String expiry_date = getFutureDate(expiry);
        try {
            element.put("name", name);
            element.put("expiry", expiry_date);
            element.put("quantity", quantity);
            element.put("original_qty", original_qty);
            element.put("unit", unit);
            element.put("location", location);
        } catch (JSONException e) {}
        return element;
    }

    /*
      Adds an element to a array in JSON Root Object.
      @param object to add
      @param root object in string with arrays
      @param location to choose between arrays
      @returns modified json root object in string
     */
    private String add_to_root(JSONObject element, String root, String location) {
        assert(element != null);
        try {
            JSONObject jsonRootObject;
            JSONArray jsonArray;
            // If root object exists
            if (root != "") {
                // Make a JSON Object from root String
                jsonRootObject = new JSONObject(root);
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

            return jsonRootObject.toString();

        } catch (JSONException e) {}

        return null;
    }

    /*
      Write to File, given an output stream writer.
      @param OutputStreamWriter to File
     */
    private void write(OutputStreamWriter osw, String word) {
        try {
            osw.write(word);
            osw.close();
        } catch (IOException e){}
    }

    /*
      Gets JSON Array from specified location.
      @param locations
     */
    public JSONArray getArray(String location) {
        String root = readFile(STORAGE_DEST);
        if (location.equals("Library")) {
            root = readFile(LIBRARY_DEST);
            return extractArray(root, "Foods");
        }
        return extractArray(root, location);
    }

    public JSONArray getSortedExpiryArray() throws JSONException, ParseException {
        String root = readFile(STORAGE_DEST);
        return getSortedExpiry(root, settings.getInt("expiryWarning",3));
    }

    /*
      @param root of the database
      @returns JSONArray containing all food items which are close to expiry sorted by expiry date
     */
    private JSONArray getSortedExpiry(String root, int expiry) throws JSONException, ParseException {

        JSONArray fridge = extractArray(root, "Fridge");
        JSONArray freezer = extractArray(root, "Freezer");
        JSONArray pantry = extractArray(root, "Pantry");
        JSONArray fresh = extractArray(root, "Fresh");
        JSONArray closeToExpiry = concatArray(fridge, freezer, pantry, fresh);
        JSONArray sortedArray = new JSONArray();

        List<ExpDateJSON> sortedList = new ArrayList<ExpDateJSON>();

        for (int i = 0; i < closeToExpiry.length(); i++) {
            JSONObject food = closeToExpiry.getJSONObject(i);
            if (foodToExpire(food, expiry))
                sortedList.add(new ExpDateJSON(daysToExpire(food), food ));
        }
        Collections.sort(sortedList);


        for (int i = 0; i < sortedList.size(); i++){
            sortedArray.put(sortedList.get(i).getObject());
        }

        return sortedArray;
    }

    /*
      Compares expiry date of food to actual time
      @param JSONObject which should be compared
      @returns 0 for not close to expiry, 1 for close to expiry
     */
    public boolean foodToExpire(JSONObject food, int expiry) throws ParseException {
        int DaysToExpiryDate = expiry;
        
        int dayDiff = daysToExpire(food);
        
        return ( (dayDiff - DaysToExpiryDate) <= 0 );
    }

        /*
      Finds days until food expires fractions of days are rounded off
      @param JSONObject from whom you want the days
      @returns days to expiry
     */
    private  int daysToExpire(JSONObject food) throws ParseException {
        Calendar actDate = Calendar.getInstance();
        TimeZone myZone = TimeZone.getDefault();
        long offsetUTC = myZone.getRawOffset();

        Calendar expDate = Calendar.getInstance();
        String expiryDate = food.optString("expiry");
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = formatter.parse(expiryDate);
        expDate.setTime(date);

        long millisPerDay = (24 * 60 * 60 * 1000);
        long today = actDate.getTimeInMillis() + offsetUTC;
        long daysToday = today /millisPerDay;

        long expiry = expDate.getTimeInMillis() + offsetUTC;
        long daysExpiry = expiry /millisPerDay;

        return (int)(daysExpiry-daysToday);
    }

    /*
      Returns a JSONArray with attribute, provided a root object.
      @param JSONArray to concatenate
      @returns Concatenated JSONArray
     */
    private JSONArray concatArray(JSONArray... arrs)
            throws JSONException {
        JSONArray result = new JSONArray();
        for (JSONArray arr : arrs) {
            for (int i = 0; i < arr.length(); i++) {
                result.put(arr.get(i));
            }
        }
        return result;
    }

    /*
      Returns a JSONArray with attribute, provided a root object.
      @param root object in string
      @param attribute
      @returns JSONObject with given attribute
     */
    private JSONArray extractArray(String root, String attribute) {
        try {
            JSONObject  jsonRootObject = new JSONObject(root);

            // Get the food array from root object
            JSONArray jsonArray = jsonRootObject.optJSONArray(attribute);

            return jsonArray;
        } catch (JSONException e) {}

        return null;
    }

    /*
      Reads the JSON file and returns it as string
      @param destination to read from
      @return raw JSON formatted string
              empty string if file does not exist
    */
    private String readFile(int destination) {
        InputStreamReader isr;
        if (destination == STORAGE_DEST) {
            try {
                isr = new InputStreamReader(context.openFileInput(storage));
                return read(isr);
            } catch (FileNotFoundException e) {
            }
        }
        else if (destination == LIBRARY_DEST) {
            try {
                isr = new InputStreamReader(context.openFileInput(library));
                return read(isr);
            } catch (FileNotFoundException e) {
            }
        }
        else if (destination == ASSETS_DEST) {
            try {
                isr = new InputStreamReader(context.getResources().getAssets().open(default_lib));
                return read(isr);
            } catch (IOException e) {
            }
        }
        else if (destination == UNDO_DEST) {
            try {
                isr = new InputStreamReader(context.openFileInput(undo_stack));
                return read(isr);
            } catch (IOException e) {
            }
        }

        return "";
    }

    /*
      A method that handles only reading.
     */
    private String read(InputStreamReader isr) {
        assert(isr != null);

        String root = "";
        try {
            // Set up string builder
            BufferedReader bufferedReader = new BufferedReader(isr);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ( (receiveString = bufferedReader.readLine()) != null ) {
                stringBuilder.append(receiveString);
            }
            isr.close();
            root = stringBuilder.toString();

        } catch (IOException e) {}

        return root;
    }

    /*
      Imports a default library to working library. Must only be called once.
     */
    public void importLibrary() {
        try {
            // Output the new JSON Root Object to File
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(this.library, Context.MODE_PRIVATE));
            write(outputStreamWriter, new JSONObject(readFile(ASSETS_DEST)).toString());
        } catch (IOException e) {} catch (JSONException e) {}
    }

    /*
      Gets the current date in DD-MM-YYYY format.
      @return current date in String
    */
    public String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(c.getTime());
    }

    /*
      Gets the date that is x days away from today.
      @param days away from today
      @return date in String
     */
    public String getFutureDate(int days) {
        Calendar c = new GregorianCalendar();
        c.add(Calendar.DATE, days);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(c.getTime());
    }

}

class ExpDateJSON implements Comparable<ExpDateJSON> {
    int daysToExpire;
    private JSONObject food;

    public ExpDateJSON(int daysToExpire, JSONObject food) {
        this.daysToExpire = daysToExpire;
        this.food = food;
    }

    @Override
    public int compareTo(ExpDateJSON o) {
        return daysToExpire < o.daysToExpire ? -1 : daysToExpire > o.daysToExpire ? 1 : 0;
    }

    public JSONObject getObject(){
        return this.food;
    }
}