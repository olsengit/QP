package com.hiof.objects;

import java.util.ArrayList;
import java.util.List;

public class Question {

	private int questionid, categoryid;
	private String question;
	
	public Question(int questionid, int categoryid, String question) {
		this.questionid = questionid;
		this.categoryid = categoryid;
		this.question = question;
	}

	@Override
	public String toString(){
		return question;
	}
	
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
