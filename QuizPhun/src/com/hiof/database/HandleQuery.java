package com.hiof.database;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hiof.objects.Answer;
import com.hiof.objects.Category;
import com.hiof.objects.Highscore;
import com.hiof.objects.Question;

/*
 * This is a static helper-class that handles the queries to the database. Getting, inserting and deleting information.
 */
public class HandleQuery {

	/*
	 * Validates the user who attempt to log in. Returns true if the user`s
	 * information is valid
	 */
	public static boolean validateUser(String username, String password) {
		String URL = "http://frigg.hiof.no/webutv_h119/android/get.py?q=adminlogin&usrname="
				+ username + "&passwrd=" + password;
		JSONArray array = new JsonParser().getJsonArray(URL);
		if (array == null) {
			return false;
		} else {
			JSONObject obj;
			try {
				obj = array.getJSONObject(0);
				if (username.equals(obj.get("username"))) {
					return true;
				}
			} catch (JSONException e) {
				return false;
			}
		}
		return false;
	}

	/*
	 * Gets ten random questions from the database. Returns a list of questions
	 */
	public static List<Question> getTenRandomQuestions(int category) {
		List<Question> questions = new ArrayList<Question>(10);

		try {
			String urlQuestion = "http://frigg.hiof.no/webutv_h119/android/get.py?q=tenquestions&categoryid="
					+ category;
			JSONArray questionArray = new JsonParser()
					.getJsonArray(urlQuestion);
			// Loops through the JSON array, and for each JSON object, we add
			// the information in the question list. The questions are randomly
			// selected from the SELECT query in the python script.
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

	/*
	 * Gets all questions from database. This is for the admin-activity
	 */
	public static List<Question> getAllQuestions() {
		List<Question> questions = new ArrayList<Question>();

		try {
			String urlQuestion = "http://frigg.hiof.no/webutv_h119/android/get.py?q=allquestions";
			JSONArray questionArray = new JsonParser()
					.getJsonArray(urlQuestion);
			// Loops through the JSON array, and for each JSON object, we add
			// the information in the question list.
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

	/*
	 * Gets the answers for the questions. Returns a list containing the 4
	 * answers
	 */
	public static List<Answer> getAnswers(int questionid) {
		String urlAnswer = "http://frigg.hiof.no/webutv_h119/android/get.py?q=answer&questionid="
				+ questionid;
		JSONArray answerArray = new JsonParser().getJsonArray(urlAnswer);
		List<Answer> answers = new ArrayList<Answer>(4);
		try {
			// Loops through the JSON array, and for each JSON object, we add
			// the information in the answer list.
			for (int i = 0; i < answerArray.length(); i++) {
				JSONObject obj;
				obj = answerArray.getJSONObject(i);
				int answerid = obj.getInt("answerid");
				int question_id = obj.getInt("questionid");
				String answer = obj.getString("answertext");
				int c = obj.getInt("correct");
				boolean correct = false;
				if (c == 1) {
					correct = true;
				} else if (c == 0) {
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

	/*
	 * Gets all the categories from the database. Returns a list of categories
	 */
	public static List<Category> getAllCategories() {
		List<Category> categories = new ArrayList<Category>();
		String URL = "http://frigg.hiof.no/webutv_h119/android/get.py?q=category";
		JSONArray categoryArray = new JsonParser().getJsonArray(URL);
		try {
			// Loops through the JSON array, and for each JSON object, we add
			// the information in the categories list.
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

	/*
	 * Gets the 10 best scores from the database. Returns a list with
	 * information of the 10 best scored users.
	 */
	public static List<Highscore> getHighscore() {
		List<Highscore> highscores = new ArrayList<Highscore>(10);
		String URL = "http://frigg.hiof.no/webutv_h119/android/get.py?q=highscore";
		JSONArray highscoreArray = new JsonParser().getJsonArray(URL);
		try {
			// Loops through the JSON array, and for each JSON object, we add
			// the information in the highscore list. The 10 best players are
			// selected in a query in the python script
			for (int i = 0; i < highscoreArray.length(); i++) {
				JSONObject obj;
				obj = highscoreArray.getJSONObject(i);
				String playerName = obj.getString("playername");
				int points = obj.getInt(("points"));
				String location = obj.getString("location");
				String date = obj.getString("date");
				highscores.add(new Highscore(i + 1, playerName, points,
						location, date));
			}
		} catch (JSONException e) {
			return null;
		}

		return highscores;
	}

	/*
	 * This method inserts the question and the answers the user made in the
	 * admin-activity. If it`s inserted successfully the method returns true
	 */
	public static boolean insertQuestionAndAnswer(String question,
			int categoryid, List<Answer> answers) {
		final String URL = "http://frigg.hiof.no/webutv_h119/android/set.py?q=";
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(URL);
		try {
			// Name value pair helps us insert the parameters to the URL
			// defined. For instance q=newquestion&... and so on
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
					11);
			nameValuePairs.add(new BasicNameValuePair("q", "newquestion"));
			nameValuePairs.add(new BasicNameValuePair("categoryid", String
					.valueOf(categoryid)));
			nameValuePairs.add(new BasicNameValuePair("question", String
					.valueOf(question)));
			nameValuePairs.add(new BasicNameValuePair("ans1", String
					.valueOf(answers.get(0).getAnswer())));
			nameValuePairs.add(new BasicNameValuePair("ans1cor", String
					.valueOf(answers.get(0).isAnwser())));
			nameValuePairs.add(new BasicNameValuePair("ans2", String
					.valueOf(answers.get(1).getAnswer())));
			nameValuePairs.add(new BasicNameValuePair("ans2cor", String
					.valueOf(answers.get(1).isAnwser())));
			nameValuePairs.add(new BasicNameValuePair("ans3", String
					.valueOf(answers.get(2).getAnswer())));
			nameValuePairs.add(new BasicNameValuePair("ans3cor", String
					.valueOf(answers.get(2).isAnwser())));
			nameValuePairs.add(new BasicNameValuePair("ans4", String
					.valueOf(answers.get(3).getAnswer())));
			nameValuePairs.add(new BasicNameValuePair("ans4cor", String
					.valueOf(answers.get(3).isAnwser())));

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return true;
			}

		} catch (UnsupportedEncodingException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return false;
	}

	/*
	 * This method inserts a new admind in the database. If it`s inserted
	 * successfully the method returns true
	 */
	public static boolean insertNewAdmin(String username, String password) {
		final String URL = "http://frigg.hiof.no/webutv_h119/android/set.py?q=";
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(URL);

		try {
			// Name value pair helps us insert the parameters to the URL
			// defined. For instance q=newadmin&... and so on
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("q", "newadmin"));
			nameValuePairs.add(new BasicNameValuePair("usrname", String
					.valueOf(username)));
			nameValuePairs.add(new BasicNameValuePair("passwrd", String
					.valueOf(password)));

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));

			HttpResponse httpResponse = httpClient.execute(httpPost);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return true;
			}
		} catch (UnsupportedEncodingException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return false;
	}

	/*
	 * This method insert the user`s scores in the database. If it`s inserted
	 * successfully the method returns true
	 */
	public static boolean insertScores(String player, int score, String location) {
		final String URL = "http://frigg.hiof.no/webutv_h119/android/set.py?q=";
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(URL);
		try {
			// Name value pair helps us insert the parameters to the URL
			// defined. For instance q=newscore&... and so on
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
			nameValuePairs.add(new BasicNameValuePair("q", "newscore"));
			nameValuePairs.add(new BasicNameValuePair("player", String
					.valueOf(player)));
			nameValuePairs.add(new BasicNameValuePair("score", String
					.valueOf(score)));
			nameValuePairs.add(new BasicNameValuePair("location", String
					.valueOf(location)));

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
			HttpResponse httpResponse = httpClient.execute(httpPost);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return true;
			}
		} catch (UnsupportedEncodingException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return false;
	}

	/*
	 * This method deletes a question in the database. If it`s inserted
	 * successfully the method returns true.
	 */
	public static boolean deleteQuestion(int questionid) {
		final String URL = "http://frigg.hiof.no/webutv_h119/android/update.py?q=";
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(URL);

		try {
			// Name value pair helps us insert the parameters to the URL
			// defined. For instance q=deletequestion&... and so on
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("q", "deletequestion"));
			nameValuePairs.add(new BasicNameValuePair("questionid", String
					.valueOf(questionid)));
			System.out.println(URL + "deletequestion" + "&questionid="
					+ String.valueOf(questionid));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			System.out.println(httpPost.toString());

			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return true;
			}
		} 
		catch (UnsupportedEncodingException e) {
			return false;
		} 
		catch (IOException e) {
			return false;
		}
		return false;
	}
}
