package com.gmail.andrewchouhs.controller;

import com.gmail.andrewchouhs.model.MusicInfo;
import com.gmail.andrewchouhs.storage.DataStorage;
import com.gmail.andrewchouhs.storage.TextStorage;
import com.gmail.andrewchouhs.storage.TextStorage.Text;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ListPageController
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
	private void initialize() 
    {
    	musicInfoTable.setItems(DataStorage.musicList);
    	musicInfoTable.getSelectionModel().selectedItemProperty().addListener
    	((observable, oldValue, newValue) -> DataStorage.musicInfo.set(newValue));
    	StringProperty nullValue = new SimpleStringProperty(TextStorage.getText(Text.ListPage_NullValue));
    	nameColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
    	artistColumn.setCellValueFactory(cellData -> 
    	(cellData.getValue().getArtistProperty().get() != null) ? cellData.getValue().getArtistProperty() : nullValue);
    	albumColumn.setCellValueFactory(cellData -> 
    	(cellData.getValue().getAlbumProperty().get() != null) ? cellData.getValue().getAlbumProperty() : nullValue);
    	//無法使用 VirtualFlow 的問題尚待更新版本。
    	DataStorage.musicInfo.addListener((observable, oldValue, newValue) -> 
    		Platform.runLater(() -> musicInfoTable.getSelectionModel().select(DataStorage.musicList.indexOf(newValue))));
	}
}
