package ui;

//Autor: Iskander Emilio Mercader Olivares

/* Esta clase tiene como objetivo servir como base para todas las funciones que tendrán los botones en el
sistema, en este caso jugar, revisar los puntajes y cerrar. */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import graphics.Assets;
import graphics.Text;
import input.MouseInput;
import math.Vector2D;

public class Button {

	/* En esta sección se declaran los atributos de la clase. */

	private final BufferedImage mouseOutImg;
	private final BufferedImage mouseInImg;
	private boolean mouseIn;
	private final Rectangle boundingBox;
	private final Action action;
	private final String text;

	/* En esta sección se declaran los métodos de la clase. */

	public Button(
			BufferedImage mouseOutImg,
			BufferedImage mouseInImg,
			int x, int y,
			String text,
			Action action
			) {
		this.mouseInImg = mouseInImg;
		this.mouseOutImg = mouseOutImg;
		this.text = text;
		boundingBox = new Rectangle(x, y, mouseInImg.getWidth(), mouseInImg.getHeight());
		this.action = action;
	}
	
	public void update() {

		mouseIn = boundingBox.contains(MouseInput.X, MouseInput.Y);
		
		if(mouseIn && MouseInput.MLB) {
			action.doAction();
		}
	}
	
	public void draw(Graphics g) {

		if(mouseIn) {
			g.drawImage(mouseInImg, boundingBox.x, boundingBox.y, null);
		}else {
			g.drawImage(mouseOutImg, boundingBox.x, boundingBox.y, null);
		}
		
		Text.drawText(
				g,
				text,
				new Vector2D(
						boundingBox.getX() + boundingBox.getWidth() / 2,
						boundingBox.getY() + boundingBox.getHeight()),
				true,
				Color.BLACK,
				Assets.fontMed);
	}
}
