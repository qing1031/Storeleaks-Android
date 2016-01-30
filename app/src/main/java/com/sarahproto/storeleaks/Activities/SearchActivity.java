package com.sarahproto.storeleaks.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.mrunia.instagram.sdk.Instagram;
import com.mrunia.instagram.sdk.InstagramAuthConfig;
import com.mrunia.instagram.sdk.InstagramLoginButton;
import com.mrunia.instagram.sdk.InstagramSession;
import com.sarahproto.storeleaks.Adapters.ImageAdapter;
import com.sarahproto.storeleaks.R;

public class SearchActivity extends Activity {
    GridView gv;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    InstagramLoginButton instagramLogin;
    EditText nameSearchEdit, locSearchEdit;

    public static String[] imageNameList = {
            "Shopping 1", "Shopping 2",
            "Shopping 3", "Shopping 4",
            "Shopping 5", "Shopping 6",
            "Shopping 7"
    };
    public static int[] images = {
            R.drawable.shop_1, R.drawable.shop_2,
            R.drawable.shop_3, R.drawable.shop_4,
            R.drawable.shop_5, R.drawable.shop_6,
            R.drawable.shop_7
    };

    String INSTA_CLIENT_ID = "5c79b244f5624229abf66c3a62e8b277";
    String INSTA_CLIENT_SECRET = "f9f0e6ea2d14430aaefcd83e4dabe735";
    String INSTA_CALLBACK_URL = "http://storeleaks.com/";
    String[] INSTA_SCOPES = {"relationships", "likes", "comments"}; // or whatever your scopes are.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InstagramAuthConfig instaAuthConfig = new InstagramAuthConfig(INSTA_CLIENT_ID, INSTA_CLIENT_SECRET, INSTA_CALLBACK_URL, INSTA_SCOPES);
        Instagram.with(this, instaAuthConfig);

        setContentView(R.layout.search_activity);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        gv = (GridView) findViewById(R.id.grid_search);
        gv.setAdapter(new ImageAdapter(this, imageNameList, images));

        TextView logout_txt = (TextView) findViewById(R.id.logout_btn);
        logout_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                LoginManager.getInstance().logOut();
                logoutProfile();
            }
        });

        instagramLogin = (InstagramLoginButton) findViewById(R.id.instagram_login_button);
        instagramLogin.setCallback(new com.mrunia.instagram.sdk.Callback<InstagramSession>() {
            @Override
            public void success(InstagramSession result) {
                // optional: put your own code here
                Log.i("Status", "Instagram Logged in..." + result.toString());
            }

            @Override
            public void failure(Exception e) {
                // optional: put your own code here
                Log.i("Status", "Instagram NOT Logged in...");
            }

            @Override
            public void logout() {
                // optional: put your own code here
                Log.i("Status", "Instagram User was logged out...");

                logoutProfile();
            }
        });

        nameSearchEdit = (EditText)findViewById(R.id.nameSearchEdit);
        nameSearchEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() <= (nameSearchEdit.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width()*1.5))  {
                        Log.d("Clicked", "Name Search edittext");

                        return true;
                    }
                }

                return false;
            }
        });

        locSearchEdit = (EditText)findViewById(R.id.locationSearchEdit);
        locSearchEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final  int DRAWABLE_LEFT = 0;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() <= (nameSearchEdit.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width()*1.5))  {
                        Log.d("Clicked", "Location search edittext");

                        return true;
                    }
                }

                return false;
            }
        });


        if (preferences.getString("LoginResult", "").equals("skip")) {
            logout_txt.setVisibility(View.GONE);
            instagramLogin.setVisibility(View.GONE);

        } else if (preferences.getString("LoginResult", "").equals("facebook")
                || preferences.getString("LoginResult", "").equals("twitter")) {
            logout_txt.setVisibility(View.VISIBLE);
            instagramLogin.setVisibility(View.GONE);

        } else if (preferences.getString("LoginResult", "").equals("instagram")) {
            logout_txt.setVisibility(View.GONE);
            instagramLogin.setVisibility(View.VISIBLE);
        }
    }

    public void logoutProfile() {
        editor.putString("LoginResult", "skip");
        editor.apply();

        startActivity(new Intent(SearchActivity.this, LoginActivity.class));
        finish();
    }

    public void showLocationPage(View view) {
        startActivity(new Intent(SearchActivity.this, LocationActivity.class));
        finish();
    }

    public void showSearchPage(View view) {
        startActivity(new Intent(SearchActivity.this, SearchActivity.class));
        finish();
    }

    public void showCameraPage(View view) {
        startActivity(new Intent(SearchActivity.this, ProfileActivity.class));
        finish();
    }

    public void showInfoPage(View view) {
        startActivity(new Intent(SearchActivity.this, InfoActivity.class));
        finish();
    }
}