package com.gmail.andrewchouhs.controller;

import java.io.File;
import java.util.LinkedList;
import java.util.Locale;
import com.gmail.andrewchouhs.storage.DataStorage;
import com.gmail.andrewchouhs.storage.MusicStorage;
import com.gmail.andrewchouhs.storage.PrefStorage;
import com.gmail.andrewchouhs.storage.PrefStorage.Pref;
import com.gmail.andrewchouhs.storage.SceneStorage;
import com.gmail.andrewchouhs.utils.MusicTreeMap;
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

public class SettingsPageController
{
	//TreeView 需增加刷新功能、增加無資料夾提示、排序。
	//TreeView 有重新開啟會變黑的問題。
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
    private MusicTreeMap dummyMusicTreeMap;
    
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
    	MusicStorage.recursiveRefreshMusicTreeMap(MusicStorage.musicTreeMap);
    	dummyMusicTreeMap = recursiveConvertToDummyMusicTreeMap(MusicStorage.musicTreeMap , new MusicTreeMap("" , null));
    	dirInfoTreeView.setRoot(recursiveAddTreeItem(dummyMusicTreeMap));
    }
    
    private MusicTreeMap recursiveConvertToDummyMusicTreeMap(MusicTreeMap parentMusicTreeMap , MusicTreeMap parentDummyMusicTreeMap)
    {
    	for(MusicTreeMap childMusicTreeMap : parentMusicTreeMap.values())
    	{
    		MusicTreeMap childDummyMusicTreeMap = new MusicTreeMap(childMusicTreeMap.path , parentDummyMusicTreeMap);
        	childDummyMusicTreeMap.visible = childMusicTreeMap.visible;
        	childDummyMusicTreeMap.ignored = childMusicTreeMap.ignored;
    		parentDummyMusicTreeMap.put(childMusicTreeMap.path , 
    				recursiveConvertToDummyMusicTreeMap(childMusicTreeMap , childDummyMusicTreeMap));
    	}
    	return parentDummyMusicTreeMap;
    }
   
    private void recursiveConvertToMusicTreeMap(MusicTreeMap parentMusicTreeMap , MusicTreeMap parentDummyMusicTreeMap)
    {
    	for(MusicTreeMap childDummyMusicTreeMap : parentDummyMusicTreeMap.values())
    	{
    		MusicTreeMap childMusicTreeMap;
    		if(parentMusicTreeMap.containsKey(childDummyMusicTreeMap.path))
    			childMusicTreeMap = parentMusicTreeMap.get(childDummyMusicTreeMap.path);
    		else
    		{
    			childMusicTreeMap = new MusicTreeMap(childDummyMusicTreeMap.path , parentMusicTreeMap);
    			parentMusicTreeMap.put(childDummyMusicTreeMap.path , childMusicTreeMap);
    		}
    		childMusicTreeMap.visible = childDummyMusicTreeMap.visible;
    		childMusicTreeMap.ignored = childDummyMusicTreeMap.ignored;
    		recursiveConvertToMusicTreeMap(childMusicTreeMap , childDummyMusicTreeMap);
    	}
    }
    
    private TreeItem<String> recursiveAddTreeItem(MusicTreeMap parentMusicTreeMap)
    {
    	String path = parentMusicTreeMap.path;
    	path = parentMusicTreeMap.path.substring(path.lastIndexOf(File.separator) + 1 , path.length());
    	if(path.length() == 0)
    		path = parentMusicTreeMap.path;
    	TreeItem<String> treeItem =  new TreeItem<String>(path);
    	for(MusicTreeMap childMusicTreeMap : parentMusicTreeMap.values())
    	{
    		if(childMusicTreeMap.visible)
    			treeItem.getChildren().add(recursiveAddTreeItem(childMusicTreeMap));
    	}
    	return treeItem;
    }
    
    private void refreshPreferences()
    {
    	startWhenOpeningPC.setSelected(Boolean.valueOf(PrefStorage.getPref(Pref.StartWhenOpeningPC)));
    	playWhenOpeningApp.setSelected(Boolean.valueOf(PrefStorage.getPref(Pref.PlayWhenOpeningApp)));
    	autoUpdate.setSelected(Boolean.valueOf(PrefStorage.getPref(Pref.AutoUpdate)));
    	notifyUpdate.setSelected(Boolean.valueOf(PrefStorage.getPref(Pref.NotifyUpdate)));
    	localeBox.setValue(PrefStorage.localeMap.get(PrefStorage.getPref(Pref.Language)));
    }
    
    @FXML
    private void ok()
    {
    	PrefStorage.setPref(Pref.StartWhenOpeningPC, Boolean.toString(startWhenOpeningPC.isSelected()));
    	PrefStorage.setPref(Pref.PlayWhenOpeningApp, Boolean.toString(playWhenOpeningApp.isSelected()));
    	PrefStorage.setPref(Pref.AutoUpdate , Boolean.toString(autoUpdate.isSelected()));
    	PrefStorage.setPref(Pref.NotifyUpdate, Boolean.toString(notifyUpdate.isSelected()));
    	PrefStorage.setPref(Pref.Language, localeBox.getValue().toString());
    	PrefStorage.save();
    	recursiveConvertToMusicTreeMap(MusicStorage.musicTreeMap , dummyMusicTreeMap);
    	MusicStorage.refreshMusicMap();
    	MusicStorage.saveMusicTreeMap();
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
				parentFile = parentFile.getParentFile();
			}
			dirFile = parentFiles.getFirst();
			MusicTreeMap childMusicTreeMap = dummyMusicTreeMap;
			for(File file : parentFiles)
			{
				String path = file.getAbsolutePath();
				if(childMusicTreeMap.containsKey(path) && childMusicTreeMap.visible)
				{
					childMusicTreeMap = childMusicTreeMap.get(path);
					dirFile = file;
				}
				else
					preloadFiles.add(file);
			}
			if(!preloadFiles.isEmpty() && preloadFiles.getFirst().equals(parentFiles.getFirst()))
				preloadFiles.removeFirst();
			if(childMusicTreeMap.parent != null)
				childMusicTreeMap = childMusicTreeMap.parent;
			if(MusicStorage.recursiveFoundMusic(dirFile , preloadFiles , childMusicTreeMap , true))
				dirInfoTreeView.setRoot(recursiveAddTreeItem(dummyMusicTreeMap));
			else
			{
				System.out.println("選擇資料夾無音樂檔案");
			}
		}
	}
    
    @FXML
    private void removeDir()
    {
    	LinkedList<TreeItem<String>> treeItemList = new LinkedList<TreeItem<String>>();
    	for(TreeItem<String> treeItem : dirInfoTreeView.getSelectionModel().getSelectedItems())
    		treeItemList.add(treeItem);
    	for(TreeItem<String> treeItem : treeItemList)
    	{
    		ObservableList<TreeItem<String>> parentTreeItemChildren = treeItem.getParent().getChildren();
    		if(parentTreeItemChildren.contains(treeItem))
    		{
    			LinkedList<String> splitPath = new LinkedList<String>();
    			TreeItem<String> parentTreeItem = treeItem;
    			while(parentTreeItem != null)
    			{
    				splitPath.addFirst(parentTreeItem.getValue());
    				parentTreeItem = parentTreeItem.getParent();
    			}
    			splitPath.removeFirst();
    			MusicTreeMap parentDummyMusicTreeMap = dummyMusicTreeMap;
    			String currentPath = splitPath.removeFirst();
    			if(parentDummyMusicTreeMap.containsKey(currentPath))//須找到更好的寫法。
					parentDummyMusicTreeMap = parentDummyMusicTreeMap.get(currentPath);
    			currentPath = currentPath.substring(0 , currentPath.length() - 1);//須找到更好的寫法。
    			for(String s : splitPath)
    			{
    				if(parentDummyMusicTreeMap.containsKey(currentPath))
    					parentDummyMusicTreeMap = parentDummyMusicTreeMap.get(currentPath);
    				currentPath+=(File.separator + s);
    			}
    			if(parentDummyMusicTreeMap.containsKey(currentPath))
    				recursiveSetValue(parentDummyMusicTreeMap.get(currentPath));
    			parentTreeItemChildren.remove(treeItem);
    		}
    	}
    }
    
    private void recursiveSetValue(MusicTreeMap parentDummyMusicTreeMap)
    {
    	parentDummyMusicTreeMap.visible = false;
    	parentDummyMusicTreeMap.ignored = true;
    	for(MusicTreeMap childDummyMusicTreeMap : parentDummyMusicTreeMap.values())
    		recursiveSetValue(childDummyMusicTreeMap);
    }
}