package com.cpen321.fridgemanager;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.cpen321.fridgemanager.Notification.Alert;
import com.cpen321.fridgemanager.Notification.AlertReceiver;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static android.R.id.message;
import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class scanResults extends AppCompatActivity {

    // Initialize ArrayLists  that store food data. Index corresponds between these three lists.
    private ArrayList<EditText> amounts = new ArrayList<EditText>();
    private ArrayList<Integer> units = new ArrayList<Integer>();
    private ArrayList<String> names = new ArrayList<String>();
    private ArrayList<String> locations = new ArrayList<String>();
    private ArrayList<Integer> expiries = new ArrayList<Integer>();

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
        ArrayList<String> texts = getIntent().getStringArrayListExtra("texts");

        JSONObject food;

        ArrayList<ArrayList<String>> consecutive_non_name = new ArrayList<ArrayList<String>>();
        ArrayList<String> temp_consecutive = null;

        for(int i = 0; i < texts.size(); i++) {
            food = ti.isFood(texts.get(i));
            if(food != null) {
                if(temp_consecutive != null) {
                    consecutive_non_name.add(new ArrayList<String>(temp_consecutive));
                    temp_consecutive = null;
                }
                names.add(food.optString("name").toString());
                units.add(Integer.parseInt(food.optString("unit").toString()));
                locations.add(food.optString("location").toString());
                expiries.add(Integer.parseInt(food.optString("expiry").toString()));
            }
            else {
                if(temp_consecutive == null)
                    temp_consecutive = new ArrayList<String>();
                temp_consecutive.add(texts.get(i));
            }
        }


        // TODO: DEALS WITH NON-CONSECUTIVE WORDS
//        for(int i = 0; i < consecutive_non_name.size(); i++) { // For each arrays of non-consecutive words
//            if (consecutive_non_name.get(i).size() > 1) {
//                for (int j = 0; j < consecutive_non_name.get(i).size(); j++) { // For each non-consecutive words
//                    for (int k = j + 1; k < consecutive_non_name.get(i).size(); k++) { // Move through the words to get all combinations
//                        String concat = "";
//                        int inc_j = j;
//                        while(inc_j <= k) {
//                            concat += consecutive_non_name.get(i).get(inc_j);
//                            inc_j++;
//                        }
//                        // TODO: CHECK NAME HERE. NEEDS A WAY TO HANDLE TWO-WORDED FOODS, e.g. "Green Onion". How??
//                    }
//                }
//            }
//        }

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
            amounts.get(i).setMaxLines(1);
            amounts.get(i).setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            float measure = amounts.get(i).getPaint().measureText("9999"); // Set width
            amounts.get(i).setWidth(amounts.get(i).getPaddingLeft() + amounts.get(i).getPaddingRight() + (int) measure);
            trs.get(i).addView(amounts.get(i));

            // Create unit text
            switch (units.get(i)) {
                // TODO: CHANGE UNIT STRING IF NECESSARY
                case 0:
                    unit_name.setText("unit");
                    break;
                case 1:
                    unit_name.setText("grams");
                    break;
                case 2:
                    unit_name.setText("kgs");
                    break;
                case 3:
                    unit_name.setText("litres");
                    break;
                case 4:
                    unit_name.setText("cups");
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

                //Alert a = new Alert();
                //a.setAlarm(findViewById(R.id.scan_result), expiry);
                setAlarm(view, expiry);

                if (amounts.get(i).getText().toString() == null || amounts.get(i).getText().toString().isEmpty()) { // If amount not entered
                    ti.addFoodToStorage(names.get(i), 0.0, units.get(i), locations.get(i), expiry);
                } else {
                    ti.addFoodToStorage(names.get(i), Double.parseDouble(amounts.get(i).getText().toString()), units.get(i), locations.get(i), expiry);
                }
            }
        }
        mainMenu();
    }


    public void setAlarm(View view, int dayToExpire) {

        Calendar calendar = Calendar.getInstance();     // possible redundancy here
        Calendar c = new GregorianCalendar();

        calendar.getTime();
        c.add(c.DATE, dayToExpire);

        calendar.set(Calendar.DAY_OF_MONTH,c.DATE);
       // calendar.set(Calendar.HOUR_OF_DAY,18);
        calendar.set(Calendar.MINUTE,00);
        calendar.set(Calendar.SECOND,00);

        Long alertTime = System.currentTimeMillis() + 5000;

        android.util.Log.i("Time Class ", " Time value in milliseconds "+alertTime);

        Intent intent = new Intent(getApplicationContext(), AlertReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime, pendingIntent);

        /* Comment above line and uncomment this line once expiry date is ready) */
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);

    }

}
