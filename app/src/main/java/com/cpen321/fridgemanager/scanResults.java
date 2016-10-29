package com.cpen321.fridgemanager;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static android.R.id.message;
import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class scanResults extends AppCompatActivity {

    private ArrayList<String> texts;


    // A Database Interaction Object
    private TextRecognitionInteraction ti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);

        ti = new TextRecognitionInteraction(getApplicationContext());

        final TextView textViewToChange = (TextView) findViewById(R.id.output);
        texts = getIntent().getStringArrayListExtra("texts");
        String display = "";
        String name = "";
        Button myButton = null;
        Button oldButton = null;
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.scan_result);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        for(int i = 0; i < texts.size(); i++) {
            myButton = null;
            name = texts.get(i);
            if (ti.isFood(name)) {
                //display = display + texts.get(i) + "\n";
                myButton = new Button(this);
                myButton.setText(name);
                myButton.setId(i);
                if(oldButton != null)
                    lp.addRule(RelativeLayout.BELOW, oldButton.getId());
                rl.addView(myButton, lp);
                oldButton = myButton;
            }
        }
        textViewToChange.setText(display);
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
