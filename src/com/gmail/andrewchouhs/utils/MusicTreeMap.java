package com.gmail.andrewchouhs.utils;

import java.util.LinkedHashMap;
import java.util.TreeMap;
import com.gmail.andrewchouhs.model.MusicData;

public class MusicTreeMap extends TreeMap<String , MusicTreeMap>
{
	private static final long serialVersionUID = 1L;
	public final String path;
	public final LinkedHashMap<String , MusicData> musicMap = new LinkedHashMap<String , MusicData>();
	public final MusicTreeMap parent;
	public boolean visible = true;
	public boolean ignored = false;
	
	public MusicTreeMap(String path , MusicTreeMap parent)
	{
		this.path = path;
		this.parent = parent;
	}
}
