package com.gmail.andrewchouhs.utils.player;

import java.io.File;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import org.tritonus.share.sampled.TAudioFormat;
import org.tritonus.share.sampled.file.TAudioFileFormat;
import com.gmail.andrewchouhs.storage.PropertyStorage;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class MusicPlayingService extends Service<Long>
{
	private File file;
	private AudioInputStream audioIn;
	private AudioFormat audioFormat;
	private SourceDataLine dataLine;
	private long bytesPerSecond;
	private long totalReadBytes;
	private long totalSkippedBytes;
	private long bitrate;
	private boolean paused = false;
	private boolean stopped = false;
	private boolean startDirectly = false;
	private int seekSecond = 2147483647;
	
	public MusicPlayingService(String filePath , long seekMillis , boolean startDirectly)
	{
		try 
		{
			file = new File(filePath);
		    AudioInputStream baseAudioIn= AudioSystem.getAudioInputStream(file);
		    AudioFormat baseFormat = baseAudioIn.getFormat();
		    AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(file);
		    audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
		                                                                                  baseFormat.getSampleRate(),
		                                                                                  16,
		                                                                                  baseFormat.getChannels(),
		                                                                                  baseFormat.getChannels() * 2,
		                                                                                  baseFormat.getSampleRate(),
		                                                                                  false);
		    audioIn = AudioSystem.getAudioInputStream(audioFormat, baseAudioIn);
			bytesPerSecond = (long)(audioFormat.getFrameSize() * audioFormat.getFrameRate());
			totalReadBytes = bytesPerSecond * seekMillis / 1000L;
			
			if (baseFileFormat instanceof TAudioFileFormat)
			{
			    Map<String , Object> properties = ((TAudioFileFormat)baseFileFormat).properties();
			    PropertyStorage.musicTotalTime.set((int)((Long)properties.get("duration") / 1000000L));
			}
			
			if (baseFormat instanceof TAudioFormat)
			{
			     Map<String , Object> properties = ((TAudioFormat)baseFormat).properties();
			     bitrate =  (Integer) properties.get("bitrate");
			     totalSkippedBytes = bitrate * seekMillis / 8000L;
			}
		} 
		catch (Exception e)
		{
		}
		setOnSucceeded((event) -> 
			PropertyStorage.musicPlayer.set(new MusicPlayingService(filePath , (Long)event.getSource().getValue() , this.startDirectly)));
		
		setOnCancelled((event) -> PropertyStorage.musicTime.set(0));
		
		if(startDirectly == true)
			start();
	}
	
	public void pause()
	{
		paused = true;
	}
	
	public void stop()
	{
		paused = true;
		stopped = true;
	}
	
	public void seek(int seekSecond)
	{
		startDirectly = true;
		paused = true;
		this.seekSecond = seekSecond;
		totalReadBytes = bytesPerSecond * seekSecond;
	}
	
	@Override
	protected Task<Long> createTask()
	{
		return new Task<Long>() 
		{
			protected Long call() 
            {
				try
				{
					audioIn.skip(totalSkippedBytes);
					byte[] data = new byte[4096];
					DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
					dataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
					dataLine.open(audioFormat);
					if (dataLine != null)
					{
						dataLine.start();
						int nBytesRead = 0;
						while (nBytesRead != -1 && !paused)
						{
							nBytesRead = audioIn.read(data, 0, data.length);
							if(nBytesRead != -1) 
							{
								dataLine.write(data, 0, nBytesRead);
								totalReadBytes += nBytesRead;
								Platform.runLater(()->PropertyStorage.musicTime.set((int)(totalReadBytes / bytesPerSecond)));
							}
							else
							{
								dataLine.flush();
								dataLine.stop();
								dataLine.close();
								audioIn.close();
								Platform.runLater(()->
								{
									int index = PropertyStorage.musicList.indexOf(PropertyStorage.musicInfo.get());
									if(index == PropertyStorage.musicList.size() - 1)
										index = -1;
									PropertyStorage.musicInfo.set(PropertyStorage.musicList.get(index + 1));
								});
								cancel();
							}
						}
						dataLine.flush();
						dataLine.stop();
						dataLine.close();
						audioIn.close();
						if(stopped == true)
							cancel();
							
					}
					else
						audioIn.close();
				}
				catch(Exception e)
				{
				}
				if(seekSecond == 2147483647)
					return totalReadBytes * 1000L / bytesPerSecond; 
				else
					return (long)seekSecond*1000L;
            }
        };
	}
}
