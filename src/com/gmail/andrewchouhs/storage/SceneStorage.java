package com.gmail.andrewchouhs.storage;

import java.util.EnumMap;
import java.util.ResourceBundle;
import com.gmail.andrewchouhs.Main;
import com.gmail.andrewchouhs.storage.TextStorage.Text;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public final class SceneStorage
{
	private static Stage mainStage;
	//有可能將 Stage 都獨立成一個類別。
    private static final Stage settingsStage = new Stage();
    private static final EnumMap<Page , Pane> pageMap = new EnumMap<Page , Pane>(Page.class);
    
	public static void init(Stage stage)
    {
		mainStage = stage;
    	try 
        {
    		ResourceBundle bundle = TextStorage.getBundle();
            BorderPane settingsPage = FXMLLoader.load(Main.class.getResource("view/SettingsPage.fxml") , bundle);
            BorderPane rootPage = FXMLLoader.load(Main.class.getResource("view/RootPage.fxml") , bundle);
            AnchorPane listPage = FXMLLoader.load(Main.class.getResource("view/ListPage.fxml") , bundle);
            AnchorPane albumPage = FXMLLoader.load(Main.class.getResource("view/AlbumPage.fxml") , bundle);
            AnchorPane statisticsPage = FXMLLoader.load(Main.class.getResource("view/StatisticsPage.fxml") , bundle);
            pageMap.put(Page.ROOT, rootPage);
            pageMap.put(Page.LIST , listPage);
            pageMap.put(Page.ALBUM, albumPage);
            pageMap.put(Page.STATISTICS , statisticsPage);
            setPage(Page.LIST);
            settingsStage.setScene(new Scene(settingsPage));
        	settingsStage.setTitle(TextStorage.getText(Text.SettingsPage_Title));
            settingsStage.initOwner(mainStage);
            mainStage.setScene(new Scene(rootPage));
        	mainStage.setTitle("SJC's Music Player");
        	mainStage.setOnCloseRequest((event)->PrefStorage.save());
            mainStage.show();
        } 
        catch (Exception e) 
        {
        	e.printStackTrace();
        }
    }
	
    public static void setPage(Page page)
    {
    	BorderPane rootPage = (BorderPane)pageMap.get(Page.ROOT);
    	rootPage.setCenter(pageMap.get(page));
    }
	
	public static Stage getMainStage()
    {
    	return mainStage;
    }
    
    public static Stage getSettingsStage()
    {
    	return settingsStage;
    }
    
    public enum Page
    {
    	ROOT,ALBUM,STATISTICS,LIST;
    }
    
	private SceneStorage()
	{
	}
}
