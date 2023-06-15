package states;

//Autor: Iskander Emilio Mercader Olivares

/* Esta clase tiene como objetivo mostrar el estado del juego y servir de conector de todas las entidades. */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import gameObjects.Constants;
import gameObjects.Message;
import gameObjects.Meteor;
import gameObjects.MovingObject;
import gameObjects.Player;
import gameObjects.PowerUp;
import gameObjects.PowerUpTypes;
import gameObjects.Size;
import gameObjects.Ufo;
import graphics.Animation;
import graphics.Assets;
import graphics.Sound;
import io.JSONParser;
import io.ScoreData;
import math.Vector2D;
import ui.Action;

public class GameState extends State{

	/* En esta sección se declaran los atributos de la clase. */

	public static final Vector2D PLAYER_START_POSITION = new Vector2D((double) Constants.WIDTH /2 - (double) Assets.player.getWidth() /2,
			(double) Constants.HEIGHT /2 - (double) Assets.player.getHeight() /2);
	
	private final Player player;
	private final ArrayList<MovingObject> movingObjects = new ArrayList<>();
	private final ArrayList<Animation> explosions = new ArrayList<>();
	private final ArrayList<Message> messages = new ArrayList<>();
	private int score = 0;
	private int lives = 3;
	private int meteors;
	private final Sound backgroundMusic;
	private long gameOverTimer;
	private boolean gameOver;
	private long ufoSpawner;
	private long powerUpSpawner;

	public GameState() {
		player = new Player(PLAYER_START_POSITION, new Vector2D(),
				Constants.PLAYER_MAX_VEL, Assets.player, this);
		
		gameOver = false;
		movingObjects.add(player);
		
		meteors = 1;
		startWave();
		backgroundMusic = new Sound(Assets.backgroundMusic);
		backgroundMusic.loop();
		backgroundMusic.changeVolume(-10.0f);
		
		gameOverTimer = 0;
		ufoSpawner = 0;
		powerUpSpawner = 0;
		
		gameOver = false;
	}

	/* En esta sección se declaran los métodos de la clase. */
	
	public void addScore(int value, Vector2D position) {
		Color c = Color.WHITE;
		String text = "+" + value + " score";

		if(player.isDoubleScoreOn()) {
			c = Color.YELLOW;
			value = value * 2;
			text = "+" + value + " score" + " (X2)";
		}
		
		score += value;
		messages.add(new Message(position, true, text, c, false, Assets.fontMed));
	}
	
	public void divideMeteor(Meteor meteor){
		
		Size size = meteor.getSize();
		BufferedImage[] textures = size.textures;
		Size newSize;

		switch (size) {
			case BIG -> newSize = Size.MED;
			case MED -> newSize = Size.SMALL;
			case SMALL -> newSize = Size.TINY;
			default -> {
				return;
			}
		}
		
		for(int i = 0; i < size.quantity; i++){
			movingObjects.add(new Meteor(
					meteor.getPosition(),
					new Vector2D(0, 1).setDirection(Math.random()*Math.PI*2),
					Constants.METEOR_INIT_VEL*Math.random() + 1,
					textures[(int)(Math.random()*textures.length)],
					this,
					newSize
					));
		}
	}
	
	private void startWave(){
		int waves = 1;
		messages.add(new Message(new Vector2D((double) Constants.WIDTH /2, (double) Constants.HEIGHT /2), false,
				"WAVE "+ waves, Color.WHITE, true, Assets.fontBig));
		
		double x, y;
		
		for(int i = 0; i < meteors; i++){
			x = i % 2 == 0 ? Math.random()*Constants.WIDTH : 0;
			y = i % 2 == 0 ? 0 : Math.random()*Constants.HEIGHT;
			
			BufferedImage texture = Assets.bigs[(int)(Math.random()*Assets.bigs.length)];
			movingObjects.add(new Meteor(
					new Vector2D(x, y),
					new Vector2D(0, 1).setDirection(Math.random()*Math.PI*2),
					Constants.METEOR_INIT_VEL*Math.random() + 1,
					texture,
					this,
					Size.BIG
					));
		}
		meteors ++;
	}
	
