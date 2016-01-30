package com.sarahproto.storeleaks.Social;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.sarahproto.storeleaks.Activities.LocationActivity;
import com.sarahproto.storeleaks.Activities.LoginActivity;
import com.sarahproto.storeleaks.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class FacebookLoginActivity extends Activity {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        setContentView(R.layout.facebook_login_activity);

        showHashKey(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        callbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        List<String> PERMISSIONS = Arrays.asList("email", "user_birthday", "user_status");
        LoginManager.getInstance().logInWithReadPermissions(this, PERMISSIONS);
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.i("Status", "Facebook Logged in..." + loginResult.toString());
                        editor.putString("LoginResult", "facebook");
                        editor.apply();

                        startActivity(new Intent(FacebookLoginActivity.this, LocationActivity.class));
                        finish();
                    }

                    @Override
                    public void onCancel() {
                        Log.i("Status", "Facebook Canceled...");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.i("Status", "Facebook NOT Logged in...");
                    }
                });
    }

    public void loginFacebook(View view) {
        callbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        List<String> PERMISSIONS = Arrays.asList("email", "user_birthday", "user_status");
        LoginManager.getInstance().logInWithReadPermissions(this, PERMISSIONS);
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.i("Status", "Facebook Logged in..." + loginResult.toString());
                        editor.putString("LoginResult", "facebook");
                        editor.apply();

                        startActivity(new Intent(FacebookLoginActivity.this, LocationActivity.class));
                        finish();
                    }

                    @Override
                    public void onCancel() {
                        Log.i("Status", "Facebook Canceled...");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.i("Status", "Instagram NOT Logged in...");
                    }
                });
    }

    public static void showHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    "kr.co.sangcomz.facebooklogin", PackageManager.GET_SIGNATURES); //Your package name here
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException ignored) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(FacebookLoginActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Status", "Resume");

        // Call the 'activateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onResume methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Status", "Pause");

        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onPause methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Status", "Destroy");
    }
}