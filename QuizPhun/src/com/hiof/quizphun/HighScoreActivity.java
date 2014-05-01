package com.hiof.quizphun;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.hiof.adapter.CustomHighscoreAdapter;
import com.hiof.database.HandleQuery;
import com.hiof.objects.Highscore;

public class HighScoreActivity extends ActionBarActivity {

	public HighScoreActivity local;
	private List<Highscore> highscores = new ArrayList<Highscore>();
	private int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_high_score);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
			local = this;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.general, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Fill listview when app is resumed
		new fillHighscoreListView().execute();
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

	/*
	 * Asynctask - Add highscores to the listview
	 */
	private class fillHighscoreListView extends
			AsyncTask<Void, Void, List<Highscore>> {
		private ProgressDialog Dialog = new ProgressDialog(local);
		private ListView highscoreItems;

		@Override
		protected void onPreExecute() {
			Dialog.setMessage("Loading highscores..");
			Dialog.show();
		}

		@Override
		protected List<Highscore> doInBackground(Void... params) {
			try {
				// Gets the 10 highest scores from the databse
				new HandleQuery();
				highscores = HandleQuery.getHighscore();
				return highscores;

			} catch (NullPointerException e) {
				System.out
						.println("AsyncTask in HighscoreActivity returns null while trying to get all highscores from database");
				return null;
			}
		}

		@Override
		protected void onPostExecute(final List<Highscore> result) {
			// If we got any data from the database
			if (result != null) {
				// Get ListView
				highscoreItems = (ListView) findViewById(R.id.listview_highscores);

				// Create custom list adapter
				CustomHighscoreAdapter adapter = new CustomHighscoreAdapter(
						local, result);

				// Set list adapter for the ListView
				highscoreItems.setAdapter(adapter);
				highscoreItems
						.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO: FIX
								System.out.println("Clicked "
										+ result.get(position));
							}
						});
			}
			// If we didn't get anything from the database we write a
			// toast-message try again every second for three seconds
			else {
				if (++count <= 3) {
					Toast.makeText(
							local,
							"Something went wrong, reloading (" + count
									+ " of 3 attempts)...", Toast.LENGTH_SHORT)
							.show();
					new CountDownTimer(2000, 1000) {
						@Override
						public void onTick(long millisUntilFinished) {
							// Do nothing
						}

						@Override
						public void onFinish() {
							new fillHighscoreListView().execute();
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
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View rootView = inflater.inflate(R.layout.fragment_high_score,
					container, false);
			return rootView;
		}

	}

}
