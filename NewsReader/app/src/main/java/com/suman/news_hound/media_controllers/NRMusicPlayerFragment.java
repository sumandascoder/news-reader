package com.suman.news_hound.media_controllers;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.suman.news_hound.R;
import com.suman.news_hound.activities.NRMainActivity;
import com.suman.news_hound.media_controllers.NRMediaPlayerControl;

import java.io.IOException;

/**
 * Created by sumansucharitdas on 7/16/16.
 */
public class NRMusicPlayerFragment extends Fragment implements MediaPlayer.OnPreparedListener, NRMediaPlayerControl {

    private static MediaPlayer           player;
    String                dir = Environment.getExternalStorageDirectory() + "/NewsReader/";
    String                fileID = "";

    private View v;

    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
        }
    }

    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.content_media_controller, parent, false);
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        return v;
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }

    // This method is called after the parent Activity's onCreate() method has completed.
    // Accessing the view hierarchy of the parent activity must be done in the onActivityCreated.
    // At this point, it is safe to search for activity View objects by their ID, for example.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    /**
     * ===============================================================================
     * |                         Implement MediaPlayer.OnPreparedListener             |
     * ===============================================================================
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        player = mp;
        mp.start();
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
        if(isPlaying()){
            pause();
        }
        else {
            player.start();
        }
    }

    @Override
    public void setVolume(float leftVol, float rightVol){
        player.setVolume(leftVol, rightVol);
    }

    public void setStream(){
        fileID = NRMainActivity.fileID;
        try {
            if(!fileID.contains(".wav")) {
                player.setDataSource(getActivity(), Uri.parse(dir + fileID + ".wav"));
            }
            else{
                player.setDataSource(getActivity(), Uri.parse(dir + fileID));
            }
            player.setOnPreparedListener(this);
            player.prepareAsync();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            player.reset();
            try {
                if(!fileID.contains(".wav")) {
                    player.setDataSource(getActivity(), Uri.parse(dir + fileID + ".wav"));
                }
                else{
                    player.setDataSource(getActivity(), Uri.parse(dir + fileID));
                }
            }
            catch (IOException ie){
                ie.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
