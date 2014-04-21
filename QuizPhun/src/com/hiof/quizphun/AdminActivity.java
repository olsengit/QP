package com.hiof.quizphun;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.hiof.database.HandleQuery;
 
public class AdminActivity extends ActionBarActivity {

	private AdminActivity local;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		} 
		local = this;
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
	
	public void buttonLogInClicked(View v){
		EditText username = (EditText)findViewById(R.id.edittext_admin_username);
		EditText password = (EditText)findViewById(R.id.edittext_admin_password);
		String[] values = {username.getText().toString(), password.getText().toString()};
		new checkLoginInfo().execute(values);
	}

	private class checkLoginInfo extends AsyncTask<String[], Void, Boolean>{

		private ProgressDialog Dialog = new ProgressDialog(local);

		@Override
		protected void onPreExecute() {
			Dialog.setMessage("Validating login..");
			Dialog.show();
		}
		
		@Override
		protected Boolean doInBackground(String[]... params) {
			String[] values = params[0];
			String username = values[0];
			String password = values[1];
			if(HandleQuery.validateUser(username, password)){
				return true;
			}else{
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if(result == true){
				getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, new AdminLoggedIn()).commit();
			}else{
				Toast.makeText(local, "Incorrect login", Toast.LENGTH_SHORT).show();
			}
			Dialog.dismiss();
		}
		
	}
	
	/**
	 * A fragment containing a menu for admins who are logged in
	 */
	public static class AdminLoggedIn extends Fragment{
		public AdminLoggedIn(){
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_admin_loggedin,
					container, false);
			return rootView;
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
			View rootView = inflater.inflate(R.layout.fragment_admin,
					container, false);
			return rootView;
		}
	}

}
