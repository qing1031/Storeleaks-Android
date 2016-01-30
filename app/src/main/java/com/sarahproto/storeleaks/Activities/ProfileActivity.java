package com.sarahproto.storeleaks.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mrunia.instagram.sdk.Instagram;
import com.mrunia.instagram.sdk.InstagramAuthConfig;
import com.mrunia.instagram.sdk.InstagramLoginButton;
import com.mrunia.instagram.sdk.InstagramSession;
import com.sarahproto.storeleaks.Api.StoreleaksAPIClient;
import com.sarahproto.storeleaks.Api.StoreleaksAPIService;
import com.sarahproto.storeleaks.Image.UserPicture;
import com.sarahproto.storeleaks.R;
import com.sarahproto.storeleaks.Response.AddItemResponse;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ProfileActivity extends Activity {

    private static final int ACTIVITY_START_CAMERA_APP = 0;
    private static final int SELECT_PICTURE = 1;
    public Camera mCamera;
    final Context context = this;

    private ImageView mPhotoCapturedImageView;
    private String mImageFileLocation = "";
    public static final String IMAGE_TYPE = "image/*";
    Uri selectedImageUri;
    File library_img;
    File profile_img;
    TextView cancel_txt, done_txt;
    LinearLayout photoGetLayout, profileLayout;
    ProgressBar progressBar;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    InstagramLoginButton instagramLogin;

    EditText name_edt, shopname_edt, location_edt, detailed_location_edt, description_edt;
    String name, shop, location, location_more, description;
    String filePath;

    String INSTA_CLIENT_ID = "5c79b244f5624229abf66c3a62e8b277";
    String INSTA_CLIENT_SECRET = "f9f0e6ea2d14430aaefcd83e4dabe735";
    String INSTA_CALLBACK_URL = "http://storeleaks.com/";
    String[] INSTA_SCOPES = {"relationships", "likes", "comments"}; // or whatever your scopes are.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InstagramAuthConfig instaAuthConfig = new InstagramAuthConfig(INSTA_CLIENT_ID, INSTA_CLIENT_SECRET, INSTA_CALLBACK_URL, INSTA_SCOPES);
        Instagram.with(this, instaAuthConfig);

        setContentView(R.layout.profile_activity);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        filePath = "";

        mPhotoCapturedImageView = (ImageView) findViewById(R.id.capturePhotoImageView);

        photoGetLayout = (LinearLayout) findViewById(R.id.photoGetLayout);
        photoGetLayout.setVisibility(View.VISIBLE);

        profileLayout = (LinearLayout) findViewById(R.id.profileEditLayout);
        profileLayout.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        name_edt = (EditText) findViewById(R.id.nameEdt);
        shopname_edt = (EditText) findViewById(R.id.shopNameEdt);
        location_edt = (EditText) findViewById(R.id.locationEdt);
        detailed_location_edt = (EditText) findViewById(R.id.detLocationEdt);
        description_edt = (EditText) findViewById(R.id.descriptionEdt);

        cancel_txt = (TextView) findViewById(R.id.cancelTxt);
        cancel_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset the layout and field.
                cancelProfileEdit();
            }
        });

        done_txt = (TextView) findViewById(R.id.doneTxt);
        done_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = name_edt.getText().toString().trim();
                shop = shopname_edt.getText().toString().trim();
                location = location_edt.getText().toString().trim();
                location_more = detailed_location_edt.getText().toString().trim();
                description = description_edt.getText().toString().trim();
                String emptyError = "Please fill out this field";

                Log.d("Imageview Result", String.valueOf(mPhotoCapturedImageView.getBackground()));

                if (mPhotoCapturedImageView.getBackground() == null) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileActivity.this);
                    alertDialog.setMessage("Please fill out the image");
                    alertDialog.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

                } else if (name.isEmpty()) {
                    name_edt.setError(emptyError);
                    name_edt.focusSearch(View.FOCUS_DOWN);

                } else if (shop.isEmpty()) {
                    shopname_edt.setError(emptyError);
                    shopname_edt.focusSearch(View.FOCUS_DOWN);

                } else if (location.isEmpty()) {
                    location_edt.setError(emptyError);
                    location_edt.focusSearch(View.FOCUS_DOWN);

                } else if (location_more.isEmpty()) {
                    detailed_location_edt.setError(emptyError);
                    detailed_location_edt.focusSearch(View.FOCUS_DOWN);

                } else if (description.isEmpty()) {
                    description_edt.setError(emptyError);
                    description_edt.focusSearch(View.FOCUS_DOWN);

                } else {
                    int userId = preferences.getInt("UserID", 0);
                    int amount = 10;

                    Log.d("audio file path : ", filePath);
                    File imgFile = new File(filePath);
                    RequestBody imgBody = RequestBody.create(MediaType.parse("image/png"), imgFile);

                    // Add the edited item.
                    addOneItem(userId, imgBody, amount, name, shop, location, location_more, description);
                    progressBar.setVisibility(View.VISIBLE);
                }

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

    private void addOneItem(int id, RequestBody imgFile, final int amount, String name, final String shop,
                            String lcoation, String location_more, String description) {
        StoreleaksAPIService client = StoreleaksAPIClient.newInstance(StoreleaksAPIService.class);
        Call<AddItemResponse> call = client.addItem(id, imgFile, amount, name, shop, lcoation, location_more, description);
        call.enqueue(new Callback<AddItemResponse>() {
            @Override
            public void onResponse(Response<AddItemResponse> response, Retrofit retrofit) {
                progressBar.setVisibility(View.GONE);

                if (response.code() == HttpURLConnection.HTTP_OK) {
                    Log.d("Status", "Success");
                    Log.d("Response Result", response.body().toString());
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();

                    cancelProfileEdit();        // Reset the layout and views.

                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    Log.d("Response Result", "Unauthorized");
                    Toast.makeText(getApplicationContext(), "Unauthorized", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("Status", t.getMessage());
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Reset the layout and views.
    public void cancelProfileEdit() {
        name_edt.setText("");
        shopname_edt.setText("");
        location_edt.setText("");
        detailed_location_edt.setText("");
        description_edt.setText("");
        mPhotoCapturedImageView.setImageResource(0);

        profileLayout.setVisibility(View.GONE);
        photoGetLayout.setVisibility(View.VISIBLE);
    }

    public void logoutProfile() {
        editor.putString("LoginResult", "skip");
        editor.apply();

        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
        finish();
    }

    public void showLocationPage(View view) {
        startActivity(new Intent(ProfileActivity.this, LocationActivity.class));
        finish();
    }

    public void showSearchPage(View view) {
        startActivity(new Intent(ProfileActivity.this, SearchActivity.class));
        finish();
    }

    public void showCameraPage(View view) {
        startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
        finish();
    }

    public void showInfoPage(View view) {
        startActivity(new Intent(ProfileActivity.this, InfoActivity.class));
        finish();
    }

    // Select the photo from gallery
    public void selectPhoto(View view) {
        Intent intent = new Intent();
        intent.setType(IMAGE_TYPE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                getString(R.string.select_picture)), SELECT_PICTURE);
    }


    // Take Photo ( Camera)
    public void takePhoto(View view) {
        Intent callCameraApplicationIntent = new Intent();
        callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createImageFile();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set the camera result size(width and height)
//        setCameraSize();

        callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

        startActivityForResult(callCameraApplicationIntent, ACTIVITY_START_CAMERA_APP);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == ACTIVITY_START_CAMERA_APP) {

                setReducedImageSize();

            } else if (requestCode == SELECT_PICTURE) {

                setSelectedImage(data);

            }
        }
    }

    private void setSelectedImage(Intent data) {
        selectedImageUri = data.getData();
        try {
            mPhotoCapturedImageView.setImageBitmap(new UserPicture(selectedImageUri, getContentResolver()).getBitmap());
            mPhotoCapturedImageView.setBackgroundColor(Color.TRANSPARENT);

            library_img = getFileFromUri(selectedImageUri);

            // Save the path of the selected image file.
            filePath = library_img.getPath();
            Log.d("selected image path", filePath);

        } catch (IOException e) {
            Log.e(ProfileActivity.class.getSimpleName(), "Failed to load image", e);
        }

        // Go to the profile edit page.
        photoGetLayout.setVisibility(View.GONE);
        profileLayout.setVisibility(View.VISIBLE);
    }

    File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "STORELEAKS_" + timeStamp + "_";
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imagesFolder = new File(storageDirectory, "StoreleaksImages");
        imagesFolder.mkdir();

        profile_img = File.createTempFile(imageFileName, ".jpg", imagesFolder);
        mImageFileLocation = profile_img.getAbsolutePath();

        return profile_img;
    }

    void setReducedImageSize() {
        // Get the dimensions of the View
        int targetW = mPhotoCapturedImageView.getWidth();
        int targetH = mPhotoCapturedImageView.getHeight();
        targetH = 500;
        targetW = 500;
        Log.d("Target width and height", String.valueOf(targetW) + " : " + String.valueOf(targetH));

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mImageFileLocation, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        Log.d("Photo width and height", String.valueOf(photoW) + " : " + String.valueOf(photoH));

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetH, photoH / targetH);
        if (scaleFactor == 0) scaleFactor = 1;

        Log.d("Bitmap Scale", String.valueOf(scaleFactor));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(mImageFileLocation, bmOptions);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        mPhotoCapturedImageView.setImageBitmap(rotatedBitmap);
        mPhotoCapturedImageView.setBackgroundColor(Color.TRANSPARENT);

        // Chnage the image size.
//        changeImageSize(rotatedBitmap);

        // Save the path of the selected image path.
        filePath = mImageFileLocation;

        // Go to the profile edit page.
        photoGetLayout.setVisibility(View.GONE);
        profileLayout.setVisibility(View.VISIBLE);
    }

    // Change the captured image size if the image size > 400*400
    public void changeImageSize(Bitmap bitmap) {
        Log.d("Bitmap width, height", String.valueOf(bitmap.getWidth()) + " : " + String.valueOf(bitmap.getHeight()));
        if (bitmap.getWidth() > 500 || bitmap.getHeight() > 500) {
            int newHeight = bitmap.getHeight() * 400 / bitmap.getHeight();
            bitmap = Bitmap.createScaledBitmap(bitmap, 400, newHeight, true);
            Log.d("Resized Image size", String.valueOf(bitmap.getWidth() + " : " + String.valueOf(bitmap.getHeight())));
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);

        if (profile_img.getPath().isEmpty()) return;

        FileOutputStream fo;
        try {
            Log.d("Resized Image Path", profile_img.getAbsolutePath());
            fo = new FileOutputStream(profile_img);
            fo.write(bytes.toByteArray());
            fo.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Get the image file from URI using aFileChooser Library.
    public File getFileFromUri(Uri uri) {
        return com.ipaulpro.afilechooser.utils.FileUtils.getFile(context, uri);
    }
}