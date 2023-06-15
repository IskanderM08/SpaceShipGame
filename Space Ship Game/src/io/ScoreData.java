package io;

//Autor: Iskander Emilio Mercader Olivares

/* Esta clase tiene como objetivo contener los puntajes obtenidos por el jugador. */

import java.text.SimpleDateFormat;
import java.util.Date;

public class ScoreData {

	/* En esta sección se declaran los atributos de la clase. */

	private String date;
	private int score;

	/* En esta sección se declaran los métodos de la clase. */
	
	public ScoreData(int score) {
		this.score = score;
		Date today = new Date(System.currentTimeMillis());
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		date = format.format(today);
	}
	
	public ScoreData() {

	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
}
