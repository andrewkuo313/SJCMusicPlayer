package com.gmail.andrewchouhs.utils;

import java.util.LinkedHashMap;
import com.gmail.andrewchouhs.model.MusicData;

public class MusicTreeMap extends LinkedHashMap<String , MusicTreeMap>
{
	private static final long serialVersionUID = 1L;
	public final String path;
	public final LinkedHashMap<String , MusicData> musicMap = new LinkedHashMap<String , MusicData>();
	public final MusicTreeMap parent;
	public boolean available = true;
	
	public MusicTreeMap(String path , MusicTreeMap parent)
	{
		this.path = path;
		this.parent = parent;
	}
}
