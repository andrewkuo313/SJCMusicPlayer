package com.gmail.andrewchouhs;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import com.gmail.andrewchouhs.model.MusicInfo;
import com.gmail.andrewchouhs.utils.MusicPlayer;
import com.gmail.andrewchouhs.utils.Page;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Storage
{
    public static final ObservableList<MusicInfo> musicInfoList = FXCollections.observableArrayList();
    public static final ObjectProperty<MusicInfo> musicInfo = new SimpleObjectProperty<MusicInfo>();
    public static final ObjectProperty<File> musicDir = new SimpleObjectProperty<File>();
    public static final MusicPlayer player = new MusicPlayer();
    private static Stage stage;
    private static EnumMap<Page , Pane> pageMap = new EnumMap<Page , Pane>(Page.class);
    
    public static void init(Stage stage)
    {
    	Storage.stage = stage;
        stage.setTitle("SJC's Music Player");
    	try 
        {
            BorderPane rootPage = FXMLLoader.load(Main.class.getResource("view/RootPage.fxml"));
            AnchorPane playingPage = FXMLLoader.load(Main.class.getResource("view/PlayingPage.fxml"));
            AnchorPane equalizerPage = FXMLLoader.load(Main.class.getResource("view/EqualizerPage.fxml"));
            pageMap.put(Page.ROOT, rootPage);
            pageMap.put(Page.PLAYING, playingPage);
            pageMap.put(Page.EQUALIZER , equalizerPage);
            setPage(Page.PLAYING);
            stage.setScene(new Scene(rootPage));
            stage.show();
        } 
        catch (IOException e) 
        {
        	e.printStackTrace();
        }
    }
    
    public static Stage getStage()
    {
    	return stage;
    }
    
    //maybe Delay load pattern
    public static void setPage(Page page)
    {
    	BorderPane rootPage = (BorderPane)pageMap.get(Page.ROOT);
    	Pane insidePage = pageMap.get(page);
    	rootPage.setCenter(insidePage);
    }
}
