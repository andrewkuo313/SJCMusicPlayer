package com.gmail.andrewchouhs.utils;

import java.util.LinkedHashMap;
import com.gmail.andrewchouhs.model.MusicInfo;

public class MusicTreeMap extends LinkedHashMap<String , MusicTreeMap>
{
	private static final long serialVersionUID = 1L;
	public final LinkedHashMap<String , MusicInfo> musicMap = new LinkedHashMap<String , MusicInfo>();
	public DirTreeItem treeItem;
	
	public MusicTreeMap(DirTreeItem treeItem)
	{
		this.treeItem = treeItem;
	}
}
