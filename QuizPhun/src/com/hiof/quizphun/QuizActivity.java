package com.hiof.quizphun;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hiof.adapter.CustomAnswerAdapter;
import com.hiof.database.HandleQuery;
import com.hiof.objects.Answer;
import com.hiof.objects.Question;

public class QuizActivity extends ActionBarActivity {

	private QuizActivity local;
	private List<Question> questions = new ArrayList<Question>(10);
	private List<Answer> answers = new ArrayList<Answer>(40);
	private int count = 0;
	private static int points = 0;
	private static String playerName, location;
	private String categoryname;
	private int timeleft;
	private int count_question = 0;
	Button nextQuestion;
	private CountDownTimer cdt;

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
		System.out.println("Point reset");
		System.out.println("LifeCycle onCreate");
		points = 0;
	}

	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("Point reset");
		System.out.println("LifeCycle onResume");
		points = 0;
		new prepareTenQuestions().execute();
	}

	private void startQuiz() {
		if (++count_question <= 3) {
			setTitle(categoryname + " (" + count_question + " of 10)");
			nextQuestion();
		}else{
			System.out.println("Done");
			playerName = getIntent().getStringExtra("USERNAME");
			location = "loc";
			getSupportFragmentManager().beginTransaction().replace(R.id.container, new Score()).commit();
			Thread insertHighscoreThread = new Thread(new Runnable() {
	            @Override
	            public void run() {
	            	HandleQuery.insertHighscore(playerName, points, location);
	            }
	        });
			insertHighscoreThread.start();
		}
	}
	
	public void nextQuestionClicked(View v){
		startQuiz();
		nextQuestion = (Button)findViewById(R.id.button_quiz_nextquestion);
		nextQuestion.setVisibility(View.INVISIBLE);
	}

	private void nextQuestion() {
		final ProgressBar pb = (ProgressBar) findViewById(R.id.progressbar_quiz_timeleft);
		final TextView tv_timeleft = (TextView) findViewById(R.id.textview_quiz_timeleft);
		final GridView gridanswers = (GridView) findViewById(R.id.gridview_quiz_answer);

		final List<Answer> answerToThisQuestion = new ArrayList<Answer>(4);
		answerToThisQuestion.add(answers.get((count_question*4)-4));
		answerToThisQuestion.add(answers.get((count_question*4)-3));
		answerToThisQuestion.add(answers.get((count_question*4)-2));
		answerToThisQuestion.add(answers.get((count_question*4)-1));
		
		pb.setMax(10);
		cdt = new CountDownTimer(10000, 1000) {

			public void onTick(long millisUntilFinished) {
				timeleft = (int) (millisUntilFinished / 1000);
				pb.setProgress(timeleft);
				tv_timeleft.setText(timeleft + "s");
			}

			public void onFinish() {
				int answerId = 0;
				for(int i = 0; i < 4; i++) {
					if(answerToThisQuestion.get(i).isAnwser())
						answerId = i;
				}
				gridanswers.getChildAt(answerId).setBackgroundColor(Color.GREEN);
				nextQuestion = (Button)findViewById(R.id.button_quiz_nextquestion);
				nextQuestion.setVisibility(View.VISIBLE);
				gridanswers.setEnabled(false);
			}
		};
		cdt.start();

		TextView question = (TextView) findViewById(R.id.textview_quiz_question);
		question.setText(questions.get((count_question-1)).getQuestion());
		gridanswers.setAdapter(new CustomAnswerAdapter(this, answerToThisQuestion));
		gridanswers.setEnabled(true);
		gridanswers.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				cdt.cancel();
				//System.out.println("Itemclicked : Parent " + parent + ". View + " + v + ". Pos : " + position + ". id " + id + "griview : " + gridanswers.getChildAt(position));
				if(answerToThisQuestion.get(position).isAnwser()){
					//System.out.println("CORRECT ANSWER");
					points += 50 + timeleft;
					try {
					    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
					    r.play();
					} catch (Exception e) {
					    e.printStackTrace();
					}
					parent.getChildAt(position).setBackgroundColor(Color.GREEN);
				}else{
					//System.out.println("WRONG ANSWER");
					points -= 15;
					getApplicationContext();
					Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
					 // Vibrate for 500 milliseconds
					vibrator.vibrate(500);
					parent.getChildAt(position).setBackgroundColor( Color.RED);
				}
				
				nextQuestion = (Button)findViewById(R.id.button_quiz_nextquestion);
				nextQuestion.setVisibility(View.VISIBLE);
				gridanswers.setEnabled(false);
			}
		});
	}
	
	@Override
	public void onStop() {
		super.onStop();
		System.out.println("LifeCycle onStop");
		Intent reminderServiceIntent = new Intent(this, ReminderService.class);
		startService(reminderServiceIntent);
		System.out.println("Service startes");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("LifeCycle onDestroy");
	}
	
	public void onRestart() {
		super.onRestart();
		System.out.println("LifeCycle onRestart");
	}
	
	@Override
	public void onStart() {
		super.onStart();
		System.out.println("LifeCycle onStart");
	}

	@Override
	public void onBackPressed() {
		// Do nothing if back pressed during game
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
			cdt.cancel();
			Intent i = new Intent(this, MainActivity.class);
			startActivity(i);
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

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
				int category = getIntent().getIntExtra("CATEGORYID", 1);
				new HandleQuery();
				questions = HandleQuery.getTenRandomQuestions(category);
				try {
					for (int i = 0; i < questions.size(); i++) {
						int questionid = questions.get(i).getQuestionid();
						answers.addAll(HandleQuery.getAnswers(questionid));
					}
				} catch (NullPointerException e) {
					System.out.println("How are we going to play without answers to the questions??");
					return false;
				}
			} catch (NullPointerException e) {
				System.out.println("AsyncTask in CategoryActivity returns null while trying to get all categories from database");
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				startQuiz();
			} else {
				if (++count <= 3) {
					Toast.makeText(local,"Something went wrong, reloading (" + count + " of 3 attempts)...", Toast.LENGTH_SHORT).show();
					new CountDownTimer(2000, 1000) {
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
	 * A fragment containing a menu for admins who are logged in
	 */
	public static class Score extends Fragment implements OnClickListener{
		
		TextView highscoreUserTv, locationTexTv, pointsTv;
		Button newGame, showHighscore;
		
		public Score() {}
			
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
			highscoreUserTv = (TextView) getView().findViewById(R.id.textview_highscore_userloggedin);
			locationTexTv = (TextView) getView().findViewById(R.id.textview_location);
			pointsTv = (TextView) getView().findViewById(R.id.textview_points);
			newGame = (Button) getView().findViewById(R.id.new_game);
			showHighscore = (Button) getView().findViewById(R.id.highscores);
			newGame.setOnClickListener(this);
			showHighscore.setOnClickListener(this);
			highscoreUserTv.setText("Player : " + playerName);
			locationTexTv.setText("Location :" + location);
			pointsTv.setText("Points :" + points);
		}
		
		@Override
		public void onClick(View view) {
			switch(view.getId()) {
				case R.id.new_game: {
					Intent intent = new Intent(getActivity().getApplicationContext(), CategoryActivity.class);
					intent.putExtra("USERNAME", playerName);
					startActivity(intent);
					getActivity().finish();
					break;
				}
				case R.id.highscores: {
					Intent intent = new Intent(getActivity().getApplicationContext(), HighScoreActivity.class);
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
