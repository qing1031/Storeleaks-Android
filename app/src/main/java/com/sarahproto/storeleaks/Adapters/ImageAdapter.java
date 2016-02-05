package com.sarahproto.storeleaks.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.sarahproto.storeleaks.Response.SearchItemResult;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private final Context context;
    private static LayoutInflater inflater = null;
    public static final String IMAGE_TYPE = "image/*";

    ImageView selectedImage;
    TextView nameTxt, shopNametxt, locTitleTxt, locationTxt, detailedLocTitileTxt;
    TextView detailedLocTxt, descriptionTxt, likeNumTxt, uploadDtTxt;
    ImageButton likeBtn, shareBtn;
    ProgressBar progressBar;

    Bitmap[] resultBmps;
    List<SearchItemResult> resultList;
    Holder holder;

    public ImageAdapter(Activity activity, Bitmap[] bitmaps, List<SearchItemResult> resultData) {
        context = activity;
        resultBmps = bitmaps;
        resultList = resultData;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // number of datelements to be displayed.
//        return result.length - 1;
        return resultBmps.length;
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
        holder = new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.grid_item, null);
        holder.tv = (TextView) rowView.findViewById(R.id.textView1);
        holder.img = (ImageView) rowView.findViewById(R.id.imageView1);

        holder.tv.setText(resultList.get(position).getName());
        holder.img.setImageBitmap(resultBmps[position]);

        // click the items of the search result.
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("List item clicked", String.valueOf(position));
                Log.d("Selcted Item Name", resultList.get(position).getName());
                Log.d("Result list", String.valueOf(resultList));

                Dialog detailsDialog = new Dialog(context);
                detailsDialog.setContentView(R.layout.result_details_dialog);
                detailsDialog.setTitle("Item Details");
                detailsDialog.show();

                selectedImage = (ImageView) detailsDialog.findViewById(R.id.selectedItemImageView);
                nameTxt = (TextView) detailsDialog.findViewById(R.id.nameTxt);
                shopNametxt = (TextView) detailsDialog.findViewById(R.id.shopNameTxt);
                locTitleTxt = (TextView) detailsDialog.findViewById(R.id.locationTitleTxt);
                locationTxt = (TextView) detailsDialog.findViewById(R.id.locationTxt);
                detailedLocTitileTxt = (TextView) detailsDialog.findViewById(R.id.detailedLocationTitleTxt);
                detailedLocTxt = (TextView) detailsDialog.findViewById(R.id.detailedLocationTxt);
                uploadDtTxt = (TextView) detailsDialog.findViewById(R.id.uploadDateTxt);
                descriptionTxt = (TextView) detailsDialog.findViewById(R.id.descriptionTxt);
                likeNumTxt = (TextView) detailsDialog.findViewById(R.id.likeNumTxt);
                likeBtn = (ImageButton) detailsDialog.findViewById(R.id.likeBtn);
                shareBtn = (ImageButton) detailsDialog.findViewById(R.id.shareBtn);
                progressBar = (ProgressBar) detailsDialog.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);

                selectedImage.setImageBitmap(resultBmps[position]);
                nameTxt.setText(resultList.get(position).getName());
                shopNametxt.setText(resultList.get(position).getShop());
                locationTxt.setText(resultList.get(position).getCountry());
                if (locationTxt.getText().toString().trim().isEmpty()) {
                    locationTxt.setVisibility(View.GONE);
                    locTitleTxt.setVisibility(View.GONE);
                }
                detailedLocTxt.setText(resultList.get(position).getLocation_more());
                if (detailedLocTxt.getText().toString().trim().isEmpty()) {
                    detailedLocTitileTxt.setVisibility(View.GONE);
                    detailedLocTxt.setVisibility(View.GONE);
                }
                uploadDtTxt.setText(resultList.get(position).getDatet());
                descriptionTxt.setText(resultList.get(position).getDesc());

                likeNumTxt.setVisibility(View.GONE);

                likeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likeNumTxt.setVisibility(View.VISIBLE);
                        likeNumTxt.setText("1");
                    }
                });

                shareBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Share this item.
                        onShareProfile(nameTxt.getText().toString(),
                                locationTxt.getText().toString(),
                                descriptionTxt.getText().toString(),
                                resultBmps[position]);
                    }
                });
            }
        });

        return rowView;
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