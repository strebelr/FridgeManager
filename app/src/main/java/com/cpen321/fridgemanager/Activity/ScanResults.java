package com.cpen321.fridgemanager.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
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

import com.cpen321.fridgemanager.Database.DatabaseInteraction;
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

import static android.R.id.message;
import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class ScanResults extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);

        ti = new TextRecognitionInteraction(getApplicationContext());
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
        for(int i = 0; i < names.size(); i++) {
            if(names.get(i) != null) { // If food not removed
                int expiry = expiries.get(i); // Numbers of days until the expiry date.
                // TODO: CALL ALARM FROM HERE
                ranNum = generateNumber();


                //Alert a = new Alert();
                //a.setAlarm(findViewById(R.id.scan_result), expiry);
                //setAlarm(view, expiry, i1);

                if(expiry > 4) {
                    setAlarm(view, expiry - 3, ranNum + 1, PRE_EXPIRY);     // sends notification 3 days before expiry
                    setAlarm(view, expiry, ranNum, EXPIRY);
                } else if (expiry <= 3 && expiry > 1) {
                    setAlarm(view, 1, ranNum + 1, PRE_EXPIRY);              // sends notification the next day
                    setAlarm(view, expiry, ranNum, EXPIRY);
                } else {
                    setAlarm(view, expiry, ranNum, EXPIRY);         // only send notification on the day of expiry
                }

                if (amounts.get(i).getText().toString() == null || amounts.get(i).getText().toString().isEmpty()) { // If amount not entered
                    ti.addFoodToStorage(names.get(i), quantities.get(i), units.get(i), locations.get(i), expiry);
                } else {
                    ti.addFoodToStorage(names.get(i), Double.parseDouble(amounts.get(i).getText().toString()), units.get(i), locations.get(i), expiry);
                }
            }
        }
        mainMenu();
    }

    /* Methods used fo Alarm */

    public int generateNumber() {
        Random r = new Random();
        int num = r.nextInt(1000) + 1;
        return num;
    }

    private int ranNum; // random number to generate unique ID
    private static final int EXPIRY = 0;        // expired
    private static final int PRE_EXPIRY = 1;    // soon to expire


    public ScanResults() {
        this.ranNum = generateNumber();
    }

    public int getNumber() {
        return this.ranNum;
    }

    // TODO:  MSG. WHAT HAPPENS IF JANUARY 31 AND DAY IS ADDED
    public void setAlarm(View view, int dayToExpire, int notifID, int alarmType) {

        Calendar calendar = Calendar.getInstance();     // possible redundancy here
        //Calendar c = new GregorianCalendar();

        calendar.add(Calendar.SECOND, 10);
        //calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.add(Calendar.DAY_OF_YEAR, dayToExpire);

        android.util.Log.i("AFTER ",": " +calendar);

        Long alertTime = System.currentTimeMillis() + 5000;

        android.util.Log.i("Time Class ", " Time value in milliseconds "+alertTime);

        Intent intent = new Intent(getApplicationContext(), AlertReceiver.class);
        intent.putExtra("NOTIF_TYPE", alarmType);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notifID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);

    }

}
