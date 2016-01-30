package com.sarahproto.storeleaks.Social;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.facebook.appevents.AppEventsLogger;
import com.sarahproto.storeleaks.Activities.LoginActivity;
import com.sarahproto.storeleaks.R;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import io.fabric.sdk.android.Fabric;
import retrofit.Response;
import retrofit.Retrofit;

public class TwitterLoginActivity extends Activity {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "gxiUjlmrXmXCo1DsLYMKhrpIf";
    private static final String TWITTER_SECRET = "EH651p2REGMV0CNq5PiiLucbSb7hHO3Oql8YEtwoymeNH3SQG4";

    private TwitterLoginButton twitterLoginButton;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
//        Fabric.with(this, new Twitter(authConfig), new Crashlytics());

        setContentView(R.layout.twitter_login_activity);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void onResponse(Response<TwitterSession> response, Retrofit retrofit) {

            }

            @Override
            public void onFailure(Throwable t) {

            }

            @Override
            public void success(Result<TwitterSession> result) {
                Log.i("Status", "Twitter Logged in..." + result.toString());
                editor.putString("LoginResult", "twitter");
                editor.apply();
            }

            @Override
            public void failure(TwitterException e) {
                // Do something on failure
                Log.i("Status", "Twitter NOT Logged in...");
                startActivity(new Intent(TwitterLoginActivity.this, LoginActivity.class));
                finish();
            }
        });

        twitterLoginButton.performClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TwitterLoginActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Status", "Resume");

        finish();

        // Call the 'activateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onResume methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Status", "Pause");
        finish();

        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onPause methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Status", "Destroy");
        finish();
    }
}