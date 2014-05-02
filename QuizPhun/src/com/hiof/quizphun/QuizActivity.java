package com.hiof.quizphun;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hiof.adapter.CustomAnswerAdapter;
import com.hiof.database.HandleQuery;
import com.hiof.objects.Answer;
import com.hiof.objects.Question;

public class QuizActivity extends ActionBarActivity implements LocationListener {

	private QuizActivity local;
	private List<Question> questions = new ArrayList<Question>(10);
	private List<Answer> answers = new ArrayList<Answer>(40);
	private int count = 0;
	private static int points = 0;
	private static String playerName, locationString;
	private int timeleft;
	private double lat, lon;
	private int count_question = 0;
	Button nextQuestion;
	private CountDownTimer cdt, cdtError;
	private LocationManager locationManager;
	private String provider, city, categoryname;
	private Location location;
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 200; 
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 5; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		categoryname = getIntent().getStringExtra("CATEGORYNAME");
		local = this;
		setTitle(categoryname);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		location = locationManager.getLastKnownLocation(provider);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Requests the locationdata when the activity is resumed, reset the
		// points and prepare ten questions
		locationManager.requestLocationUpdates(provider, 400, 1, this);
		points = 0;
		new prepareTenQuestions().execute();
		Intent reminderServiceIntent = new Intent(this, ReminderService.class);
		stopService(reminderServiceIntent);
	}

	@Override
	public void onStop() {
		super.onStop();
		// Start the service when the user stop playing
		Intent reminderServiceIntent = new Intent(this, ReminderService.class);
		startService(reminderServiceIntent);
	}

	@Override
	public void onBackPressed() {
		// Do nothing if back pressed during game
	}

	@Override
	public void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		lat = (location.getLatitude());
		lon = (location.getLongitude());
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	/*
	 * Starts the quiz
	 */
	private void startQuiz() {
		// If the size of the list is 10, we know everything is alright
		if (questions.size() == 10) {
			// If test to keep track of when the quiz is finished
			if (++count_question <= 10) {
				setTitle(categoryname + " (" + count_question + " of 10)");
				nextQuestion();
			} else {
				// Quiz is finished, gets gps location and converts it to a
				// city. Then we add the score in database
				getGpsLocation();
				convertGpsLocToCity();
				playerName = getIntent().getStringExtra("USERNAME");
				if (location != null) {
					locationString = city;
				} else {
					locationString = "Unknown";
				}
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.container, new Score())
						.addToBackStack("Score").commit();
				Thread insertScoreThread = new Thread(new Runnable() {
					@Override
					public void run() {
						HandleQuery.insertScores(playerName, points,
								locationString);
					}
				});
				insertScoreThread.start();
			}
		}
		// Something went wrong, there isn't enough question in the list. Show a
		// toast-message and send the user back to the categoryactivity
		else {
			Toast.makeText(local,
					"Not enough questions in this category to play",
					Toast.LENGTH_SHORT).show();
			Intent i = new Intent(this, CategoryActivity.class);
			startActivity(i);
			finish();
		}
	}

	/*
	 * Converts from latitude and longitude to a city
	 */
	private void convertGpsLocToCity() {
		Geocoder gcd = new Geocoder(this, Locale.getDefault());
		List<Address> addresses = null;
		try {
			addresses = gcd.getFromLocation(lat, lon, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (addresses.size() > 0 && addresses != null) {
			city = addresses.get(0).getLocality();
		}
		// If we can't get a city, we set the text to Unknown
		else {
			city = "Unknown";
		}
	}

	/*
	 * Gets the gps location, through the network or gps provider
	 */
	private Location getGpsLocation() {
		try {
			boolean isGPSEnabled = false;
			boolean isNetworkEnabled = false;
			boolean canGetLocation = false;
			locationManager = (LocationManager) this
					.getSystemService(LOCATION_SERVICE);

			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// No network provider is enabled
			} else {
				canGetLocation = true;
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							lat = location.getLatitude();
							lon = location.getLongitude();
						}
					}
				}

				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								lat = location.getLatitude();
								lon = location.getLongitude();
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}

	/*
	 * Invoked when next button is clicked
	 */
	public void nextQuestionClicked(View v) {
		startQuiz();
		nextQuestion = (Button) findViewById(R.id.button_quiz_nextquestion);
		nextQuestion.setVisibility(View.INVISIBLE);
	}

	/*
	 * Adds a new question
	 */
	private void nextQuestion() {
		final ProgressBar pb = (ProgressBar) findViewById(R.id.progressbar_quiz_timeleft);
		final TextView tv_timeleft = (TextView) findViewById(R.id.textview_quiz_timeleft);
		final GridView gridanswers = (GridView) findViewById(R.id.gridview_quiz_answer);

		final List<Answer> answerToThisQuestion = new ArrayList<Answer>(4);
		answerToThisQuestion.add(answers.get((count_question * 4) - 4));
		answerToThisQuestion.add(answers.get((count_question * 4) - 3));
		answerToThisQuestion.add(answers.get((count_question * 4) - 2));
		answerToThisQuestion.add(answers.get((count_question * 4) - 1));

		// Sets the max value of the progresbar
		pb.setMax(10);
		// Adds a coundowntimer
		cdt = new CountDownTimer(10000, 1000) {

			public void onTick(long millisUntilFinished) {
				// Every second we udate the progressbar and the timeleft
				timeleft = (int) (millisUntilFinished / 1000);
				pb.setProgress(timeleft);
				tv_timeleft.setText(timeleft + "s");
			}

			// Gets the answerid
			public void onFinish() {
				int answerId = 0;
				for (int i = 0; i < 4; i++) {
					if (answerToThisQuestion.get(i).isAnwser())
						answerId = i;
				}
				Vibrator vibrator = (Vibrator) getApplicationContext()
						.getSystemService(Context.VIBRATOR_SERVICE);
				// Vibrate for 500 milliseconds
				vibrator.vibrate(500);
				// Show user the right answers
				gridanswers.getChildAt(answerId)
						.setBackgroundColor(Color.GREEN);
				nextQuestion = (Button) findViewById(R.id.button_quiz_nextquestion);
				nextQuestion.setVisibility(View.VISIBLE);
				gridanswers.setEnabled(false);
			}
		};
		cdt.start();

		TextView question = (TextView) findViewById(R.id.textview_quiz_question);
		question.setText(questions.get((count_question - 1)).getQuestion());
		gridanswers.setAdapter(new CustomAnswerAdapter(this,
				answerToThisQuestion));
		gridanswers.setEnabled(true);
		gridanswers.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				cdt.cancel();
				// If the answer is correct
				if (answerToThisQuestion.get(position).isAnwser()) {
					points += 50 + timeleft;
					try {
						Uri notification = RingtoneManager
								.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
						Ringtone r = RingtoneManager.getRingtone(
								getApplicationContext(), notification);
						r.play();
					} catch (Exception e) {
						e.printStackTrace();
					}
					parent.getChildAt(position).setBackgroundColor(Color.GREEN);
				}
				// If the answer is wrong
				else {
					points -= 15;
					getApplicationContext();
					Vibrator vibrator = (Vibrator) getApplicationContext()
							.getSystemService(Context.VIBRATOR_SERVICE);
					// Vibrate for 500 milliseconds
					vibrator.vibrate(500);
					parent.getChildAt(position).setBackgroundColor(Color.RED);
				}

				nextQuestion = (Button) findViewById(R.id.button_quiz_nextquestion);
				nextQuestion.setVisibility(View.VISIBLE);
				gridanswers.setEnabled(false);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.general, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.log_out) {
			// Stop the countdowntimers
			if (cdtError != null)
				cdtError.cancel();
			if (cdt != null)
				cdt.cancel();
			Intent i = new Intent(this, MainActivity.class);
			startActivity(i);
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * Asynctask - Get 10 questions from the database
	 */
	private class prepareTenQuestions extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog Dialog = new ProgressDialog(local);

		@Override
		protected void onPreExecute() {
			Dialog.setMessage("Loading quiz..");
			Dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				// Gets the questions from the database, and them the answers
				// list
				int category = getIntent().getIntExtra("CATEGORYID", 1);
				new HandleQuery();
				questions = HandleQuery.getTenRandomQuestions(category);
				try {
					for (int i = 0; i < questions.size(); i++) {
						int questionid = questions.get(i).getQuestionid();
						answers.addAll(HandleQuery.getAnswers(questionid));
					}
				} catch (NullPointerException e) {
					System.out
							.println("How are we going to play without answers to the questions??");
					return false;
				}
			} catch (NullPointerException e) {
				System.out
						.println("AsyncTask in CategoryActivity returns null while trying to get all categories from database");
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// If we got data from the database, start the quiz
			if (result) {
				startQuiz();
			}
			// If we didn`t get any questions, something went wrong. Show a
			// toast-message, and try again every second for three seconds.
			else {
				if (++count <= 3) {
					Toast.makeText(
							local,
							"Something went wrong, reloading ("
									+ count
									+ " of 3 attempts)... Check your connection",
							Toast.LENGTH_SHORT).show();
					cdtError = new CountDownTimer(2000, 1000) {
						@Override
						public void onTick(long millisUntilFinished) {
							// Do nothing
						}

						@Override
						public void onFinish() {
							new prepareTenQuestions().execute();
						}
					}.start();
				} else {
					// There is something wrong, send user to start
					Intent i = new Intent(local, MainActivity.class);
					startActivity(i);
				}
			}
			Dialog.dismiss();
		}
	}

	/**
	 * A fragment showing the score
	 */
	public static class Score extends Fragment implements OnClickListener {

		TextView highscoreUserTv, locationTexTv, pointsTv;
		Button newGame, showHighscore;

		public Score() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_score,
					container, false);
			return rootView;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			highscoreUserTv = (TextView) getView().findViewById(
					R.id.textview_highscore_userloggedin);
			locationTexTv = (TextView) getView().findViewById(
					R.id.textview_location);
			pointsTv = (TextView) getView().findViewById(R.id.textview_points);
			newGame = (Button) getView().findViewById(R.id.new_game);
			showHighscore = (Button) getView().findViewById(R.id.highscores);
			newGame.setOnClickListener(this);
			showHighscore.setOnClickListener(this);
			highscoreUserTv.setText("Player : " + playerName);
			locationTexTv.setText("Location : " + locationString);
			pointsTv.setText("Points : " + points);
		}

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			// Invoked when user press the new game button
			case R.id.new_game: {
				Intent intent = new Intent(getActivity()
						.getApplicationContext(), CategoryActivity.class);
				intent.putExtra("USERNAME", playerName);
				startActivity(intent);
				getActivity().finish();
				break;
			}
			case R.id.highscores: {
				// Invoked when user press the highscore button
				Intent intent = new Intent(getActivity()
						.getApplicationContext(), HighScoreActivity.class);
				startActivity(intent);
				getActivity().finish();
				break;
			}
			}
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_quiz, container,
					false);
			return rootView;
		}
	}
}
