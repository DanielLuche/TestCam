package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dluche.testcam.R;

import java.io.File;

/**
 * Created by d.luche on 25/10/2017.
 */

public class PictureListAdapter extends BaseAdapter {

    private Context context;
    private File[] source;
    private int resource;
    private setOnDeleteImageClick setOnDeleteImageClick;
    private setOnThumbnailClick setOnThumbnailClick;
    private setOnLinkClick setOnLinkClick;

    public interface setOnThumbnailClick{
        void onThumbnailClick(File file);
    }

    public interface setOnDeleteImageClick{
        void onDeleteImageClick(File file);
    }

    public interface setOnLinkClick{
        void onLinkClick(String url);
    }

    public void setSetOnThumbnailClick(PictureListAdapter.setOnThumbnailClick setOnThumbnailClick) {
        this.setOnThumbnailClick = setOnThumbnailClick;
    }

    public void setSetOnDeleteImageClick(PictureListAdapter.setOnDeleteImageClick setOnDeleteImageClick) {
        this.setOnDeleteImageClick = setOnDeleteImageClick;
    }

    public void setSetOnLinkClick(PictureListAdapter.setOnLinkClick setOnLinkClick) {
        this.setOnLinkClick = setOnLinkClick;
    }

    public PictureListAdapter(Context context, File[] source, int resource) {
        this.context = context;
        this.source = source;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return source.length;
    }

    @Override
    public Object getItem(int i) {
        return source[i];
    }

    @Override
    public long getItemId(int i) {
        return 0L;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);

            convertView = inflater.inflate(resource, parent, false);

        }

        final File item = source[position];

        if (!item.isFile()) {
            convertView.setVisibility(View.GONE);
        } else {
            convertView.setVisibility(View.VISIBLE);
            //
            TextView tv_name = convertView.findViewById(R.id.picture_cell_tv_name);
            //
            ImageView iv_delete = convertView.findViewById(R.id.picture_cell_iv_delete);
            //
            ImageView iv_picture = convertView.findViewById(R.id.picture_cell_iv_picture);
            //
            TextView tv_lat = convertView.findViewById(R.id.picture_cell_tv_lat);
            //
            TextView tv_long = convertView.findViewById(R.id.picture_cell_tv_long);
            //
            final TextView tv_lat_long_link = convertView.findViewById(R.id.picture_cell_tv_lat_long_link);
            //
            TextView tv_orientation = convertView.findViewById(R.id.picture_cell_tv_orientation);
            //
            tv_name.setText(item.getName());
            tv_lat.setText("Lat:");
            tv_long.setText("Long:");
            tv_orientation.setText("Orientation:");
            //
            Bitmap thumbnail = BitmapFactory.decodeFile(item.getAbsolutePath());
            iv_picture.setImageBitmap(thumbnail);
            //
            //iv_picture.setImageBitmap(Bitmap.createScaledBitmap(image,200,200,false));
            //
            float[] latLong = new float[2];
            try {
                ExifInterface exifInterface = new ExifInterface(item.getAbsolutePath());
                //
                if (exifInterface.getLatLong(latLong)) {
                    tv_lat.setText("Lat: " + latLong[0]);
                    tv_long.setText("Long: " + latLong[1]);

                    //tv_lat.setText("Lat: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
                    //tv_long.setText("Long: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
                    //
                    String url = "https://www.google.com/maps/search/?api=1&query="
                            + latLong[0]+ ","
                            + latLong[1];
                    tv_lat_long_link.setText(url);

                    Log.i("GPS_CAM", "true");
                }
                tv_orientation.setText(exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //
        /*
        * Listners
        * */
            iv_picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (setOnThumbnailClick != null) {
                        setOnThumbnailClick.onThumbnailClick(item);
                    }
                }
            });
            //
            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (setOnDeleteImageClick != null) {
                        setOnDeleteImageClick.onDeleteImageClick(item);
                    }
                }
            });
            //
            tv_lat_long_link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (setOnLinkClick != null) {
                        setOnLinkClick.onLinkClick(tv_lat_long_link.getText().toString().trim());
                    }
                }
            });
            //
        }
        return convertView;
    }

}
