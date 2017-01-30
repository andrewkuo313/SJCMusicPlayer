package com.gmail.andrewchouhs.controller;

import java.io.File;
import java.util.Iterator;
import com.gmail.andrewchouhs.Storage;
import com.gmail.andrewchouhs.model.DirInfo;
import com.gmail.andrewchouhs.utils.DirFilter;
import com.gmail.andrewchouhs.utils.DirXMLParser;
import com.gmail.andrewchouhs.utils.MusicFileFilter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;

public class SettingsPageController
{
	@FXML
    private TableView<DirInfo> dirInfoTable;
    @FXML
    private TableColumn<DirInfo, String> pathColumn;
    @FXML
    private ChoiceBox<String> localeBox;
    
    @FXML
    private void initialize() 
    {
    	dirInfoTable.setItems(Storage.dirList);
    	pathColumn.setCellValueFactory(cellData -> cellData.getValue().path);
    	localeBox.setItems(FXCollections.observableArrayList("繁體中文" , "English"));
    }
    
    @FXML
    private void okDir()
    {
    	DirXMLParser.save();
    	Storage.refreshMusicInfoList();
    	Storage.getSettingsStage().close();
    }
    
    @FXML
    private void cancelDir()
    {
    	DirXMLParser.load();
    	Storage.getSettingsStage().close();
    }
    
    @FXML
	private void addDir()
	{
		DirectoryChooser dirChooser = new DirectoryChooser();
		File selectedDir = dirChooser.showDialog(Storage.getSettingsStage());
		
		if(selectedDir != null && selectedDir.isDirectory())
		{
			Storage.dirList.add(new DirInfo(selectedDir.getAbsolutePath()));
			for(File file : selectedDir.listFiles(new DirFilter()))
				recursiveFoundMusic(file);
		}
	}
    
    private void recursiveFoundMusic(File dirFile)
    {
    	File[] musicFileList = dirFile.listFiles(new MusicFileFilter());
    	File[] dirFileList = dirFile.listFiles(new DirFilter());
    	if(musicFileList == null && dirFileList == null)
    		return;
    	if(musicFileList.length != 0)
    		Storage.dirList.add(new DirInfo(dirFile.getAbsolutePath()));
    	for(File file : dirFileList)
    		recursiveFoundMusic(file);
    }
    
    @FXML
    private void removeDir()
    {
    	Storage.dirList.remove(dirInfoTable.getSelectionModel().getSelectedIndex());
    }
    
    @FXML
    private void removeChildDir()
    {
    	String parentPath = dirInfoTable.getSelectionModel().getSelectedItem().path.get();
    	Iterator<DirInfo> iter = Storage.dirList.iterator();
    	while(iter.hasNext())
    	{
    		if(iter.next().path.get().startsWith(parentPath))
    			iter.remove();
    	}
    }
    
    @FXML
    private void removeAllDir()
    {
    	Storage.dirList.clear();
    }
}
