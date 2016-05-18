package com.suman.news_hound.navigation_informational;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.suman.news_hound.activities.NRMainActivity;
import com.suman.news_hound.navigation_older_news.NROlderNewsList;
import com.suman.news_hound.utils.CameraUtils;
import com.suman.news_hound.utils.PermissionUtils;
import com.suman.news_hound.utils.ViewUtils;
import com.suman.news_reader.R;

import java.io.File;

/**
 * @author sumansucharitdas
 *  */
public class AboutActivity extends AppCompatActivity{
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navView;
    FloatingActionButton favFAB;
    private String uriOfFile = "";
    public static final int         CAMERA_IMAGE_REQUEST = 3;
    public static final int         CAMERA_PERMISSIONS_REQUEST = 2;
    public static String            FILE_NAME = "temp.jpg";

    private CameraUtils             cameraUtils;
    private ViewUtils               viewUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutAbout);
        drawerToggle = new ActionBarDrawerToggle(AboutActivity.this, drawerLayout, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(drawerToggle);
        navView = (NavigationView) findViewById(R.id.navigationAbout);
        favFAB = (FloatingActionButton) findViewById(R.id.favFAB);

        cameraUtils = new CameraUtils();
        viewUtils = new ViewUtils();

        // Show a dialog if meets conditions
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                if (menuItem.getItemId() == R.id.nav_older_news) {
                    Intent newsActivity = new Intent(getApplication(), NROlderNewsList.class);
                    startActivityForResult(newsActivity, 5);
                } else if (menuItem.getItemId() == R.id.nav_capture_image) {
                    startCamera();
                } else if (menuItem.getItemId() == R.id.nav_load_from_gallery) {
                    Intent mainActivity = new Intent(AboutActivity.this, NRMainActivity.class);
                    mainActivity.putExtra("selectedNav", "Gallery");
                    setResult(RESULT_OK);
                    startActivity(mainActivity);
                    finish();
                } else if (menuItem.getItemId() == R.id.nav_about) {
                    // Same page
                } else if (menuItem.getItemId() == R.id.nav_contact) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"sumandas.freaky@gmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "Say hello or let us know whatsup?");
                    i.putExtra(Intent.EXTRA_TEXT, "Please add details");
                    try {
                        startActivity(Intent.createChooser(i, "Contact Us"));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(AboutActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
        favFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Link to Playstore : http://developer.android.com/distribute/tools/promote/linking.html
                showRateDialog();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAbout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            viewUtils.setTranslucentStatus(true, AboutActivity.this);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.colorPrimaryDark);
        tintManager.setNavigationBarTintColor(R.color.colorPrimary);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.about_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.close_menu:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showRateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
        builder.setTitle(getString(R.string.about_rate_title));
        builder.setMessage(getString(R.string.about_rate_message));

        String positiveText = getString(R.string.about_rate_positive);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = "market://details?id=com.suman.news_hound";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });

        String negativeText = getString(R.string.about_rate_negative);
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

    /**
     * ===============================================================================
     * |            Need to optimize same across all: DEVICE CAMERA USE              |
     * ===============================================================================
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Intent mainActivity = new Intent(AboutActivity.this, NRMainActivity.class);
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
            cameraUtils.setCameraPermissionsRequest(AboutActivity.this);
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