package com.gmail.andrewchouhs.utils;

import java.util.LinkedHashMap;

public class MusicTreeMap extends LinkedHashMap<String , MusicTreeMap>
{
	private static final long serialVersionUID = 1L;
	public transient DirTreeItem treeItem;
	
	public MusicTreeMap(DirTreeItem treeItem)
	{
		this.treeItem = treeItem;
	}
	
	public MusicTreeMap()
	{
	}
}
