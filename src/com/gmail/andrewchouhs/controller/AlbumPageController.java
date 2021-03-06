package com.gmail.andrewchouhs.controller;

import static com.gmail.andrewchouhs.storage.MusicStorage.albumCoverList;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

public class AlbumPageController
{
	@FXML
	private TextField searchTextField;
	@FXML
	private FlowPane albumCoverPane;
	private int albumCoverCount = -4;
	
	@FXML
	private void initialize()
	{
		albumCoverList.addListener((ListChangeListener<Image>)(c -> 
		{
			albumCoverCount++;
			if(albumCoverCount == albumCoverList.size())
			{
				albumCoverPane.getChildren().clear();
				for(Image image : albumCoverList)
				{
					ImageView imageView = new ImageView(image);
					imageView.setFitWidth(250);
					imageView.setFitHeight(image.getHeight() / image.getWidth() * 250d);
					albumCoverPane.getChildren().add(imageView);
				}
				albumCoverCount = -4;
			}
		}));
	}
}
