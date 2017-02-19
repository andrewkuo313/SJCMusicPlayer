package com.gmail.andrewchouhs.controller;

import java.io.File;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import com.gmail.andrewchouhs.storage.DataStorage;
import com.gmail.andrewchouhs.storage.PrefStorage;
import com.gmail.andrewchouhs.storage.PrefStorage.Pref;
import com.gmail.andrewchouhs.storage.SceneStorage;
import com.gmail.andrewchouhs.utils.DirTreeItem;
import com.gmail.andrewchouhs.utils.MusicTreeMap;
import com.gmail.andrewchouhs.utils.fliter.DirFilter;
import com.gmail.andrewchouhs.utils.fliter.MusicFilter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import javafx.util.StringConverter;
import static com.gmail.andrewchouhs.storage.DataStorage.musicTreeMap;

public class SettingsPageController
{
	//TreeView 需增加刷新功能、增加無資料夾提示、排序。
	@FXML
    private TreeView<String> dirInfoTreeView;
	@FXML
	private CheckBox startWhenOpeningPC;
	@FXML
	private CheckBox playWhenOpeningApp;
	@FXML
	private CheckBox autoUpdate;
	@FXML
	private CheckBox notifyUpdate;
    @FXML
    private ComboBox<Locale> localeBox;
    @FXML
    private Label versionLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label articleLabel;
    
    @FXML
    private void initialize() 
    {
    	dirInfoTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    	ObservableList<Locale> localeList = FXCollections.observableArrayList();
    	localeList.addAll(PrefStorage.localeMap.values());
    	localeBox.setItems(localeList);
    	localeBox.setConverter(new StringConverter<Locale>()
    	{
    		//可能會造成 Bug。
			@Override
			public Locale fromString(String arg0)
			{
				return null;
			}

			@Override
			public String toString(Locale locale)
			{
				return locale.getDisplayName(locale);
			}
    	});
    	DataStorage.updateInfo.addListener((observable , oldValue , newValue) -> 
    	{
    		versionLabel.setText(newValue.getVersion());
    		dateLabel.setText(newValue.getDate());
    		articleLabel.setText(newValue.getArticle());
    	});
    	SceneStorage.getSettingsStage().setOnShowing((event) -> refreshAll());
    }
    
    private void refreshAll()
    {
    	refreshPreferences();
    	DataStorage.loadMusicTreeMap();
    	dirInfoTreeView.setRoot(recursiveRefreshDirTreeItem("" , musicTreeMap));
    }
    
    private DirTreeItem recursiveRefreshDirTreeItem(String path , MusicTreeMap parentMusicTreeMap)
    {
    	parentMusicTreeMap.treeItem = new DirTreeItem(
    			path.substring(path.lastIndexOf(File.separator) + 1 , path.length()) , parentMusicTreeMap , path);
    	for(Map.Entry<String, MusicTreeMap> childEntry : parentMusicTreeMap.entrySet())
    		parentMusicTreeMap.treeItem.getChildren().add(recursiveRefreshDirTreeItem(childEntry.getKey() , childEntry.getValue()));
    	return parentMusicTreeMap.treeItem;
    }
    
    private void refreshPreferences()
    {
    	startWhenOpeningPC.setSelected(Boolean.valueOf(PrefStorage.getPref(Pref.StartWhenOpeningPC)));
    	playWhenOpeningApp.setSelected(Boolean.valueOf(PrefStorage.getPref(Pref.PlayWhenOpeningApp)));
    	autoUpdate.setSelected(Boolean.valueOf(PrefStorage.getPref(Pref.AutoUpdate)));
    	notifyUpdate.setSelected(Boolean.valueOf(PrefStorage.getPref(Pref.NotifyUpdate)));
    	localeBox.setValue(PrefStorage.localeMap.get(PrefStorage.getPref(Pref.Language)));
    }
    
    //AudioFileFormat 時快時慢，須找到原因。
    @FXML
    private void ok()
    {
    	PrefStorage.setPref(Pref.StartWhenOpeningPC, Boolean.toString(startWhenOpeningPC.isSelected()));
    	PrefStorage.setPref(Pref.PlayWhenOpeningApp, Boolean.toString(playWhenOpeningApp.isSelected()));
    	PrefStorage.setPref(Pref.AutoUpdate , Boolean.toString(autoUpdate.isSelected()));
    	PrefStorage.setPref(Pref.NotifyUpdate, Boolean.toString(notifyUpdate.isSelected()));
    	PrefStorage.setPref(Pref.Language, localeBox.getValue().toString());
    	PrefStorage.save();
    	DataStorage.saveMusicTreeMap();
    	DataStorage.refreshMusicMap();
        SceneStorage.getSettingsStage().close();
    }
    
