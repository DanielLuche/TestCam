package com.dluche.testcam;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.IOException;

/**
 * Created by d.luche on 26/10/2017.
 */

public class PictureViewer extends AppCompatActivity {


    private ImageView iv_picture;
    private Context context;
    private Bundle bundle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.activity_picture_view);
        //
        iniVar();
        //
        iniAction();


    }

    private void iniVar() {
        //
        context = this;
        //
        bundle = getIntent().getExtras();
        //
        iv_picture = (ImageView) findViewById(R.id.picture_viewer_iv_picture);
        //
    }

    private void iniAction() {
        //
        iv_picture.setImageBitmap(generateBitmap());
    }

    private Bitmap generateBitmap() {
        Bitmap bitmap = null;
        Bitmap bitmapRotated = null;

        String filePath = "";

        if(bundle.containsKey(MainActivity.FULL_FILE_PATH)){
            filePath = bundle.getString(MainActivity.FULL_FILE_PATH);
            //
            bitmap = BitmapFactory.decodeFile(filePath);
            try {
                ExifInterface exifInterface = new ExifInterface(filePath);
                bitmapRotated = rotateBitmap(bitmap, Integer.parseInt(exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmapRotated != null ? bitmapRotated : bitmap ;
    }


    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bmRotated.recycle();
            return bmRotated;

            /*Bitmap bmRotated = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(bmRotated,matrix,new Paint());

            return bmRotated;*/

        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
