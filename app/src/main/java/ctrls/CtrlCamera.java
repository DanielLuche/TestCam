package ctrls;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dluche.testcam.CamActivity;
import com.dluche.testcam.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by d.luche on 24/10/2017.
 */

public class CtrlCamera extends LinearLayout implements View.OnClickListener {
    public static final String DEFAULT_PATH = "DEFAULT_PATH";
    public static final String IMG_PREIX = "IMG_PREIX";

    private Context context;
    private ArrayList<String> pictureList;
    private int pictureLimit;
    private String defaultPath;
    private File newImage;
    private int selfIcon;
    private String prefix;
    private ImageView iv_icon;
    private TextView tv_qty;

    public CtrlCamera(Context context) {
        super(context);
        //
        initialize(context);
    }

    public CtrlCamera(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public CtrlCamera(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        setDefaultValues(context);
        //
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.ctrl_camera_layout, this,true);
        //
        iv_icon = findViewById(R.id.ctrl_cam_iv_icon);
        //
        tv_qty = findViewById(R.id.ctrl_cam_tv_qty);
        //
        updateIconState();

    }

    public void setDefaultValues(Context context) {
        this.context = context;
        this.pictureList = new ArrayList<>();
        this.pictureLimit = 5;
        this.defaultPath = Environment
                .getExternalStorageDirectory() + "/testCameraCtrlDir";
        this.newImage = null;
        this.selfIcon = R.drawable.ic_camera_alt_black_24dp;
        this.prefix = "123";
    }

    public File getNewImage() {
        return newImage;
    }

    public void setNewImage(File newImage) {
        this.newImage = newImage;
    }

    public int getSelfIcon() {
        return selfIcon;
    }

    public void setSelfIcon(int selfIcon) {
        this.selfIcon = selfIcon;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void onClick(View view) {
        callCamActivity();
    }

    private void callCamActivity() {
        Intent mIntent = new Intent(context,CamActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(DEFAULT_PATH,defaultPath);
        mIntent.putExtras(bundle);
        context.startActivity(mIntent);
    }

    public int getCountPictureAssociated(){
        File fileList = new File(defaultPath);
        File[] files = fileList.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.startsWith(prefix)) {
                    return true;
                }

                return false;
            }
        });
        //
        if (files != null) {
            Arrays.sort(files);
            return files.length;
        }
        //
        return 0;
    }

    private void updateIconState(){
        if(getCountPictureAssociated() == 0){
            iv_icon.setColorFilter(R.color.colorAccent);
            tv_qty.setText("");
            tv_qty.setVisibility(GONE);
        }else{
            iv_icon.setColorFilter(R.color.colorPrimaryDark);
            tv_qty.setText(String.valueOf(getCountPictureAssociated()));
            tv_qty.setVisibility(VISIBLE);
        }
    }

}
