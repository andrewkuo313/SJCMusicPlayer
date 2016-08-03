package com.gmail.andrewchouhs.controller;

import java.io.File;
import com.gmail.andrewchouhs.Storage;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;

public class RootPageController
{
	@FXML
	private MenuItem chooseFolder;
	
	@FXML
	private void setDir()
	{
		DirectoryChooser dirChooser = new DirectoryChooser();
		File selectedDir = dirChooser.showDialog(Storage.getStage());
		if(selectedDir != null)
			Storage.musicDir.set(new File(selectedDir.getAbsolutePath()));
	}
}