    @FXML
    private void cancel()
    {
    	SceneStorage.getSettingsStage().close();
    }
    
    @FXML
	private void addDir()
	{
		File dirFile = new DirectoryChooser().showDialog(SceneStorage.getSettingsStage());
		if(dirFile != null && dirFile.isDirectory())
		{
			File parentFile = dirFile;
			LinkedList<File> parentFiles = new LinkedList<File>();
			LinkedList<File> preloadFiles = new LinkedList<File>();
			while(parentFile != null)
			{
				parentFiles.addFirst(parentFile);
				if(musicTreeMap.containsKey(parentFile.getAbsolutePath()))
					break;
				parentFile = parentFile.getParentFile();
			}
			MusicTreeMap childMusicTreeMap = musicTreeMap;
			for(File file : parentFiles)
			{
				String path = file.getAbsolutePath();
				if(childMusicTreeMap.containsKey(path))
				{
					childMusicTreeMap = childMusicTreeMap.get(path);
					dirFile = file;
				}
				else
					preloadFiles.add(file);
			}
			if(!preloadFiles.isEmpty() && preloadFiles.getLast().equals(dirFile))
				preloadFiles = new LinkedList<File>();
			DirTreeItem parentTreeItem = (DirTreeItem)childMusicTreeMap.treeItem.getParent();
			if(parentTreeItem != null)
				childMusicTreeMap = parentTreeItem.musicTreeMap;
			if(recursiveFoundMusic(dirFile , preloadFiles , childMusicTreeMap))
				dirInfoTreeView.setRoot(recursiveRefreshDirTreeItem("" , musicTreeMap));
			else
			{
				System.out.println("選擇資料夾無音樂檔案");
			}
		}
	}
    
    private boolean recursiveFoundMusic(File dirFile , LinkedList<File> preloadFiles , MusicTreeMap parentMusicTreeMap)
    {
    	boolean orLogicGate = false;
    	File[] musicFileList = dirFile.listFiles(new MusicFilter());
    	File[] dirFileList = dirFile.listFiles(new DirFilter());
    	if(musicFileList == null && (dirFileList == null || preloadFiles.isEmpty()))
    		return false;
    	String path = dirFile.getAbsolutePath();
    	MusicTreeMap childMusicTreeMap;
//    	避免重複 new 節省時間，但待測，且可能導致不刷新。
    	if(parentMusicTreeMap.containsKey(path))
    		childMusicTreeMap = parentMusicTreeMap.get(path);
    	else
    	{
    		childMusicTreeMap = new MusicTreeMap();
        	parentMusicTreeMap.put(path , childMusicTreeMap);
    	}
    	if(preloadFiles.isEmpty())
    	{
    		for(File file : dirFileList)
    			orLogicGate = recursiveFoundMusic(file , preloadFiles , childMusicTreeMap) || orLogicGate;
    	}
    	else
    		orLogicGate = recursiveFoundMusic(preloadFiles.removeFirst() , preloadFiles , childMusicTreeMap) || orLogicGate;
    	if(musicFileList.length != 0)
    		orLogicGate = true;
    	if(!orLogicGate)
    		parentMusicTreeMap.remove(path);
    	return orLogicGate;
    }
    
    @FXML
    private void removeDir()
    {
    	LinkedList<DirTreeItem> treeItemList = new LinkedList<DirTreeItem>();
    	for(TreeItem<String> treeItem : dirInfoTreeView.getSelectionModel().getSelectedItems())
    		treeItemList.add((DirTreeItem)treeItem);
    	for(DirTreeItem treeItem : treeItemList)
    	{
    		ObservableList<TreeItem<String>> parentTreeItemChildren = treeItem.getParent().getChildren();
    		if(parentTreeItemChildren.contains(treeItem))
    		{
    			((DirTreeItem)treeItem.getParent()).musicTreeMap.remove(treeItem.path);
    			parentTreeItemChildren.remove(treeItem);
    		}
    	}
    }
}