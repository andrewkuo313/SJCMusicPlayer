package com.gmail.andrewchouhs.utils;

import java.io.File;
import java.io.FilenameFilter;

public class MusicFileFilter implements FilenameFilter
{
	private String[] filter = {".mp3",".ogg",".3gp",".mp4"};
	
	public boolean accept(File file, String filename) 
	{
	    for(int i= 0 ;i< filter.length ; i++)
	    {
		    if(filename.endsWith(filter[i]))
		    	return true;
	    }
	    return false;
	}
	
}
