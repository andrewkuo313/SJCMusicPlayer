package com.gmail.andrewchouhs.controller;

import com.gmail.andrewchouhs.storage.DataStorage;
import com.gmail.andrewchouhs.storage.PrefStorage;
import com.gmail.andrewchouhs.storage.PrefStorage.Pref;
import static com.gmail.andrewchouhs.storage.DataStorage.musicPlayer;
import static com.gmail.andrewchouhs.storage.DataStorage.musicList;
import static com.gmail.andrewchouhs.storage.DataStorage.currentMusicInfo;
import com.gmail.andrewchouhs.storage.SceneStorage;
import com.gmail.andrewchouhs.storage.SceneStorage.Page;
import com.gmail.andrewchouhs.storage.TextStorage;
import com.gmail.andrewchouhs.storage.TextStorage.Text;
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
	private Button playModeButton;
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
    	//同 changePlayMode() 須修正。
    	String playMode = PrefStorage.getPref(Pref.PlayMode);
		if(playMode.equals(PrefStorage.getPrefKey(Pref.NormalPlay)))
			playModeButton.setText(TextStorage.getText(Text.ListPage_NormalPlay));
		if(playMode.equals(PrefStorage.getPrefKey(Pref.RandomPlay)))
			playModeButton.setText(TextStorage.getText(Text.ListPage_RandomPlay));
		if(playMode.equals(PrefStorage.getPrefKey(Pref.RepeatPlay)))
			playModeButton.setText(TextStorage.getText(Text.ListPage_RepeatPlay));
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
    	DataStorage.musicTime.addListener((observable, oldValue, newValue) -> 
    	{
    		if(!holdingSlider)
    			timeSlider.setValue(newValue.intValue());
    	});
    	DataStorage.musicTotalTime.addListener((observable, oldValue, newValue) -> 
    	{
    		timeSlider.setMax(newValue.intValue());
    	});
    	DataStorage.currentMusicInfo.addListener((observable, oldValue, newValue) ->
    	{
    		if(newValue != null)
    			nameLabel.setText(newValue.name.get());
    		else
    			nameLabel.setText(TextStorage.getText(Text.ListPage_Name));
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
		if((int)timeSlider.getValue() == DataStorage.musicTime.get() || musicPlayer.get() == null)
			return;
		musicPlayer.get().seek((int)timeSlider.getValue());
	}
	
	@FXML
	private void openSettings()
	{
		SceneStorage.getSettingsStage().show();
		SceneStorage.getSettingsStage().toFront();
	}
	
	//可以更精簡。
	@FXML
	private void changePlayMode()
	{
		String playMode = playModeButton.getText();
		if(playMode.equals(TextStorage.getText(Text.ListPage_NormalPlay)))
		{
			PrefStorage.setPref(Pref.PlayMode, PrefStorage.getPrefKey(Pref.RandomPlay));
			playModeButton.setText(TextStorage.getText(Text.ListPage_RandomPlay));
		}
		if(playMode.equals(TextStorage.getText(Text.ListPage_RandomPlay)))
		{
			PrefStorage.setPref(Pref.PlayMode, PrefStorage.getPrefKey(Pref.RepeatPlay));
			playModeButton.setText(TextStorage.getText(Text.ListPage_RepeatPlay));
		}
		if(playMode.equals(TextStorage.getText(Text.ListPage_RepeatPlay)))
		{
			PrefStorage.setPref(Pref.PlayMode , PrefStorage.getPrefKey(Pref.NormalPlay));
			playModeButton.setText(TextStorage.getText(Text.ListPage_NormalPlay));
		}
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
		int index = musicList.indexOf(currentMusicInfo.get());
		if(index == -1)
			return;
		if(index == 0)
			index = musicList.size();
		currentMusicInfo.set(musicList.get(index - 1));
	}
	
	@FXML
	private void nextMusic()
	{
		int index = musicList.indexOf(currentMusicInfo.get());
		if(index == -1)
			return;
		if(index == musicList.size() - 1)
			index = -1;
		currentMusicInfo.set(musicList.get(index + 1));
	}
}
