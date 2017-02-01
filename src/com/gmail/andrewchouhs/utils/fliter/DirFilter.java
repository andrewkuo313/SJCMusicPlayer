package com.gmail.andrewchouhs.utils.fliter;

import java.io.File;
import java.io.FileFilter;

public class DirFilter implements FileFilter
{
	@Override
	public boolean accept(File file) 
	{
	    return file.isDirectory();
	}
}
