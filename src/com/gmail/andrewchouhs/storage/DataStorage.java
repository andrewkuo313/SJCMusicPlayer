package com.gmail.andrewchouhs.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import org.tritonus.share.sampled.file.TAudioFileFormat;
import com.gmail.andrewchouhs.model.MusicInfo;
import com.gmail.andrewchouhs.model.UpdateInfo;
import com.gmail.andrewchouhs.utils.DirTreeItem;
import com.gmail.andrewchouhs.utils.MusicTreeMap;
import com.gmail.andrewchouhs.utils.fliter.MusicFilter;
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
	public static final String musicInfoPath = dataRootPath + "MusicInfo";
	public static final String prefsPath = dataRootPath + "Preferences.properties";
	public static final String gitHubUpdatesURL = "https://raw.githubusercontent.com/andrewkuo313/SJCMusicPlayer/master/updates/Updates.xml";
	private static final LinkedHashMap<String , MusicInfo> musicMap = new LinkedHashMap<String , MusicInfo>();
	public static MusicTreeMap musicTreeMap = new MusicTreeMap(new DirTreeItem("" , null , null));
	public static final ObservableList<MusicInfo> musicList = FXCollections.observableArrayList();
    public static final ObservableList<Image> albumCoverList = FXCollections.observableArrayList();
    public static final ObjectProperty<MusicInfo> currentMusicInfo = new SimpleObjectProperty<MusicInfo>();
    public static final ObjectProperty<MusicPlayingService> musicPlayer = new SimpleObjectProperty<MusicPlayingService>();
    public static final ObjectProperty<UpdateInfo> updateInfo = new SimpleObjectProperty<UpdateInfo>();
    public static final IntegerProperty musicTime = new SimpleIntegerProperty(0);
    public static final IntegerProperty musicTotalTime = new SimpleIntegerProperty(0);
    
	public static void init()
	{
		currentMusicInfo.addListener((observable, oldValue, newValue) -> 
    	{
    		if(musicPlayer.get() != null)
    		{
    			musicPlayer.get().stop();
    			musicPlayer.set(null);
    		}
    		if(newValue != null)
    			musicPlayer.set(new MusicPlayingService(newValue.path.get() , 0L , true));
    		else
    			musicTotalTime.set(0);
    	});
		loadMusicTreeMap();
		loadMusicInfo();
		refreshMusicMap();
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
	
	public static void saveMusicTreeMap()
	{
    	try(FileOutputStream fOut = new FileOutputStream(new File(DataStorage.dirPathsPath)); 
    			ObjectOutputStream oOut = new ObjectOutputStream(fOut))
    	{     
    		oOut.writeObject(musicTreeMap); 
    	} 
    	catch(Exception e) 
    	{ 
    		e.printStackTrace(); 
    	}
	}
	
	public static void loadMusicInfo()
	{
    	try(FileInputStream fIn = new FileInputStream(new File(DataStorage.musicInfoPath));
    			ObjectInputStream oIn = new ObjectInputStream(fIn))
    	{
            while(fIn.available() > 0)
            {
            	@SuppressWarnings("unchecked")//須修正。
				HashMap<String , String> dummyObject = (HashMap<String , String>)oIn.readObject();
            	musicMap.put(dummyObject.get("path") , 
            			new MusicInfo(dummyObject.get("path") , dummyObject.get("name") , dummyObject.get("artist") , 
            					dummyObject.get("album") , dummyObject.get("date")));
            }
        } 
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
	}
	
	public static void saveMusicInfo()
	{
    	try(FileOutputStream fOut = new FileOutputStream(new File(DataStorage.musicInfoPath)); 
    			ObjectOutputStream oOut = new ObjectOutputStream(fOut))
    	{     
    		for(MusicInfo musicInfo : musicMap.values())
    		{
    			HashMap<String , String> dummyObject = new HashMap<String , String>();
    			dummyObject.put("path" , musicInfo.path.get());
    			dummyObject.put("name" , musicInfo.name.get());
    			dummyObject.put("artist" , musicInfo.artist.get());
    			dummyObject.put("album" , musicInfo.album.get());
    			dummyObject.put("date" , musicInfo.date.get());
    			oOut.writeObject(dummyObject);
    		}
    	} 
    	catch(Exception e) 
    	{ 
    		e.printStackTrace(); 
    	}
	}
	
    public static void refreshMusicMap()
    {
    	musicList.clear();
    	for(MusicInfo musicInfo : musicMap.values())
    		musicInfo.available = false;
    	albumCoverList.add(null);
    	albumCoverList.clear();
    	recursiveSetMusicInfo("" , musicTreeMap);
    		//非常耗時間和記憶體，需要修正。
//    		for(File file : dirFile.listFiles(new AlbumCoverFilter()))
//    		{
//    			//需加入判斷同一資料夾最接近封面的檔案、取而代之從音樂檔擷取封面。	
//    			albumCoverList.add(new Image(file.toURI().toString()));
//    		}
		albumCoverList.add(null);
		albumCoverList.remove(albumCoverList.size() - 1);
		saveMusicInfo();
		for(MusicInfo musicInfo : musicMap.values())
		{
			if(musicInfo.available)
				musicList.add(musicInfo);
		}
    }
	
    //須隨資料夾自動讀取取消 dirFile.exists()、absolutePath() 替換。
    private static void recursiveSetMusicInfo(String path , MusicTreeMap parentMusicTreeMap)
    {
		File dirFile = new File(path);
		if(dirFile.exists())
		{
			for(File file : dirFile.listFiles(new MusicFilter()))
			{
				if(musicMap.containsKey(file.getAbsolutePath()))
				{
					musicMap.get(file.getAbsolutePath()).available = true;
					continue;
				}
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
				musicMap.put(file.getAbsolutePath() , 
						new MusicInfo(file.getAbsolutePath() , musicName , artistName , albumName , dateName));
			}
		}
		for(Map.Entry<String, MusicTreeMap> entry : parentMusicTreeMap.entrySet())
			recursiveSetMusicInfo(entry.getKey() , entry.getValue());
    }
    
	private DataStorage()
	{
	}
}
