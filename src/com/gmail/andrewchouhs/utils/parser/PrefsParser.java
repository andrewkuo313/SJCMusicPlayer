package com.gmail.andrewchouhs.utils.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import com.gmail.andrewchouhs.storage.DataStorage;
import static com.gmail.andrewchouhs.storage.DataStorage.prefs;

public class PrefsParser
{
	public static void load()
	{
		File prefsFile = new File(DataStorage.prefsPath);
		Properties samplePrefs = new Properties();
		samplePrefs.setProperty(DataStorage.Locale, Locale.getDefault().toString());
		samplePrefs.setProperty(DataStorage.PlayMode, DataStorage.NormalPlay);
		samplePrefs.setProperty(DataStorage.StartWhenOpeningPC, "false");
		samplePrefs.setProperty(DataStorage.PlayWhenOpeningApp, "true");
		samplePrefs.setProperty(DataStorage.AutoUpdate , "false");
		samplePrefs.setProperty(DataStorage.NotifyUpdate , "true");
		if(!prefsFile.exists())
		{
			for(Map.Entry<Object , Object> entry : samplePrefs.entrySet())
				prefs.setProperty((String)entry.getKey(), (String)entry.getValue());
		}
		else
		{
			prefs.clear();
			try
			{	
				prefs.load(new FileInputStream(prefsFile));
				for(Object key : samplePrefs.keySet())
				{
					if(!prefs.containsKey(key))
						prefs.setProperty((String)key , (String)samplePrefs.get(key));
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		if(!DataStorage.availableLocales.containsKey(prefs.getProperty(DataStorage.Locale)))
			prefs.setProperty(DataStorage.Locale, "en");
		save();
	}
	
	public static void save()
	{
		try
		{
			prefs.store(new FileOutputStream(new File(DataStorage.prefsPath)), null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private PrefsParser()
	{
	}
}