package com.gmail.andrewchouhs.controller;

import com.gmail.andrewchouhs.storage.DataStorage;
import com.gmail.andrewchouhs.storage.PropertyStorage;
import static com.gmail.andrewchouhs.storage.PropertyStorage.musicPlayer;
import static com.gmail.andrewchouhs.storage.PropertyStorage.musicList;
import static com.gmail.andrewchouhs.storage.PropertyStorage.musicInfo;
import com.gmail.andrewchouhs.storage.SceneStorage;
import com.gmail.andrewchouhs.storage.SceneStorage.Page;
import com.gmail.andrewchouhs.utils.player.MusicPlayingService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;

public class RootPageController
{
	@FXML
	private Tab albumPageTab;
	@FXML
	private Tab statisticsPageTab;
	@FXML
	private Tab listPageTab;
	
	@FXML
	private Button playAndPauseButton;
	
	@FXML
	private Slider timeSlider;
	
	@FXML
	private Label nameLabel;
	@FXML
	private Label albumLabel;
	
	private boolean holdingSlider = false;
	
	@FXML
	private void initialize()
	{
    	albumPageTab.setOnSelectionChanged((event)->SceneStorage.setPage(Page.ALBUM));
    	statisticsPageTab.setOnSelectionChanged((event)->SceneStorage.setPage(Page.STATISTICS));
    	listPageTab.setOnSelectionChanged((event)->SceneStorage.setPage(Page.LIST));
    	
    	timeSlider.valueProperty().addListener((observable, oldValue, newValue) ->
    	{
    		int time = newValue.intValue();
    		
    		if(oldValue.intValue() != time)
    		{
	    		int minute = time / 60;
				int second = time % 60;
				
				playAndPauseButton.setText((minute < 10 ? "0" + minute : minute)+ ":" + (second < 10 ? "0" + second : second));
				
				timeSlider.setValue(time);
    		}
    	});
    	
    	PropertyStorage.musicTime.addListener((observable, oldValue, newValue) -> 
    	{
    		if(!holdingSlider)
    			timeSlider.setValue(newValue.intValue());
    	});
    	
    	PropertyStorage.musicTotalTime.addListener((observable, oldValue, newValue) -> 
    	{
    		timeSlider.setMax(newValue.intValue());
    	});
    	
    	PropertyStorage.musicInfo.addListener((observable, oldValue, newValue) ->
    	{
    		if(newValue != null)
    			nameLabel.setText(newValue.name.get());
    		else
    			nameLabel.setText(DataStorage.bundle.getString("ListPage.Name"));
    	});
	}
	
	@FXML
	private void onTimeSliderPressed()
	{
		holdingSlider = true;
	}
	
	@FXML
	private void onTimeSliderReleased()
	{
		holdingSlider = false;
		
		//很難修，問題貌似出於撥放器的時差未調整。
		if((int)timeSlider.getValue() == PropertyStorage.musicTime.get() || musicPlayer.get() == null)
			return;
		
		musicPlayer.get().seek((int)timeSlider.getValue());
	}
	
	@FXML
	private void openSettings()
	{
		SceneStorage.getSettingsStage().show();
		SceneStorage.getSettingsStage().toFront();
	}
	
	@FXML
	private void playAndPause()
	{
		MusicPlayingService player = musicPlayer.get();
		
		if(player == null)
			return;
		
		if(player.isRunning())
			player.pause();
		else
			player.start();
	}
	
	@FXML
	private void previousMusic()
	{
		int index = musicList.indexOf(musicInfo.get());
		
		if(index == -1)
			return;
		
		if(index == 0)
			index = musicList.size();
		
		musicInfo.set(musicList.get(index - 1));
	}
	
	@FXML
	private void nextMusic()
	{
		int index = musicList.indexOf(musicInfo.get());
		
		if(index == -1)
			return;
		
		if(index == musicList.size() - 1)
			index = -1;
		
		musicInfo.set(musicList.get(index + 1));
	}
}
