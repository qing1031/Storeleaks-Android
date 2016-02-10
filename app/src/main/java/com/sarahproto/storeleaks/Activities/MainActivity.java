package com.sarahproto.storeleaks.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.sarahproto.storeleaks.R;

public class MainActivity extends Activity {
    SharedPreferences preferences;
    Double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        final TextView understand_txt = (TextView) findViewById(R.id.understand_txt);
        understand_txt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    understand_txt.setBackgroundResource(R.drawable.start_btn_selected);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    understand_txt.setBackgroundResource(R.drawable.start_btn_normal);

                    Log.d("LoginResult", preferences.getString("LoginResult", ""));

                    if (preferences.getString("LoginResult", "").equals("skip") || preferences.getString("LoginResult", "").equals("")) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();

                    } else {
                        startActivity(new Intent(MainActivity.this, LocationActivity.class));
                        finish();
                    }
                }

                return true;
            }
        });
    }

    // turning off the GPS if its in on state. to avoid the battery drain.
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}