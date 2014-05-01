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
		
		/*
		// ---- GET HASH KEY FOR FACEBOOK ------
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo("com.hiof.quizphun",  PackageManager.GET_SIGNATURES);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		for (Signature signature : info.signatures)
		    {
		        MessageDigest md = null;
				try {
					md = MessageDigest.getInstance("SHA");
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
		        md.update(signature.toByteArray());
		        Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
		    }
		*/
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
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
        	System.out.println("Session - navigerer til category" + session.toString());
        	Intent i = new Intent(this, CategoryActivity.class);
    		startActivity(i);
        } else if (state.isClosed()) {
        	System.out.println("Ingen session - navigerer ikke");
			getSupportFragmentManager().beginTransaction()
			.replace(R.id.container, new PlaceholderFragment()).addToBackStack(null).commit();
        }
	}
	
	@Override
	protected void onResumeFragments() {
	    super.onResumeFragments();
	    Session session = Session.getActiveSession();

	    if (session != null && session.isOpened()) {
	    	System.out.println("Session resumed" + session.toString());
	    	Intent i = new Intent(this, CategoryActivity.class);
    		startActivity(i);
	    } else {
	    	System.out.println("Session not resumed" + session.toString());
			getSupportFragmentManager().beginTransaction()
			.replace(R.id.container, new PlaceholderFragment()).addToBackStack(null).commit();
	    }
	}
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = 
	    new Session.StatusCallback() {
	    @Override
	    public void call(Session session, 
	            SessionState state, Exception exception) {
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
	
	public void buttonLoginWithUsernameClicked(View v){
		//Show login with username fragment
		getSupportFragmentManager().beginTransaction()
		.replace(R.id.container, new PlayWithUsernameFragment()).addToBackStack(null).commit();
	}
	
	public void buttonUsernameSelectedClicked(View v){
		//Send user to CategoryActivity
		Intent i = new Intent(this, CategoryActivity.class);
		EditText usernameEditText = (EditText)findViewById(R.id.edittext_main_username);
		String username = usernameEditText.getText().toString().trim();
		if(username.length() == 0 || username.length() > 12) {
			Toast.makeText(getApplicationContext(), "Username can`t contains 0 or more than 12 letters", Toast.LENGTH_SHORT).show();
		}
		else {
			SqliteDatabaseHandler db = new SqliteDatabaseHandler(this);
			db.addUser(new User(username));
			startActivity(i);
		}
	}
	
	public void buttonUsernameSelectFromListClicked(View v){
		getSupportFragmentManager().beginTransaction()
		.replace(R.id.container, new ChooseUsernameFromListFragment()).addToBackStack(null).commit();
	}
	
	public void fuck2() {
		ListView userItems = (ListView) findViewById(R.id.listview_users);
		
		SqliteDatabaseHandler db = new SqliteDatabaseHandler(this);
		List<User> users = db.getAllUsers();
		// Create custom list adapter
		CustomUserAdapter adapter = new CustomUserAdapter(local, users);
		
		// Set list adapter for the ListView
		userItems.setAdapter(adapter);
		userItems.setEnabled(true);
		userItems.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				System.out.println("Clicked");
				/*System.out.println("Clicked "+users.get(position));
				System.out.println("Clicked "+parent.getChildAt(position));
				System.out.println("Clicked "+adapter.getItem(position));
				parent.getChildAt(position).setBackgroundColor(Color.GREEN);
				userItems.getChildAt(2).setBackgroundColor(Color.GREEN);*/
			}
		});
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
	
	public static class ChooseUsernameFromListFragment extends Fragment {

		public ChooseUsernameFromListFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_choose_username_from_list, container,
					false);
			return rootView;
		}
		@Override
		public void onResume() {
			super.onResume();
			fillUserList();
		}
		
		private void fillUserList() {
			final ListView userItems = (ListView) getView().findViewById(R.id.listview_users);
			
			SqliteDatabaseHandler db = new SqliteDatabaseHandler(getActivity());
			final List<User> users = db.getAllUsers();
			// Create custom list adapter
			final CustomUserAdapter adapter = new CustomUserAdapter(getActivity(), users);
			
			// Set list adapter for the ListView
			userItems.setAdapter(adapter);
			userItems.setEnabled(true);
			userItems.setOnItemClickListener(new OnItemClickListener() {
				
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					String userName = users.get(position).getUserName();
					Intent i = new Intent(getActivity(), CategoryActivity.class);
					i.putExtra("USERNAME", userName);
					startActivity(i);
				}
			});
		}
		
	}

}
