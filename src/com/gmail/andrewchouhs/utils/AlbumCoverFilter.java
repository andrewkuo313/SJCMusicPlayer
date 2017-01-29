package com.gmail.andrewchouhs.utils;

import java.io.File;
import java.io.FileFilter;

public class AlbumCoverFilter implements FileFilter
{
	private String[] extensionFilter = {".bmp",".gif",".jpeg" , ".png" , ".jpg"};
	private String[] nameFilter = {"cover" , "jacket"};
	
	@Override
	public boolean accept(File file) 
	{
	    for(String extension : extensionFilter)
	    {
		    if(file.getName().endsWith(extension) && !file.isDirectory())
		    {
		    	for(String name : nameFilter)
		    	{
		    		if(file.getName().contains(name))
		    			return true;
		    	}
		    }
	    }
	    return false;
	}
}
