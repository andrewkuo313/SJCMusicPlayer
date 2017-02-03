package com.gmail.andrewchouhs.storage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import com.gmail.andrewchouhs.utils.parser.DirParser;
import com.gmail.andrewchouhs.utils.parser.PrefsParser;

public class DataStorage
{
	private static final String dataRootPath = System.getenv("APPDATA") + "\\SJCMusicPlayer\\";
	
	private static final String updatesPath = dataRootPath + "Updates.xml";
	public static final String dirPathPath = dataRootPath + "DirectoryPath.xml";
	public static final String prefsPath = dataRootPath + "Preferences.properties";
	
	private static final String gitHubUpdatesURL = "https://raw.githubusercontent.com/andrewkuo313/SJCMusicPlayer/master/updates/Updates.xml";
	
	public static final Properties prefs = new Properties();
	//bundle 的 getString 應透過 Enum。
	public static final ResourceBundle bundle = 
			ResourceBundle.getBundle("com.gmail.andrewchouhs.i18n.text" , new Locale("en" , "US"));
	
	//可能需要抽出變成 Method。
	static
	{
		if(!new File(dataRootPath).mkdirs())
		{
			PrefsParser.load();
			DirParser.load();
		}
		
		//轉存至 String，而無需存回硬碟，需分出執行緒否則占用太大開啟時間。
		try(BufferedInputStream in = new BufferedInputStream(new URL(gitHubUpdatesURL).openStream());
				FileOutputStream out = new FileOutputStream(new File(updatesPath)))
		{
			final byte data[] = new byte[1024];
			int count;
			while((count = in.read(data, 0, 1024)) != -1) 
				out.write(data, 0, count);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private DataStorage()
	{
		
	}
}
