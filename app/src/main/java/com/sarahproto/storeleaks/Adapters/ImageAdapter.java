package com.sarahproto.storeleaks.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sarahproto.storeleaks.R;

public class ImageAdapter extends BaseAdapter {

    String[] result;
    int[] imageId;
    private static LayoutInflater inflater = null;
    private final Context context;
    int[] images = {
            R.drawable.shop_1, R.drawable.shop_2,
            R.drawable.shop_3, R.drawable.shop_4,
            R.drawable.shop_5, R.drawable.shop_6,
            R.drawable.shop_7
    };

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
                detailsDialog.setContentView(R.layout.result_details_activity);

                TextView nameTxt = (TextView) detailsDialog.findViewById(R.id.nameTxt);
                TextView shopNametxt = (TextView) detailsDialog.findViewById(R.id.shopNameTxt);
                TextView locationTxt = (TextView) detailsDialog.findViewById(R.id.locationTxt);
                TextView descriptionTxt = (TextView) detailsDialog.findViewById(R.id.descriptionTxt);

                detailsDialog.setTitle("Item Details");
                nameTxt.setText(result[position]);

                detailsDialog.show();

            }
        });

        return rowView;
    }
}
