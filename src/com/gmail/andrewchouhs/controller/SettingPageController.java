package com.gmail.andrewchouhs.controller;

import java.io.File;
import java.util.Iterator;
import com.gmail.andrewchouhs.Storage;
import com.gmail.andrewchouhs.model.DirInfo;
import com.gmail.andrewchouhs.utils.DirFilter;
import com.gmail.andrewchouhs.utils.MusicFileFilter;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.DirectoryChooser;

public class SettingPageController
{
	@FXML
    private TableView<DirInfo> dirInfoTable;
    @FXML
    private TableColumn<DirInfo, String> pathColumn;
    
    @FXML
    private void initialize() 
    {
    	dirInfoTable.setItems(Storage.dirList);
    	//setCellValueFactory need to be learned
    	pathColumn.setCellValueFactory(cellData -> cellData.getValue().path);
    }
    
    @FXML
    private void okDir()
    {
    	Storage.refreshMusicInfoList();
    	Storage.getSettingStage().close();
    }
    
    @FXML
    private void cancelDir()
    {
    	Storage.getSettingStage().close();
    }
    
    @FXML
	private void addDir()
	{
		DirectoryChooser dirChooser = new DirectoryChooser();
		File selectedDir = dirChooser.showDialog(Storage.getSettingStage());
		
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
    private void removeChildrenDir()
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
