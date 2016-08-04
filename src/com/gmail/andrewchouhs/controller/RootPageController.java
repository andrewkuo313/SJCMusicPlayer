package com.gmail.andrewchouhs.controller;

import java.io.File;
import com.gmail.andrewchouhs.Storage;
import com.gmail.andrewchouhs.model.MusicInfo;
import com.gmail.andrewchouhs.utils.MusicFileFilter;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;

public class RootPageController
{
	@FXML
	private MenuItem chooseFolder;
	
	@FXML
	private void initialize()
	{
    	Storage.musicDir.addListener((observable, oldValue, newValue)->refreshMusicInfoList());
	}
	
	@FXML
	private void setDir()
	{
		DirectoryChooser dirChooser = new DirectoryChooser();
		File selectedDir = dirChooser.showDialog(Storage.getStage());
		if(selectedDir != null)
			Storage.musicDir.set(new File(selectedDir.getAbsolutePath()));
	}
    
    private static void refreshMusicInfoList()
    {
    	File[] musicFileList = Storage.musicDir.get().listFiles(new MusicFileFilter());
    	for(File musicFile : musicFileList)
    	{
    		Media media = new Media(musicFile.toURI().toString());
    		new MediaPlayer(media).setOnReady(()->
    		{
				String title;
				if(media.getMetadata().get("title") != null)
					title = (String)media.getMetadata().get("title");
				else
					title = musicFile.getName().substring(0, musicFile.getName().lastIndexOf('.'));
				Storage.musicInfoList.add(new MusicInfo(musicFile.toURI().toString(), title, 
						(String)media.getMetadata().get("artist"), (String)media.getMetadata().get("album"),
						(Image)media.getMetadata().get("image")));
    		});
    	}
    }
}
