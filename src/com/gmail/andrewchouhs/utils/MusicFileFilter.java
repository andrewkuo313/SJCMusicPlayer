package com.gmail.andrewchouhs.utils;

import java.io.File;
import java.io.FileFilter;

public class MusicFileFilter implements FileFilter
{
	private String[] filter = {".mp3",".ogg",".flac" , ".wav"};
	
	@Override
	public boolean accept(File file) 
	{
	    for(int i= 0 ; i< filter.length ; i++)
	    {
		    if(file.getName().endsWith(filter[i]) && !file.isDirectory())
		    	return true;
	    }
	    return false;
	}
}
