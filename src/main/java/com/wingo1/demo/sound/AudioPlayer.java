package com.wingo1.demo.sound;

import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * 基本只支持wav
 * 
 * @author cdatc-wingo1
 *
 */
public class AudioPlayer {
	public static void main(String[] args) throws Exception {
		AudioSystem.getAudioFileTypes();
		AudioInputStream audioIn = AudioSystem.getAudioInputStream(AudioPlayer.class.getResource("alarm.wav"));
		Clip clip = AudioSystem.getClip();
		clip.open(audioIn);
		play(clip);
		TimeUnit.SECONDS.sleep(5000L);// 等声音播放
	}

	private static void play(Clip clip) {
		clip.setFramePosition(0);
		clip.start();
	}
}
