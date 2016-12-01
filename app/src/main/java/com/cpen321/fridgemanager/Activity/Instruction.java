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

    // adding image icons to instruction page
    private ImageView mImageViewCamera;
    private ImageView mImageViewUndo;
    private ImageView mImageViewMenu;
    private ImageView mImageViewFridge;
    private ImageView mImageViewExpire;
    private ImageView mImageViewManual;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_instruction);

        mImageViewCamera = (ImageView) findViewById(R.id.cameraIcon);
        mImageViewUndo = (ImageView) findViewById(R.id.undoIcon);
        mImageViewMenu = (ImageView) findViewById(R.id.menuIcon);
        mImageViewFridge = (ImageView) findViewById(R.id.fridgeIcon);
        mImageViewExpire = (ImageView) findViewById(R.id.expireIcon);
        mImageViewManual = (ImageView) findViewById(R.id.manualIcon);

        mImageViewCamera.setImageResource(R.drawable.ic_camera);
        mImageViewUndo.setImageResource(R.drawable.ic_undo);
        mImageViewMenu.setImageResource(R.drawable.ic_menu);
        mImageViewFridge.setImageResource(R.drawable.ic_food_stock);
        mImageViewExpire.setImageResource(R.drawable.ic_food_to_expire);
        mImageViewManual.setImageResource(R.drawable.ic_plus);

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

