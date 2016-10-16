package com.cpen321.fridgemanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import static android.R.id.button1;
import static android.R.id.message;
import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainMenu extends AppCompatActivity {

    private ImageButton imageButton1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        imageButton1 = (ImageButton) findViewById(R.id.imageButton1);
        imageButton1.setMinimumHeight(imageButton1.getMeasuredWidth());

    }

    public void scanReceipt(View view){
        Intent intent = new Intent(this, scanReceipt.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void foodStock(View view) {
        Intent intent = new Intent(this, foodStock.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void expenditures(View view) {
        Intent intent = new Intent(this, expenditures.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void foodToExpire(View view) {
        Intent intent = new Intent(this, foodToExpire.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void freshFood(View view) {
        Intent intent = new Intent(this, freshFood.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

}
