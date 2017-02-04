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
import static com.gmail.andrewchouhs.storage.PropertyStorage.musicList;
import static com.gmail.andrewchouhs.storage.PropertyStorage.musicInfo;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class MusicPlayingService extends Service<Long>
{
	private AudioInputStream in;
	private AudioFormat format;
	
	private long bytesPerSecond;
	private long totalReadBytes;
	
	private boolean paused = false;
	private boolean stopped = false;
	private boolean nextStartDirectly = false;
	
	private int seekSecond = -1;
	
	public MusicPlayingService(String filePath , long seekMillis , boolean startDirectly)
	{
		try 
		{
			File file = new File(filePath);
			
		    AudioInputStream baseIn= AudioSystem.getAudioInputStream(file);
		    AudioFormat baseFormat = baseIn.getFormat();
		    AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(file);
		    
		    format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
		                                                                                  baseFormat.getSampleRate(),
		                                                                                  16,
		                                                                                  baseFormat.getChannels(),
		                                                                                  baseFormat.getChannels() * 2,
		                                                                                  baseFormat.getSampleRate(),
		                                                                                  false);
		    in = AudioSystem.getAudioInputStream(format, baseIn);
		    
			bytesPerSecond = (long)(format.getFrameSize() * format.getFrameRate());
			totalReadBytes = bytesPerSecond * seekMillis / 1000L;
			
			if(baseFileFormat instanceof TAudioFileFormat)
			{
			    Map<String , Object> properties = ((TAudioFileFormat)baseFileFormat).properties();
			    PropertyStorage.musicTotalTime.set((int)((long)properties.get("duration") / 1000000L));
			}
			if(baseFormat instanceof TAudioFormat)
			{
			     Map<String , Object> properties = ((TAudioFormat)baseFormat).properties();
				 in.skip((long)((int)properties.get("bitrate")) * seekMillis / 8000L);
			}
			
			setOnSucceeded((event) -> 
			PropertyStorage.musicPlayer.set(new MusicPlayingService(filePath , (long)event.getSource().getValue() , this.nextStartDirectly)));
			setOnCancelled((event) -> PropertyStorage.musicTime.set(0));
		
			if(startDirectly == true)
				start();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void pause()
	{
		paused = true;
	}
	
	public void stop()
	{
		stopped = true;
		paused = true;
	}
	
	public void seek(int seekSecond)
	{
		this.seekSecond = seekSecond;
		nextStartDirectly = true;
		paused = true;
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
					DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
					SourceDataLine dataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
					
					dataLine.open(format);
					dataLine.start();
					
					final byte[] data = new byte[4096];
					int count = 0;
					
					while (!paused)
					{
						if((count = in.read(data, 0, data.length)) != -1) 
						{
							dataLine.write(data, 0, count);
							
							totalReadBytes += count;
							
							Platform.runLater(()->PropertyStorage.musicTime.set((int)(totalReadBytes / bytesPerSecond)));
						}
						else
						{					
							Platform.runLater(()->
							{
								int index = musicList.indexOf(musicInfo.get());
								
								if(index == musicList.size() - 1)
									index = -1;
								
								musicInfo.set(musicList.get(index + 1));
							});
							
							stopped = true;
							break;
						}
					}
					
					dataLine.flush();
					dataLine.stop();
					dataLine.close();
					in.close();
					
					if(stopped == true)
						cancel();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
				if(seekSecond == -1)
					return totalReadBytes * 1000L / bytesPerSecond; 
				else
					return ((long)seekSecond) * 1000L;
            }
        };
	}
}
