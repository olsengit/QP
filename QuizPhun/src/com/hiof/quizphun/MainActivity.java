package com.hiof.quizphun;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.hiof.adapter.CustomUserAdapter;
import com.hiof.database.SqliteDatabaseHandler;
import com.hiof.objects.User;

public class MainActivity extends ActionBarActivity {

	public static MainActivity local;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

		local = this;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		// If a sessionstate from facebook is opened, we send the user to the
		// categoryactivity
		if (state.isOpened()) {
			Intent i = new Intent(this, CategoryActivity.class);
			startActivity(i);
		}
		// If there is no state we add the fragment
		else if (state.isClosed()) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, new PlaceholderFragment())
					.addToBackStack(null).commit();
		}
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		// Get the session when app is resumed
		Session session = Session.getActiveSession();
		// If the session is opened, navigate to the categoryactivity
		if (session != null && session.isOpened()) {
			System.out.println("Session resumed" + session.toString());
			Intent i = new Intent(this, CategoryActivity.class);
			startActivity(i);
		}
		// Else add the fragment
		else {
			System.out.println("Session not resumed" + session.toString());
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, new PlaceholderFragment())
					.addToBackStack(null).commit();
		}
	}

	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

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

	/*
	 * Invoked when the login with username button is clicked
	 */
	public void buttonLoginWithUsernameClicked(View v) {
		// Show login with username fragment
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, new PlayWithUsernameFragment())
				.addToBackStack(null).commit();
	}

	/*
	 * Invoked when the user start the quiz.
	 */
	public void buttonUsernameSelectedClicked(View v) {
		// Validates the length of the username
		Intent i = new Intent(this, CategoryActivity.class);
		EditText usernameEditText = (EditText) findViewById(R.id.edittext_main_username);
		String username = usernameEditText.getText().toString().trim();
		if (username.length() == 0 || username.length() > 12) {
			Toast.makeText(getApplicationContext(),
					"Username can`t contains 0 or more than 12 letters",
					Toast.LENGTH_SHORT).show();
		}
		// If the username is ok, add the user to the SQLite database and send
		// user to the categoryactivity
		else {
			SqliteDatabaseHandler db = new SqliteDatabaseHandler(this);
			db.addUser(new User(username));
			startActivity(i);
		}
	}

	/*
	 * Invoked when user wanna select a username from the list
	 */
	public void buttonUsernameSelectFromListClicked(View v) {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, new ChooseUsernameFromListFragment())
				.addToBackStack(null).commit();
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
			View rootView = inflater.inflate(R.layout.fragment_main_username,
					container, false);
			return rootView;
		}
	}

	/**
	 * Fragment containing view to choose username from a list
	 */
	public static class ChooseUsernameFromListFragment extends Fragment {

		public ChooseUsernameFromListFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_main_choose_username_from_list,
					container, false);
			return rootView;
		}

		@Override
		public void onResume() {
			super.onResume();
			// Fills the list when the fragment is resumed
			fillUserList();
		}

		/*
		 * Fills the usernames from SQLite to the listview
		 */
		private void fillUserList() {
			final ListView userItems = (ListView) getView().findViewById(
					R.id.listview_users);

			SqliteDatabaseHandler db = new SqliteDatabaseHandler(getActivity());
			final List<User> users = db.getAllUsers();
			// Create custom list adapter
			final CustomUserAdapter adapter = new CustomUserAdapter(
					getActivity(), users);

			// Set list adapter for the ListView
			userItems.setAdapter(adapter);
			userItems.setEnabled(true);
			userItems.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// Gets the position of the item clicked in the listview,
					// and sends the user to the categoryactivity
					String userName = users.get(position).getUserName();
					Intent i = new Intent(getActivity(), CategoryActivity.class);
					i.putExtra("USERNAME", userName);
					startActivity(i);
				}
			});
		}

	}

}
