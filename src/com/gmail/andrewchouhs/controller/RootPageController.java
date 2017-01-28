package com.gmail.andrewchouhs.controller;

import com.gmail.andrewchouhs.Storage;
import com.gmail.andrewchouhs.utils.Page;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;

public class RootPageController
{
	@FXML
	private Tab album;
	@FXML
	private Tab statistics;
	@FXML
	private Tab list;
	@FXML
	private Button playAndPause;
	
	@FXML
	private void initialize()
	{
    	album.setOnSelectionChanged((event)->Storage.setPage(Page.ALBUM));
    	statistics.setOnSelectionChanged((event)->Storage.setPage(Page.STATISTICS));
    	list.setOnSelectionChanged((event)->Storage.setPage(Page.LIST));
    	Storage.musicTime.addListener( (observable, oldValue, newValue) -> 
    	{
    		if(oldValue.longValue() != newValue.longValue())
    		{
    			long time = newValue.longValue();
    			int minute = (int) (time / 60L);
    			int second = (int) (time % 60L);
    			playAndPause.setText((minute < 10 ? "0" + minute : minute)+ ":" + (second < 10 ? "0" + second : second));
    		}
    	});
	}
	
	@FXML
	private void openSetting()
	{
		Storage.getSettingStage().show();
		Storage.getSettingStage().toFront();
	}
	
	@FXML
	private void playAndPause()
	{
		if(Storage.musicPlayer.isRunning())
			Storage.musicPlayer.stop();
		else
			Storage.musicPlayer.start();
	}
}
