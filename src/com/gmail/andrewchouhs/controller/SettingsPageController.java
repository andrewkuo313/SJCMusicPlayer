package com.gmail.andrewchouhs.controller;

import java.io.File;
import java.util.Iterator;
import com.gmail.andrewchouhs.model.DirInfo;
import com.gmail.andrewchouhs.storage.PropertyStorage;
import com.gmail.andrewchouhs.storage.SceneStorage;
import com.gmail.andrewchouhs.utils.fliter.DirFilter;
import com.gmail.andrewchouhs.utils.fliter.MusicFilter;
import com.gmail.andrewchouhs.utils.parser.DirParser;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
    	dirInfoTable.setItems(PropertyStorage.dirList);
    	pathColumn.setCellValueFactory(cellData -> cellData.getValue().getPathProperty());
    	localeBox.setItems(FXCollections.observableArrayList("繁體中文" , "English"));
    }
    
    @FXML
    private void okDir()
    {
    	DirParser.save();
    	PropertyStorage.refreshMusicList();
    	SceneStorage.getSettingsStage().close();
    }
    
    @FXML
    private void cancelDir()
    {
    	DirParser.load();
    	SceneStorage.getSettingsStage().close();
    }
    
    @FXML
	private void addDir()
	{
		DirectoryChooser dirChooser = new DirectoryChooser();
		File selectedDir = dirChooser.showDialog(SceneStorage.getSettingsStage());
		
		if(selectedDir != null && selectedDir.isDirectory())
		{
			PropertyStorage.dirList.add(new DirInfo(selectedDir.getAbsolutePath()));
			for(File file : selectedDir.listFiles(new DirFilter()))
				recursiveFoundMusic(file);
		}
	}
    
    private void recursiveFoundMusic(File dirFile)
    {
    	File[] musicFileList = dirFile.listFiles(new MusicFilter());
    	File[] dirFileList = dirFile.listFiles(new DirFilter());
    	if(musicFileList == null && dirFileList == null)
    		return;
    	if(musicFileList.length != 0)
    		PropertyStorage.dirList.add(new DirInfo(dirFile.getAbsolutePath()));
    	for(File file : dirFileList)
    		recursiveFoundMusic(file);
    }
    
    @FXML
    private void removeDir()
    {
    	PropertyStorage.dirList.remove(dirInfoTable.getSelectionModel().getSelectedIndex());
    }
    
    @FXML
    private void removeChildDir()
    {
    	String parentPath = dirInfoTable.getSelectionModel().getSelectedItem().getPath();
    	Iterator<DirInfo> iter = PropertyStorage.dirList.iterator();
    	while(iter.hasNext())
    	{
    		if(iter.next().getPath().startsWith(parentPath))
    			iter.remove();
    	}
    }
    
    @FXML
    private void removeAllDir()
    {
    	PropertyStorage.dirList.clear();
    }
}
