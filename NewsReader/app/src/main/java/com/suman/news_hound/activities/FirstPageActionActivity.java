package com.suman.news_hound.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.suman.news_hound.R;
import com.suman.news_hound.navigation_informational.AboutActivity;
import com.suman.news_hound.navigation_older_news.NROlderNewsList;
import com.suman.news_hound.navigation_older_news.OlderNewsFileNamesPOJO;
import com.suman.news_hound.user_on_boarding.NROnboardingActivity;
import com.suman.news_hound.utils.CameraUtils;
import com.suman.news_hound.utils.PermissionUtils;
import com.suman.news_hound.utils.ViewUtils;

import java.io.File;

/**
 * Created by sumansucharitdas on 5/15/16.
 */
public class FirstPageActionActivity extends AppCompatActivity{

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navView;
    public static String            FILE_NAME = "temp.jpg";
    public static String uriOfFile = "";
    private CoordinatorLayout rootLayout;
    public static final int         OLDER_NEWS_REQUEST = 5;
    public static final String      PREF_USER_FIRST_TIME = "user_first_time";
    public static final int         CAMERA_IMAGE_REQUEST = 3;
    public static final int         CAMERA_PERMISSIONS_REQUEST = 2;
    private static final int        GALLERY_IMAGE_REQUEST = 1;

    private boolean                 isUserFirstTime;
    private static final String     PREFERENCES_FILE = "materialsample_settings";
    private CameraUtils             cameraUtils;
    private ViewUtils               viewUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isUserFirstTime = Boolean.valueOf(readSharedSetting(FirstPageActionActivity.this, PREF_USER_FIRST_TIME, "true"));
        Intent introIntent = new Intent(FirstPageActionActivity.this, NROnboardingActivity.class);
        introIntent.putExtra(PREF_USER_FIRST_TIME, isUserFirstTime);

        cameraUtils = new CameraUtils();
        viewUtils = new ViewUtils();

        if (isUserFirstTime)
            startActivity(introIntent);
        else {
            setContentView(R.layout.activity_camera);
            drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutCamera);
            drawerToggle = new ActionBarDrawerToggle(FirstPageActionActivity.this, drawerLayout, R.string.app_name, R.string.app_name);
            drawerLayout.setDrawerListener(drawerToggle);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
            navView = (NavigationView) findViewById(R.id.navigationCamera);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCamera);
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            new OlderNewsFileNamesPOJO();

            rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayoutCamera);

            // Coloring and maintaining Material design UI
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                viewUtils.setTranslucentStatus(true, FirstPageActionActivity.this);
            }
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.colorPrimaryDark);
            tintManager.setNavigationBarTintColor(R.color.colorPrimary);

            navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    menuItem.setChecked(true);
                    if (menuItem.getItemId() == R.id.nav_older_news) {
                        Intent newsActivity = new Intent(getApplication(), NROlderNewsList.class);
                        startActivityForResult(newsActivity, OLDER_NEWS_REQUEST);
                    }
                    else if (menuItem.getItemId() == R.id.nav_capture_image) {
                        startCamera();
                    }
                    else if (menuItem.getItemId() == R.id.nav_load_from_gallery) {
                        Intent mainActivity = new Intent(FirstPageActionActivity.this, NRMainActivity.class);
                        mainActivity.putExtra("selectedNav", "Gallery");
                        setResult(RESULT_OK);
                        startActivity(mainActivity);
                        finish();
                    }
                    else if (menuItem.getItemId() == R.id.nav_about) {
                        Intent aboutIntent = new Intent(FirstPageActionActivity.this, AboutActivity.class);
                        startActivity(aboutIntent);
                    }
                    else if (menuItem.getItemId() == R.id.nav_contact){
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"sumandas.freaky@gmail.com"});
                        i.putExtra(Intent.EXTRA_SUBJECT, "Say hello or let us know whatsup?");
                        i.putExtra(Intent.EXTRA_TEXT   , "Please add details");
                        try {
                            startActivity(Intent.createChooser(i, "Contact Us"));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(FirstPageActionActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    drawerLayout.closeDrawers();
                    return true;
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause () {
        super.onPause();
    }

    @Override
    protected void onStop (){
        super.onStop();
    }

    @Override
    protected void onResume () {
        super.onResume();
    }

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(drawerToggle != null){
            drawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * ===============================================================================
     * |            Need to optimize same across all: DEVICE CAMERA USE              |
     * ===============================================================================
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Intent mainActivity = new Intent(FirstPageActionActivity.this, NRMainActivity.class);
            mainActivity.putExtra("selectedNav","CaptureImage");
            mainActivity.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(uriOfFile)));
            setResult(RESULT_OK);
            startActivity(mainActivity);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
            startCamera();
        }
        else {
            cameraUtils.setCameraPermissionsRequest(FirstPageActionActivity.this);
        }
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(this, CAMERA_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File f = new File(dir, FILE_NAME);
            uriOfFile = f.getAbsolutePath();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }
}