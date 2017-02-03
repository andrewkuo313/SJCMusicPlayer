package com.gmail.andrewchouhs.controller;

import com.gmail.andrewchouhs.model.MusicInfo;
import com.gmail.andrewchouhs.storage.PropertyStorage;
import javafx.application.Platform;
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
    	musicInfoTable.setItems(PropertyStorage.musicList);
    	musicInfoTable.getSelectionModel().selectedItemProperty().addListener
    	((observable, oldValue, newValue) -> PropertyStorage.musicInfo.set(newValue));
    	
    	nameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
    	artistColumn.setCellValueFactory(cellData -> cellData.getValue().artist);
    	albumColumn.setCellValueFactory(cellData -> cellData.getValue().album);
    	
    	//無法使用 VirtualFlow 的問題尚待更新版本。
    	PropertyStorage.musicInfo.addListener((observable, oldValue, newValue) -> 
    		Platform.runLater(() -> musicInfoTable.getSelectionModel().select(PropertyStorage.musicList.indexOf(newValue))));
	}
}
