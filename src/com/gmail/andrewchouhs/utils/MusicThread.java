package com.gmail.andrewchouhs.utils;

import com.gmail.andrewchouhs.Storage;

public class MusicThread extends Thread
{
	public MusicThread()
	{
		setDaemon(true);
	}
	
	@Override
	public void run()
	{
		Storage.player.get().play();
	}
}
