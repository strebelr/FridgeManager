package com.cpen321.fridgemanager.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.cpen321.fridgemanager.Database.DatabaseInteraction;
import com.cpen321.fridgemanager.Notification.Alarm;
import com.cpen321.fridgemanager.OcrReader.OcrCaptureActivity;
import com.cpen321.fridgemanager.R;
import com.cpen321.fridgemanager.Algorithm.TextRecognitionInteraction;

import org.json.JSONObject;

import java.util.ArrayList;

import static android.R.id.message;
import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class ScanResults extends AppCompatActivity {
    // Table Layout containing each scanned items
    TableLayout mTlayout;

    // Initial ArrayList of Texts
    private ArrayList<String> texts = new ArrayList<String>();

    // Initialize ArrayLists that store food data. Indices corresponds between these seven lists.
    private ArrayList<EditText> amounts = new ArrayList<EditText>();
    private ArrayList<Integer> units = new ArrayList<Integer>();
    private ArrayList<String> names = new ArrayList<String>();
    private ArrayList<String> locations = new ArrayList<String>();
    private ArrayList<Integer> expirys = new ArrayList<Integer>();
    private ArrayList<Integer> quantities = new ArrayList<Integer>();
    private ArrayList<TableRow> trs = new ArrayList<TableRow>();

    // A TextRecognitionInteraction Object to check if word is valid food item
    private TextRecognitionInteraction ti;

    // A DatabaseInteraction Object to add food to database
    private DatabaseInteraction di;

    // Application Context
    private Context mContext;

    // Alarm object to perform Alarm interactions
    private Alarm mAlarm;

    //TODO: may move this to Alarm file
    public static int EXPIRY_ID;
    public static int PRE_EXPIRY_ID;
    private static final int EXPIRY = 0;        // expired
    private static final int PRE_EXPIRY = 1;    // soon to expire
    public static double[] counterID = new double[400000];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);

        // Initialize global variables
        initializeGlobal();
        // Populate the global arrays with valid food items.
        checkValidFoodToPopulate();
        // Fill the screen with valid food items.
        fillScreen();

        SharedPreferences settings = getSharedPreferences("prefs",0);
        boolean firstCamera = settings.getBoolean("firstScan",false);
        if (firstCamera == false) {
            SharedPreferences.Editor editor= settings.edit();
            editor.putBoolean("firstScan",true);
            editor.commit();

            popDialog("Scan Results", getString(R.string.scanresults_explanation));
        }
    }

    /*
      Fills the screen with names of food, and their corresponding buttons.
     */
    private void fillScreen() {
        for(int i = 0; i < names.size(); i++) {
            // Add table row and quantity field to array list.
            trs.add(new TableRow(this));
            // Add table row to layout and initialize button and text views
            mTlayout.addView(trs.get(i));
            // Make fields
            makeAmountField(i); // amount field
            makeUnitField(i); // unit field
            makeDeleteButton(i); // delete button
            makeFoodNameField(i); // food text
        }
    }

    /*
      Make food name field for corresponding food item
      @param index at which food item is located
     */
    private void makeFoodNameField(int index) {
        // Create food name field
        TextView food_name = new TextView(this);
        food_name.setText(names.get(index));
        food_name.setId(index);
        food_name.setGravity(Gravity.CENTER_VERTICAL);
        TableRow.LayoutParams trLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        trLayoutParams.weight = 1;
        food_name.setLayoutParams(trLayoutParams);
        trs.get(index).addView(food_name, 0);
    }

    /*
      Make delete button for corresponding food item
      @param index at which food item is located
     */
    private void makeDeleteButton(int index) {
        ImageButton btn_del = new ImageButton(this);
        btn_del.setImageResource(R.drawable.ic_trash);
        btn_del.setId(index);
        // Set on click listener
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = v.getId();
                // Assign null to elements removed
                names.set(index, null);
                amounts.set(index, null);
                units.set(index, null);
                quantities.set(index, null);
                // Remove table row from table layout
                mTlayout.removeView(trs.get(index));
            }
        });
        trs.get(index).addView(btn_del);
    }

    /*
      Make amount field for corresponding food item
      @param index at which food item is located
     */
    private void makeAmountField(int index) {
        amounts.add(new EditText(this));
        amounts.get(index).setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        amounts.get(index).setGravity(Gravity.RIGHT);
        amounts.get(index).setMaxLines(1);
        if (quantities.get(index) != 0)
            amounts.get(index).setHint(quantities.get(index) + "");
        if (units.get(index) != DatabaseInteraction.UNIT) // If unit is other than type: unit
            amounts.get(index).setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        else // If unit is type: unit
            amounts.get(index).setInputType(InputType.TYPE_CLASS_NUMBER);
        float measure = amounts.get(index).getPaint().measureText("9999"); // Set width
        amounts.get(index).setWidth(amounts.get(index).getPaddingLeft() + amounts.get(index).getPaddingRight() + (int) measure);
        trs.get(index).addView(amounts.get(index));
    }

    /*
      Make unit field for corresponding food item
      @param index at which food item is located
     */
    private void makeUnitField(int index) {
        TextView unit_name = new TextView(this);

        // Create unit text
        switch (units.get(index)) {
            // TODO: CHANGE UNIT STRING IF NECESSARY
            case DatabaseInteraction.UNIT:
                unit_name.setText("pcs");
                break;
            case DatabaseInteraction.GRAM:
                unit_name.setText(" g");
                break;
            case DatabaseInteraction.KG:
                unit_name.setText(" kg");
                break;
            case DatabaseInteraction.L:
                unit_name.setText(" l");
                break;
            case DatabaseInteraction.CUP:
                unit_name.setText(" cups");
                break;
        }

        // Create unit text view
        unit_name.setId(index);
        unit_name.setGravity(Gravity.CENTER_VERTICAL);
        TableRow.LayoutParams trLayoutParams_unit = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        unit_name.setLayoutParams(trLayoutParams_unit);
        trs.get(index).addView(unit_name);
    }

    /*
      Check if food items supplied to this activity are valid. If so, check for duplicate names to merge.
      Then, populate array lists from library.
     */
    private void checkValidFoodToPopulate() {
        JSONObject food;
        ArrayList<JSONObject> text_no_duplicates = new ArrayList<>();
        ArrayList<String> text_no_duplicates_compare = new ArrayList<>();
        ArrayList<JSONObject> duplicates = new ArrayList<>();

        // Get rid of duplicates
        for(int i = 0; i < texts.size(); i++){
            food = ti.isFood(texts.get(i));
            if(food != null) {
                if (!text_no_duplicates_compare.contains(food.optString("name").toString())) {
                    text_no_duplicates.add(food);
                    text_no_duplicates_compare.add(food.optString("name").toString());
                }
                else
                    duplicates.add(food);
            }
        }

        for(int i = 0; i < text_no_duplicates.size(); i++) {
            int count = 1;
            names.add(text_no_duplicates.get(i).optString("name").toString());
            units.add(Integer.parseInt(text_no_duplicates.get(i).optString("unit").toString()));
            locations.add(text_no_duplicates.get(i).optString("location").toString());
            expirys.add(Integer.parseInt(text_no_duplicates.get(i).optString("expiry").toString()));
            if(Integer.parseInt(text_no_duplicates.get(i).optString("unit").toString()) == DatabaseInteraction.UNIT) {
                for(int j = 0; j < duplicates.size(); j++) {
                    if(text_no_duplicates.get(i).optString("name").toString().equals(duplicates.get(j).optString("name").toString())) {
                        count++;
                    }
                }
                quantities.add(count);
            }
            else
                quantities.add(0);
        }
    }

    /*
      Initialize global variables.
     */
    private void initializeGlobal() {
        mContext = getApplicationContext();
        mAlarm = new Alarm();
        ti = new TextRecognitionInteraction(getApplicationContext());
        di = new DatabaseInteraction(getApplicationContext());
        mTlayout = (TableLayout) findViewById(R.id.mTlayout);
        texts = getIntent().getStringArrayListExtra("texts");
    }

    /*
      Show a dialogue with provided message.
      @param message to show in the dialogue
     */
    private void popDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    /*
      Show a toast notifying of a successful add.
      @param number of food items added
     */
    private void showSuccessToast(int count) {
        CharSequence text; // Success toast
        if (count == 0) {
            text = getString(R.string.add_nofood);
        }
        else {
            text = "Success: " + count + " foods are added!";
        }

        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onBackPressed(){
        mainMenu();
    }

    /*
      Moves to OcrCaptureActivity.
      @param view
     */
    public void OcrCaptureActivity(View view){
        Intent intent = new Intent(this, OcrCaptureActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    /*
      Moves to OcrCaptureActivity while supplying with current list of food items.
      Called to take more receipts/long receipts.
      @param view
     */
    public void OcrCaptureActivityWithArray(View view){
        Intent intent = new Intent(this, OcrCaptureActivity.class);
        intent.putStringArrayListExtra("texts", texts);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    /*
      Moves to Main Menu UI
     */
    public void mainMenu() {
        Intent intent = new Intent(this, MainMenu.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    /*
      Add food to the database and initialize expiry alerts for them.
      @param view
     */
    public void AddFood(View view){
        // Checks if there is empty quantity / hint
        boolean alert = false; // Assume all quantities entered
        String message = getString(R.string.alert_initial); // message to alert
        for(int i = 0; i < amounts.size(); i++ ){
            if(amounts.get(i) != null) {
                if (amounts.get(i).getText().toString().isEmpty() && quantities.get(i).equals(0)) {
                    alert = true; // Empty quantity found
                    message = message + names.get(i) + getString(R.string.alert_reenter);
                    break;
                }
            }
        }

        // If there is empty quantity, give an alert.
        if(alert) {
            popDialog("Error", message);
        }
        else { // If all quantities are valid, add food
            // Count number of food added
            int count = 0;

            for (int index = 0; index < names.size(); index++) { // For each food item
                if (names.get(index) != null) {          // If food not removed
                    // Alarm
                    int expiry = expirys.get(index);   // Numbers of days until the expiry date.
                    prepAlarm(expiry, view, index);
                    // Add food
                    writeDatabase(index, expiry);
                    // Increment food counter
                    count++;
                }
            }
            // Show toast
            showSuccessToast(count);
            // Force back to main menu
            mainMenu();
        }
    }

    /*
      Decides which days the alarm will be set on and the message given
      @param expiry
      @param view
      @param index
    */
    private void prepAlarm(int expiry, View view, int index) {
        EXPIRY_ID =  mAlarm.convertToID(di.getFutureDate(expiry));
        PRE_EXPIRY_ID =  mAlarm.convertToID(di.getFutureDate(expiry)) + 50000;

        if (counterID[EXPIRY_ID] == 0 || counterID[PRE_EXPIRY_ID] == 0) {

            //sets alarm with cases
            if (expiry > 4) {
                mAlarm.setAlarm(mContext, view, expiry - 3, PRE_EXPIRY_ID, PRE_EXPIRY, quantities.get(index));  // sends notification 3 days before expiry
                mAlarm.setAlarm(mContext, view, expiry, EXPIRY_ID, EXPIRY, quantities.get(index));
            } else if (expiry <= 3 && expiry > 1) {
                mAlarm.setAlarm(mContext, view, 1, PRE_EXPIRY_ID, PRE_EXPIRY, quantities.get(index));           // sends notification the next day
                mAlarm.setAlarm(mContext, view, expiry, EXPIRY_ID, EXPIRY, quantities.get(index));
            } else {
                mAlarm.setAlarm(mContext, view, expiry, EXPIRY_ID, EXPIRY, quantities.get(index));              // only sends notification on the day of expiry
            }
            counterID[EXPIRY_ID] += quantities.get(index);
            counterID[PRE_EXPIRY_ID]+= quantities.get(index);
            android.util.Log.i("Notification ID", " ID Remaining: " + counterID[EXPIRY_ID] + " and " + counterID[PRE_EXPIRY_ID]);
        } else {

            counterID[EXPIRY_ID]+= quantities.get(index);
            counterID[PRE_EXPIRY_ID]+= quantities.get(index);;
            android.util.Log.i("Notification ID", " ID Remaining: " + counterID[EXPIRY_ID] + " and " + counterID[PRE_EXPIRY_ID]);
        }
    }

    private void writeDatabase(int index, int expiry) {
        if (amounts.get(index).getText().toString() == null || amounts.get(index).getText().toString().isEmpty()) { // If amount not entered, use hint
            ti.addFoodToStorage(names.get(index), quantities.get(index), units.get(index), locations.get(index), expiry);
        } else { // If amount entered
            ti.addFoodToStorage(names.get(index), Double.parseDouble(amounts.get(index).getText().toString()), units.get(index), locations.get(index), expiry);
        }
    }

}