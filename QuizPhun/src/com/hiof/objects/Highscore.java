package com.hiof.objects;

public class Highscore {
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
