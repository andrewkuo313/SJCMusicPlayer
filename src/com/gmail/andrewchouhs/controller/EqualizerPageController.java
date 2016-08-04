package com.gmail.andrewchouhs.controller;

import com.gmail.andrewchouhs.Storage;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class EqualizerPageController
{
	@FXML
	private HBox rectBox;
	private int rectCount = 63;
	private int threshold = -100;
	private Rectangle[] equalizer = new Rectangle[rectCount];
	
	@FXML
	private void initialize()
	{
		for(int i = 0 ; i<rectCount ; i++)
		{
			Rectangle rect = new Rectangle();
			rect.setWidth(15);
			rect.setHeight(5);
			rect.setFill(Color.YELLOW);
			equalizer[i] = rect;
			rectBox.getChildren().add(rect);
		}
		
		Storage.player.mp.addListener((observable , oldValue , newValue)->
		{
			newValue.setOnReady(()->
			{
				MediaPlayer player = Storage.player.mp.get();
				player.setAudioSpectrumNumBands(rectCount);
				player.setAudioSpectrumThreshold(threshold);
				player.setAudioSpectrumListener((timestamp, duration, magnitudes, phases)->
				{		 
				    for(int i = 0 ; i<rectCount;i++)
				    {
				    	System.out.println(magnitudes[i]);
				    	if(magnitudes[i] == 0)
				    		equalizer[i].setHeight(5);
				    	else
				    		equalizer[i].setHeight((magnitudes[i] - threshold) *3);
				    }
				});
			});
		});
	}
}
