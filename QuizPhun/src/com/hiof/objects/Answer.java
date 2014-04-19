package com.hiof.objects;

public class Answer {
	
	private int answerid;
	private int questionid;
	private String answer;
	private boolean isAnswer;
	
	public Answer(int answerid, int questionid, String answer, boolean isAnswer) {
		this.answerid = answerid;
		this.questionid = questionid;
		this.answer = answer;
		this.isAnswer = isAnswer;
	}
	
	@Override
	public String toString(){
		return answer;
	}
	
	public int getAnswerid() {
		return answerid;
	}
	public void setAnswerid(int answerid) {
		this.answerid = answerid;
	}
	public int getQuestionid() {
		return questionid;
	}
	public void setQuestionid(int questionid) {
		this.questionid = questionid;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public boolean isAnwser() {
		return isAnswer;
	}
	public void setAnwser(boolean isAnwser) {
		this.isAnswer = isAnwser;
	}


}
