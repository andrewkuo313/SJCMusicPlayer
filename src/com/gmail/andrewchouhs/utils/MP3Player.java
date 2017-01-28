package com.gmail.andrewchouhs.utils;

import java.io.File;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import com.gmail.andrewchouhs.Storage;
import javafx.application.Platform;

public class MP3Player implements MusicPlayer
{
	private File file;
	private AudioInputStream audioIn;
	private AudioFormat audioFormat;
	private SourceDataLine dataLine;
	private int bytesPerSecond;
	private int totalReadBytes = 0;
	private boolean isPlaying = true;
	
	public MP3Player(String filePath)
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
				bytesPerSecond = (int)(audioFormat.getSampleSizeInBits() * audioFormat.getSampleRate() / 8 * baseFormat.getChannels());
		} 
		catch (Exception e)
		{
		}
	}

	@Override
	public void playAndPause()
	{
		isPlaying = !isPlaying;
		if(isPlaying)
		{
			try
			{
				AudioInputStream baseAudioIn= AudioSystem.getAudioInputStream(file);
				audioIn = AudioSystem.getAudioInputStream(audioFormat, baseAudioIn);
				audioIn.skip(totalReadBytes);
			}
			catch(Exception e)
			{
				
			}
			Storage.resetMusicThread();
		}
	}
	
	
	@Override
	public void stop()
	{
		dataLine.drain();
		dataLine.stop();
		dataLine.close();
		try
		{
			audioIn.close();
		}
		catch(Exception e)
		{
		}
	}

	@Override
	public void seek(int millis)
	{
		stop();
		try
		{
			AudioInputStream baseAudioIn= AudioSystem.getAudioInputStream(file);
			audioIn = AudioSystem.getAudioInputStream(audioFormat, baseAudioIn);
			int totalBytesSkipped = bytesPerSecond * millis / 1000; 
			audioIn.skip(totalBytesSkipped);
			totalReadBytes = totalBytesSkipped;
		}
		catch(Exception e)
		{
			
		}
		Storage.resetMusicThread();
	}
	
	@Override
	public void play()
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
			
				while (nBytesRead != -1 && isPlaying == true)
				{
					nBytesRead = audioIn.read(data, 0, data.length);
					if(nBytesRead != -1) 
					{
						dataLine.write(data, 0, nBytesRead);
						totalReadBytes += nBytesRead;
						Platform.runLater(()->Storage.musicTime.set(totalReadBytes / bytesPerSecond));
					}
				}
				stop();
			}
			else
				audioIn.close();
		}
		catch(Exception e)
		{
			
		}
	}
}
