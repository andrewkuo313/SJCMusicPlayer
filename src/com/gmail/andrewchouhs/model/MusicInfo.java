package com.gmail.andrewchouhs.model;

import java.io.Serializable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MusicInfo implements Serializable
{
	private static final long serialVersionUID = 1L;
	public final StringProperty path = new SimpleStringProperty();
    public final StringProperty name = new SimpleStringProperty();
    public final StringProperty artist = new SimpleStringProperty();
    public final StringProperty album = new SimpleStringProperty();
    public final StringProperty date = new SimpleStringProperty();
    public transient boolean available = true; 
    
    public MusicInfo(String path, String name , String artist , String album , String date) 
    {
        this.path.set(path);
        this.name.set(name);
        this.artist.set(artist);
        this.album.set(album);
        this.date.set(date);
    }

    public MusicInfo()
    {
    }
}
