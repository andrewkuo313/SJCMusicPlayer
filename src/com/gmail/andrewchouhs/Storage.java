package com.gmail.andrewchouhs;

import java.io.File;
import java.io.IOException;
import com.gmail.andrewchouhs.model.MusicInfo;
import com.gmail.andrewchouhs.utils.MusicPlayer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Storage
{
    public static final ObservableList<MusicInfo> musicInfoList = FXCollections.observableArrayList();
    public static final ObjectProperty<MusicInfo> musicInfo = new SimpleObjectProperty<MusicInfo>();
    public static final ObjectProperty<File> musicDir = new SimpleObjectProperty<File>();
    public static final MusicPlayer player = new MusicPlayer();
    private static Stage stage;
    private static BorderPane rootPane;
    
    public static void init(Stage stage)
    {
    	Storage.stage = stage;
        stage.setTitle("SJC's Music Player");
    	try 
        {
            Storage.rootPane = (BorderPane) FXMLLoader.load(Main.class.getResource("view/RootPage.fxml"));
            rootPane.setCenter(FXMLLoader.load(Main.class.getResource("view/PlayingPage.fxml")));
            stage.setScene(new Scene(Storage.rootPane));
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
}
