package com.dluche.testcam;

import android.content.Context;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import adapter.PictureListAdapter;

public class MainActivity extends AppCompatActivity {

    public static final String FULL_FILE_PATH = "FULL_FILE_PATH";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final String PREFIX_THUMBNAIL = "thumbnail_";

    static final String path = Environment
            .getExternalStorageDirectory() + "/testCameraDir";
    static final String pathThumbnail = path + "/thumbnail";



    private Button btn_ctrl_act;
    private Button btn_picture;
    private TextView tv_ttl;
    private Context context;
    private File newImage = null;
    private ListView lv_pictures;
    private PictureListAdapter mAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniVars();

        iniAction();
    }

    private void iniVars() {
        context = this;
        //
        btn_ctrl_act = findViewById(R.id.main_btn_ctrl_act);
        //
        tv_ttl = (TextView) findViewById(R.id.main_tv_ttl);
        //
        btn_picture = (Button) findViewById(R.id.main_btn_picture);
        //
        lv_pictures = (ListView) findViewById(R.id.main_lv_pictures);

    }

    private void iniAction() {

        generatePictureDir();

        btn_ctrl_act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(context,CallCameraCtrl.class);
                context.startActivity(mIntent);
            }
        });

        btn_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        loadPictureList();
    }

    private void loadPictureList() {
        File dir = new File(pathThumbnail);
        File[] pictureList = dir.listFiles();
        //
        //ArrayList<String> thumbnailList = new ArrayList<>();
        //
        mAdapter = new PictureListAdapter(
                context,
                pictureList,
                R.layout.picture_cell
        );
        //
        mAdapter.setSetOnThumbnailClick(new PictureListAdapter.setOnThumbnailClick() {
            @Override
            public void onThumbnailClick(File file) {
                Intent intent = new Intent(context,PictureViewer.class);
                Bundle bundle = new Bundle();

                bundle.putString(FULL_FILE_PATH,path + "/" + file.getName().substring(PREFIX_THUMBNAIL.length()));

                intent.putExtras(bundle);
                startActivity(intent);
                //Toast.makeText(context, "Abrir foto", Toast.LENGTH_SHORT).show();
            }
        });
        //
        mAdapter.setSetOnDeleteImageClick(new PictureListAdapter.setOnDeleteImageClick() {
            @Override
            public void onDeleteImageClick(File file) {
                //
                try {
                    ExifInterface exif = new ExifInterface(file.getAbsolutePath());
                    //
                    File original = new File(exif.getAttribute(ExifInterface.TAG_USER_COMMENT));
                    //Deleta arquivo orignal
                    if(original.isFile()){
                        original.delete();
                    }
                    //Deleta Thumbail
                    file.delete();
                    //
                    loadPictureList();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        //
        mAdapter.setSetOnLinkClick(new PictureListAdapter.setOnLinkClick() {
            @Override
            public void onLinkClick(String url) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        //
        lv_pictures.setAdapter(mAdapter);

    }

    private void generatePictureDir() {
        try {
            File picDir = new File(path);
            //
            if (!picDir.exists()) {
                picDir.mkdir();
            }
            File thumbnailDir = new File(pathThumbnail);
            //
            if (!thumbnailDir.exists()) {
                thumbnailDir.mkdir();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            newImage = createFile();
            /*
            try {
                image = createTempPic();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(image != null){
                Uri photoUri = FileProvider.getUriForFile(
                        this,
                        getApplication().getPackageName()
                        ,image
                );*/

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newImage));
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private File createFile() {
        //
        String imageFileName =
                new SimpleDateFormat("yyyy_MM_dd_HHmmss").format(new Date())
                        + ".png"
                ;
        //
        File image = new File(path,imageFileName);
        //
        return image;
    }

    private File createTempPic() throws IOException {
        //
        String imageFileName = new SimpleDateFormat("yyyy_MM_dd_HHmmss").format(new Date());
        File imagePath = new File(path);
        File image = File.createTempFile(
                imageFileName,
                ".png",
                imagePath
        );

        return image;

    }

    @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                //proceedSavePictureV2(data);
                generateThumbnail(data);

                loadPictureList();

                Toast.makeText(context, "Sucesso ao salvar foto\n", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Erro ao salvar foto\n", Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(context, "Erro ao receber foto\n", Toast.LENGTH_LONG).show();
        }
    }

    private void generateThumbnail(Intent data) throws IOException {
        File thumbnailFile = new File(pathThumbnail + "/"+PREFIX_THUMBNAIL +newImage.getName());
        //
        //Bitmap thumbnail = BitmapFactory.decodeFile(newImage.getAbsolutePath());
        ExifInterface exifOrigin = new ExifInterface(newImage.getAbsolutePath());
        //
        FileOutputStream fileOutputStream = new FileOutputStream(thumbnailFile);
        fileOutputStream.write(exifOrigin.getThumbnail());
        fileOutputStream.flush();
        fileOutputStream.close();
        //
        ExifInterface exifThumbnail = new ExifInterface(thumbnailFile.getAbsolutePath());
        float[] latLong = new float[2];
        //
        if (exifOrigin.getLatLong(latLong)) {
            String lat ,longe,orientation;

            //lat = exifOrigin.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            lat = String.valueOf(latLong[0]);
            longe = String.valueOf(latLong[1]);
            orientation = exifOrigin.getAttribute(ExifInterface.TAG_ORIENTATION);
            //
            exifThumbnail.setAttribute(ExifInterface.TAG_GPS_LATITUDE,exifOrigin.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
            exifThumbnail.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF,exifOrigin.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF));
            exifThumbnail.setAttribute(ExifInterface.TAG_GPS_LONGITUDE,exifOrigin.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
            exifThumbnail.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF,exifOrigin.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF));
            exifThumbnail.setAttribute(ExifInterface.TAG_ORIENTATION,exifOrigin.getAttribute(ExifInterface.TAG_ORIENTATION));
            exifThumbnail.setAttribute(ExifInterface.TAG_USER_COMMENT,newImage.getAbsolutePath());
        }
        exifThumbnail.saveAttributes();

    }

    private void proceedSavePictureV2(Intent data) throws IOException {
        /*Bitmap bm = BitmapFactory.decodeFile(Uri.fromFile(newImage).getPath());

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, bytes);*/

       /* File new_file = new File(newImage.getPath());
        //
        new_file.createNewFile();
        //
        FileOutputStream fo = new FileOutputStream(new_file);
        fo.write(bytes.toByteArray());
        fo.close();*/

        /*OutputStream outputStream = new FileOutputStream(image);
        outputStream.flush();
        outputStream.close();*/
        //
        //MediaStore.Images.Media.insertImage(getContentResolver(),imagePath.getAbsolutePath(),imagePath.getName(),imagePath.getName());
        //MediaStore.Images.Media.insertImage(getContentResolver(),picture,image.getName(),image.getName());

    }

//    private void proceedSavePicture(Intent data) throws IOException {
//        //Resgata informações da intent retornada.
//        Bundle dataExtras = data.getExtras();
//        //Resgata o bitmap retornado atras da chave data
//        Bitmap picture = (Bitmap) dataExtras.get("data");
//        //
//        String imageFileName =
//                new SimpleDateFormat("yyyy_MM_dd_HHmmss").format(new Date())
//                + ".png"
//                ;
//        //
//        File image = new File(path,imageFileName);
//
//        if(!image.canWrite()){
//            image.setWritable(true);
//        }
//
//        OutputStream outputStream = new FileOutputStream(image);
//        picture.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//        outputStream.flush();
//        outputStream.close();
//        //
//        //MediaStore.Images.Media.insertImage(getContentResolver(),imagePath.getAbsolutePath(),imagePath.getName(),imagePath.getName());
//        //MediaStore.Images.Media.insertImage(getContentResolver(),picture,image.getName(),image.getName());
//
//    }
}
