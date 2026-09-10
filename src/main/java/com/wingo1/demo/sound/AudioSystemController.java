package com.wingo1.demo.sound;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class AudioSystemController implements Initializable {
	File wavFile;
	Clip clip;
	ScheduledExecutorService progressBarService = Executors.newScheduledThreadPool(1);
	@FXML
	ProgressBar progressBar;
	@FXML
	TextField current;
	@FXML
	TextField total;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		progressBarService.scheduleAtFixedRate(() -> {
			if (clip != null && clip.isActive()) {
				Platform.runLater(() -> {
					current.setText(String.valueOf(clip.getFramePosition()));
					total.setText(String.valueOf(clip.getFrameLength()));
					progressBar.setProgress(((double) clip.getFramePosition()) / ((double) clip.getFrameLength()));
				});
			}
		}, 1, 100, TimeUnit.MILLISECONDS);
	}

	@FXML
	public void chooseFile() throws Exception {
		if (clip != null) {
			clip.close();
		}
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("wav");
		fileChooser.setInitialDirectory(new File("."));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("wav", "*.wav"));
		wavFile = fileChooser.showOpenDialog(null);
		AudioInputStream audioIn = AudioSystem.getAudioInputStream(wavFile);
		clip = AudioSystem.getClip();
		clip.open(audioIn);
		audioIn.close();
	}

	@FXML
	public void loop() {
		clip.loop(1);

	}

	public void stop() {
		clip.stop();
	}

	@FXML
	public void setPosition() {
		clip.setFramePosition(Integer.valueOf(current.getText()));
	}

}
