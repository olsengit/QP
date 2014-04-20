package com.hiof.objects;

import java.io.Serializable;

public class Highscore implements Serializable{

	private static final long serialVersionUID = 1L;
	private String playername;
	private int points;
	private String location;
	private String date;
	
	public Highscore(String playername, int points, String location, String date) {
		this.playername = playername;
		this.points = points;
		this.location = location;
		this.date = date;
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
