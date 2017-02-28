package com.gmail.andrewchouhs.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MusicInfo
{
	public final StringProperty path = new SimpleStringProperty();
    public final StringProperty name = new SimpleStringProperty();
    public final StringProperty artist = new SimpleStringProperty();
    public final StringProperty album = new SimpleStringProperty();
    public final StringProperty year = new SimpleStringProperty();
    public final StringProperty bitrate = new SimpleStringProperty();
    
    public MusicInfo(String path, String name , String artist , String album , String year , String bitrate) 
    {
        this.path.set(path);
        this.name.set(name);
        this.artist.set(artist);
        this.album.set(album);
        this.year.set(year);
        this.bitrate.set(bitrate);
    }
}
