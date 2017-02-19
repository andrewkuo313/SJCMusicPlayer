package com.gmail.andrewchouhs.storage;

import java.util.EnumMap;
import java.util.ResourceBundle;
import com.gmail.andrewchouhs.storage.PrefStorage.Pref;

public final class TextStorage
{
	private static final EnumMap<Text , String> textMap = new EnumMap<Text , String>(Text.class);
	private static ResourceBundle bundle;
	
	public static void init()
	{
		bundle = ResourceBundle.getBundle("com.gmail.andrewchouhs.i18n.text" , PrefStorage.localeMap.get(PrefStorage.getPref(Pref.Language)));
		textMap.put(Text.ListPage_Name , bundle.getString("ListPage.Name"));
		textMap.put(Text.ListPage_NullValue , bundle.getString("ListPage.NullValue"));
		textMap.put(Text.ListPage_NormalPlay , bundle.getString("ListPage.NormalPlay"));
		textMap.put(Text.ListPage_RandomPlay , bundle.getString("ListPage.RandomPlay"));
		textMap.put(Text.ListPage_RepeatPlay, bundle.getString("ListPage.RepeatPlay"));
		textMap.put(Text.SettingsPage_Title , bundle.getString("SettingsPage.Title"));
		textMap.put(Text.SettingsPage_Updates_NoUpdates , bundle.getString("SettingsPage.Updates.NoUpdates"));
	}
	
	public enum Text
	{
		ListPage_Name , ListPage_NullValue , ListPage_NormalPlay , ListPage_RandomPlay , ListPage_RepeatPlay ,
		SettingsPage_Title , SettingsPage_Updates_NoUpdates
	}
	
	public static String getText(Text text)
	{
		return textMap.get(text);
	}
	
	public static ResourceBundle getBundle()
	{
		return bundle;
	}
	
	private TextStorage()
	{
	}
}
