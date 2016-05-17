package com.suman.news_hound.media_controllers;

/**
 * @author sumansucharitdas
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
