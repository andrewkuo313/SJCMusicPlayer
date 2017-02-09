package com.gmail.andrewchouhs.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
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
	//減省名稱。
	//TreeView 增加無資料夾提示。
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
    private MusicTreeMap musicTreeMap = new MusicTreeMap(new DirTreeItem("" , null));
    
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
    	dirInfoTreeView.setRoot(musicTreeMap.treeItem);
    	System.out.println(musicTreeMap.get("D:\\Music\\Touhou"));
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
//    	for(DirInfo dirInfo : DataStorage.dirList)
//    	{
//    		File dirFile = new File(dirInfo.getPath());
//    		for(File file : dirFile.listFiles(new MusicFilter()))
//    		{
//    			AudioFileFormat baseFileFormat = null;
//    			try
//    			{
//    				 baseFileFormat = AudioSystem.getAudioFileFormat(file);
//    			}
//    			catch(Exception e)
//    			{
//    				e.printStackTrace();
//    			}
//    			String musicName = file.getName().substring(0, file.getName().lastIndexOf('.'));
//    			String artistName = null;
//    			String albumName = null;
//    			String dateName = null;
//    			if(baseFileFormat instanceof TAudioFileFormat)
//    			{
//    			    Map<String , Object> properties = ((TAudioFileFormat)baseFileFormat).properties();
//    			    String name = (String)properties.get("title");
//    			    //未解決。
//    			    if(name != null)
//    			    	musicName = name;
//    			    artistName = (String)properties.get("author");
//    			    albumName = (String)properties.get("album");
//    			    dateName = (String)properties.get("date");
//    			}
//    			PropertyStorage.musicList.add
//    			(new MusicInfo(file.getAbsolutePath() , musicName , artistName , albumName , dateName));
//    		}
//    	}
//    	PropertyStorage.refreshMusicList();
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
			while(parentFile != null)
			{
				parentFiles.addFirst(parentFile);
				if(musicTreeMap.containsKey(parentFile.getAbsolutePath()))
					break;
				parentFile = parentFile.getParentFile();
			}
			MusicTreeMap childMusicTreeMap = musicTreeMap;
			MusicTreeMap forAvailableMusicTreeMap = musicTreeMap;
			for(File file : parentFiles)
			{
				String path = file.getAbsolutePath();
				MusicTreeMap prev = childMusicTreeMap;////////////////////////////////
				if(childMusicTreeMap.containsKey(path) && childMusicTreeMap.get(path).available)
				{
					childMusicTreeMap = childMusicTreeMap.get(path);
					dirFile = file;
				}
				if(forAvailableMusicTreeMap.containsKey(path))
				{
					forAvailableMusicTreeMap = forAvailableMusicTreeMap.get(path);
					if(prev.containsKey(path))
					{
						prev.get(path).available = true;
					}
				}
				else
					break;
			}
			recursiveAvailable(forAvailableMusicTreeMap , true);
			DirTreeItem parentTreeItem = (DirTreeItem)childMusicTreeMap.treeItem.getParent();
			if(parentTreeItem != null)
			{
				parentTreeItem.getChildren().remove(childMusicTreeMap.treeItem);
				childMusicTreeMap = parentTreeItem.musicTreeMap;
			}
			if(!recursiveFoundMusic(dirFile , childMusicTreeMap , false))
			{
				System.out.println("選擇資料夾無音樂檔案");
			}
		}
	}
    
    private boolean recursiveFoundMusic(File dirFile , MusicTreeMap parentMusicTreeMap , boolean checkDuplicate)
    {
    	boolean orLogicGate = false;
    	File[] musicFileList = dirFile.listFiles(new MusicFilter());
    	File[] dirFileList = dirFile.listFiles(new DirFilter());
    	if(musicFileList == null && dirFileList == null)
    		return false;
    	if(checkDuplicate && musicTreeMap.containsKey(dirFile.getAbsolutePath()))
    	{
			DirTreeItem dirTreeItem = musicTreeMap.remove(dirFile.getAbsolutePath()).treeItem;
			ObservableList<TreeItem<String>> treeItemChildren = musicTreeMap.treeItem.getChildren();
			if(treeItemChildren.contains(dirTreeItem))
				treeItemChildren.remove(dirTreeItem);
    	}
    	DirTreeItem childTreeItem = new DirTreeItem(dirFile.getName() , null);
    	DirTreeItem parentTreeItem = parentMusicTreeMap.treeItem;
    	parentTreeItem.setExpanded(true);
    	parentTreeItem.musicTreeMap = parentMusicTreeMap;/////////////////
    	MusicTreeMap childMusicTreeMap;
    	boolean available;
    	if(parentMusicTreeMap.containsKey(dirFile.getAbsolutePath()))
    	{
    		childMusicTreeMap = parentMusicTreeMap.get(dirFile.getAbsolutePath());
    		available = childMusicTreeMap.available;
    		childTreeItem = childMusicTreeMap.treeItem;
    		childTreeItem.getChildren().clear();
    	}
    	else
    	{
    		childMusicTreeMap = new MusicTreeMap(childTreeItem);
    		available = true;
    	}
    	childTreeItem.musicTreeMap = childMusicTreeMap;
    	parentTreeItem.getChildren().add(childTreeItem);
    	parentMusicTreeMap.put(dirFile.getAbsolutePath() , childMusicTreeMap);
    	if(available)
    	{
	    	for(File file : dirFileList)
	    	{
	    		orLogicGate = recursiveFoundMusic(file , childMusicTreeMap , true) || orLogicGate;
	    	}
    	}
    	if(musicFileList.length != 0)
    		orLogicGate = true;
    	orLogicGate = orLogicGate && available;
    	if(!orLogicGate)
    	{
    		parentMusicTreeMap.remove(childMusicTreeMap);
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
    			parentTreeItemChildren.remove(treeItem);
    		recursiveAvailable(treeItem.musicTreeMap , false);
    	}
    }
    
    private void recursiveAvailable(MusicTreeMap parentMusicTreeMap , boolean available)
    {
    	parentMusicTreeMap.available = available;
    	if(parentMusicTreeMap.isEmpty())
    		return;
    	for(MusicTreeMap childMusicTreeMap : parentMusicTreeMap.values())
    		recursiveAvailable(childMusicTreeMap , available);
    }
}