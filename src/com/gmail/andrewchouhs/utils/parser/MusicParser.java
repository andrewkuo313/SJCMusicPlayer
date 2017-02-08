package com.gmail.andrewchouhs.utils.parser;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import com.gmail.andrewchouhs.storage.DataStorage;
import com.gmail.andrewchouhs.utils.wrapper.MusicInfoWrapper;
import static com.gmail.andrewchouhs.storage.PropertyStorage.musicList;
import java.io.File;

public class MusicParser
{
	public static void load()
	{
		File musicInfoFile = new File(DataStorage.musicInfoPath);
		if(!musicInfoFile.exists())
			return;
		try
		{
			musicList.clear();
			musicList.addAll((((MusicInfoWrapper)
					JAXBContext.newInstance(MusicInfoWrapper.class).createUnmarshaller().unmarshal(musicInfoFile)).getMusicList()));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void save()
	{
		try
		{
			Marshaller out = JAXBContext.newInstance(MusicInfoWrapper.class).createMarshaller();
			out.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT , true);
			out.marshal(new MusicInfoWrapper(musicList) , new File(DataStorage.musicInfoPath));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private MusicParser()
	{
	}
}
