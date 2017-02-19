package com.gmail.andrewchouhs.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import org.tritonus.share.sampled.file.TAudioFileFormat;
import com.gmail.andrewchouhs.model.MusicInfo;
import com.gmail.andrewchouhs.storage.DataStorage;
import com.gmail.andrewchouhs.storage.PropertyStorage;
import com.gmail.andrewchouhs.storage.SceneStorage;
import com.gmail.andrewchouhs.utils.DirTreeItem;
import com.gmail.andrewchouhs.utils.MusicTreeMap;
import com.gmail.andrewchouhs.utils.fliter.DirFilter;
import com.gmail.andrewchouhs.utils.fliter.MusicFilter;
import com.gmail.andrewchouhs.utils.parser.PrefsParser;
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
import static com.gmail.andrewchouhs.storage.DataStorage.prefs;

public class SettingsPageController
{
	//TreeView 需增加刷新功能、增加無資料夾提示、減省名稱重新檢查一次此頁程式碼。
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
    //需決定其與其相關方法存放地點。
    private MusicTreeMap musicTreeMap = new MusicTreeMap(new DirTreeItem("" , null , null));
    
    @FXML
    private void initialize() 
    {
    	dirInfoTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    	ObservableList<Locale> localeList = FXCollections.observableArrayList();
    	localeList.addAll(DataStorage.availableLocales.values());
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
    	PropertyStorage.updateInfo.addListener((observable , oldValue , newValue) -> 
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
    	try(FileInputStream fIn = new FileInputStream(new File(DataStorage.dirPathsPath));
    			ObjectInputStream oIn = new ObjectInputStream(fIn))
    	{
    		//或許不需要迴圈。
            while(fIn.available() > 0) 
            	musicTreeMap = (MusicTreeMap)oIn.readObject();
        } 
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
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
    	startWhenOpeningPC.setSelected(Boolean.valueOf(prefs.getProperty(DataStorage.StartWhenOpeningPC)));
    	playWhenOpeningApp.setSelected(Boolean.valueOf(prefs.getProperty(DataStorage.PlayWhenOpeningApp)));
    	autoUpdate.setSelected(Boolean.valueOf(prefs.getProperty(DataStorage.AutoUpdate)));
    	notifyUpdate.setSelected(Boolean.valueOf(prefs.getProperty(DataStorage.NotifyUpdate)));
    	localeBox.setValue(DataStorage.availableLocales.get(prefs.getProperty(DataStorage.Locale)));
    }
    
    //用方法分開。
    //AudioFileFormat 時快時慢，須找到原因。
    @FXML
    private void ok()
    {
    	prefs.setProperty(DataStorage.StartWhenOpeningPC, Boolean.toString(startWhenOpeningPC.isSelected()));
    	prefs.setProperty(DataStorage.PlayWhenOpeningApp, Boolean.toString(playWhenOpeningApp.isSelected()));
    	prefs.setProperty(DataStorage.AutoUpdate , Boolean.toString(autoUpdate.isSelected()));
    	prefs.setProperty(DataStorage.NotifyUpdate, Boolean.toString(notifyUpdate.isSelected()));
    	prefs.setProperty(DataStorage.Locale, localeBox.getValue().toString());
    	PrefsParser.save();
    	//不確定tws的內括順序是否正確。
    	try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(DataStorage.dirPathsPath))))
    	{     
    		out.writeObject(musicTreeMap); 
    	} 
    	catch(Exception e) 
    	{ 
    		e.printStackTrace(); 
    	}
    	PropertyStorage.musicList.clear();
    	recursiveSetMusicInfo("" , musicTreeMap);
    	PropertyStorage.refreshMusicList();
        SceneStorage.getSettingsStage().close();
    }
    
    private void recursiveSetMusicInfo(String path , MusicTreeMap parentMusicTreeMap)
    {
		File dirFile = new File(path);
		if(dirFile.exists())
		{
			for(File file : dirFile.listFiles(new MusicFilter()))
			{
				AudioFileFormat baseFileFormat = null;
				try
				{
					 baseFileFormat = AudioSystem.getAudioFileFormat(file);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				String musicName = file.getName().substring(0, file.getName().lastIndexOf('.'));
				String artistName = null;
				String albumName = null;
				String dateName = null;
				if(baseFileFormat instanceof TAudioFileFormat)
				{
				    Map<String , Object> properties = ((TAudioFileFormat)baseFileFormat).properties();
				    String name = (String)properties.get("title");
				    //未解決。
				    if(name != null)
				    	musicName = name;
				    artistName = (String)properties.get("author");
				    albumName = (String)properties.get("album");
				    dateName = (String)properties.get("date");
				}
				PropertyStorage.musicList.add
				(new MusicInfo(file.getAbsolutePath() , musicName , artistName , albumName , dateName));
			}
		}
		for(Map.Entry<String, MusicTreeMap> entry : parentMusicTreeMap.entrySet())
			recursiveSetMusicInfo(entry.getKey() , entry.getValue());
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
			if(!recursiveFoundMusic(dirFile , preloadFiles , childMusicTreeMap , false))
			{
				System.out.println("選擇資料夾無音樂檔案");
			}
		}
	}
    
    private boolean recursiveFoundMusic(File dirFile , LinkedList<File> preloadFiles , MusicTreeMap parentMusicTreeMap , boolean checkDuplicate)
    {
    	boolean orLogicGate = false;
    	File[] musicFileList = dirFile.listFiles(new MusicFilter());
    	File[] dirFileList = dirFile.listFiles(new DirFilter());
    	if(musicFileList == null && (dirFileList == null || preloadFiles.isEmpty()))
    		return false;
    	String path = dirFile.getAbsolutePath();
    	if(checkDuplicate && musicTreeMap.containsKey(path))
    	{
			DirTreeItem dirTreeItem = musicTreeMap.remove(path).treeItem;
			ObservableList<TreeItem<String>> treeItemChildren = musicTreeMap.treeItem.getChildren();
			if(treeItemChildren.contains(dirTreeItem))
				treeItemChildren.remove(dirTreeItem);
    	}
    	DirTreeItem childTreeItem = new DirTreeItem(dirFile.getName() , null , path);
    	DirTreeItem parentTreeItem = parentMusicTreeMap.treeItem;
    	parentTreeItem.setExpanded(true);
    	parentTreeItem.musicTreeMap = parentMusicTreeMap;
    	MusicTreeMap childMusicTreeMap;
    	if(parentMusicTreeMap.containsKey(path))
    	{
    		childMusicTreeMap = parentMusicTreeMap.get(path);
    		childTreeItem = childMusicTreeMap.treeItem;
    	}
    	else
    	{
    		childMusicTreeMap = new MusicTreeMap(childTreeItem);
    		parentTreeItem.getChildren().add(childTreeItem);
        	childTreeItem.musicTreeMap = childMusicTreeMap;
        	parentMusicTreeMap.put(path , childMusicTreeMap);
    	}
    	if(preloadFiles.isEmpty())
    	{
    		for(File file : dirFileList)
    			orLogicGate = recursiveFoundMusic(file , preloadFiles , childMusicTreeMap , true) || orLogicGate;
    	}
    	else
    		orLogicGate = recursiveFoundMusic(preloadFiles.removeFirst() , preloadFiles , childMusicTreeMap , true) || orLogicGate;
    	if(musicFileList.length != 0)
    		orLogicGate = true;
    	if(!orLogicGate)
    	{
    		parentMusicTreeMap.remove(path);
    		parentTreeItem.getChildren().remove(childTreeItem);
    	}
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