	public void playExplosion(Vector2D position){
		explosions.add(new Animation(
				Assets.exp,
				50,
				position.subtract(new Vector2D((double) Assets.exp[0].getWidth() /2, (double) Assets.exp[0].getHeight() /2))
				));
	}
	
	private void spawnUfo(){
		int rand = (int) (Math.random()*2);
		double x = rand == 0 ? (Math.random()*Constants.WIDTH): Constants.WIDTH;
		double y = rand == 0 ? Constants.HEIGHT : (Math.random()*Constants.HEIGHT);
		ArrayList<Vector2D> path = new ArrayList<>();
		double posX, posY;
		
		posX = Math.random()*Constants.WIDTH/2;
		posY = Math.random()*Constants.HEIGHT/2;	
		path.add(new Vector2D(posX, posY));

		posX = Math.random()*((double) Constants.WIDTH /2) + (double) Constants.WIDTH /2;
		posY = Math.random()*Constants.HEIGHT/2;	
		path.add(new Vector2D(posX, posY));
		
		posX = Math.random()*Constants.WIDTH/2;
		posY = Math.random()*((double) Constants.HEIGHT /2) + (double) Constants.HEIGHT /2;
		path.add(new Vector2D(posX, posY));
		
		posX = Math.random()*((double) Constants.WIDTH /2) + (double) Constants.WIDTH /2;
		posY = Math.random()*((double) Constants.HEIGHT /2) + (double) Constants.HEIGHT /2;
		path.add(new Vector2D(posX, posY));
		
		movingObjects.add(new Ufo(
				new Vector2D(x, y),
				new Vector2D(),
				Constants.UFO_MAX_VEL,
				Assets.ufo,
				path,
				this
				));
	}

	private void spawnPowerUp() {
		final int x = (int) ((Constants.WIDTH - Assets.orb.getWidth()) * Math.random());
		final int y = (int) ((Constants.HEIGHT - Assets.orb.getHeight()) * Math.random());
		int index = (int) (Math.random() * (PowerUpTypes.values().length));
		PowerUpTypes p = PowerUpTypes.values()[index];
		final String text = p.text;
		Action action = null;
		Vector2D position = new Vector2D(x , y);

		switch (p) {
			case LIFE -> action = () -> {
				lives++;
				messages.add(new Message(
						position,
						false,
						text,
						Color.GREEN,
						false,
						Assets.fontMed
				));
			};
			case SHIELD -> action = () -> {
				player.setShield();
				messages.add(new Message(
						position,
						false,
						text,
						Color.DARK_GRAY,
						false,
						Assets.fontMed
				));
			};
			case SCORE_X2 -> action = () -> {
				player.setDoubleScore();
				messages.add(new Message(
						position,
						false,
						text,
						Color.YELLOW,
						false,
						Assets.fontMed
				));
			};
			case FASTER_FIRE -> action = () -> {
				player.setFastFire();
				messages.add(new Message(
						position,
						false,
						text,
						Color.BLUE,
						false,
						Assets.fontMed
				));
			};
			case SCORE_STACK -> action = () -> {
				score += 1000;
				messages.add(new Message(
						position,
						false,
						text,
						Color.MAGENTA,
						false,
						Assets.fontMed
				));
			};
			case DOUBLE_GUN -> action = () -> {
				player.setDoubleGun();
				messages.add(new Message(
						position,
						false,
						text,
						Color.ORANGE,
						false,
						Assets.fontMed
				));
			};
			default -> {
			}
		}
		
		this.movingObjects.add(new PowerUp(
				position,
				p.texture,
				action,
				this
				));
	}
	
