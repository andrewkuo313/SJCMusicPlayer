package com.gmail.andrewchouhs.utils;

import javafx.scene.control.TreeItem;

public class DirTreeItem extends TreeItem<String>
{
	public MusicTreeMap musicTreeMap;
	public final String path;/////////////
	
	public DirTreeItem(String name , MusicTreeMap musicTreeMap , String path)
	{
		super(name);
		this.musicTreeMap = musicTreeMap;
		this.path = path;
	}
}
