package states;

//Autor: Iskander Emilio Mercader Olivares

/* Esta clase tiene como objetivo servir de base para las demás clases estado. */

import java.awt.Graphics;

public abstract class State {

	/* En esta sección se declaran los atributos de la clase. */

	private static State currentState = null;

	/* En esta sección se declaran los métodos de la clase. */

	public static State getCurrentState() {return currentState;}
	public static void changeState(State newState) {
		currentState = newState;
	}
	public abstract void update(float dt);
	public abstract void draw(Graphics g);
}
