package com.gmail.andrewchouhs.utils;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import org.kc7bfi.jflac.sound.spi.Flac2PcmAudioInputStream;
import org.kc7bfi.jflac.sound.spi.FlacAudioFileReader;

public class FLACPlayer implements MusicPlayer
{
	private boolean isPlaying = true;
	
	public void play(String filePath) 
	{  
		if(isPlaying == true)
		{
			final File file = new File(filePath);  
			try 
			{  
				final  FlacAudioFileReader reader = new  FlacAudioFileReader();
				final AudioInputStream  in = reader.getAudioInputStream(file);
				final AudioFormat outFormat = getOutFormat(in.getFormat());  
				final Flac2PcmAudioInputStream fin = new Flac2PcmAudioInputStream(in,outFormat,in.getFrameLength());
				final DataLine.Info info = new DataLine.Info(SourceDataLine.class, outFormat);  
				final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);  
				if (line != null) 
				{  
					line.open(outFormat);  
					line.start();  
					stream(fin, line);  
					line.drain();  
					line.stop();  
				}  
			} catch (Exception e) 
			{  
				throw new IllegalStateException(e);  
			}  
		}
		else
			isPlaying = true;
    }  
   
	public void playAndPause()
	{
		isPlaying = false;
	}
	//unfinished
	public void stop()
	{
		
	}
	
    private AudioFormat getOutFormat(AudioFormat inFormat) 
    {  
        final int ch = inFormat.getChannels();  
        final float rate = inFormat.getSampleRate();  
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);  
    }  
   
    private void stream( Flac2PcmAudioInputStream fin, SourceDataLine line)  throws IOException 
    {  
        final byte[] buffer = new byte[65536];  
        for (int n = 0; n != -1; )
        {
        	if(isPlaying == true)
        	{
        		line.write(buffer, 0, n);
        		n = fin.read(buffer, 0, buffer.length);
        	}
        }
    }  
}
