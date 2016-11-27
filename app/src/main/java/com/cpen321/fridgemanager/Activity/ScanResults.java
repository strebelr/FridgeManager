package com.cpen321.fridgemanager.Activity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.cpen321.fridgemanager.Notification.Alert;
import com.cpen321.fridgemanager.Notification.AlertReceiver;
import com.cpen321.fridgemanager.OcrReader.OcrCaptureActivity;
import com.cpen321.fridgemanager.R;
import com.cpen321.fridgemanager.Algorithm.TextRecognitionInteraction;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import static android.R.id.message;
import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class ScanResults extends Alert {
    // Initial Texts
    private ArrayList<String> texts = new ArrayList<String>();

    // Initialize ArrayLists  that store food data. Index corresponds between these three lists.
    private ArrayList<EditText> amounts = new ArrayList<EditText>();
    private ArrayList<Integer> units = new ArrayList<Integer>();
    private ArrayList<String> names = new ArrayList<String>();
    private ArrayList<String> locations = new ArrayList<String>();
    private ArrayList<Integer> expiries = new ArrayList<Integer>();
    private ArrayList<Integer> quantities = new ArrayList<Integer>();

    TableLayout mTlayout;
    private ArrayList<TableRow> trs = new ArrayList<TableRow>();

    // A Text Recognition Interaction Object
    private TextRecognitionInteraction ti;

    private DatabaseInteraction di;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);

        ti = new TextRecognitionInteraction(getApplicationContext());
        di = new DatabaseInteraction(getApplicationContext());
        mTlayout = (TableLayout) findViewById(R.id.mTlayout);
        texts = getIntent().getStringArrayListExtra("texts");

        JSONObject food;

        // TODO: Handle the case where 2 items have identical name. Do not display two items, instead increase the default quantity
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
            expiries.add(Integer.parseInt(text_no_duplicates.get(i).optString("expiry").toString()));
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

        for(int i = 0; i < names.size(); i++) {
            // Add table row and quantity field to array list.
            trs.add(new TableRow(this));
            amounts.add(new EditText(this));

            // Add table row to layout and initialize button and text views
            mTlayout.addView(trs.get(i));
            ImageButton btn_del = new ImageButton(this);
            TextView food_name = new TextView(this);
            TextView unit_name = new TextView(this);

            // Create amount field
            TableRow.LayoutParams amountLayoutParams = new TableRow.LayoutParams();
            amounts.get(i).setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
            amounts.get(i).setGravity(Gravity.RIGHT);
            amounts.get(i).setMaxLines(1);
            if (quantities.get(i) != 0)
                amounts.get(i).setHint(quantities.get(i) + "");
            amounts.get(i).setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            float measure = amounts.get(i).getPaint().measureText("9999"); // Set width
            amounts.get(i).setWidth(amounts.get(i).getPaddingLeft() + amounts.get(i).getPaddingRight() + (int) measure);
            trs.get(i).addView(amounts.get(i));

            // Create unit text
            switch (units.get(i)) {
                // TODO: CHANGE UNIT STRING IF NECESSARY
                case DatabaseInteraction.UNIT:
                    unit_name.setText("");
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
            unit_name.setId(i);
            unit_name.setGravity(Gravity.CENTER_VERTICAL);
            TableRow.LayoutParams trLayoutParams_unit = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            unit_name.setLayoutParams(trLayoutParams_unit);
            trs.get(i).addView(unit_name);

            // Create delete button
            btn_del.setImageResource(R.drawable.ic_trash);
            btn_del.setId(i);
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
            trs.get(i).addView(btn_del);

            // Create food text
            food_name.setText(names.get(i));
            food_name.setId(i);
            food_name.setGravity(Gravity.CENTER_VERTICAL);
            TableRow.LayoutParams trLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            trLayoutParams.weight = 1;
            food_name.setLayoutParams(trLayoutParams);
            trs.get(i).addView(food_name, 0);
        }
    }

    @Override
    public void onBackPressed(){
        mainMenu();
    }

    public void OcrCaptureActivity(View view){
        Intent intent = new Intent(this, OcrCaptureActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void OcrCaptureActivityWithArray(View view){
        Intent intent = new Intent(this, OcrCaptureActivity.class);
        intent.putStringArrayListExtra("texts", texts);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void mainMenu() {
        Intent intent = new Intent(this, MainMenu.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void AddFood(View view){

        // Count number of food added
        int count = 0;

        // Checks if there is empty quantity
        boolean alert = false; // Assume all quantities entered
        String message = "Invalid amount for item: "; // message to alert
        for(int i = 0; i < amounts.size(); i++ ){
            if(amounts.get(i) != null) {
                if (amounts.get(i).getText().toString().isEmpty() && quantities.get(i).equals(0)) {
                    alert = true; // Empty quantity found
                    message = message + names.get(i);
                    break;
                }
            }
        }

        if(alert) { // If empty quantity exists
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage(message + "\nRe-enter to try again.");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }

        else { // If all quantities are valid, add food

            for (int i = 0; i < names.size(); i++) {
                if (names.get(i) != null) {          // If food not removed
                    int expiry = expiries.get(i);   // Numbers of days until the expiry date.
                    // TODO: CALL ALARM FROM HERE

                    String catted = concatenate(names.get(i), String.valueOf(quantities.get(i)), di.getCurrentDate(), di.getFutureDate(expiry));
                    EXPIRY_ID = convertToID(catted);
                    PRE_EXPIRY_ID = convertToID(catted) + 50000;

                    if (counterID[EXPIRY_ID] == 0 || counterID[PRE_EXPIRY_ID] == 0) {
                        //set alarm with cases
                        if (expiry > 4) {
                            setAlarm(view, expiry - 3, PRE_EXPIRY_ID, PRE_EXPIRY);     // sends notification 3 days before expiry
                            setAlarm(view, expiry, EXPIRY_ID, EXPIRY);
                        } else if (expiry <= 3 && expiry > 1) {
                            setAlarm(view, 1, PRE_EXPIRY_ID, PRE_EXPIRY);              // sends notification the next day
                            setAlarm(view, expiry, EXPIRY_ID, EXPIRY);
                        } else {
                            setAlarm(view, expiry, EXPIRY_ID, EXPIRY);         // only send notification on the day of expiry
                        }
                        counterID[EXPIRY_ID]++;
                        counterID[PRE_EXPIRY_ID]++;
                        android.util.Log.i("Notification ID", " ID Remaining: " + counterID[EXPIRY_ID] + " and " + counterID[PRE_EXPIRY_ID]);
                    } else {
                        counterID[EXPIRY_ID]++;
                        counterID[PRE_EXPIRY_ID]++;
                        android.util.Log.i("Notification ID", " ID Remaining: " + counterID[EXPIRY_ID] + " and " + counterID[PRE_EXPIRY_ID]);

                    }


                /*if(expiry > 4) {
                    setAlarm(view, expiry - 3, ID + 1, PRE_EXPIRY, names.get(i));     // sends notification 3 days before expiry
                    setAlarm(view, expiry, ID, EXPIRY, names.get(i));
                } else if (expiry <= 3 && expiry > 1) {
                    setAlarm(view, 1, ID + 1, PRE_EXPIRY, names.get(i));              // sends notification the next day
                    setAlarm(view, expiry, ID, EXPIRY, names.get(i));
                } else {
                    setAlarm(view, expiry, ID, EXPIRY, names.get(i));         // only send notification on the day of expiry
                }*/

                    if (amounts.get(i).getText().toString() == null || amounts.get(i).getText().toString().isEmpty()) { // If amount not entered
                        ti.addFoodToStorage(names.get(i), quantities.get(i), units.get(i), locations.get(i), expiry);
                    } else {
                        ti.addFoodToStorage(names.get(i), Double.parseDouble(amounts.get(i).getText().toString()), units.get(i), locations.get(i), expiry);
                    }
                    count++;
                }
            }

            CharSequence text; // Success toast
            if (count == 0) {
                text = "No food added: Returning to Main Menu";
            }
            else {
                text = "Success: " + count + " foods are added!";
            }

            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
            toast.show();

            mainMenu();
        }
    }

    /********* Variables used for Alarm **********/

    public static int EXPIRY_ID; // random number to generate unique ID
    public static int PRE_EXPIRY_ID;
    private static final int EXPIRY = 0;        // expired
    private static final int PRE_EXPIRY = 1;    // soon to expire
    public static int[] counterID = new int[100000];

}