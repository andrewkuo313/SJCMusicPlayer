package com.gmail.andrewchouhs.model;

import java.util.Collection;
import java.util.HashSet;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MusicInfo
{
    private final StringProperty pathProperty = new SimpleStringProperty();
    private final StringProperty nameProperty = new SimpleStringProperty();
    private final StringProperty artistProperty = new SimpleStringProperty();
    private final StringProperty albumProperty = new SimpleStringProperty();
    private final StringProperty dateProperty = new SimpleStringProperty();
    private final HashSet<String> tags = new HashSet<String>();
    
    public MusicInfo(String path, String name , String artist , String album , String date) 
    {
        setPath(path);
        setName(name);
        setArtist(artist);
        setAlbum(album);
        setDate(date);
    }
    
    @XmlAttribute
    public String getPath()
    {
    	return pathProperty.get();
    }

    public void setPath(String path)
    {
    	pathProperty.set(path);
    }
    
    public StringProperty getPathProperty()
    {
    	return pathProperty;
    }
    
    @XmlAttribute
    public String getName()
    {
    	return nameProperty.get();
    }

    public void setName(String name)
    {
    	nameProperty.set(name);
    }
    
    public StringProperty getNameProperty()
    {
    	return nameProperty;
    }
    
    @XmlAttribute
    public String getArtist()
    {
    	return artistProperty.get();
    }

    public void setArtist(String artist)
    {
    	artistProperty.set(artist);
    }
    
    public StringProperty getArtistProperty()
    {
    	return artistProperty;
    }
    
    @XmlAttribute
    public String getAlbum()
    {
    	return albumProperty.get();
    }

    public void setAlbum(String album)
    {
    	albumProperty.set(album);
    }
    
    public StringProperty getAlbumProperty()
    {
    	return albumProperty;
    }
    
    @XmlAttribute
    public String getDate()
    {
    	return dateProperty.get();
    }

    public void setDate(String date)
    {
    	dateProperty.set(date);
    }
    
    public StringProperty getDateProperty()
    {
    	return dateProperty;
    }
    
    @XmlElement(name = "tag")
    public HashSet<String> getTags()
    {
    	return tags;
    }
    
    public void setTags(Collection<? extends String> c)
    {
    	tags.clear();
    	tags.addAll(c);
    }
    
    public MusicInfo()
    {
    }
}
