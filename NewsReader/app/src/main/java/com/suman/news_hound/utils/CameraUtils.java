package com.suman.news_hound.utils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import com.suman.news_hound.R;

/**
 * Created by sumansucharitdas on 5/17/16.
 */
public class CameraUtils {

    public static final int         CAMERA_PERMISSIONS_REQUEST = 2;

    public void setCameraPermissionsRequest(final Activity localActivity){
        AlertDialog.Builder builder = new AlertDialog.Builder(localActivity);
        builder.setTitle(localActivity.getString(R.string.camera_permission_title));

        String positiveText = localActivity.getString(R.string.camera_positive);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(localActivity,
                                new String[]{Manifest.permission.CAMERA},
                                CAMERA_PERMISSIONS_REQUEST);
                    }
                });

        String negativeText = localActivity.getString(R.string.camera_negative);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }
}
