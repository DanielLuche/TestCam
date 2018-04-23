package com.dluche.testcam;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import ctrls.CtrlCamera;

import static com.dluche.testcam.MainActivity.REQUEST_IMAGE_CAPTURE;

public class CamActivity extends AppCompatActivity {

    private Context context;
    private String path;
    private String prefix;
    private File newImage;
    private Bundle recoverBundle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        initVars();
        //
        recoverIntentInfo();
        //
        dispatchTakePictureIntent();
    }

    private void initVars() {
        context = getBaseContext();
        path = "";
        prefix = "";
        newImage = null;
    }

    private void recoverIntentInfo() {
        recoverBundle = getIntent().getExtras();
        //
        if(recoverBundle != null){
            if(recoverBundle.containsKey(CtrlCamera.DEFAULT_PATH)){
                path = recoverBundle.getString(CtrlCamera.DEFAULT_PATH,"");
                prefix = recoverBundle.getString(CtrlCamera.IMG_PREIX,"");
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            //
            newImage = createFile();
            //
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newImage));
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }
    }

    private File createFile() {
        String imageFileName = prefix +"_"+
                new SimpleDateFormat("yyyy_MM_dd_HHmmss").format(new Date())
                        + ".png"
                ;
        //
        File image = new File(path,imageFileName);
        //
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                //proceedSavePictureV2(data);
                //generateThumbnail(data);

                //loadPictureList();

                Toast.makeText(context, "Sucesso ao salvar foto\n", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Erro ao salvar foto\n", Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(context, "Erro ao receber foto\n", Toast.LENGTH_LONG).show();
        }
    }
}
