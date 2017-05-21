package com.gmail.andrewchouhs.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import com.gmail.andrewchouhs.model.MusicData;
import com.gmail.andrewchouhs.model.MusicInfo;
import com.gmail.andrewchouhs.utils.MusicTreeMap;
import com.gmail.andrewchouhs.utils.TagMap;
import com.gmail.andrewchouhs.utils.fliter.DirFilter;
import com.gmail.andrewchouhs.utils.fliter.MusicFilter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public class MusicStorage
{
	private static final String musicDataPath = DataStorage.dataRootPath + "MusicData";
	public static final LinkedHashMap<String , MusicData> musicMap = new LinkedHashMap<>();
	public static LinkedHashMap<String , TagMap> tagMap = new LinkedHashMap<>();
	public static MusicTreeMap musicTreeMap = new MusicTreeMap("" , null);
	public static final ObservableList<MusicInfo> musicList = FXCollections.observableArrayList();
    public static final ObservableList<Image> albumCoverList = FXCollections.observableArrayList();
    
    public static void init()
    {
		loadMusicTreeMap();
		recursiveRefreshMusicTreeMap(musicTreeMap);
		refreshMusicMap();
    }
    
    public static void loadMusicTreeMap()
	{
    	try(FileInputStream fIn = new FileInputStream(new File(musicDataPath));
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
    	try(FileOutputStream fOut = new FileOutputStream(new File(musicDataPath)); 
    			ObjectOutputStream oOut = new ObjectOutputStream(fOut))
    	{     
    		oOut.writeObject(musicTreeMap); 
    	} 
    	catch(Exception e) 
    	{ 
    		e.printStackTrace(); 
    	}
	}

    public static void recursiveRefreshMusicTreeMap(MusicTreeMap parentMusicTreeMap)
    {
    		for(MusicTreeMap childMusicTreeMap : parentMusicTreeMap.values())
    		{
    			if(childMusicTreeMap.ignored)
    				recursiveRefreshMusicTreeMap(childMusicTreeMap);
    			else
    	    		recursiveFoundMusic(new File(childMusicTreeMap.path) , new LinkedList<File>() , parentMusicTreeMap , false);
    		}
    }
    
    public static boolean recursiveFoundMusic(File dirFile , LinkedList<File> preloadFiles , MusicTreeMap parentMusicTreeMap , boolean loading)
    {
    	boolean orLogicGate = false;
    	File[] musicFileList = null;
    	File[] dirFileList = null;
    	if(preloadFiles.isEmpty())
    	{
    		musicFileList = dirFile.listFiles(new MusicFilter());
    		dirFileList = dirFile.listFiles(new DirFilter());
    	}
    	String path = dirFile.getAbsolutePath();
    	if((musicFileList == null && dirFileList == null && preloadFiles.isEmpty()) || !dirFile.exists())
    	{
    		if(parentMusicTreeMap.containsKey(path))
    			parentMusicTreeMap.get(path).visible = false;
    		return false;
    	}
    	MusicTreeMap childMusicTreeMap;
    	if(parentMusicTreeMap.containsKey(path))
    	{
    		childMusicTreeMap = parentMusicTreeMap.get(path);
    		orLogicGate = !childMusicTreeMap.musicMap.isEmpty();
    		if(preloadFiles.isEmpty())
    			childMusicTreeMap.ignored = false;
    	}
    	else
    	{
    		childMusicTreeMap = new MusicTreeMap(path , parentMusicTreeMap);
    		if(!preloadFiles.isEmpty())
    			childMusicTreeMap.ignored = true;
        	parentMusicTreeMap.put(path , childMusicTreeMap);
    	}
    	if(preloadFiles.isEmpty())
    	{
    		for(File file : dirFileList)
    			orLogicGate = recursiveFoundMusic(file , preloadFiles , childMusicTreeMap , loading) || orLogicGate;
    	}
    	else
    		orLogicGate = recursiveFoundMusic(preloadFiles.removeFirst() , preloadFiles , childMusicTreeMap , loading) || orLogicGate;
    	if(musicFileList != null && musicFileList.length != 0)
    		orLogicGate = true;
    	if(!orLogicGate)
    		parentMusicTreeMap.remove(path);
    	else
    	{
    		if(loading)
    			childMusicTreeMap.visible = true;
    	}
    	return orLogicGate;
    }
    
    public static void refreshMusicMap()
    {
    	musicList.clear();
    	musicMap.clear();
    	albumCoverList.add(null);
    	albumCoverList.clear();
    	recursiveSetMusicInfo(musicTreeMap);
    		//非常耗時間和記憶體，需要修正。
//    		for(File file : dirFile.listFiles(new AlbumCoverFilter()))
//    		{
//    			//需加入判斷同一資料夾最接近封面的檔案、取而代之從音樂檔擷取封面。	
//    			albumCoverList.add(new Image(file.toURI().toString()));
//    		}
		albumCoverList.add(null);
		albumCoverList.remove(albumCoverList.size() - 1);
		saveMusicTreeMap();
    }
    
    private static void recursiveSetMusicInfo(MusicTreeMap parentMusicTreeMap)
    {
		File dirFile = new File(parentMusicTreeMap.path);
		if(parentMusicTreeMap.visible && !parentMusicTreeMap.ignored && dirFile.exists())
		{
			LinkedHashMap<String , MusicData> insideMap = parentMusicTreeMap.musicMap;
			for(File file : dirFile.listFiles(new MusicFilter()))
			{
				String path = file.getAbsolutePath();
				MusicData musicData;
				if(insideMap.containsKey(file.getName()))
					musicData = insideMap.get(file.getName());
				else
					musicData = new MusicData();
				if(!(insideMap.containsKey(file.getName()) && insideMap.get(file.getName()).modDate == file.lastModified()))
				{
					Tag tag = null;
					AudioHeader header = null;
					try
					{
						 AudioFile f = AudioFileIO.read(file);
						 tag = f.getTag();
						 header = f.getAudioHeader();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					String musicName = file.getName().substring(0, file.getName().lastIndexOf('.'));
					String artistName = null;
					String albumName = null;
					String yearName = null;
					long bitrate = 0;
					if(tag != null)
					{
						musicName = tag.getFirst(FieldKey.TITLE);
						artistName = tag.getFirst(FieldKey.ARTIST);
						albumName = tag.getFirst(FieldKey.ALBUM);
						yearName = tag.getFirst(FieldKey.YEAR);
					}
					bitrate = header.getBitRateAsNumber();
					musicData.path = file.getName();
					musicData.name = musicName;
					musicData.artist = artistName;
					musicData.album = albumName;
					musicData.year = yearName;
					musicData.bitrate = bitrate;
					musicData.modDate = file.lastModified();
				}
				musicData.musicInfo = new MusicInfo(path , musicData.name , musicData.artist ,
						musicData.album , musicData.year , Long.toString(musicData.bitrate) + "kbps");
				insideMap.put(file.getName() , musicData);
				musicMap.put(path , musicData);
				musicList.add(musicData.musicInfo);
			}
		}
		for(MusicTreeMap childMusicTreeMap : parentMusicTreeMap.values())
			recursiveSetMusicInfo(childMusicTreeMap);
    }
//    
////    測試用方法。
//    public static void recursiveGetMusicTreeMapInfo(MusicTreeMap parentMusicTreeMap)
//    {
//    	System.out.println("=-=-=-=-=-=-=-=-=-=-=");
//    	System.out.println("Path: " + parentMusicTreeMap.path);
//    	System.out.println("Available: " + parentMusicTreeMap.visible);
//    	System.out.println("Ignored: " + parentMusicTreeMap.ignored);
//    	for(MusicTreeMap childMusicTreeMap : parentMusicTreeMap.values())
//    		recursiveGetMusicTreeMapInfo(childMusicTreeMap);
//    }
    
	private MusicStorage()
	{
	}
}
