package com.dluche.testcam;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ctrls.CtrlCamera;

public class CallCameraCtrl extends AppCompatActivity {

    private Context context;
    private CtrlCamera ctrlCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_camera_ctrl);

        initVars();

    }

    private void initVars() {
        context = this;
        //
        ctrlCamera = findViewById(R.id.callCamCtrl_ctrl_cam);

    }

}
