package com.gmail.andrewchouhs;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import com.gmail.andrewchouhs.model.DirInfo;
import com.gmail.andrewchouhs.model.MusicInfo;
import com.gmail.andrewchouhs.utils.AlbumCoverFilter;
import com.gmail.andrewchouhs.utils.DirXMLParser;
import com.gmail.andrewchouhs.utils.MusicFileFilter;
import com.gmail.andrewchouhs.utils.MusicPlayingService;
import com.gmail.andrewchouhs.utils.Page;
import com.gmail.andrewchouhs.utils.PreferencesParser;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Storage
{
	//to next song timing seems too short
	//should prevent object repeat in observable list , maybe throw to set first
    public static final ObservableList<MusicInfo> musicInfoList = FXCollections.observableArrayList();
    public static final ObjectProperty<MusicInfo> musicInfo = new SimpleObjectProperty<MusicInfo>();
    public static final IntegerProperty musicTime = new SimpleIntegerProperty(0);
    public static final IntegerProperty musicTotalTime = new SimpleIntegerProperty(0);
    public static final ObservableList<DirInfo> dirList = FXCollections.observableArrayList();
    public static final ObservableList<Image> albumCoverList = FXCollections.observableArrayList();
    public static final ObjectProperty<MusicPlayingService> musicPlayer = new SimpleObjectProperty<MusicPlayingService>();
    private static Stage mainStage;
    private static final Stage settingsStage = new Stage();
    public static final Properties prefs = new Properties();
    private static EnumMap<Page , Pane> pageMap = new EnumMap<Page , Pane>(Page.class);
    public static final ResourceBundle bundle = ResourceBundle.getBundle("com.gmail.andrewchouhs.i18n.text" , new Locale("en" , "US"));
//    private static final String[] trackNumberPrefix = {"\\(\\d+\\).*"}; 
    
    public static void init(Stage stage)
    {
    	mainStage = stage;
        stage.setTitle("SJC's Music Player");
        settingsStage.setTitle(bundle.getString("SettingsPage.Title"));
    	try 
        {
            BorderPane rootPage = FXMLLoader.load(Main.class.getResource("view/RootPage.fxml") , bundle);
            AnchorPane albumPage = FXMLLoader.load(Main.class.getResource("view/AlbumPage.fxml") , bundle);
            AnchorPane statisticsPage = FXMLLoader.load(Main.class.getResource("view/StatisticsPage.fxml") , bundle);
            AnchorPane listPage = FXMLLoader.load(Main.class.getResource("view/ListPage.fxml") , bundle);
            AnchorPane settingsPage = FXMLLoader.load(Main.class.getResource("view/SettingsPage.fxml") , bundle);
            pageMap.put(Page.ROOT, rootPage);
            pageMap.put(Page.ALBUM, albumPage);
            pageMap.put(Page.STATISTICS , statisticsPage);
            pageMap.put(Page.LIST , listPage);
            setPage(Page.LIST);
            mainStage.setScene(new Scene(rootPage));
            settingsStage.setScene(new Scene(settingsPage));
            settingsStage.initOwner(stage);
            settingsStage.setOnCloseRequest((event)->DirXMLParser.load());
            mainStage.show();
        } 
        catch (IOException e) 
        {
        	e.printStackTrace();
        }
    	
    	File fileDir = new File(System.getenv("APPDATA") + "\\SJCMusicPlayer");
    	if(!fileDir.exists())
    		fileDir.mkdirs();
    	else
    	{
    		DirXMLParser.load();
    		PreferencesParser.load();
    		refreshMusicInfoList();
    	}
    	
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
    
    public static void refreshMusicInfoList()
    {
    	try
    	{
	    	musicInfoList.clear();
	    	albumCoverList.add(null);
	    	albumCoverList.clear();
	    	for(DirInfo dirInfo : dirList)
	    	{
	    		File dirFile = new File(dirInfo.path.get());
	    		for(File file : dirFile.listFiles(new MusicFileFilter()))
	    		{
	    			String musicName = file.getName().substring(0, file.getName().lastIndexOf('.'));
	//    			for(String s : trackNumberPrefix)
	//    			{
	//    				if(musicName.matches(s))
	//    					musicName.replaceFirst("\\(\\d+\\)", "0");
	//    			}
	    			
	    			String artistName = null;
	    			String albumName = null;
//	    			AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(file);
//	    			if (baseFileFormat instanceof TAudioFileFormat)
//	    			{
//	    			    Map<String , Object> properties = ((TAudioFileFormat)baseFileFormat).properties();
//	    			    artistName = (String)properties.get("artist");
//	    			    albumName = (String)properties.get("album");
//	    			}
	    			
	    			musicInfoList.add
	    			(new MusicInfo(file.getAbsolutePath() , musicName , artistName , albumName));
	    		}
	    		for(File file : dirFile.listFiles(new AlbumCoverFilter()))
	    			albumCoverList.add(new Image(file.toURI().toString()));
	    	}
			albumCoverList.add(null);
			albumCoverList.remove(albumCoverList.size() - 1);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    public static Stage getMainStage()
    {
    	return mainStage;
    }
    
    public static Stage getSettingsStage()
    {
    	return settingsStage;
    }
    
    public static void setPage(Page page)
    {
    	BorderPane rootPage = (BorderPane)pageMap.get(Page.ROOT);
    	Pane insidePage = pageMap.get(page);
    	rootPage.setCenter(insidePage);
    }
}
