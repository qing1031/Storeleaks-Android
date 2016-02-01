package com.sarahproto.storeleaks.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sarahproto.storeleaks.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {

    String[] result;
    int[] imageId;
    public static final String IMAGE_TYPE = "image/*";
    private static LayoutInflater inflater = null;
    private final Context context;

    public static final String URL =
            "http://storeleaks.com/img/items/57ef6eb594a614088dcb34466c9fcd2b.png";
    ImageView selectedImage;
    TextView nameTxt, shopNametxt, locationTxt, descriptionTxt, likeNumTxt;
    ImageButton likeBtn, shareBtn;
    ProgressBar progressBar;

    public ImageAdapter(Activity activity, String[] imageNameList, int[] images) {
        result = imageNameList;
        context = activity;
        imageId = images;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // number of datelements to be displayed.
        return result.length - 1;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView tv;
        ImageView img;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.grid_item, null);
        holder.tv = (TextView) rowView.findViewById(R.id.textView1);
        holder.img = (ImageView) rowView.findViewById(R.id.imageView1);

        holder.tv.setText(result[position]);
        holder.img.setImageResource(imageId[position]);

        // click the items of the search result.
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog detailsDialog = new Dialog(context);
                detailsDialog.setContentView(R.layout.result_details_dialog);
                detailsDialog.setTitle("Item Details");
                detailsDialog.show();

                selectedImage = (ImageView) detailsDialog.findViewById(R.id.selectedItemImageView);
                nameTxt = (TextView) detailsDialog.findViewById(R.id.nameTxt);
                shopNametxt = (TextView) detailsDialog.findViewById(R.id.shopNameTxt);
                locationTxt = (TextView) detailsDialog.findViewById(R.id.locationTxt);
                descriptionTxt = (TextView) detailsDialog.findViewById(R.id.descriptionTxt);
                likeNumTxt = (TextView) detailsDialog.findViewById(R.id.likeNumTxt);
                likeBtn = (ImageButton) detailsDialog.findViewById(R.id.likeBtn);
                shareBtn = (ImageButton) detailsDialog.findViewById(R.id.shareBtn);
                progressBar = (ProgressBar) detailsDialog.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);

                nameTxt.setText(result[position]);
                likeNumTxt.setVisibility(View.GONE);

                likeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likeNumTxt.setVisibility(View.VISIBLE);
                        likeNumTxt.setText("2");
                    }
                });

                shareBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Create an object for subclass of AsyncTask
                        GetXMLTask task = new GetXMLTask();
                        // Execute the task
                        task.execute(URL);

                        progressBar.setVisibility(View.VISIBLE);        // Run progressbar
                    }
                });
            }
        });

        return rowView;
    }

    // Get the bitmap from image url. and share the image
    private class GetXMLTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap map = null;
            for (String url : urls) {
                map = downloadImage(url);
            }
            return map;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the imageview from the url here.

            // Share the informations with this result bitmap.
            onShareProfile(nameTxt.getText().toString(),
                    locationTxt.getText().toString(),
                    descriptionTxt.getText().toString(),
                    result);

            progressBar.setVisibility(View.GONE);       // Stop progressbar
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

    // Share the profile
    private void onShareProfile(String itemName, String location, String des, Bitmap bitmap) {

        Intent it = new Intent(Intent.ACTION_SEND);
        it.setType(IMAGE_TYPE);
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(it, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);

        List<Intent> targetedShareItents = new ArrayList<>();

        String imgFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/images.jpg";
        String subjectStr = itemName + " - List items - Storeleaks";
        String messageStr = String.format("Fine and Share %s in , %s. %s", itemName, location, des);
        Log.d("subject value :", subjectStr);
        Log.d("message value :", messageStr);

        for (ResolveInfo info : resolveInfos) {
            Intent targeted = new Intent(Intent.ACTION_SEND);
            targeted.setType(IMAGE_TYPE);

            ActivityInfo activityInfo = info.activityInfo;

            targeted.setPackage(activityInfo.packageName);

            targeted.putExtra(Intent.EXTRA_SUBJECT, subjectStr);

            targeted.putExtra(Intent.EXTRA_TEXT, messageStr);
            targeted.putExtra("sms_body", messageStr);

            String pathOfBitmap = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Insert Image", null);
            targeted.putExtra(Intent.EXTRA_STREAM, Uri.parse(pathOfBitmap));
            targeted.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            targetedShareItents.add(targeted);
        }

        Intent openInChooser = Intent.createChooser(targetedShareItents.remove(0), "Sharing Image");
        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareItents.toArray(new Parcelable[]{}));
        context.startActivity(openInChooser);
    }
}