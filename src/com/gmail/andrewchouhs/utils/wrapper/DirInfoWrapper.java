package com.gmail.andrewchouhs.utils.wrapper;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.gmail.andrewchouhs.model.DirInfo;

@XmlRootElement(name = "dirpaths")
public class DirInfoWrapper
{
	private List<DirInfo> dirList;
	
	public DirInfoWrapper(List<DirInfo> dirList)
	{
		setDirList(dirList);
	}
	
	@XmlElement(name = "dirpath")
	public List<DirInfo> getDirList()
	{
		return dirList;
	}
	
	public void setDirList(List<DirInfo> dirList)
	{
		this.dirList = dirList;
	}
	
	public DirInfoWrapper()
	{
	}
}
