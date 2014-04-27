package com.hiof.quizphun;

import java.util.ArrayList;
import java.util.List;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemSelectedListener;

import com.hiof.database.HandleQuery;
import com.hiof.objects.Answer;
import com.hiof.objects.Category;
 
public class AdminActivity extends ActionBarActivity {

	private AdminActivity local;
	private static Spinner categorySpinner;
	private static int categoryid;
	private int correctAns = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new AdminLoginFragment()).commit();
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
		new CheckLoginInfo().execute(values);
	}
	
	public void newQuestionClicked(View v){
		getSupportFragmentManager().beginTransaction()
		.replace(R.id.container, new NewQuestionFragment()).addToBackStack(null).commit();
		new FillCategorySpinner().execute();
	}

	
	public void buttonSaveQuestionClicked(View v){
		Category category = (Category)((Spinner)findViewById(R.id.spinner_admin_newquestion_category)).getSelectedItem();
		final int catid = category.getCategoryid();
		final String question = ((EditText)findViewById(R.id.edittext_admin_question)).getText().toString();
		String answer1 = ((EditText)findViewById(R.id.edittext_admin_answer1)).getText().toString();
		boolean ans1 = false;
		String answer2 = ((EditText)findViewById(R.id.edittext_admin_answer2)).getText().toString();
		boolean ans2 = false;
		String answer3 = ((EditText)findViewById(R.id.edittext_admin_answer3)).getText().toString();
		boolean ans3 = false;
		String answer4 = ((EditText)findViewById(R.id.edittext_admin_answer4)).getText().toString();
		boolean ans4 = false;
		
		RadioGroup rg = ((RadioGroup)findViewById(R.id.radiogroup_admin_correct_answer));
		
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				correctAns = checkedId;
			}
		});
		switch(correctAns){
			case 0:
				ans1 = true;
				break;
			case 1:
				ans2 = true;
				break;
			case 2:
				ans3 = true;
				break;
			case 3:
				ans4 = true;
				break;
			default:
				ans1 = true;
				break;
		}
		
		final List<Answer> answers = new ArrayList<Answer>(4);
		answers.add(new Answer(0, 0, answer1, ans1));
		answers.add(new Answer(0, 0, answer2, ans2));
		answers.add(new Answer(0, 0, answer3, ans3));
		answers.add(new Answer(0, 0, answer4, ans4));
		
		Thread insertQuestionToDatabaseThread = new Thread(new Runnable() {
			@Override
			public void run() {
				HandleQuery.insertQuestionAndAnswer(question, catid, answers);
			}
		});
		insertQuestionToDatabaseThread.start();
		
		getSupportFragmentManager().beginTransaction().replace(R.id.container, new AdminLoggedInFragment()).commit();
	}

	private class CheckLoginInfo extends AsyncTask<String[], Void, Boolean>{

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
				.replace(R.id.container, new AdminLoggedInFragment()).addToBackStack(null).commit();
			}else{
				Toast.makeText(local, "Incorrect login", Toast.LENGTH_SHORT).show();
			}
			Dialog.dismiss();
		}
	}
	
	public class FillCategorySpinner extends AsyncTask<Void, Void, List<Category>>{

		@Override
		protected List<Category> doInBackground(Void... params) {
			List<Category> categories;
			try {
				categories = HandleQuery.getAllCategories();
				return categories;
			} catch (NullPointerException e) {
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(List<Category> result){
			if(result!=null){
				categorySpinner = (Spinner)findViewById(R.id.spinner_admin_newquestion_category);
				ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(AdminActivity.this, android.R.layout.simple_spinner_item, result);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				categorySpinner.setAdapter(adapter);
			}else{
				Toast.makeText(AdminActivity.this, "Something went wrong loading data", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	/**
	 * A fragment for adding new questions to database
	 */
	public static class NewQuestionFragment extends Fragment{

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_admin_newquestion,
					container, false);
			return rootView;
		}
	}
	
	
	/**
	 * A fragment containing a menu for admins who are logged in
	 */
	public static class AdminLoggedInFragment extends Fragment{
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
	public static class AdminLoginFragment extends Fragment {

		public AdminLoginFragment() {
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
