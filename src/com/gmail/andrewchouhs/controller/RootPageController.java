package com.gmail.andrewchouhs.controller;

import com.gmail.andrewchouhs.Storage;
import com.gmail.andrewchouhs.utils.MusicPlayingService;
import com.gmail.andrewchouhs.utils.Page;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;

public class RootPageController
{
	@FXML
	private Tab albumTab;
	@FXML
	private Tab statisticsTab;
	@FXML
	private Tab listTab;
	@FXML
	private Button playAndPauseButton;
	@FXML
	private Slider timeSlider;
	@FXML
	private Label nameLabel;
	@FXML
	private Label albumLabel;
	private boolean isHoldingSlider = false;
	
	@FXML
	private void initialize()
	{
    	albumTab.setOnSelectionChanged((event)->Storage.setPage(Page.ALBUM));
    	statisticsTab.setOnSelectionChanged((event)->Storage.setPage(Page.STATISTICS));
    	listTab.setOnSelectionChanged((event)->Storage.setPage(Page.LIST));
    	Storage.musicTime.addListener( (observable, oldValue, newValue) -> 
    	{
    		if(!isHoldingSlider)
    			timeSlider.setValue(newValue.intValue());
    	});
    	
    	timeSlider.valueProperty().addListener((observable, oldValue, newValue) ->
    	{
    		if(oldValue.intValue() != newValue.intValue())
    		{
	    		int time = newValue.intValue();
	    		int minute = time / 60;
				int second = time % 60;
				playAndPauseButton.setText((minute < 10 ? "0" + minute : minute)+ ":" + (second < 10 ? "0" + second : second));
				timeSlider.setValue(newValue.intValue());
    		}
    	});
    	
    	Storage.musicTotalTime.addListener((observable, oldValue, newValue) -> 
    	{
    		timeSlider.setMax(newValue.intValue());
    	});
    	
    	Storage.musicInfo.addListener((observable, oldValue, newValue) ->
    	{
    		if(newValue != null)
    			nameLabel.setText(newValue.name.get());
    		else
    			nameLabel.setText("曲名");
    	});
	}
	
	@FXML
	private void onTimeSliderPressed()
	{
		isHoldingSlider = true;
	}
	
	@FXML
	private void onTimeSliderReleased()
	{
		isHoldingSlider = false;
		//this fucking bug didn't get fixed in fact
		if(Storage.musicPlayer.get() == null || ((int)timeSlider.getValue()) == Storage.musicTime.get())
			return;
		Storage.musicPlayer.get().seek((int)timeSlider.getValue());
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
		MusicPlayingService player = Storage.musicPlayer.get();
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
		int index = Storage.musicInfoList.indexOf(Storage.musicInfo.get());
		if(index == -1)
			return;
		if(index == 0)
			index = Storage.musicInfoList.size();
		Storage.musicInfo.set(Storage.musicInfoList.get(index - 1));
	}
	
	@FXML
	private void nextMusic()
	{
		int index = Storage.musicInfoList.indexOf(Storage.musicInfo.get());
		if(index == -1)
			return;
		if(index == Storage.musicInfoList.size() - 1)
			index = -1;
		Storage.musicInfo.set(Storage.musicInfoList.get(index + 1));
	}
}
