package com.gmail.andrewchouhs.utils.wrapper;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.gmail.andrewchouhs.model.MusicInfo;

@XmlRootElement(name = "musicinfo")
public class MusicInfoWrapper
{
	private List<MusicInfo> musicList;
	
	public MusicInfoWrapper(List<MusicInfo> musicList)
	{
		setMusicList(musicList);
	}
	
	@XmlElement(name = "music")
	public List<MusicInfo> getMusicList()
	{
		return musicList;
	}
	
	public void setMusicList(List<MusicInfo> musicList)
	{
		this.musicList = musicList;
	}
	
	public MusicInfoWrapper()
	{
	}
}
