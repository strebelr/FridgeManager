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
    TableRow tr;

    // A Database Interaction Object
    private TextRecognitionInteraction ti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);

        ti = new TextRecognitionInteraction(getApplicationContext());
        mTlayout = (TableLayout) findViewById(R.id.mTlayout);

        texts = getIntent().getStringArrayListExtra("texts");
        String name = "";


        for(int i = 0; i < texts.size(); i++) {
            name = texts.get(i);
            if (ti.isFood(name)) {
                tr = new TableRow(this);
                mTlayout.addView(tr);

                EditText amount = new EditText(this);
                ImageButton btn_del = new ImageButton(this);
                TextView food_name = new TextView(this);

                //Create amount field
                TableRow.LayoutParams amountLayoutParams = new TableRow.LayoutParams();
                amount.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4) });
                amount.setMaxLines(1);
                amount.setInputType(InputType.TYPE_CLASS_NUMBER);
                amount.setWidth(R.dimen.max_char);
                tr.addView(amount);

                //Create delete button
                btn_del.setImageResource(R.drawable.ic_trash);
                btn_del.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO DELETE
                        System.out.println("v.getid is:- " + v.getId());
                    }
                });
                tr.addView(btn_del);

                //Create food text
                food_name.setText(name);
                food_name.setId(i);
                food_name.setGravity(Gravity.CENTER_VERTICAL);
                TableRow.LayoutParams trLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                trLayoutParams.weight = 1;
                food_name.setLayoutParams(trLayoutParams);
                tr.addView(food_name,0);



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
