package com.gmail.andrewchouhs.model;

import java.util.HashSet;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MusicInfo
{
    public final StringProperty path = new SimpleStringProperty();
    public final StringProperty name = new SimpleStringProperty();
    public final StringProperty artist = new SimpleStringProperty("無");
    public final StringProperty album = new SimpleStringProperty("無");
    public final HashSet<String> tags = new HashSet<String>();
    
    public MusicInfo(String path, String name , String artist , String album) 
    {
//        this.artist.addListener((observable, oldValue, newValue) -> checkNull(this.artist));
//        this.album.addListener((observable, oldValue, newValue) -> checkNull(this.album));
        this.path.set(path);
        this.name.set(name);
        this.artist.set(artist);
        this.album.set(album);
        checkNull(this.artist);
        checkNull(this.album);
    }
    
    private void checkNull(StringProperty stringProperty)
    {
    	if(stringProperty.get() == null)
    		stringProperty.set("無");
    }
}
