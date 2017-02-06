package com.gmail.andrewchouhs.utils.service;

import java.net.URL;
import javax.xml.bind.JAXBContext;
import com.gmail.andrewchouhs.model.UpdateInfo;
import com.gmail.andrewchouhs.storage.DataStorage;
import com.gmail.andrewchouhs.storage.PropertyStorage;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class UpdatesDownloadService extends Service<UpdateInfo>
{
	@Override
	protected Task<UpdateInfo> createTask()
	{
		return new Task<UpdateInfo>() 
		{
			protected UpdateInfo call() 
            {
				try
				{
					PropertyStorage.updateInfo.set((UpdateInfo)
							JAXBContext.newInstance(UpdateInfo.class).createUnmarshaller().unmarshal
							(new URL(DataStorage.gitHubUpdatesURL)));
		    		System.out.println("do");
					return null;
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
				PropertyStorage.updateInfo.set(new UpdateInfo(DataStorage.SettingsPage_Updates_NoUpdates , "" , ""));
				return null;
            }
		};
	}
}
