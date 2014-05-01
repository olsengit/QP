package com.hiof.objects;

public class User {
	
	//Fields
	int id;
	String userName; 
	
	
	//Constructor
	public User (int id, String userName){
		this.id = id;
		this.userName = userName;
	}

	// Constructor
	public User (String userName){
		this.userName = userName;
	}
	
	//Constructor
	public User() {}

	//Getters and setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}

