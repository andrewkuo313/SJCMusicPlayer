package com.gmail.andrewchouhs.model;

import java.io.Serializable;

public class MusicData implements Serializable
{
	private static final long serialVersionUID = 1L;
	public final String path;
    public final String name;
    public final String artist;
    public final String album;
    public final String date;
    
    public MusicData(String path, String name , String artist , String album , String date)
    {
    	this.path = path;
    	this.name = name;
    	this.artist = artist;
    	this.album = album;
    	this.date = date;
    }
}
