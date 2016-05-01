package com.suman.news_reader.media_controllers;

/**
 * Created by sumansucharitdas on 4/16/16.
 */
public interface NRMediaPlayerControl {
    void    start();
    void    pause();
    int     getDuration();
    int     getCurrentPosition();
    void    seekTo(int pos);
    boolean isPlaying();
    int     getBufferPercentage();
    boolean canPause();
    boolean canSeekBackward();
    boolean canSeekForward();
    void    setVolume(float leftVolume, float rightvolume);
}
