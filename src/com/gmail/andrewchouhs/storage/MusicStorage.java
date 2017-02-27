package com.gmail.andrewchouhs.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import org.tritonus.share.sampled.file.TAudioFileFormat;
import com.gmail.andrewchouhs.model.MusicInfo;
import com.gmail.andrewchouhs.utils.MusicTreeMap;
import com.gmail.andrewchouhs.utils.fliter.DirFilter;
import com.gmail.andrewchouhs.utils.fliter.MusicFilter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public class MusicStorage
{
	private static final String musicDataPath = DataStorage.dataRootPath + "MusicData";
	private static final LinkedHashMap<String , MusicInfo> musicMap = new LinkedHashMap<String , MusicInfo>();
	public static MusicTreeMap musicTreeMap = new MusicTreeMap("" , null);
	public static final ObservableList<MusicInfo> musicList = FXCollections.observableArrayList();
    public static final ObservableList<Image> albumCoverList = FXCollections.observableArrayList();
	
    public static void init()
    {
		loadMusicTreeMap();
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
	
    public static void refreshMusicMap()//重新命名。
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
		saveMusicTreeMap();
		for(MusicInfo musicInfo : musicMap.values())
		{
			if(musicInfo.available)
				musicList.add(musicInfo);
		}
    }
    
    public static boolean recursiveFoundMusic(File dirFile , LinkedList<File> preloadFiles , MusicTreeMap parentMusicTreeMap , boolean loading)
    {
    	boolean orLogicGate = false;
    	File[] musicFileList = dirFile.listFiles(new MusicFilter());
    	File[] dirFileList = dirFile.listFiles(new DirFilter());
    	String path = dirFile.getAbsolutePath();
    	if((musicFileList == null && dirFileList == null) || !dirFile.exists())
    	{
    		if(parentMusicTreeMap.containsKey(path))
    			parentMusicTreeMap.get(path).available = false;
    		return false;
    	}
    	MusicTreeMap childMusicTreeMap;
    	if(parentMusicTreeMap.containsKey(path))
    	{
    		childMusicTreeMap = parentMusicTreeMap.get(path);
    		orLogicGate = !childMusicTreeMap.musicMap.isEmpty();
    	}
    	else
    	{
    		childMusicTreeMap = new MusicTreeMap(path , parentMusicTreeMap);
        	parentMusicTreeMap.put(path , childMusicTreeMap);
    	}
    	if(preloadFiles.isEmpty())
    	{
    		for(File file : dirFileList)
    			orLogicGate = recursiveFoundMusic(file , preloadFiles , childMusicTreeMap , loading) || orLogicGate;
    	}
    	else
    		orLogicGate = recursiveFoundMusic(preloadFiles.removeFirst() , preloadFiles , childMusicTreeMap , loading) || orLogicGate;
    	if(musicFileList.length != 0)
    		orLogicGate = true;
    	if(!orLogicGate)
    		parentMusicTreeMap.remove(path);
    	else
    	{
    		if(loading)
    			childMusicTreeMap.available = true;
    	}
    	return orLogicGate;
    }
    
    //須替換absolutePath()、AudioFileFormat 時快時慢，須找到原因。path可取消。
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
    
	private MusicStorage()
	{
	}
}
