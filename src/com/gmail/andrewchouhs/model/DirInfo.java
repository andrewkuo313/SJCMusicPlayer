package com.gmail.andrewchouhs.model;

import javax.xml.bind.annotation.XmlAttribute;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DirInfo
{
	private final StringProperty pathProperty = new SimpleStringProperty();
	
    public DirInfo(String path) 
    {
        setPath(path);
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
    
    @Override
    public String toString()
    {
    	return getPath().substring(getPath().lastIndexOf("\\") + 1 , getPath().length());
    }
    
    public DirInfo()
    {
    }
}