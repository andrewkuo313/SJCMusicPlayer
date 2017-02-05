package com.gmail.andrewchouhs.utils.parser;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import com.gmail.andrewchouhs.storage.DataStorage;
import com.gmail.andrewchouhs.utils.wrapper.DirInfoWrapper;
import static com.gmail.andrewchouhs.storage.PropertyStorage.dirList;
import java.io.File;

public class DirParser
{
	public static void load()
	{
		File dirPathsFile = new File(DataStorage.dirPathsPath);
		if(!dirPathsFile.exists())
			return;
		try
		{
			dirList.clear();
			dirList.addAll(((DirInfoWrapper)
					JAXBContext.newInstance(DirInfoWrapper.class).createUnmarshaller().unmarshal(dirPathsFile)).getDirList());
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
			Marshaller out = JAXBContext.newInstance(DirInfoWrapper.class).createMarshaller();
			out.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT , true);
			out.marshal(new DirInfoWrapper(dirList) , new File(DataStorage.dirPathsPath));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
