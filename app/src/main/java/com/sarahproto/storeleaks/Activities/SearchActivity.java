package com.sarahproto.storeleaks.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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

public class SearchActivity extends Activity {
    GridView gv;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    InstagramLoginButton instagramLogin;
    EditText nameSearchEdit, locSearchEdit;
    ProgressBar progressBar;

    public String[] imageUrls;
    public String[] imageNames;
    public String URL_PREFIX = "http://storeleaks.com/img/items/";
    public List<SearchItemResult> resultData;

    String INSTA_CLIENT_ID = "5c79b244f5624229abf66c3a62e8b277";
    String INSTA_CLIENT_SECRET = "f9f0e6ea2d14430aaefcd83e4dabe735";
    String INSTA_CALLBACK_URL = "http://storeleaks.com/";
    String[] INSTA_SCOPES = {"relationships", "likes", "comments" }; // or whatever your scopes are.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InstagramAuthConfig instaAuthConfig = new InstagramAuthConfig(INSTA_CLIENT_ID, INSTA_CLIENT_SECRET, INSTA_CALLBACK_URL, INSTA_SCOPES);
        Instagram.with(this, instaAuthConfig);

        setContentView(R.layout.search_activity);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        gv = (GridView) findViewById(R.id.grid_search);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

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

        nameSearchEdit = (EditText) findViewById(R.id.nameSearchEdit);
        locSearchEdit = (EditText) findViewById(R.id.locationSearchEdit);

        nameSearchEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    if (event.getRawX() <=
                            (nameSearchEdit.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width() * 1.5)) {

                        Log.d("Clicked", "Name Search edittext");

                        if (nameSearchEdit.getText().toString().trim().isEmpty()) {
                            nameSearchEdit.setError("Please fill out this field");
                            nameSearchEdit.focusSearch(View.FOCUS_DOWN);

                        } else {
                            startSearching();
                        }

                        return true;
                    }
                }

                return false;
            }
        });

        locSearchEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    if (event.getRawX() <= (nameSearchEdit.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width() * 1.5)) {
                        Log.d("Clicked", "Location search edittext");

                        if (locSearchEdit.getText().toString().trim().isEmpty()) {
                            locSearchEdit.setError("Please fill out this field");
                            locSearchEdit.focusSearch(View.FOCUS_DOWN);

                        } else {
                            startSearching();
                        }

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

    // Start the searching.
    public void startSearching() {

        searchItems(nameSearchEdit.getText().toString(),
                locSearchEdit.getText().toString(),
                0, 20);

        progressBar.setVisibility(View.VISIBLE);
        nameSearchEdit.setEnabled(false);
        locSearchEdit.setEnabled(false);
    }

    // finish the searching.
    public void finishSearching() {
        progressBar.setVisibility(View.GONE);
        nameSearchEdit.setEnabled(true);
        locSearchEdit.setEnabled(true);
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

    // Items Search Request.
    public void searchItems(String name, String countryName, int start_pos, int amount) {
        StoreleaksAPIService client = StoreleaksAPIClient.newInstance(StoreleaksAPIService.class);
        Call<SearchResponse> call = client.searchItem(name, countryName, start_pos, amount);
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Response<SearchResponse> response, Retrofit retrofit) {
                if (response.code() == HttpURLConnection.HTTP_OK) {

                    imageUrls = new String[response.body().getData().size()];
                    imageNames = new String[response.body().getData().size()];

                    resultData = response.body().getData();

                    Log.d("Response Result", response.body().toString());
                    Log.d("Items size", String.valueOf(imageUrls.length));

                    if (!response.body().getError()) {
                        Log.d("Status", "Search Success");

                        int amount = response.body().getData().size();
                        for (int i = 0; i < amount; i++) {
                            imageUrls[i] = URL_PREFIX + response.body().getData().get(i).getImages();
                            imageNames[i] = response.body().getData().get(i).getName();
                        }

                        Log.d("ImageUrl Result", Arrays.toString(imageUrls));
                        Log.d("ImageNames Result", Arrays.toString(imageNames));

                        // Get the images(bitmap) for the url result array.
                        GetXMLTask getXMLTask = new GetXMLTask();
                        getXMLTask.execute(imageUrls);

                    } else {
                        Log.d("Status", "No item");
                        Toast.makeText(getApplicationContext(), "Could not search items", Toast.LENGTH_LONG).show();
                    }

                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    Log.d("Response Result", "Unauthorized");
                    Toast.makeText(getApplicationContext(), "Unauthorized", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("Status", "Failure");
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();
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

            gv.setAdapter(new ImageAdapter(SearchActivity.this, result, resultData));

            finishSearching();
        }

        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

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
}