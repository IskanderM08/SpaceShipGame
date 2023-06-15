package main;

//Autor: Iskander Emilio Mercader Olivares

/* Esta clase tiene como objetivo servir de clase main, la cual realizará el despliegue de una interfaz,
* donde estarán disponibles las funciones principales del sistema. */

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.io.Serial;
import javax.swing.JFrame;
import gameObjects.Constants;
import graphics.Assets;
import input.KeyBoard;
import input.MouseInput;
import states.LoadingState;
import states.State;

public class Window extends JFrame implements Runnable{

	/* En esta sección se declaran los atributos de la clase. */

	@Serial
	private static final long serialVersionUID = 1L;
	private final Canvas canvas;
	private Thread thread;
	private boolean running = false;
	private final int FPS = 55;
	private double delta = 0;
	private int AVERAGEFPS = FPS;
	private final KeyBoard keyBoard;

	public Window() {
		setTitle("Space Ship Game");
		setSize(Constants.WIDTH, Constants.HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);
		canvas = new Canvas();
		keyBoard = new KeyBoard();
		MouseInput mouseInput = new MouseInput();
		canvas.setPreferredSize(new Dimension(Constants.WIDTH, Constants.HEIGHT));
		canvas.setMaximumSize(new Dimension(Constants.WIDTH, Constants.HEIGHT));
		canvas.setMinimumSize(new Dimension(Constants.WIDTH, Constants.HEIGHT));
		canvas.setFocusable(true);
		add(canvas);
		canvas.addKeyListener(keyBoard);
		canvas.addMouseListener(mouseInput);
		canvas.addMouseMotionListener(mouseInput);
		setVisible(true);
	}

	/* En esta sección se declaran los métodos de la clase. */

	public static void main(String[] args) {
		new Window().start();
	}

	private void update(float dt){
		keyBoard.update();
		State.getCurrentState().update(dt);
	}

	private void draw(){
		BufferStrategy bs = canvas.getBufferStrategy();
		if(bs == null) {
			canvas.createBufferStrategy(3);
			return;
		}

		Graphics g = bs.getDrawGraphics();
		
		//-----------------------
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, Constants.WIDTH, Constants.HEIGHT);
		State.getCurrentState().draw(g);
		g.setColor(Color.WHITE);
		g.drawString(String.valueOf(AVERAGEFPS), 10, 20);
		
		//---------------------
		
		g.dispose();
		bs.show();
	}
	
	private void init() {
		
		Thread loadingThread = new Thread(Assets::init
		);

		State.changeState(new LoadingState(loadingThread));
	}
	
	
	@Override
	public void run() {
		long now;
		long lastTime = System.nanoTime();
		int frames = 0;
		long time = 0;
		
		init();
		
		while(running) {
			now = System.nanoTime();
			double TARGETTIME = (double) 1000000000 / FPS;
			delta += (now - lastTime)/ TARGETTIME;
			time += (now - lastTime);
			lastTime = now;
			
			if(delta >= 1) {
				update((float) (delta * TARGETTIME * 0.000001f));
				draw();
				delta --;
				frames ++;
			}

			if(time >= 1000000000) {
				AVERAGEFPS = frames;
				frames = 0;
				time = 0;
				
			}
		}
		stop();
	}
	
	private void start(){
		thread = new Thread(this);
		thread.start();
		running = true;
	}

	private void stop(){
		try {
			thread.join();
			running = false;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}