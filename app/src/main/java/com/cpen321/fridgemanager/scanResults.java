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

    private ArrayList<String> texts;

    TableLayout mTlayout;
    private ArrayList<TableRow> trs;

    // A Database Interaction Object
    private TextRecognitionInteraction ti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);

        ti = new TextRecognitionInteraction(getApplicationContext());
        mTlayout = (TableLayout) findViewById(R.id.mTlayout);

        initializeScreen();
    }

    private void initializeScreen() {
        texts = getIntent().getStringArrayListExtra("texts");
        trs = new ArrayList<TableRow>();
        String name = "";
        String unit = "";
        int current_row = 0;

        for(int i = 0; i < texts.size(); i++) {
            name = texts.get(i);
            unit = ti.getUnit(name);
            if (unit != null) {
                trs.add(new TableRow(this));

                mTlayout.addView(trs.get(current_row));


                EditText amount = new EditText(this);
                ImageButton btn_del = new ImageButton(this);
                TextView food_name = new TextView(this);
                TextView unit_name = new TextView(this);

                // Create amount field
                TableRow.LayoutParams amountLayoutParams = new TableRow.LayoutParams();
                amount.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4) });
                amount.setMaxLines(1);
                amount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                float measure = amount.getPaint().measureText("9999");
                amount.setWidth(amount.getPaddingLeft() + amount.getPaddingRight() + (int) measure);
                trs.get(current_row).addView(amount);

                // Create unit text
                unit_name.setText(unit);
                unit_name.setId(i);
                unit_name.setGravity(Gravity.CENTER_VERTICAL);
                TableRow.LayoutParams trLayoutParams_unit = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                unit_name.setLayoutParams(trLayoutParams_unit);
                trs.get(current_row).addView(unit_name);

                //Create delete button
                btn_del.setImageResource(R.drawable.ic_trash);
                btn_del.setId(current_row);
                btn_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO DELETE
                        int index = v.getId();
                        texts.remove(index);
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
        for(int i = 0; i < texts.size(); i++) {
            ti.addFoodToStorage(texts.get(i));
        }
        mainMenu();
    }
}
