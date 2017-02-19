package com.gmail.andrewchouhs.storage;

import static com.gmail.andrewchouhs.storage.DataStorage.musicTreeMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.LinkedHashMap;
import com.gmail.andrewchouhs.model.MusicInfo;
import com.gmail.andrewchouhs.model.UpdateInfo;
import com.gmail.andrewchouhs.utils.DirTreeItem;
import com.gmail.andrewchouhs.utils.MusicTreeMap;
import com.gmail.andrewchouhs.utils.player.MusicPlayingService;
import com.gmail.andrewchouhs.utils.service.UpdatesDownloadService;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public final class DataStorage
{
	//至下一首歌的間隔需要修改。
	//有暴吃記憶體的現象。
	//須修正至不須使用 Platform.runLater()。
	//需重新檢查是否有物件比較使用 ==。
	//需將使用 Lambda 後大幅提升效率的程式碼修正。
	//更改名稱。
	//需測試 static 是否影響撥放器運作。
	//仍有些許程式碼需重新檢查一次。
	public static final String dataRootPath = System.getenv("APPDATA") + "\\SJCMusicPlayer\\";
	public static final String dirPathsPath = dataRootPath + "DirectoryPaths";
	public static final String musicInfoPath = dataRootPath + "MusicInfo.xml";
	public static final String prefsPath = dataRootPath + "Preferences.properties";
	public static final String gitHubUpdatesURL = "https://raw.githubusercontent.com/andrewkuo313/SJCMusicPlayer/master/updates/Updates.xml";
	public static final LinkedHashMap<String , MusicInfo> musicInfoMap = new LinkedHashMap<String , MusicInfo>();
	public static MusicTreeMap musicTreeMap = new MusicTreeMap(new DirTreeItem("" , null , null));
	public static final ObservableList<MusicInfo> musicList = FXCollections.observableArrayList();
    public static final ObservableList<Image> albumCoverList = FXCollections.observableArrayList();
    public static final ObjectProperty<MusicInfo> musicInfo = new SimpleObjectProperty<MusicInfo>();
    public static final ObjectProperty<MusicPlayingService> musicPlayer = new SimpleObjectProperty<MusicPlayingService>();
    public static final ObjectProperty<UpdateInfo> updateInfo = new SimpleObjectProperty<UpdateInfo>();
    public static final IntegerProperty musicTime = new SimpleIntegerProperty(0);
    public static final IntegerProperty musicTotalTime = new SimpleIntegerProperty(0);
    
	public static void init()
	{
		musicInfo.addListener((observable, oldValue, newValue) -> 
    	{
    		if(musicPlayer.get() != null)
    		{
    			musicPlayer.get().stop();
    			musicPlayer.set(null);
    		}
    		if(newValue != null)
    			musicPlayer.set(new MusicPlayingService(newValue.getPathProperty().get() , 0L , true));
    		else
    			musicTotalTime.set(0);
    	});
		loadMusicTreeMap();
		new UpdatesDownloadService().start();
	}
	
	public static void loadMusicTreeMap()
	{
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
	}
	
    public static void refreshMusicList()
    {
//    	musicList.clear();
//    	albumCoverList.add(null);
//    	albumCoverList.clear();
//    		File dirFile = new File(dirInfo.getPath());
//    		for(File file : dirFile.listFiles(new MusicFilter()))
//    		{
//    			String musicName = file.getName().substring(0, file.getName().lastIndexOf('.'));
//    			String artistName = null;
//    			String albumName = null;
//    			String dateName = null;
//    			musicList.add
//    			(new MusicInfo(file.getAbsolutePath() , musicName , artistName , albumName , dateName));
//    		}
    		//非常耗時間和記憶體，需要修正。
//    		for(File file : dirFile.listFiles(new AlbumCoverFilter()))
//    		{
//    			//需加入判斷同一資料夾最接近封面的檔案、取而代之從音樂檔擷取封面。	
//    			albumCoverList.add(new Image(file.toURI().toString()));
//    		}
//		albumCoverList.add(null);
//		albumCoverList.remove(albumCoverList.size() - 1);
    }
	
	private DataStorage()
	{
	}
}
