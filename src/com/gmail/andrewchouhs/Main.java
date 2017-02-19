package com.gmail.andrewchouhs;

import com.gmail.andrewchouhs.storage.DataStorage;
import com.gmail.andrewchouhs.storage.PrefStorage;
import com.gmail.andrewchouhs.storage.SceneStorage;
import com.gmail.andrewchouhs.storage.TextStorage;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application 
{
    @Override
    public void start(Stage stage) 
    {
    	PrefStorage.init();
    	DataStorage.init();
    	TextStorage.init();
    	SceneStorage.init(stage);
    }
    
    public static void main(String[] args) 
    {
        launch(args);
    }
}
