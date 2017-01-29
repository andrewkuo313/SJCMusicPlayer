package com.gmail.andrewchouhs.utils;

import java.io.File;
import java.util.Map;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import org.tritonus.share.sampled.TAudioFormat;
import com.gmail.andrewchouhs.Storage;
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
	private boolean stopped = false;
	
	public MusicPlayingService(String filePath , long seekMillis)
	{
		try 
		{
				file = new File(filePath);
			    AudioInputStream baseAudioIn= AudioSystem.getAudioInputStream(file);
			    AudioFormat baseFormat = baseAudioIn.getFormat();
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
				
				long byteRate = 0;
				if (baseFormat instanceof TAudioFormat)
				{
				     Map properties = ((TAudioFormat)baseFormat).properties();
				     String key = "bitrate";
				     byteRate = (Integer) properties.get(key) / 8L;
				}
				System.out.println(byteRate);
				long bytesSkipped = byteRate * seekMillis / 1000L; 
				audioIn.skip(bytesSkipped);
				
		} 
		catch (Exception e)
		{
		}
		
		setOnSucceeded((event) -> 
			Storage.musicPlayer = new MusicPlayingService(filePath , (Long)event.getSource().getValue()));
	}
	
	public void stop()
	{
		stopped = true;
	}
	
	public void seek()
	{
		
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
					byte[] data = new byte[4096];
					
					DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
					dataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
					dataLine.open(audioFormat);
					if (dataLine != null)
					{
						dataLine.start();
						int nBytesRead = 0;
						while (nBytesRead != -1 && stopped == false)
						{
							nBytesRead = audioIn.read(data, 0, data.length);
							if(nBytesRead != -1) 
							{
								dataLine.write(data, 0, nBytesRead);
								totalReadBytes += nBytesRead;
								Platform.runLater(()->Storage.musicTime.set(totalReadBytes / bytesPerSecond));
							}
						}
						dataLine.flush();
						dataLine.stop();
						dataLine.close();
						audioIn.close();
					}
					else
						audioIn.close();
				}
				catch(Exception e)
				{
				}
				return totalReadBytes * 1000L / bytesPerSecond;  
            }
        };
	}
}
