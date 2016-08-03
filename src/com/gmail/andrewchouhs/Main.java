package com.gmail.andrewchouhs;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application 
{
    @Override
    public void start(Stage stage) 
    {
        Storage.init(stage);
    }
  
    public static void main(String[] args) 
    {
        launch(args);
    }
}
