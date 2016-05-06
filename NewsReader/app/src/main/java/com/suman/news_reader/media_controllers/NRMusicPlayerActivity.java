package com.suman.news_reader.media_controllers;

import android.annotation.TargetApi;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import java.io.IOException;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.suman.news_reader.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

//        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutMusic);
//        drawerToggle = new ActionBarDrawerToggle(NRMusicPlayerActivity.this, drawerLayout, R.string.app_name, R.string.app_name);
//        drawerLayout.setDrawerListener(drawerToggle);
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMusic);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fileID = getIntent().getStringExtra("fileID");
        audioSurface = (SurfaceView) findViewById(R.id.videoSurface);
        SurfaceHolder videoHolder = audioSurface.getHolder();
        videoHolder.addCallback(this);

        player = new MediaPlayer();
        controller = new NRAudioControllerView(this);

        //rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayoutMusic);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
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
        controller.show();
        return false;
    }

    @Override
    public void onBackPressed() {
        if(player.isPlaying()){
            player.stop();
        }
        finish();
    }

//    @Override
//    public void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        drawerToggle.syncState();
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        drawerToggle.onConfigurationChanged(newConfig);
//    }

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
        audioSurface.setBackgroundResource(R.drawable.photogallery); // draw the background
        player.prepareAsync();
    }

    @TargetApi(19) private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
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
}