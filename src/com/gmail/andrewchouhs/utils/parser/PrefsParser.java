package com.gmail.andrewchouhs.utils.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Locale;
import com.gmail.andrewchouhs.storage.DataStorage;
import static com.gmail.andrewchouhs.storage.DataStorage.prefs;

public class PrefsParser
{
	public static void load()
	{
		File prefsFile = new File(DataStorage.prefsPath);
		//應直接複製內部檔案，而非一直呼叫 setProperty()，至於 Locale 的 Property 問題可使用給予 default 此字串來解決。
		if(!prefsFile.exists())
		{
			prefs.setProperty("Locale", Locale.getDefault().toString());
			prefs.setProperty("RepeatPlay", "false");
			prefs.setProperty("RandomPlay", "false");
			
			save();
		}
		else
		{
			prefs.clear();
			
			try
			{
				prefs.load(new FileInputStream(prefsFile));
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
			File prefsFile = new File(DataStorage.prefsPath);
			prefs.store(new FileOutputStream(prefsFile), null);
		}
		catch (Exception e)
		{
			
		}
	}
}
