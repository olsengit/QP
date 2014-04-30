package com.hiof.objects;

public class User {
	
	int id;
	String userName; 
	
	public User (int id, String userName){
		this.id = id;
		this.userName = userName;
	}
	
	public User (String userName){
		this.userName = userName;
	}
	
	public User() {}

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

