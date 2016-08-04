package com.gmail.andrewchouhs.controller;

import com.gmail.andrewchouhs.Storage;
import com.gmail.andrewchouhs.model.MusicInfo;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;

public class PlayingPageController
{
    @FXML
    private TableView<MusicInfo> musicInfoTable;
    @FXML
    private TableColumn<MusicInfo, String> nameColumn;
    @FXML
    private TableColumn<MusicInfo, String> artistColumn;
    @FXML
    private TableColumn<MusicInfo, String> albumColumn;
    @FXML
    private Label titleLabel;
    @FXML
    private Label artistLabel;
    @FXML
    private Label albumLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Slider timeSlider;
    @FXML
    private Button playButton;
    @FXML
    private Button beforeButton;
    @FXML
    private Button afterButton;
    @FXML
    private Button repeatButton;
    @FXML
    private ImageView imageView;

    @FXML
    private void initialize() 
    {
    	musicInfoTable.setItems(Storage.musicInfoList);
    	//setCellValueFactory need to be learned
    	nameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
    	artistColumn.setCellValueFactory(cellData -> cellData.getValue().artist);
    	albumColumn.setCellValueFactory(cellData -> cellData.getValue().album);
    	musicInfoTable.getSelectionModel().selectedItemProperty().addListener
    	( (observable, oldValue, newValue) -> playMusic(newValue) );
    	Storage.musicInfo.addListener((observable , oldValue , newValue) -> refreshMusicInfo(newValue));
    }
    
    private void playMusic(MusicInfo musicInfo)
    {
    	Storage.musicInfo.set(musicInfo);
    	//Focus had no answer
    	musicInfoTable.scrollTo(musicInfoTable.getSelectionModel().getSelectedIndex());
    }
    
    private void refreshMusicInfo(MusicInfo musicInfo)
    {
    	Storage.player.play(musicInfo.path.get());
    	titleLabel.setText("Title: " + musicInfo.name.get());
    	albumLabel.setText("Album: " + musicInfo.album.get());
    	artistLabel.setText("Artist: " + musicInfo.artist.get());
    	imageView.setImage(musicInfo.image.get());
    }
}
