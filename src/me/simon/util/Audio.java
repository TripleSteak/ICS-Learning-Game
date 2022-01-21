/*
 * File: Audio.java
 * Name: Simon Ou
 * Date: 1/19/2018
 * Description: Manipulation of a single audio file, handles sound and music
 */

package me.simon.util;

import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import me.simon.Main;

public class Audio {
	public static final ArrayList<Audio> LIST = new ArrayList<Audio>();
	public static boolean enableAudio = true;

	private Clip clip;

	public Audio(String name) {
		try {
			AudioInputStream audioInput = AudioSystem
					.getAudioInputStream(Main.class
							.getResourceAsStream("/assets/audio/" + name
									+ ".wav"));
			clip = AudioSystem.getClip();
			clip.open(audioInput);
			LIST.add(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void play() {
		if (enableAudio) {
			clip.setMicrosecondPosition(0);
			clip.start();
		}
	}

	public void loop() {
		clip.loop(Clip.LOOP_CONTINUOUSLY);
		play();
	}
	
	public void stop() {
		clip.stop();
	}

	public static void stopAll() {
		for (Audio audio : LIST)
			audio.stop();
	}
}
