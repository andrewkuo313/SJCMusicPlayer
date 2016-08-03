package com.gmail.andrewchouhs;

import java.io.File;
import java.io.IOException;
import com.gmail.andrewchouhs.model.MusicInfo;
import com.gmail.andrewchouhs.utils.MusicFileFilter;
import com.gmail.andrewchouhs.utils.MusicPlayer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.stage.Stage;

public class Storage
{
    public static final ObservableList<MusicInfo> musicInfoList = FXCollections.observableArrayList();
    public static final ObjectProperty<MusicInfo> musicInfo = new SimpleObjectProperty<MusicInfo>();
    public static final ObjectProperty<File> musicDir = new SimpleObjectProperty<File>();
    public static final MusicPlayer player = new MusicPlayer();
    private static Stage stage;
    
    public static void init(Stage stage)
    {
    	Storage.stage = stage;
        stage.setTitle("SJC Music Player");
    	try 
        {
            BorderPane rootPane = (BorderPane) FXMLLoader.load(Main.class.getResource("view/RootPage.fxml"));
            stage.setScene(new Scene(rootPane));
            rootPane.setCenter(FXMLLoader.load(Main.class.getResource("view/PlayingPage.fxml")));
            stage.show();
        } 
        catch (IOException e) 
        {
        	e.printStackTrace();
        }
    	musicDir.addListener((observable, oldValue, newValue)->refreshMusicInfoList());
    }
    
    private static void refreshMusicInfoList()
    {
    	File[] musicFileList = musicDir.get().listFiles(new MusicFileFilter());
    	for(File musicFile : musicFileList)
    	{
    		Media media = new Media(musicFile.toURI().toString());
			String title = musicFile.getName().substring(0, musicFile.getName().lastIndexOf('.'));
			if(media.getMetadata().get("title") != null)
				title = (String)media.getMetadata().get("title");
			musicInfoList.add(new MusicInfo(musicFile.toURI().toString(), title, 
					(String)media.getMetadata().get("artist"), (String)media.getMetadata().get("album"),
					(Image)media.getMetadata().get("image")));
    	}
    }
    	
    public static Stage getStage()
    {
    	return stage;
    }
}
