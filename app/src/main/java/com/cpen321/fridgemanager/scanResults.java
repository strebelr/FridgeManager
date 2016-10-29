package com.cpen321.fridgemanager;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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

                Button btn_add = new Button(this);
                ImageButton btn_del = new ImageButton(this);
                TextView food_name = new TextView(this);

                //Create add button
                btn_add.setText(getResources().getString(R.string.add_one_item));
                tr.addView(btn_add);

                //Create delete button
                btn_del.setImageResource(R.drawable.ic_trash);
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
