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
    	nameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
    	artistColumn.setCellValueFactory(cellData -> cellData.getValue().artist);
    	albumColumn.setCellValueFactory(cellData -> cellData.getValue().album);
    	//setCellValueFactory need to be learned
    	musicInfoTable.setItems(Storage.musicInfoList);
    	musicInfoTable.getSelectionModel().selectedItemProperty().addListener
    	( (observable, oldValue, newValue) -> playMusic(newValue) );
    	Storage.musicInfo.addListener((observable , oldValue , newValue) -> setComponent(newValue));
    }
    
    private void playMusic(MusicInfo musicInfo)
    {
    	Storage.player.play(musicInfo.path.get());
    	//Focus had no answer
    	musicInfoTable.scrollTo(musicInfoTable.getSelectionModel().getSelectedIndex());
    }
    
    private void setComponent(MusicInfo musicInfo)
    {
    	titleLabel.setText("Title: " + musicInfo.name.get());
    	albumLabel.setText("Album: " + musicInfo.album.get());
    	artistLabel.setText("Artist: " + musicInfo.artist.get());
    	imageView.setImage(musicInfo.image.get());
    }
}
