package com.cpen321.fridgemanager.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.cpen321.fridgemanager.R;

import static android.R.id.message;
import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static com.cpen321.fridgemanager.R.layout.activity_instruction;

/**
 * Created by jefflol on 2016-11-15.
 */

public class Instruction extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_instruction);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mainMenu();
            return true;
        } else {
            return false;
        }
    }

    public void mainMenu() {
        Intent intent = new Intent(this, MainMenu.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }



}

