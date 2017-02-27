package com.gmail.andrewchouhs.storage;

import com.gmail.andrewchouhs.model.MusicInfo;
import com.gmail.andrewchouhs.model.UpdateInfo;
import com.gmail.andrewchouhs.utils.player.MusicPlayingService;
import com.gmail.andrewchouhs.utils.service.UpdatesDownloadService;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class DataStorage
{
	//至下一首歌的間隔需要修改。
	//有暴吃記憶體的現象。
	//需重新檢查是否有物件比較使用 ==。
	//需將使用 Lambda 後大幅提升效率的程式碼修正。
	//仍有些許程式碼需重新檢查一次，減省或更改名稱、精簡流程、修正潛在問題或無意義動作。
	//Object I/O 因會紀錄字串故更需減省。
	//需將 dirPaths 和 musicInfo 排序。
	public static final String dataRootPath = System.getenv("APPDATA") + "\\SJCMusicPlayer\\";
	public static final String gitHubUpdatesURL = "https://raw.githubusercontent.com/andrewkuo313/SJCMusicPlayer/master/updates/Updates.xml";
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
		new UpdatesDownloadService().start();
	}
	private DataStorage()
	{
	}
}
