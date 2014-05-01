package com.hiof.objects;

import java.io.Serializable;

public class Highscore implements Serializable {

	// Fields
	private static final long serialVersionUID = 1L;
	private int position;
	private String playername;
	private int points;
	private String location;
	private String date;

	// Constructor
	public Highscore(int position, String playername, int points,
			String location, String date) {
		this.playername = playername;
		this.points = points;
		this.location = location;
		this.date = date;
		this.position = position;
	}

	// Getters and setters
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getPlayername() {
		return playername;
	}

	public void setPlayername(String playername) {
		this.playername = playername;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
