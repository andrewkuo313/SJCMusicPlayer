package com.gmail.andrewchouhs.utils.fliter;

import java.io.File;
import java.io.FileFilter;

public class MusicFilter implements FileFilter
{
	private String[] extensions = {".mp3"};
	
	@Override
	public boolean accept(File file) 
	{
		if(file.isDirectory())
			return false;
		
		String lowerCaseFileName = file.getName().toLowerCase();
		
	    for(String extension : extensions)
	    {
		    if(lowerCaseFileName.endsWith(extension))
		    	return true;
	    }
	    
	    return false;
	}
}
