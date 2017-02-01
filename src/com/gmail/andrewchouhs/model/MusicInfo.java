package com.gmail.andrewchouhs.model;

import java.util.HashSet;
import static com.gmail.andrewchouhs.storage.DataStorage.bundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MusicInfo
{
    public final StringProperty path = new SimpleStringProperty();
    public final StringProperty name = new SimpleStringProperty();
    public final StringProperty artist = new SimpleStringProperty();
    public final StringProperty album = new SimpleStringProperty();
    public final HashSet<String> tags = new HashSet<String>();
    
    public MusicInfo(String path, String name , String artist , String album) 
    {
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
    		stringProperty.set(bundle.getString("ListPage.NullValue"));
    }
}
