package com.hiof.quizphun;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent i = new Intent(this, AdminActivity.class);
			startActivity(i);
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		// Do nothing (to prevent user to accidentally quit QuizPhun)
	}
	
	public void facebookLoginClicked(View v) {
		//TODO: Implement facebook login
		Toast.makeText(this, "Facebook login", Toast.LENGTH_SHORT).show();
	}
	
	public void buttonLoginWithUsernameClicked(View v){
		//Show login with username fragment
		getSupportFragmentManager().beginTransaction()
		.replace(R.id.container, new PlayWithUsernameFragment()).addToBackStack(null).commit();
	}
	
	public void buttonUsernameSelectedClicked(View v){
		//Send user to CategoryActivity
		Intent i = new Intent(this, CategoryActivity.class);
		String username = ((EditText)findViewById(R.id.edittext_main_username)).toString();
		i.putExtra("USERNAME", username);
		startActivity(i);
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
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	/**
	 * Fragment containing view to select a username
	 */
	public static class PlayWithUsernameFragment extends Fragment {

		public PlayWithUsernameFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_username, container,
					false);
			return rootView;
		}
	}

}
