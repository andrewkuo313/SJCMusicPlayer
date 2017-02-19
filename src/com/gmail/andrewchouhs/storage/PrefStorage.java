package com.gmail.andrewchouhs.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public final class PrefStorage
{
	private static final Properties prefs = new Properties();
	private static final EnumMap<Pref , String> prefKeyMap = new EnumMap<Pref , String>(Pref.class);
	public static final LinkedHashMap<String , Locale> localeMap = new LinkedHashMap<String , Locale>();
	
	public static void init()
	{
		prefKeyMap.put(Pref.Language , "Language");
		prefKeyMap.put(Pref.PlayMode , "PlayMode");
		prefKeyMap.put(Pref.NormalPlay , "NormalPlay");
		prefKeyMap.put(Pref.RandomPlay , "RandomPlay");
		prefKeyMap.put(Pref.RepeatPlay , "RepeatPlay");
		prefKeyMap.put(Pref.StartWhenOpeningPC , "StartWhenOpeningPC");
		prefKeyMap.put(Pref.PlayWhenOpeningApp , "PlayWhenOpeningApp");
		prefKeyMap.put(Pref.AutoUpdate , "AutoUpdate");
		prefKeyMap.put(Pref.NotifyUpdate , "NotifyUpdate");
		localeMap.put("zh_TW" , Locale.TRADITIONAL_CHINESE);
		localeMap.put("en" , Locale.ENGLISH);
		if(!new File(DataStorage.dataRootPath).mkdirs())
			load();
	}
	
	public static void load()
	{
		File prefsFile = new File(DataStorage.prefsPath);
		Properties samplePrefs = new Properties();
		samplePrefs.setProperty(prefKeyMap.get(Pref.Language) , Locale.getDefault().toString());
		samplePrefs.setProperty(prefKeyMap.get(Pref.PlayMode), prefKeyMap.get(Pref.NormalPlay));
		samplePrefs.setProperty(prefKeyMap.get(Pref.StartWhenOpeningPC) , "false");
		samplePrefs.setProperty(prefKeyMap.get(Pref.PlayWhenOpeningApp) , "true");
		samplePrefs.setProperty(prefKeyMap.get(Pref.AutoUpdate) , "false");
		samplePrefs.setProperty(prefKeyMap.get(Pref.NotifyUpdate) , "true");
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
		if(!localeMap.containsKey(prefs.getProperty(prefKeyMap.get(Pref.Language))))
			setPref(Pref.Language , "en");
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
	
	public enum Pref
	{
		Language , PlayMode , NormalPlay , RandomPlay , RepeatPlay ,
		StartWhenOpeningPC , PlayWhenOpeningApp , AutoUpdate , NotifyUpdate
	}
	
	public static String getPref(Pref key)
	{
		return prefs.getProperty(prefKeyMap.get(key));
	}
	
	public static String getPrefKey(Pref key)
	{
		return prefKeyMap.get(key);
	}
	public static void setPref(Pref key , String value)
	{
		prefs.setProperty(prefKeyMap.get(key) , value);
	}
	
	private PrefStorage()
	{
	}
}
