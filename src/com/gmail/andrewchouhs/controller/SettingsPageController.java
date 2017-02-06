package com.gmail.andrewchouhs.controller;

import java.io.File;
import java.util.LinkedHashMap;
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
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import static com.gmail.andrewchouhs.storage.DataStorage.dirList;
import static com.gmail.andrewchouhs.storage.DataStorage.prefs;

public class SettingsPageController
{
	//TreeView 待 MusicInfo 的讀寫完成再大修正。
	@FXML
    private TreeView<DirInfo> dirInfoTreeView;
    @FXML
    private ChoiceBox<String> localeBox;
    
    @FXML
    private void initialize() 
    {
    	dirInfoTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    	SceneStorage.getSettingsStage().setOnShowing((event) -> refreshAll());
    }
    
    private void refreshAll()
    {
    	refreshDirInfoTreeView();
    	ObservableList<String> displayNameList = FXCollections.observableArrayList();
    	for(Locale locale : DataStorage.availableLocales.values())
    		displayNameList.add(locale.getDisplayName(locale));
    	localeBox.setItems(displayNameList);
    	Locale locale = DataStorage.availableLocales.get(prefs.getProperty(DataStorage.Locale));
    	localeBox.setValue(locale.getDisplayName(locale));
    }
    
    private void refreshDirInfoTreeView()
    {
    	TreeItem<DirInfo> root = new TreeItem<DirInfo>(new DirInfo());
    	root.setExpanded(true);
    	String rootest = " ";
    	LinkedHashMap<String , TreeItem<DirInfo>> treeItems = new LinkedHashMap<String , TreeItem<DirInfo>>();
    	for(DirInfo dirInfo : dirList)
    	{
    		TreeItem<DirInfo> treeItem = new TreeItem<DirInfo>(dirInfo);
    		treeItem.setExpanded(true);
    		if(treeItems.containsKey(dirInfo.getPath()))//
    			continue;
    		treeItems.put(dirInfo.getPath() , treeItem);
    		if(!dirInfo.getPath().startsWith(rootest))
    		{
    			rootest = dirInfo.getPath();
    			root.getChildren().add(treeItem);
    			continue;
    		}
    		String detect = dirInfo.getPath();
    		while(true)
    		{
				detect = detect.substring(0 , detect.lastIndexOf("\\"));
    			if(treeItems.containsKey(detect))
    			{
    				treeItems.get(detect).getChildren().add(treeItem);
    				break;
    			}
    		}
    	}
    	dirInfoTreeView.setRoot(root);
    }
    
    @FXML
    private void ok()
    {
    	dirList.clear();
    	for(TreeItem<DirInfo> treeItem : dirInfoTreeView.getRoot().getChildren())
    		recursiveSearchChildren(treeItem);
    	DirParser.save();
    	PropertyStorage.refreshMusicList();
    	SceneStorage.getSettingsStage().close();
    }
    
    private void recursiveSearchChildren(TreeItem<DirInfo> treeItem)
    {
    	dirList.add(treeItem.getValue());
    	System.out.println(treeItem.getValue().getPath());
    	if(treeItem.getChildren() == null)
    		return;
    	for(TreeItem<DirInfo> subTreeItem : treeItem.getChildren())
    		recursiveSearchChildren(subTreeItem);
    }
    
    @FXML
    private void cancel()
    {
//    	DirParser.load();
    	SceneStorage.getSettingsStage().close();
    }
    
    @FXML
	private void addDir()
	{
		File dirFile = new DirectoryChooser().showDialog(SceneStorage.getSettingsStage());
		if(dirFile != null && dirFile.isDirectory())
		{
			if(!recursiveFoundMusic(dirFile))
			{
				System.out.println("選擇資料夾無音樂檔案");
			}
			else
				refreshDirInfoTreeView();
		}
	}
    
    private boolean recursiveFoundMusic(File dirFile)
    {
    	boolean orLogicGate = false;
    	File[] musicFileList = dirFile.listFiles(new MusicFilter());
    	File[] dirFileList = dirFile.listFiles(new DirFilter());
    	if(musicFileList == null && dirFileList == null)
    		return false;
    	DirInfo dirInfo = new DirInfo(dirFile.getAbsolutePath());
		dirList.add(dirInfo);
    	for(File file : dirFileList)
    		orLogicGate = recursiveFoundMusic(file) || orLogicGate;
    	if(musicFileList.length != 0)
    		orLogicGate = true;
    	if(!orLogicGate)
    		dirList.remove(dirInfo);
    	return orLogicGate;
    }
    
    //需修復多重選擇異常的問題。
    @FXML
    private void removeDir()
    {
    	for(TreeItem<DirInfo> treeItem : dirInfoTreeView.getSelectionModel().getSelectedItems())
        {
        	treeItem.getParent().getChildren().removeAll(dirInfoTreeView.getSelectionModel().getSelectedItems());	
        }
    }
}
