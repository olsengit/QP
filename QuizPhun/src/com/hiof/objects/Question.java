package com.hiof.objects;

public class Question {

	// Fields
	private int questionid, categoryid;
	private String question;

	// Constructor
	public Question(int questionid, int categoryid, String question) {
		this.questionid = questionid;
		this.categoryid = categoryid;
		this.question = question;
	}

	// Overriding the toString method
	@Override
	public String toString() {
		return question;
	}

	// Getters and setters
	public int getQuestionid() {
		return questionid;
	}

	public void setQuestionid(int questionid) {
		this.questionid = questionid;
	}

	public int getCategoryid() {
		return categoryid;
	}

	public void setCategoryid(int categoryid) {
		this.categoryid = categoryid;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}
}
