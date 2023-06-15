package graphics;

//Autor: Iskander Emilio Mercader Olivares

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {

	/* En esta sección se declaran los atributos de la clase. */

	private final Clip clip;
	private final FloatControl volume;

	/* En esta sección se declaran los métodos de la clase. */

	public Sound(Clip clip) {
		this.clip = clip;
		volume = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
	}
	
	public void play() {
		clip.setFramePosition(0);
		clip.start();
	}

	public void loop() {
		clip.setFramePosition(0);
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public void stop() {
		clip.stop();
	}
	
	public int getFramePosition() {
		return clip.getFramePosition();
	}
	
	public void changeVolume(float value) {
		volume.setValue(value);
	}
}
