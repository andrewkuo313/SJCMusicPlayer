package com.gmail.andrewchouhs.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Locale;
import com.gmail.andrewchouhs.Storage;

public class PreferencesParser
{
	private static File file = new File(System.getenv("APPDATA") + "\\SJCMusicPlayer\\Preferences.properties");
	
	public static void load()
	{
		if(!file.exists())
		{
			Storage.prefs.setProperty("Locale", Locale.getDefault().toString());
			Storage.prefs.setProperty("RepeatPlay", "false");
			Storage.prefs.setProperty("RandomPlay", "false");
			save();
		}
		else
		{
			Storage.prefs.clear();
			try
			{
				Storage.prefs.load(new FileInputStream(file));
			}
			catch (Exception e)
			{
				
			}
		}
	}
	
	public static void save()
	{
		try
		{
			Storage.prefs.store(new FileOutputStream(file), null);
		}
		catch (Exception e)
		{
			
		}
	}
}
