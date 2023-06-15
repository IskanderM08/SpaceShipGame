package graphics;

//Autor: Iskander Emilio Mercader Olivares

import java.awt.image.BufferedImage;
import math.Vector2D;

public class Animation {

	/* En esta sección se declaran los atributos de la clase. */

	private final BufferedImage[] frames;
	private final int velocity;
	private int index;
	private boolean running;
	private final Vector2D position;
	private long time;

	/* En esta sección se declaran los métodos de la clase. */
	
	public Animation(BufferedImage[] frames, int velocity, Vector2D position){
		this.frames = frames;
		this.velocity = velocity;
		this.position = position;
		index = 0;
		running = true;
	}
	
	public void update(float dt){
		time += dt;
		
		if(time > velocity){
			time  = 0;
			index ++;
			if(index >= frames.length){
				running = false;
				index = 0;
			}
		}
	}

	public boolean isRunning() {
		return running;
	}

	public Vector2D getPosition() {
		return position;
	}
	
	public BufferedImage getCurrentFrame(){
		return frames[index];
	}
}
