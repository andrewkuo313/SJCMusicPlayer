package com.gmail.andrewchouhs.controller;

import com.gmail.andrewchouhs.model.MusicInfo;
import com.gmail.andrewchouhs.storage.DataStorage;
import com.gmail.andrewchouhs.storage.MusicStorage;
import com.gmail.andrewchouhs.storage.TextStorage;
import com.gmail.andrewchouhs.storage.TextStorage.Text;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;

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
	private TableColumn<MusicInfo, String> bitrateColumn;
	@FXML
	private TableColumn<MusicInfo, String> yearColumn;
	@FXML
	private TableColumn<MusicInfo, String> pathColumn;
	@FXML
	private TabPane tagPane;

    @FXML
	private void initialize() 
    {
    	musicInfoTable.setItems(MusicStorage.musicList);
    	musicInfoTable.getSelectionModel().selectedItemProperty().addListener
    	((observable, oldValue, newValue) -> DataStorage.currentMusicInfo.set(newValue));
    	StringProperty nullValue = new SimpleStringProperty(TextStorage.getText(Text.ListPage_NullValue));
    	nameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
    	artistColumn.setCellValueFactory(cellData -> 
    	(cellData.getValue().artist.get() != null) ? cellData.getValue().artist : nullValue);
    	albumColumn.setCellValueFactory(cellData -> 
    	(cellData.getValue().album.get() != null) ? cellData.getValue().album : nullValue);
    	bitrateColumn.setCellValueFactory(cellData -> 
    	(cellData.getValue().bitrate.get() != null) ? cellData.getValue().bitrate : nullValue);
    	yearColumn.setCellValueFactory(cellData -> 
    	(cellData.getValue().year.get() != null) ? cellData.getValue().year : nullValue);
    	pathColumn.setCellValueFactory(cellData -> 
    	(cellData.getValue().path.get() != null) ? cellData.getValue().path : nullValue);
    	//無法使用 VirtualFlow 的問題尚待更新版本。
    	DataStorage.currentMusicInfo.addListener((observable, oldValue, newValue) -> 
    		Platform.runLater(() -> musicInfoTable.getSelectionModel().select(MusicStorage.musicList.indexOf(newValue))));
	}
    
    @FXML
    private void addNewTagFolder()
    {
    	Tab tab = new Tab("Custom");
    	ScrollPane scrollPane = new ScrollPane();
    	scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
    	scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
    	scrollPane.setPannable(true);
    	FlowPane flowPane = new FlowPane();
    	scrollPane.setContent(flowPane);
    	tab.setContent(scrollPane);
    	tagPane.getTabs().add(tab);
    }
}
