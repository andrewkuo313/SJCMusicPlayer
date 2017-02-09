package com.gmail.andrewchouhs.utils;

import java.io.Serializable;
import javafx.scene.control.TreeItem;

//須了解為何需要序列化和 MusicTreeMap 為何不用。
public class DirTreeItem extends TreeItem<String> implements Serializable
{
	private static final long serialVersionUID = 1L;
	public MusicTreeMap musicTreeMap;
	
	public DirTreeItem(String name , MusicTreeMap musicTreeMap)
	{
		super(name);
		this.musicTreeMap = musicTreeMap;
	}
}
