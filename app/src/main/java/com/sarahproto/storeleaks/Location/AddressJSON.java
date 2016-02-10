package com.sarahproto.storeleaks.Location;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sarahproto.storeleaks.Activities.LocationActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddressJSON extends AsyncTask<Double, Void, String> {

    private TextView address;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public AddressJSON(EditText address) {
        this.address = address;
    }


    @Override
    protected String doInBackground(Double... params) {
        double latitude = params[0];
        double longitude = params[1];
        String urlJson = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&sensor=true";

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String response = null;
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(urlJson);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }
            response = buffer.toString();
            Log.v("AMCC", response);
        } catch (IOException e) {
            Log.e("AMCC", "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("AMCC", "Error closing stream", e);
                }
            }
        }

        if (response != null) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                jsonObject = jsonArray.getJSONObject(0);

                Log.d("Location Result", jsonObject.getString("formatted_address"));

                JSONArray address_components = jsonObject.getJSONArray("address_components");

                preferences = PreferenceManager.getDefaultSharedPreferences(LocationActivity.context);
                editor = preferences.edit();

                for (int i = 0; i < address_components.length(); i++) {
                    JSONObject zero = address_components.getJSONObject(i);
                    String long_name = zero.getString("long_name");
                    JSONArray mtypes = zero.getJSONArray("types");
                    String Type = mtypes.getString(0);

                    if (!TextUtils.isEmpty(long_name) || !long_name.equals("")) {
                        if (Type.equalsIgnoreCase("street_number")) {
                            Log.d("Address1 ", long_name);

                        } else if (Type.equalsIgnoreCase("route")) {
                            Log.d("Route", long_name);

                        } else if (Type.equalsIgnoreCase("sublocality")) {
                            Log.d("sublocality", long_name);

                        } else if (Type.equalsIgnoreCase("locality")) {
                            Log.d("City", long_name);
                            result.append(long_name).append(", ");

                            editor.putString("CurrentLocationCity", long_name);
                            editor.apply();

                        } else if (Type.equalsIgnoreCase("administrative_area_level_2")) {
                            Log.d("area_2", long_name);

                        } else if (Type.equalsIgnoreCase("administrative_area_level_1")) {
                            Log.d("area_1", long_name);

                        } else if (Type.equalsIgnoreCase("country")) {
                            Log.d("country", long_name);
                            result.append(long_name);

                            editor.putString("CurrentLocationCountry", long_name);
                            editor.apply();

                        } else if (Type.equalsIgnoreCase("postal_code")) {
                            Log.d("postal_code", long_name);
                        }
                    }
                }

            } catch (Exception e) {
                Log.e("AMCC", e.getMessage());
            }
        }

        return result.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        address.setText(s);
        LocationActivity.progressBar.setVisibility(View.GONE);
    }
}
