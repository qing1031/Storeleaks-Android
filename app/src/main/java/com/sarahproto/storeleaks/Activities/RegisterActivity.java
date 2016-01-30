package com.sarahproto.storeleaks.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.sarahproto.storeleaks.R;
import com.sarahproto.storeleaks.Response.UserRegisterResponse;

import java.net.HttpURLConnection;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class RegisterActivity extends Activity {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String userName, email, password;
    public static ProgressBar progressBar;
    EditText userNameEdit, emailEdit, passwordEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#80DAEB"),
                android.graphics.PorterDuff.Mode.MULTIPLY);
        progressBar.setVisibility(View.GONE);

        userNameEdit = (EditText) findViewById(R.id.userNameEdit);
        emailEdit = (EditText) findViewById(R.id.emailEdit);
        passwordEdit = (EditText) findViewById(R.id.passwordEdit);

        final TextView skip_txt = (TextView) findViewById(R.id.skip_txt);
        skip_txt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    editor.putString("LoginResult", "skip");
                    editor.apply();

                    startActivity(new Intent(RegisterActivity.this, LocationActivity.class));
                    finish();
                }

                return true;
            }
        });

        final Button login_btn = (Button) findViewById(R.id.loginBtn);
        login_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    login_btn.setBackgroundResource(R.drawable.start_btn_selected);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    login_btn.setBackgroundResource(R.drawable.start_btn_normal);

                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
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

                    userName = userNameEdit.getText().toString().trim();
                    email = emailEdit.getText().toString().trim();
                    password = passwordEdit.getText().toString().trim();

                    String email_pattern = "[a-zA-Z0-9._-]+@[a-z0-9]+\\.+[a-z]+";

                    // Compare user signup info.
                    if (TextUtils.isEmpty(userName)) {
                        userNameEdit.setError("Please fill out this field.");
                        userNameEdit.focusSearch(View.FOCUS_DOWN);

                    } else if (TextUtils.isEmpty(email)) {
                        emailEdit.setError("Please fill out this field.");
                        emailEdit.focusSearch(View.FOCUS_DOWN);

                    } else if (!email.matches(email_pattern)) {
                        emailEdit.setError("Email must be in format:abc@abc.com");
                        emailEdit.focusSearch(View.FOCUS_DOWN);

                    } else if (TextUtils.isEmpty(password)) {
                        passwordEdit.setError("Please fill out this field.");
                        passwordEdit.focusSearch(View.FOCUS_DOWN);

                    } else {
                        // register the account with user infos.
                        userRegister(userName, email, password);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }

                return true;
            }
        });
    }

    private void userRegister(String name, final String email, String password) {

        StoreleaksAPIService client = StoreleaksAPIClient.newInstance(StoreleaksAPIService.class);
        Call<UserRegisterResponse> call = client.userRegister(name, email, password);
        call.enqueue(new Callback<UserRegisterResponse>() {
            @Override
            public void onResponse(Response<UserRegisterResponse> response, Retrofit retrofit) {
                progressBar.setVisibility(View.GONE);

                if (response.code() == HttpURLConnection.HTTP_OK) {

                    Log.d("Status", response.body().getMessage());

                    if (response.body().getError()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setTitle("Register Error");
                        builder.setMessage(response.body().getMessage());

                        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                userNameEdit.setText("");
                                emailEdit.setText("");
                                passwordEdit.setText("");
                                userNameEdit.focusSearch(View.FOCUS_DOWN);

                                dialogInterface.dismiss();
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Success Register", Toast.LENGTH_LONG).show();

                        startActivity(new Intent(RegisterActivity.this, LocationActivity.class));
                        finish();
                    }

                } else if (response.code() == HttpURLConnection.HTTP_CREATED) {
                    Log.d("Response Result", response.body().toString());

                    if (!response.body().getError()) {
                        Toast.makeText(getApplicationContext(), "Success Register", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegisterActivity.this, LocationActivity.class));
                        finish();
                    }

                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    Log.d("Response Result", "Unauthorized");
                    Toast.makeText(getApplicationContext(), "Unauthorized", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("Status", t.getMessage());
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
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