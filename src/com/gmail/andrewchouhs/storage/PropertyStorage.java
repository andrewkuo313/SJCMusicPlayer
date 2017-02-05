package com.gmail.andrewchouhs.storage;

import java.io.File;
import com.gmail.andrewchouhs.model.DirInfo;
import com.gmail.andrewchouhs.model.MusicInfo;
import com.gmail.andrewchouhs.utils.fliter.MusicFilter;
import com.gmail.andrewchouhs.utils.player.MusicPlayingService;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public class PropertyStorage
{
	//至下一首歌的間隔需要修改。
	//須避免 List 有重複的物件，也許先套入 HashSet 可以解決。
	//有暴吃記憶體的現象。
	//須修正至不須使用 Platform.runLater()。
	//Dir 的讀取時間點要修正。
    public static final ObservableList<DirInfo> dirList = FXCollections.observableArrayList();
	public static final ObservableList<MusicInfo> musicList = FXCollections.observableArrayList();
    public static final ObservableList<Image> albumCoverList = FXCollections.observableArrayList();
    public static final ObjectProperty<MusicInfo> musicInfo = new SimpleObjectProperty<MusicInfo>();
    public static final ObjectProperty<MusicPlayingService> musicPlayer = new SimpleObjectProperty<MusicPlayingService>();
    public static final IntegerProperty musicTime = new SimpleIntegerProperty(0);
    //應合併至 MusicInfo。
    public static final IntegerProperty musicTotalTime = new SimpleIntegerProperty(0);
	
    static
	{
    	musicInfo.addListener((observable, oldValue, newValue) -> 
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
	}
	
    public static void refreshMusicList()
    {
    	musicList.clear();
    	albumCoverList.add(null);
    	albumCoverList.clear();
    	for(DirInfo dirInfo : dirList)
    	{
    		File dirFile = new File(dirInfo.getPath());
    		for(File file : dirFile.listFiles(new MusicFilter()))
    		{
    			String musicName = file.getName().substring(0, file.getName().lastIndexOf('.'));
    			String artistName = null;
    			String albumName = null;
    			musicList.add
    			(new MusicInfo(file.getAbsolutePath() , musicName , artistName , albumName));
    		}
    		//非常耗時間和記憶體，需要修正。
//    		for(File file : dirFile.listFiles(new AlbumCoverFilter()))
//    		{
//    			//需加入判斷同一資料夾最接近封面的檔案、取而代之從音樂檔擷取封面。	
//    			albumCoverList.add(new Image(file.toURI().toString()));
//    		}
    	}
		albumCoverList.add(null);
		albumCoverList.remove(albumCoverList.size() - 1);
    }
    
	private PropertyStorage()
	{
	}
}
