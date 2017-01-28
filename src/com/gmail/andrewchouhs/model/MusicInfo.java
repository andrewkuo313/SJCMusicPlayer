package com.gmail.andrewchouhs.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public class MusicInfo
{
    public final StringProperty path = new SimpleStringProperty();
    public final StringProperty name = new SimpleStringProperty();
    public final StringProperty artist = new SimpleStringProperty("µL");
    public final StringProperty album = new SimpleStringProperty("µL");
    public final ObjectProperty<Image> image = new SimpleObjectProperty<Image>();

    public MusicInfo(String path, String name , String artist , String album , Image image) 
    {
//        this.artist.addListener((observable, oldValue, newValue) -> checkNull(this.artist));
//        this.album.addListener((observable, oldValue, newValue) -> checkNull(this.album));
        this.path.set(path);
        this.name.set(name);
        this.artist.set(artist);
        this.album.set(album);
        this.image.set(image);
        checkNull(this.artist);
        checkNull(this.album);
    }
    
    private void checkNull(StringProperty stringProperty)
    {
    	if(stringProperty.get() == null)
    		stringProperty.set("µL");
    }
}
