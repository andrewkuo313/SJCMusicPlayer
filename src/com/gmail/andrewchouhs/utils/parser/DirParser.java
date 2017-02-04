package com.gmail.andrewchouhs.utils.parser;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
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
			Unmarshaller in = JAXBContext.newInstance(DirInfoWrapper.class).createUnmarshaller();
			
			DirInfoWrapper wrapper = (DirInfoWrapper)in.unmarshal(dirPathsFile);
			
			dirList.clear();
			dirList.addAll(wrapper.getDirList());
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
			File dirPathsFile = new File(DataStorage.dirPathsPath);
			
			Marshaller out = JAXBContext.newInstance(DirInfoWrapper.class).createMarshaller();
			out.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT , true);
			
			DirInfoWrapper wrapper = new DirInfoWrapper(dirList);
			
			out.marshal(wrapper , dirPathsFile);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
