package com.cpen321.fridgemanager;

import android.app.ActionBar;
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

import java.util.ArrayList;

import static android.R.id.message;
import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class scanResults extends AppCompatActivity {

    // Initialize ArrayLists  that store food data. Index corresponds between these three lists.
    private ArrayList<EditText> amounts = new ArrayList<EditText>();
    private ArrayList<Integer> units = new ArrayList<Integer>();
    private ArrayList<String> names = new ArrayList<String>();

    TableLayout mTlayout;
    private ArrayList<TableRow> trs = new ArrayList<TableRow>();

    // A Database Interaction Object
    private TextRecognitionInteraction ti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);

        ti = new TextRecognitionInteraction(getApplicationContext());
        mTlayout = (TableLayout) findViewById(R.id.mTlayout);
        ArrayList<String> texts = getIntent().getStringArrayListExtra("texts");

        String name = "";
        int int_unit; // Int value for unit type.
        int current_row = 0; // Keeps track of number of elements in array list

        for(int i = 0; i < texts.size(); i++) {
            name = texts.get(i);
            int_unit = ti.getUnit(name);

            if (int_unit != -1) {
                // Add table row, unit, quantity, and food name to array list.
                trs.add(new TableRow(this));
                units.add(int_unit);
                amounts.add(new EditText(this));
                names.add(texts.get(i));

                // Add table row to layout and initialize button and text views
                mTlayout.addView(trs.get(current_row));
                ImageButton btn_del = new ImageButton(this);
                TextView food_name = new TextView(this);
                TextView unit_name = new TextView(this);

                // Create amount field
                TableRow.LayoutParams amountLayoutParams = new TableRow.LayoutParams();
                amounts.get(current_row).setFilters(new InputFilter[] { new InputFilter.LengthFilter(4) });
                amounts.get(current_row).setMaxLines(1);
                amounts.get(current_row).setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                float measure = amounts.get(current_row).getPaint().measureText("9999"); // Set width
                amounts.get(current_row).setWidth(amounts.get(current_row).getPaddingLeft() + amounts.get(current_row).getPaddingRight() + (int) measure);
                trs.get(current_row).addView(amounts.get(current_row));

                // Create unit text
                switch(int_unit) {
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
                trs.get(current_row).addView(unit_name);

                // Create delete button
                btn_del.setImageResource(R.drawable.ic_trash);
                btn_del.setId(current_row);
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
                trs.get(current_row).addView(btn_del);

                // Create food text
                food_name.setText(name);
                food_name.setId(i);
                food_name.setGravity(Gravity.CENTER_VERTICAL);
                TableRow.LayoutParams trLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                trLayoutParams.weight = 1;
                food_name.setLayoutParams(trLayoutParams);
                trs.get(current_row).addView(food_name,0);

                // Increment array list count
                current_row++;
            }
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
                if (amounts.get(i).getText().toString() == null || amounts.get(i).getText().toString().isEmpty()) { // If amount not entered
                    ti.addFoodToStorage(names.get(i), 0.0, units.get(i));
                } else {
                    ti.addFoodToStorage(names.get(i), Double.parseDouble(amounts.get(i).getText().toString()), units.get(i));
                }
            }
        }
        mainMenu();
    }
}
