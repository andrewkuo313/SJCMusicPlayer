package com.gmail.andrewchouhs.controller;

import com.gmail.andrewchouhs.Storage;
import com.gmail.andrewchouhs.model.MusicInfo;
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
    	musicInfoTable.setItems(Storage.musicInfoList);
    	//setCellValueFactory need to be learned
    	nameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
    	artistColumn.setCellValueFactory(cellData -> cellData.getValue().artist);
    	albumColumn.setCellValueFactory(cellData -> cellData.getValue().album);
    	musicInfoTable.getSelectionModel().selectedItemProperty().addListener
    	( (observable, oldValue, newValue) -> Storage.musicInfo.set(newValue));
    }
}
