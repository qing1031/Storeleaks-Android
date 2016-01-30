package com.sarahproto.storeleaks.Activities;

import android.content.Intent;
import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mrunia.instagram.sdk.Instagram;
import com.mrunia.instagram.sdk.InstagramAuthConfig;
import com.mrunia.instagram.sdk.InstagramLoginButton;
import com.mrunia.instagram.sdk.InstagramSession;
import com.sarahproto.storeleaks.R;

public class InfoActivity extends Activity {
    InstagramLoginButton instagramLogin;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    String INSTA_CLIENT_ID = "5c79b244f5624229abf66c3a62e8b277";
    String INSTA_CLIENT_SECRET = "f9f0e6ea2d14430aaefcd83e4dabe735";
    String INSTA_CALLBACK_URL = "http://storeleaks.com/";
    String[] INSTA_SCOPES = {"relationships", "likes", "comments"}; // or whatever your scopes are.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InstagramAuthConfig instaAuthConfig = new InstagramAuthConfig(INSTA_CLIENT_ID, INSTA_CLIENT_SECRET, INSTA_CALLBACK_URL, INSTA_SCOPES);
        Instagram.with(this, instaAuthConfig);

        setContentView(R.layout.information_activity);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        TextView about_txt = (TextView) findViewById(R.id.aboutTxt);
        about_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://storeleaks.com/about");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        TextView rate_txt = (TextView) findViewById(R.id.rateTxt);
        rate_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.wStoreleaks");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

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

        startActivity(new Intent(InfoActivity.this, LoginActivity.class));
        finish();
    }

    public void showLocationPage(View view) {
        startActivity(new Intent(InfoActivity.this, LocationActivity.class));
        finish();
    }

    public void showSearchPage(View view) {
        startActivity(new Intent(InfoActivity.this, SearchActivity.class));
        finish();
    }

    public void showCameraPage(View view) {
        startActivity(new Intent(InfoActivity.this, ProfileActivity.class));
        finish();
    }

    public void showInfoPage(View view) {
        startActivity(new Intent(InfoActivity.this, InfoActivity.class));
        finish();
    }
}