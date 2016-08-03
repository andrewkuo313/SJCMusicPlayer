package com.gmail.andrewchouhs.utils;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MusicPlayer 
{
	private Media media;
	private MediaPlayer player;
	
	public void play(String path)
	{
		if(player != null)
		{
			player.stop();
		}
		media = new Media(path);
        player = new MediaPlayer(media);
		player.play();
	}
}
