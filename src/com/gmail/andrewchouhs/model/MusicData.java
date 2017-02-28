package com.gmail.andrewchouhs.model;

import java.io.Serializable;
import java.util.LinkedList;

public class MusicData implements Serializable
{
	private static final long serialVersionUID = 1L;
	public final LinkedList<Long> count = new LinkedList<>();
	public String path;
    public String name;
    public String artist;
    public String album;
    public String year;
    public long bitrate;
    public long modDate;
    public transient MusicInfo musicInfo;
    
    public MusicData(String path, String name , String artist , String album , String year  , long bitrate , long modDate)
    {
    	this.path = path;
    	this.name = name;
    	this.artist = artist;
    	this.album = album;
    	this.year = year;
    	this.bitrate = bitrate;
    	this.modDate = modDate;
    }
    
    public MusicData()
    {
    }
}
