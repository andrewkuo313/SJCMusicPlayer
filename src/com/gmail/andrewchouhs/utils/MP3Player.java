package com.gmail.andrewchouhs.utils;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import com.gmail.andrewchouhs.Storage;
import javafx.application.Platform;

public class MP3Player implements MusicPlayer
{
	private boolean isPlaying = true;
	private boolean isStopped = false;
	
	public void play(String filePath)
	{
	  try {
	    File file = new File(filePath);
	    AudioInputStream in= AudioSystem.getAudioInputStream(file);
	    AudioFormat baseFormat = in.getFormat();
	    AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
	                                                                                  baseFormat.getSampleRate(),
	                                                                                  16,
	                                                                                  baseFormat.getChannels(),
	                                                                                  baseFormat.getChannels() * 2,
	                                                                                  baseFormat.getSampleRate(),
	                                                                                  false);
	    AudioInputStream din = AudioSystem.getAudioInputStream(decodedFormat, in);
	    rawplay(decodedFormat, din);
	    in.close();
	  } catch (Exception e)
	    {
	    }
} 
	
	public void playAndPause()
	{
		isPlaying = !isPlaying;
	}
	
	public void stop()
	{
		isStopped = true;
	}

	private void rawplay(AudioFormat targetFormat, AudioInputStream din) throws IOException,                                                                                                LineUnavailableException
	{
	  byte[] data = new byte[4096];
	  SourceDataLine line = getLine(targetFormat); 
	  if (line != null)
	  {
	    line.start();
	    int nBytesRead = 0, nBytesWritten = 0;
	  
	    while (nBytesRead != -1 && isStopped == false)
	    {
	    	if(isPlaying == true)
	    	{
	    		Platform.runLater(()->Storage.musicTime.set((int)(line.getMicrosecondPosition() / 1000000)));
	    		nBytesRead = din.read(data, 0, data.length);
	    		if (nBytesRead != -1) nBytesWritten = line.write(data, 0, nBytesRead);
	    	}
	    }
	    line.drain();
	    line.stop();
	    line.close();
	    din.close();
	  } 
	}

	private SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException
	{
	  SourceDataLine res = null;
	  DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
	  res = (SourceDataLine) AudioSystem.getLine(info);
	  res.open(audioFormat);
	  return res;
	} 
}
