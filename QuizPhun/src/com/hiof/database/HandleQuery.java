package com.hiof.database;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hiof.objects.Answer;
import com.hiof.objects.Category;
import com.hiof.objects.Highscore;
import com.hiof.objects.Question;

public class HandleQuery {

	public static List<Question> getTenRandomQuestions(int category) {
		List<Question> questions = new ArrayList<Question>(10);

		try {
			String urlQuestion = "http://frigg.hiof.no/webutv_h119/android/get.py?q=tenquestions&categoryid="+ category;
			JSONArray questionArray = new JsonParser().getJsonArray(urlQuestion);
			for (int i = 0; i < questionArray.length(); i++) {
				JSONObject qobj;
				qobj = questionArray.getJSONObject(i);
				int questionid = qobj.getInt("questionid");
				int categoryid = qobj.getInt("categoryid");
				String question = qobj.getString("questiontext");
				questions.add(new Question(questionid, categoryid, question));
			}
		} catch (JSONException e) {
			return null;
		}
		return questions;
	}

	public static List<Answer> getAnswers(int questionid) {
		String urlAnswer = "http://frigg.hiof.no/webutv_h119/android/get.py?q=answer&questionid="+ questionid;
		JSONArray answerArray = new JsonParser().getJsonArray(urlAnswer);
		List<Answer> answers = new ArrayList<Answer>(4);
		try {
			for (int i = 0; i < answerArray.length(); i++) {
				JSONObject obj;
				obj = answerArray.getJSONObject(i);

				int answerid = obj.getInt("answerid");
				int question_id = obj.getInt("questionid");
				String answer = obj.getString("answertext");
				int c = obj.getInt("correct");
				boolean correct = false;
				if (c == 1){
					correct = true;
				}else if (c == 0){
					correct = false;
				}
				answers.add(new Answer(answerid, question_id, answer, correct));
			}
		} catch (JSONException e) {
			System.out.println("Exception in getAnswers");
			return null;
		}
		return answers;
	}

	public static List<Category> getAllCategories() {
		List<Category> categories = new ArrayList<Category>();
		String URL = "http://frigg.hiof.no/webutv_h119/android/get.py?q=category";
		JSONArray categoryArray = new JsonParser().getJsonArray(URL);
		try {
			for (int i = 0; i < categoryArray.length(); i++) {
				JSONObject obj;
				obj = categoryArray.getJSONObject(i);

				int categoryid = obj.getInt("categoryid");
				String category = obj.getString("categoryname");
				categories.add(new Category(categoryid, category));
			}
		} catch (JSONException e) {
			return null;
		}

		return categories;
	}

	public static List<Highscore> getHighscore() {
		List<Highscore> highscores = new ArrayList<Highscore>(10);
		final String URL = "http://frigg.hiof.no/bo14-g23/py/hcserv.py?q=";
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(URL);
		return highscores;
	}
}
