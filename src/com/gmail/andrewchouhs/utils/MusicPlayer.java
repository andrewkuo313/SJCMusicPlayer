package com.gmail.andrewchouhs.utils;

public interface MusicPlayer
{
	public void play();
    public void playAndPause();
    public void stop();
    public void seek(int millis);
}
