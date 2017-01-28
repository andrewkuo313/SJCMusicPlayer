package com.gmail.andrewchouhs.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DirInfo
{
	public final StringProperty path = new SimpleStringProperty();

    public DirInfo(String path) 
    {
        this.path.set(path);
    }
}
