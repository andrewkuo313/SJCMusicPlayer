package com.gmail.andrewchouhs.utils.fliter;

import java.io.File;
import java.io.FileFilter;
import java.util.Locale;

public class AlbumCoverFilter implements FileFilter
{
	private String[] extensions = {".jpg",".png",".jpeg" , ".bmp" , ".gif"};
	private String[] names = {"cover" , "folder" , "jacket"};
	
	@Override
	public boolean accept(File file)
	{
		if(file.isDirectory() || file.isHidden())
			return false;
		String lowerCaseFileName = file.getName().toLowerCase(Locale.ENGLISH);
	    for(String name : names)
	    {
		    if(lowerCaseFileName.contains(name))
		    {
		    	for(String extension : extensions)
		     	{
		    		if(lowerCaseFileName.endsWith(extension))
		    			return true;
		     	}
		    	return false;
		    }
	    }
	    return false;
	}
}