	public void update(float dt) {
		if(gameOver)
			gameOverTimer += dt;
		
		powerUpSpawner += dt;
		ufoSpawner += dt;
		
		for(int i = 0; i < movingObjects.size(); i++) {
			MovingObject mo = movingObjects.get(i);
			mo.update(dt);
			if(mo.isDead()) {
				movingObjects.remove(i);
				i--;
			}
		}
		
		for(int i = 0; i < explosions.size(); i++){
			Animation anim = explosions.get(i);
			anim.update(dt);
			if(!anim.isRunning()){
				explosions.remove(i);
			}
		}
		
		if(gameOverTimer > Constants.GAME_OVER_TIME) {
			try {
				ArrayList<ScoreData> dataList = JSONParser.readFile();
				dataList.add(new ScoreData(score));
				JSONParser.writeFile(dataList);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			backgroundMusic.stop();
			State.changeState(new MenuState());
		}
		
		if(powerUpSpawner > Constants.POWER_UP_SPAWN_TIME) {
			spawnPowerUp();
			powerUpSpawner = 0;
		}

		if(ufoSpawner > Constants.UFO_SPAWN_RATE) {
			spawnUfo();
			ufoSpawner = 0;
		}

		for (MovingObject movingObject : movingObjects)
			if (movingObject instanceof Meteor)
				return;
		
		startWave();
	}
	
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		for(int i = 0; i < messages.size(); i++) {
			messages.get(i).draw(g2d);
			if(messages.get(i).isDead())
				messages.remove(i);
		}

		for (MovingObject movingObject : movingObjects) movingObject.draw(g);

		for (Animation anim : explosions) {
			g2d.drawImage(anim.getCurrentFrame(), (int) anim.getPosition().getX(), (int) anim.getPosition().getY(),
					null);

		}

		drawScore(g);
		drawLives(g);
	}
	
	private void drawScore(Graphics g) {
		Vector2D pos = new Vector2D(850, 25);
		String scoreToString = Integer.toString(score);
		
		for(int i = 0; i < scoreToString.length(); i++) {
			g.drawImage(Assets.numbers[Integer.parseInt(scoreToString.substring(i, i + 1))],
					(int)pos.getX(), (int)pos.getY(), null);
			pos.setX(pos.getX() + 20);
		}
	}
	
	private void drawLives(Graphics g){
		if(lives < 1)
			return;
		
		Vector2D livePosition = new Vector2D(25, 25);
		g.drawImage(Assets.life, (int)livePosition.getX(), (int)livePosition.getY(), null);
		g.drawImage(Assets.numbers[10], (int)livePosition.getX() + 40,
				(int)livePosition.getY() + 5, null);
		
		String livesToString = Integer.toString(lives);
		Vector2D pos = new Vector2D(livePosition.getX(), livePosition.getY());
		
		for(int i = 0; i < livesToString.length(); i ++) {
			int number = Integer.parseInt(livesToString.substring(i, i+1));
			
			if(number <= 0)
				break;
			g.drawImage(Assets.numbers[number],
					(int)pos.getX() + 60, (int)pos.getY() + 5, null);
			pos.setX(pos.getX() + 20);
		}
	}
	
	public ArrayList<MovingObject> getMovingObjects() {
		return movingObjects;
	}

	public ArrayList<Message> getMessages() {
		return messages;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public boolean subtractLife(Vector2D position) {
		lives --;
		Message lifeLostMesg = new Message(
				position,
				false,
				"-1 LIFE",
				Color.RED,
				false,
				Assets.fontMed
				);
		messages.add(lifeLostMesg);

		return lives > 0;
	}

	public void gameOver() {
		Message gameOverMsg = new Message(
				PLAYER_START_POSITION,
				true,
				"GAME OVER",
				Color.WHITE,
				true,
				Assets.fontBig);
		
		this.messages.add(gameOverMsg);
		gameOver = true;
	}
}
