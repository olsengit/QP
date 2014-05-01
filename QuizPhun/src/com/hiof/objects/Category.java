package com.hiof.objects;

public class Category {
	// Fields
	private int categoryid;
	private String categoryname;

	// Constructor
	public Category(int categoryid, String categoryname) {
		this.categoryid = categoryid;
		this.categoryname = categoryname;
	}

	// Overriding the toString method
	@Override
	public String toString() {
		return categoryname;
	}

	// Getters and setters
	public int getCategoryid() {
		return categoryid;
	}

	public void setCategoryid(int categoryid) {
		this.categoryid = categoryid;
	}

	public String getCategoryname() {
		return categoryname;
	}

	public void setCategoryname(String categoryname) {
		this.categoryname = categoryname;
	}
}
