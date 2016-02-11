package com.sarahproto.storeleaks.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mrunia.instagram.sdk.Instagram;
import com.mrunia.instagram.sdk.InstagramAuthConfig;
import com.mrunia.instagram.sdk.InstagramLoginButton;
import com.mrunia.instagram.sdk.InstagramSession;
import com.sarahproto.storeleaks.Adapters.ImageAdapter;
import com.sarahproto.storeleaks.Api.StoreleaksAPIClient;
import com.sarahproto.storeleaks.Api.StoreleaksAPIService;
import com.sarahproto.storeleaks.Location.AddressJSON;
import com.sarahproto.storeleaks.Location.GPSTracker;
import com.sarahproto.storeleaks.R;
import com.sarahproto.storeleaks.Response.SearchItemResult;
import com.sarahproto.storeleaks.Response.SearchResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class LocationActivity extends Activity {
    GridView gv;
    InstagramLoginButton instagramLogin;
    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;

    EditText locationSearchEdt;

    public String URL_PREFIX = "http://storeleaks.com/img/items/";
    public List<SearchItemResult> resultData;

    String INSTA_CLIENT_ID = "5c79b244f5624229abf66c3a62e8b277";
    String INSTA_CLIENT_SECRET = "f9f0e6ea2d14430aaefcd83e4dabe735";
    String INSTA_CALLBACK_URL = "http://storeleaks.com/";
    String[] INSTA_SCOPES = {"relationships", "likes", "comments"}; // or whatever your scopes are.
    String TAG = "LocationActivity";
    String cityName, countryName;

    public static ProgressBar progressBar;
    public static Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        InstagramAuthConfig instaAuthConfig = new InstagramAuthConfig(INSTA_CLIENT_ID, INSTA_CLIENT_SECRET, INSTA_CALLBACK_URL, INSTA_SCOPES);
        Instagram.with(this, instaAuthConfig);

        setContentView(R.layout.location_activity);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        gv = (GridView) findViewById(R.id.grid_search);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        cityName = preferences.getString("CurrentLocationCity", "");
        countryName = preferences.getString("CurrentLocationCountry", "");

        TextView logout_txt = (TextView) findViewById(R.id.logout_btn);
        logout_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                LoginManager.getInstance().logOut();
                logoutProfile();
            }
        });

        locationSearchEdt = (EditText) findViewById(R.id.locationSearchEdit);
        locationSearchEdt.setText("");
        locationSearchEdt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    if (event.getRawX() <=
                            (locationSearchEdt.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width() * 1.5)) {

                        Log.d(TAG, "Search the things.");
                        startSearching();

                        return true;
                    }
                }

                return false;
            }
        });
        locationSearchEdt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                        case KeyEvent.KEYCODE_SEARCH:
                            startSearching();

                            return true;

                        default:
                            break;
                    }
                }

                return false;
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

        Log.d(TAG, "Current Location - " + cityName + ", " + countryName);

        if (preferences.getString("LoginResult", "").equals("skip")) {
            logout_txt.setVisibility(View.GONE);
            instagramLogin.setVisibility(View.GONE);

            if (cityName.equals("")) {
                getLocation();          // Get the address

            } else {
                String locationAddress = cityName + ", " + countryName;
                locationSearchEdt.setText(locationAddress);
            }

        } else if (preferences.getString("LoginResult", "").equals("facebook")
                || preferences.getString("LoginResult", "").equals("twitter")
                || preferences.getString("LoginResult", "").equals("email")) {
            logout_txt.setVisibility(View.VISIBLE);
            instagramLogin.setVisibility(View.GONE);


            if (cityName.equals("")) {
                getLocation();          // Get the address

            } else {
                String locationAddress = cityName + ", " + countryName;
                locationSearchEdt.setText(locationAddress);
            }


        } else if (preferences.getString("LoginResult", "").equals("instagram")) {
            logout_txt.setVisibility(View.GONE);
            instagramLogin.setVisibility(View.VISIBLE);


            if (cityName.equals("")) {
                getLocation();          // Get the address

            } else {
                String locationAddress = cityName + ", " + countryName;
                locationSearchEdt.setText(locationAddress);
            }
        }
    }

    public void startSearching() {

        String locationAddress = cityName + ", " + countryName;
        if (locationSearchEdt.getText().toString().isEmpty()) {
            locationSearchEdt.setError("Plese fill out this field.");

        } else if (locationSearchEdt.getText().toString() == locationAddress) {
            progressBar.setVisibility(View.VISIBLE);
            searchItems(countryName, cityName, 0, 30);

        } else {
            progressBar.setVisibility(View.VISIBLE);
            searchItems("", locationSearchEdt.getText().toString(), 0, 30);
        }
    }

    public void logoutProfile() {
        editor.putString("LoginResult", "skip");
        editor.putString("CurrentLocationCity", "");
        editor.putString("CurrentLocationCountry", "");
        editor.apply();

        startActivity(new Intent(LocationActivity.this, LoginActivity.class));
        finish();
    }

    public void showLocationPage(View view) {
        startActivity(new Intent(LocationActivity.this, LocationActivity.class));
        finish();
    }

    public void showSearchPage(View view) {
        startActivity(new Intent(LocationActivity.this, SearchActivity.class));
        finish();
    }

    public void showCameraPage(View view) {
        startActivity(new Intent(LocationActivity.this, ProfileActivity.class));
        finish();
    }

    public void showInfoPage(View view) {
        startActivity(new Intent(LocationActivity.this, InfoActivity.class));
        finish();
    }

    // Request the search items from location info.
    public void searchItems(String country, String city, int start_pos, int amount) {
        StoreleaksAPIService client = StoreleaksAPIClient.newInstance(StoreleaksAPIService.class);
        Call<SearchResponse> call = client.getListItem(country, city, start_pos, amount);
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Response<SearchResponse> response, Retrofit retrofit) {

                if (response.code() == HttpURLConnection.HTTP_OK) {

                    Log.d("Response Result", response.body().toString());

                    if (!response.body().getError()) {
                        Log.d("Status", "Search Success");

                        String[] imageUrls = new String[response.body().getData().size()];
                        resultData = response.body().getData();

                        Log.d("Items size", String.valueOf(imageUrls.length));

                        int amount = response.body().getData().size();
                        for (int i = 0; i < amount; i++) {
                            imageUrls[i] = URL_PREFIX + response.body().getData().get(i).getImages();
                        }

                        Log.d("ImageUrl Result", Arrays.toString(imageUrls));

                        // Get the images(bitmap) for the url result array.
                        GetXMLTask getXMLTask = new GetXMLTask();
                        getXMLTask.execute(imageUrls);

                    } else {
                        Log.d("Status", "No item");
                        Toast.makeText(getApplicationContext(), "Could not search items", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    Log.d("Status", String.valueOf(response.code()));
                    Toast.makeText(getApplicationContext(), "Unauthorized", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("Status", "Failure");
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // Get the bitmap from image url. and share the image
    private class GetXMLTask extends AsyncTask<String, Void, Bitmap[]> {
        @Override
        protected Bitmap[] doInBackground(String... urls) {
            Bitmap[] map = new Bitmap[urls.length];
            for (int i = 0; i < urls.length; i++) {
                map[i] = downloadImage(urls[i]);
            }
            return map;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Bitmap[] result) {
            // Set the imageview from the url here.
            Log.d("Result", "Got one image");
            Log.d("Bitmap Result", Arrays.toString(result));

            gv.setAdapter(new ImageAdapter(LocationActivity.this, result, resultData));

            progressBar.setVisibility(View.GONE);
        }

        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 4;

            try {
                stream = getHttpConnection(url);
                bitmap = BitmapFactory.
                        decodeStream(stream, null, bmOptions);
                stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }
    }

    private void getLocation() {

        GPSTracker gpsTracker = new GPSTracker(this, this);

        if (gpsTracker.isCanGetLocation()) {
            progressBar.setVisibility(View.VISIBLE);
            new AddressJSON(locationSearchEdt).execute(gpsTracker.getLatitude(), gpsTracker.getLongitude());

        } else {
            showSettingsAlert();
        }
    }

    // Show the location setting alert.
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                LocationActivity.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        LocationActivity.this.startActivity(intent);
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
}