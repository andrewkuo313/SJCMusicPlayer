package com.gmail.andrewchouhs.utils;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MusicPlayer 
{
	private Media media;
	//all the listener need to listen when property changes
	public final ObjectProperty<MediaPlayer> mp = new SimpleObjectProperty<MediaPlayer>();
	
	public void play(String path)
	{
		MediaPlayer player = mp.get();
		if(player != null)
		{
			player.stop();
			player.dispose();
		}
		media = new Media(path);
        mp.set(new MediaPlayer(media));
		mp.get().play();
	}
}
