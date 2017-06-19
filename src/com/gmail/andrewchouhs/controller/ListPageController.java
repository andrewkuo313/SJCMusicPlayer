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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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

    	ScrollPane scrollPane = new ScrollPane();
    	FlowPane flowPane = new FlowPane();
    	flowPane.setAlignment(Pos.TOP_CENTER);
    	flowPane.setVgap(15);
    	flowPane.setPadding(new Insets(20 , 0 , 20 , 0));
    	scrollPane.setContent(flowPane);
    	Tab tab = new Tab();
    	tab.setContent(scrollPane);
    	tagPane.getTabs().add(tab);
    	tagPane.getSelectionModel().select(tab);
    	renameTag(tab);
    }
    
    private void renameTag(Tab tab)
    {
    	TextField textField = new TextField(tab.getText());
    	tab.setText(null);
    	tab.setGraphic(textField);
    	//暫時的寫法。
    	textField.sceneProperty().addListener((observable , oldValue , newValue) -> textField.requestFocus());
    	textField.focusedProperty().addListener((observable , oldValue , newValue) -> 
    	{
    		if(newValue == false)
    		{
    			tab.setGraphic(null);
    			tab.setText(textField.getText());
    		}
    	});
    	textField.setOnKeyPressed((event) -> 
    	{
    		if(event.getCode() == KeyCode.ENTER)
    			//暫時的寫法。
    			textField.getParent().requestFocus();
    	});
    }
    
    @FXML
    private void addNewTag()
    {
    	FlowPane flowPane = (FlowPane)((ScrollPane)tagPane.getSelectionModel().getSelectedItem().getContent())
    			.getContent();
    	Rectangle rect = new Rectangle(20 , 50);
    	rect.setFill(Color.BLACK);
    	rect.setArcHeight(10);
    	rect.setArcWidth(10);
    	rect.setOnMouseClicked((event) -> 
    	{
    		switch(event.getButton())
    		{
    			case PRIMARY:
    				if(rect.getEffect() == null)
    				{
    					DropShadow dropShadow = new DropShadow();
    					dropShadow.setRadius(20);
    					dropShadow.setSpread(0.5);
    					dropShadow.setHeight(25);
    					dropShadow.setWidth(25);
    					dropShadow.setColor(Color.LIGHTGREEN);
    					rect.setEffect(dropShadow);
    				}
    				else
    					rect.setEffect(null);
    				break;
    			case SECONDARY:
    				if(rect.getEffect() == null)
    				{
    					DropShadow dropShadow = new DropShadow();
    					dropShadow.setRadius(20);
    					dropShadow.setSpread(0.5);
    					dropShadow.setHeight(25);
    					dropShadow.setWidth(25);
    					dropShadow.setColor(Color.RED);
    					rect.setEffect(dropShadow);
    				}
    				else
    					rect.setEffect(null);
    				break;
    			default:
    		}
    		
    	});
    	rect.widthProperty().bind(flowPane.widthProperty().subtract(20));
    	flowPane.getChildren().add(rect);
    }
}
