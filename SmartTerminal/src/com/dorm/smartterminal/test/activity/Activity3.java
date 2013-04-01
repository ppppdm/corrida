package com.dorm.smartterminal.test.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.dorm.smartterminal.R;

public class Activity3 extends Activity {

    String TAG = "Activity3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity3);

        Log.v(TAG, "onCreate()");

    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.v(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.v(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.v(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.v(TAG, "onStop()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.v(TAG, "onRestart()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.v(TAG, "onDestroy()");
    }
}
