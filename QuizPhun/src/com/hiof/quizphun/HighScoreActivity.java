package com.hiof.quizphun;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hiof.objects.Highscore;

public class HighScoreActivity extends ActionBarActivity {
	
	private static Highscore h;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_high_score);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
			h = (Highscore) getIntent().getSerializableExtra("HIGHSCORE");
			System.out.println("Highscore : "  + h.getDate() + " " + h.getLocation() + " " + h.getPlayername() + " " + h.getPoints());
		}
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
			Intent i = new Intent(this, MainActivity.class);
			startActivity(i);
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements OnClickListener{
		
		TextView highscoreUserTv, dateTv, locationTexTv, pointsTv;
		Button newGame, showHighscore;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			View rootView = inflater.inflate(R.layout.fragment_high_score,
					container, false);
			return rootView;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			//TODO: Check if player achieved highscore, write a toast message.
			String playername = h.getPlayername();
			String date = h.getDate();
			String location = h.getLocation();
			int points = h.getPoints();
			highscoreUserTv = (TextView) getView().findViewById(R.id.textview_highscore_userloggedin);
			dateTv = (TextView) getView().findViewById(R.id.textview_date);
			locationTexTv = (TextView) getView().findViewById(R.id.textview_location);
			pointsTv = (TextView) getView().findViewById(R.id.textview_points);
			newGame = (Button) getView().findViewById(R.id.new_game);
			showHighscore = (Button) getView().findViewById(R.id.highscores);
			newGame.setOnClickListener(this);
			showHighscore.setOnClickListener(this);
			highscoreUserTv.setText("Player : " + playername);
			dateTv.setText("Date : " + date);
			locationTexTv.setText("Location :" + location);
			pointsTv.setText("Points :" + points);
			
		}

		@Override
		public void onClick(View view) {
			//TODO: Finish this shit
			switch(view.getId()) {
				case R.id.new_game: {
					//TODO: Navigate to category, remember to send username by the intent
					break;
				}
				case R.id.highscores: {
					//TODO: Navigate to highscores
					break;
				}
			}
			

		}
		
		
	}

}
