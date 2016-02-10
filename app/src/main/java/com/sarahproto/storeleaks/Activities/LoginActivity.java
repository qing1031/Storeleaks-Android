package com.sarahproto.storeleaks.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.sarahproto.storeleaks.Api.StoreleaksAPIClient;
import com.sarahproto.storeleaks.Api.StoreleaksAPIService;
import com.sarahproto.storeleaks.Location.GPSTracker;
import com.sarahproto.storeleaks.R;
import com.sarahproto.storeleaks.Response.UserLoginResponse;
import com.sarahproto.storeleaks.Social.FacebookLoginActivity;
import com.sarahproto.storeleaks.Social.InstagramLoginActivity;

import java.net.HttpURLConnection;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class LoginActivity extends Activity {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String email, password;
    public static ProgressBar progressBar;
    Button login_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#80DAEB"),
                android.graphics.PorterDuff.Mode.MULTIPLY);
        progressBar.setVisibility(View.GONE);

        final EditText emailEdit = (EditText) findViewById(R.id.emailEdit);
        final EditText passwordEdit = (EditText) findViewById(R.id.passwordEdit);

        final TextView skip_txt = (TextView) findViewById(R.id.skip_txt);
        skip_txt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    editor.putString("LoginResult", "skip");
                    editor.apply();

                    startActivity(new Intent(LoginActivity.this, LocationActivity.class));
                    finish();
                }

                return true;
            }
        });

        login_btn = (Button) findViewById(R.id.loginBtn);
        login_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    login_btn.setBackgroundResource(R.drawable.start_btn_selected);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    login_btn.setBackgroundResource(R.drawable.start_btn_normal);

                    email = emailEdit.getText().toString().trim();
                    password = passwordEdit.getText().toString().trim();

                    String email_pattern = "[a-zA-Z0-9._-]+@[a-z0-9]+\\.+[a-z]+";

                    GPSTracker gpsTracker = new GPSTracker(LoginActivity.this, LoginActivity.this);

                    // Compare the user login info.
                    if (TextUtils.isEmpty(email)) {
                        emailEdit.setError("Please fill out this field.");
                        emailEdit.focusSearch(View.FOCUS_DOWN);

                    } else if (!email.matches(email_pattern)) {
                        emailEdit.setError("Email must be in format:abc@abc.com");
                        emailEdit.focusSearch(View.FOCUS_DOWN);

                    } else if (TextUtils.isEmpty(password)) {
                        passwordEdit.setError("Please fill out this field.");
                        passwordEdit.focusSearch(View.FOCUS_DOWN);


                    } else if (!gpsTracker.isCanGetLocation()) {
                        showSettingsAlert();

                    } else {

                        // ***User Login***
                        loginUser(email, password);
                        login_btn.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }

                return true;
            }
        });

        final Button signup_btn = (Button) findViewById(R.id.signupBtn);
        signup_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    signup_btn.setBackgroundResource(R.drawable.start_btn_selected);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    signup_btn.setBackgroundResource(R.drawable.start_btn_normal);

                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                    finish();
                }

                return true;
            }
        });
    }

    // Show the location setting alert.
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                LoginActivity.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        LoginActivity.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    // Email Login
    public void loginUser(String email, String password) {
        StoreleaksAPIService client = StoreleaksAPIClient.newInstance(StoreleaksAPIService.class);
        Call<UserLoginResponse> call = client.userLogin(email, password);
        call.enqueue(new Callback<UserLoginResponse>() {
            @Override
            public void onResponse(Response<UserLoginResponse> response, Retrofit retrofit) {
                progressBar.setVisibility(View.GONE);
                login_btn.setEnabled(true);

                if (response.code() == HttpURLConnection.HTTP_OK) {
                    Log.d("Response Result", response.body().toString());

                    if (!response.body().getError()) {
                        Log.d("Status", "Login Success");

                        editor.putString("LoginResult", "email");
                        editor.apply();

                        Log.d("user id", response.body().getData().get(0).getId());
                        editor.putInt("UserID", Integer.parseInt(response.body().getData().get(0).getId()));
                        editor.apply();

                        startActivity(new Intent(LoginActivity.this, LocationActivity.class));
                        finish();
                    } else {
                        Log.d("Status", "isn't logged in");
                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }

                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    Log.d("Response Result", "Unauthorized");
                    Toast.makeText(getApplicationContext(), "Unauthorized", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("Status", t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                login_btn.setEnabled(true);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // FaceBook Login
    public void loginFacebook(View view) {
        startActivity(new Intent(LoginActivity.this, FacebookLoginActivity.class));
        finish();
    }

    // Twitter Login
    public void loginTwitter(View view) {
//        startActivity(new Intent(LoginActivity.this, TwitterLoginActivity.class));
//        finish();
    }

    // Instagram Login
    public void loginInstagram(View view) {
        startActivity(new Intent(LoginActivity.this, InstagramLoginActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Call the 'activateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onResume methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onPause methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}