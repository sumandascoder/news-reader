package com.suman.news_reader.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import com.suman.news_reader.user_on_boarding.NROnboardingActivity;
import com.suman.news_reader.utils.PermissionUtils;
import java.io.File;
/**
 * Created by sumansucharitdas on 4/30/16.
 * First launch of Image Capture
 */
public class ImageLaunchActivity extends Activity {

    public static final int         CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int         CAMERA_IMAGE_REQUEST = 3;
    public static final String      PREF_USER_FIRST_TIME = "user_first_time";

    private boolean                 isUserFirstTime;
    private static final String     PREFERENCES_FILE = "materialsample_settings";
    public static String            FILE_NAME = "temp.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        isUserFirstTime = Boolean.valueOf(readSharedSetting(ImageLaunchActivity.this, PREF_USER_FIRST_TIME, "true"));
        Intent introIntent = new Intent(ImageLaunchActivity.this, NROnboardingActivity.class);
        introIntent.putExtra(PREF_USER_FIRST_TIME, isUserFirstTime);

        if (isUserFirstTime)
            startActivity(introIntent);
        else {
            startCamera();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Intent mainActivity = new Intent(this, NRMainActivity.class);
                mainActivity.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getCameraFile()));
                startActivity(mainActivity);
            }
            else {
                Intent mainActivity = new Intent(this, NRMainActivity.class);
                startActivity(mainActivity);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(this, CAMERA_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getCameraFile()));
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    // Get the camera file
    public File getCameraFile() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }
}