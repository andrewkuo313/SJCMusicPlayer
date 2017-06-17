package com.gmail.andrewchouhs.utils.player;

import java.io.File;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import org.tritonus.share.sampled.file.TAudioFileFormat;
import com.gmail.andrewchouhs.storage.DataStorage;
import com.gmail.andrewchouhs.storage.MusicStorage;
import com.gmail.andrewchouhs.storage.PrefStorage;
import com.gmail.andrewchouhs.storage.PrefStorage.Pref;
import static com.gmail.andrewchouhs.storage.MusicStorage.musicList;
import static com.gmail.andrewchouhs.storage.DataStorage.currentMusicInfo;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class MusicPlayingService extends Service<Long>
{
	private AudioInputStream in;
	private AudioFormat format;
	private boolean paused = false;
	private boolean stopped = false;
	private boolean nextStartDirectly = false;
	private int seekSecond = -1;
	private long currentMillis = 0;
	private boolean hadSeek = false;
	
	public MusicPlayingService(String filePath , long seekMillis , boolean startDirectly , boolean hadSeek)
	{
		try 
		{
			currentMillis = seekMillis;
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
			if(baseFileFormat instanceof TAudioFileFormat)
			{
			    Map<String , Object> properties = ((TAudioFileFormat)baseFileFormat).properties();
			    DataStorage.musicTotalTime.set((int)((long)properties.get("duration") / 1000000L));
			    in.skip((int)properties.get("mp3.length.bytes") * 1000L * seekMillis / (long)properties.get("duration"));
			}
			setOnSucceeded((event) -> 
			DataStorage.musicPlayer.set(new MusicPlayingService(filePath , (long)event.getSource().getValue() , this.nextStartDirectly , this.hadSeek)));
			setOnCancelled((event) -> DataStorage.musicTime.set(0));
			this.hadSeek = hadSeek;
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
		hadSeek = true;
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
					long previousPosition = 0;
					while (!paused)
					{
						if((count = in.read(data, 0, data.length)) != -1) 
						{
							//須減緩傳入。
							Platform.runLater(() -> DataStorage.musicTime.set((int)(currentMillis / 1000L)));
							dataLine.write(data, 0, count);
							currentMillis += (dataLine.getMicrosecondPosition() - previousPosition) / 1000L;
							previousPosition = dataLine.getMicrosecondPosition();
						}
						else
						{
							if(!hadSeek)
							{
								MusicStorage.musicMap.get(currentMusicInfo.get().path.get()).count.add(System.currentTimeMillis());
								System.out.println(currentMusicInfo.get().path.get() + " : " + System.currentTimeMillis());
							}
							//同 RootPageController 寫壞的各項。
							String playMode = PrefStorage.getPref(Pref.PlayMode);
							if(playMode.equals(PrefStorage.getPrefKey(Pref.NormalPlay)))
							{
								Platform.runLater(() ->
								{
									int index = musicList.indexOf(currentMusicInfo.get());
									if(index == musicList.size() - 1)
										index = -1;
									currentMusicInfo.set(musicList.get(index + 1));
								});
								stopped = true;
								break;
							}
							if(playMode.equals(PrefStorage.getPrefKey(Pref.RandomPlay)))
							{
								Platform.runLater(() -> currentMusicInfo.set(musicList.get((int)(musicList.size() * Math.random()))));
								stopped = true;
								break;
							}
							if(playMode.equals(PrefStorage.getPrefKey(Pref.RepeatPlay)))
							{
								seek(0);
								hadSeek = false;
								break;
							}
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
					return currentMillis;
				else
					return ((long)seekSecond) * 1000L;
            }
        };
	}
}
