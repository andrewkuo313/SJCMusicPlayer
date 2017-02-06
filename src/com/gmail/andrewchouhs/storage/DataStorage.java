package com.gmail.andrewchouhs.storage;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import com.gmail.andrewchouhs.model.DirInfo;
import com.gmail.andrewchouhs.utils.parser.DirParser;
import com.gmail.andrewchouhs.utils.parser.PrefsParser;
import com.gmail.andrewchouhs.utils.service.UpdatesDownloadService;

public class DataStorage
{
	//PropertyStorage 或許可以合併過來。
	private static final String dataRootPath = System.getenv("APPDATA") + "\\SJCMusicPlayer\\";
	public static final String dirPathsPath = dataRootPath + "DirectoryPaths.xml";
	public static final String prefsPath = dataRootPath + "Preferences.properties";
	public static final String gitHubUpdatesURL = "https://raw.githubusercontent.com/andrewkuo313/SJCMusicPlayer/master/updates/Updates.xml";
	public static final String Locale = "Locale";
	public static final String RepeatPlay = "RepeatPlay";
	public static final String RandomPlay= "RandomPlay";
	public static final String StartWhenOpeningPC = "StartWhenOpeningPC";
	public static final String PlayWhenOpeningApp = "PlayWhenOpeningApp";
	public static final String AutoUpdate = "AutoUpdate";
	public static final String NotifyUpdate = "NotifyUpdate";
	public static final String ListPage_Name;
	public static final String ListPage_NullValue;
	public static final String SettingsPage_Title;
	public static final String SettingsPage_Updates_NoUpdates;
    public static final ResourceBundle bundle;
	public static final Properties prefs = new Properties();
	//或許可以同 LinkedHashSet 變成 LinkedHashMap，並改名。
	public static final HashMap<String , Locale> availableLocales = new HashMap<String , Locale>();
	public static final LinkedHashSet<DirInfo> dirList = new LinkedHashSet<DirInfo>();
	//可能需要抽出變成 Method。
	static
	{
		availableLocales.put("zh_TW" , java.util.Locale.TRADITIONAL_CHINESE);
		availableLocales.put("en" , java.util.Locale.ENGLISH);
		if(!new File(dataRootPath).mkdirs())
		{
			PrefsParser.load();
			DirParser.load();
		}
		bundle = ResourceBundle.getBundle("com.gmail.andrewchouhs.i18n.text" , new Locale(prefs.getProperty(Locale)));
		ListPage_Name = bundle.getString("ListPage.Name");
		ListPage_NullValue = bundle.getString("ListPage.NullValue");
		SettingsPage_Title = bundle.getString("SettingsPage.Title");
		SettingsPage_Updates_NoUpdates = bundle.getString("SettingsPage.Updates.NoUpdates");
		new UpdatesDownloadService().start();
	}

	private DataStorage()
	{
	}
}
