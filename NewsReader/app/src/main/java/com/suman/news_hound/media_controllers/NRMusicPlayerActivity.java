package com.suman.news_hound.media_controllers;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.suman.news_hound.activities.NRMainActivity;
import com.suman.news_hound.navigation_informational.AboutActivity;
import com.suman.news_hound.navigation_older_news.NROlderNewsList;
import com.suman.news_hound.utils.CameraUtils;
import com.suman.news_hound.utils.PermissionUtils;
import com.suman.news_hound.utils.ViewUtils;
import com.suman.news_reader.R;

import java.io.File;
import java.io.IOException;

/**
 * @author sumansucharitdas
 * */
public class NRMusicPlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, NRMediaPlayerControl {

    SurfaceView           audioSurface;
    MediaPlayer           player;
    NRAudioControllerView controller;
    String                dir = Environment.getExternalStorageDirectory() + "/NewsReader/";
    String                fileID = "";
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    CoordinatorLayout rootLayout;
    private NavigationView navView;
    public static final int         CAMERA_IMAGE_REQUEST = 3;
    public static final int         CAMERA_PERMISSIONS_REQUEST = 2;
    public static String            FILE_NAME = "temp.jpg";
    public static String uriOfFile = "";

    private CameraUtils             cameraUtils;
    private ViewUtils               viewUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_controller);

        cameraUtils = new CameraUtils();
        viewUtils = new ViewUtils();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutMedia);
        drawerToggle = new ActionBarDrawerToggle(NRMusicPlayerActivity.this, drawerLayout, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(drawerToggle);
        navView = (NavigationView) findViewById(R.id.navigationMedia);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                if (menuItem.getItemId() == R.id.nav_older_news) {
                    Intent newsActivity = new Intent(getApplication(), NROlderNewsList.class);
                    startActivityForResult(newsActivity, 5);
                }
                else if (menuItem.getItemId() == R.id.nav_capture_image) {
                    startCamera();
                }
                else if (menuItem.getItemId() == R.id.nav_load_from_gallery) {
                    Intent mainActivity = new Intent(NRMusicPlayerActivity.this, NRMainActivity.class);
                    mainActivity.putExtra("selectedNav", "Gallery");
                    setResult(RESULT_OK);
                    startActivity(mainActivity);
                    finish();
                }
                else if (menuItem.getItemId() == R.id.nav_about) {
                    Intent aboutIntent = new Intent(NRMusicPlayerActivity.this, AboutActivity.class);
                    startActivity(aboutIntent);
                }
                else if (menuItem.getItemId() == R.id.nav_contact){
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"sumandas.freaky@gmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "Say hello or let us know whatsup?");
                    i.putExtra(Intent.EXTRA_TEXT, "Please add details");
                    try {
                        startActivity(Intent.createChooser(i, "Contact Us"));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(NRMusicPlayerActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
                if(player.isPlaying()){
                    player.stop();
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMedia);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fileID = getIntent().getStringExtra("fileID");
        audioSurface = (SurfaceView) findViewById(R.id.videoSurface);
        SurfaceHolder videoHolder = audioSurface.getHolder();
        videoHolder.addCallback(this);

        player = new MediaPlayer();
        controller = new NRAudioControllerView(this);

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayoutMedia);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            viewUtils.setTranslucentStatus(true, NRMusicPlayerActivity.this);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.colorPrimaryDark);
        tintManager.setNavigationBarTintColor(R.color.colorPrimary);

        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            if(!fileID.contains(".wav")) {
                player.setDataSource(this, Uri.parse(dir + fileID + ".wav"));
            }
            else{
                player.setDataSource(this, Uri.parse(dir + fileID));
            }
            player.setOnPreparedListener(this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onBackPressed() {
        if(player.isPlaying()){
            player.stop();
        }
        finish();
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
    protected void onStart() {
        super.onStart();
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

    /**
     * ===============================================================================
     * |                         Implement SurfaceHolder.Callback                     |
     * ===============================================================================
      */

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        player.setDisplay(holder);
        audioSurface.setBackgroundResource(R.drawable.photogallery);
        // draw the background
        player.prepareAsync();
    }
    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

    /**
     * ===============================================================================
     * |                         Implement MediaPlayer.OnPreparedListener             |
     * ===============================================================================
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        controller.setMediaPlayer(this);
        controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        controller.show();
        player.start();
    }

    /**
     * ===============================================================================
     * |           Get methods for NRVideoMediaController.MediaPlayerControl         |
     * ===============================================================================
     */
    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return player.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public void pause() {
        player.pause();
    }

    @Override
    public void seekTo(int i) {
        player.seekTo(i);
    }

    @Override
    public void start() {
        player.start();
    }

    @Override
    public void setVolume(float leftVol, float rightVol){
        player.setVolume(leftVol, rightVol);
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
            Intent mainActivity = new Intent(NRMusicPlayerActivity.this, NRMainActivity.class);
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
            cameraUtils.setCameraPermissionsRequest(NRMusicPlayerActivity.this);
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