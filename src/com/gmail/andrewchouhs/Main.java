package com.gmail.andrewchouhs;

import com.gmail.andrewchouhs.storage.SceneStorage;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application 
{
    @Override
    public void start(Stage stage) 
    {
    	SceneStorage.init(stage);
    }
    
    public static void main(String[] args) 
    {
        launch(args);
    }
}
