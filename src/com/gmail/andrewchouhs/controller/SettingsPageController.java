package com.gmail.andrewchouhs.controller;

import java.io.File;
import java.util.Iterator;
import java.util.Locale;
import com.gmail.andrewchouhs.model.DirInfo;
import com.gmail.andrewchouhs.storage.DataStorage;
import com.gmail.andrewchouhs.storage.PropertyStorage;
import com.gmail.andrewchouhs.storage.SceneStorage;
import com.gmail.andrewchouhs.utils.fliter.DirFilter;
import com.gmail.andrewchouhs.utils.fliter.MusicFilter;
import com.gmail.andrewchouhs.utils.parser.DirParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.DirectoryChooser;
import static com.gmail.andrewchouhs.storage.PropertyStorage.dirList;
import static com.gmail.andrewchouhs.storage.DataStorage.prefs;

public class SettingsPageController
{
	//改用TreeTableView
	@FXML
    private TableView<DirInfo> dirInfoTable;
    @FXML
    private TableColumn<DirInfo, String> pathColumn;
    @FXML
    private ChoiceBox<String> localeBox;
    
    @FXML
    private void initialize() 
    {
    	dirInfoTable.setItems(dirList);
    	pathColumn.setCellValueFactory(cellData -> cellData.getValue().getPathProperty());
    	ObservableList<String> displayNameList = FXCollections.observableArrayList();
    	for(Locale locale : DataStorage.availableLocales.values())
    		displayNameList.add(locale.getDisplayName(locale));
    	localeBox.setItems(displayNameList);
    	Locale locale = DataStorage.availableLocales.get(prefs.getProperty(DataStorage.Locale));
    	localeBox.setValue(locale.getDisplayName(locale));
    }
    
    @FXML
    private void ok()
    {
    	DirParser.save();
    	PropertyStorage.refreshMusicList();
    	SceneStorage.getSettingsStage().close();
    }
    
    @FXML
    private void cancel()
    {
    	DirParser.load();
    	SceneStorage.getSettingsStage().close();
    }
    
    @FXML
	private void addDir()
	{
		File dirFile = new DirectoryChooser().showDialog(SceneStorage.getSettingsStage());
		if(dirFile != null && dirFile.isDirectory())
		{
			dirList.add(new DirInfo(dirFile.getAbsolutePath()));
			for(File file : dirFile.listFiles(new DirFilter()))
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
    		dirList.add(new DirInfo(dirFile.getAbsolutePath()));
    	for(File file : dirFileList)
    		recursiveFoundMusic(file);
    }
    
    @FXML
    private void removeDir()
    {
    	dirList.remove(dirInfoTable.getSelectionModel().getSelectedIndex());
    }
    
    @FXML
    private void removeChildDir()
    {
    	String parentPath = dirInfoTable.getSelectionModel().getSelectedItem().getPath();
    	Iterator<DirInfo> iter = dirList.iterator();
    	while(iter.hasNext())
    	{
    		if(iter.next().getPath().startsWith(parentPath))
    			iter.remove();
    	}
    }
    
    @FXML
    private void removeAllDir()
    {
    	dirList.clear();
    }
}
