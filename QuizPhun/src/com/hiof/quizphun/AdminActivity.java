package com.hiof.quizphun;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.hiof.database.HandleQuery;
import com.hiof.objects.Answer;
import com.hiof.objects.Category;
import com.hiof.objects.Question;

public class AdminActivity extends ActionBarActivity {

	private AdminActivity local;
	private Spinner categorySpinner;
	private ListView questionlist;
	private ArrayAdapter<Question> questionArrayAdapter;
	private int selectedQuestionId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new AdminLoginFragment())
					.addToBackStack("Login").commit();
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

	/*
	 * Overrides the backbutton. Handles the when the backbuttons are pressed in
	 * this activity
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
		int stackCount = fm.getBackStackEntryCount();
		System.out.println("Stack : " + stackCount);
		if (stackCount >= 2) {
			Fragment f = new AdminLoggedInFragment();
			fm.beginTransaction().replace(R.id.container, f)
					.addToBackStack("Logged in").commit();
		}
	}

	/*
	 * Invoked when loginbutton is clicked.
	 */
	public void buttonLogInClicked(View v) {
		EditText username = (EditText) findViewById(R.id.edittext_admin_username);
		EditText password = (EditText) findViewById(R.id.edittext_admin_password);
		String[] values = { username.getText().toString(),
				password.getText().toString() };
		new CheckLoginInfo().execute(values);
	}

	/*
	 * Invoked when new questions is clicked
	 */
	public void buttonNewQuestionClicked(View v) {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, new NewQuestionFragment())
				.addToBackStack("New Question").commit();
		new FillCategorySpinner().execute();
	}

	/*
	 * Invoked when delete question is clicked
	 */
	public void buttonDeleteQuestionClicked(View v) {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, new DeleteQuestionFragment())
				.addToBackStack("Delete Question").commit();
		new DeleteQuestionsListView().execute();
	}

	/*
	 * Invoked when save question is clicked
	 */
	public void buttonSaveQuestionClicked(View v) {
		Category category = (Category) ((Spinner) findViewById(R.id.spinner_admin_newquestion_category))
				.getSelectedItem();
		final int catid = category.getCategoryid();
		final String question = ((EditText) findViewById(R.id.edittext_admin_question))
				.getText().toString();
		String answer1 = ((EditText) findViewById(R.id.edittext_admin_answer1))
				.getText().toString();
		boolean ans1 = false;
		String answer2 = ((EditText) findViewById(R.id.edittext_admin_answer2))
				.getText().toString();
		boolean ans2 = false;
		String answer3 = ((EditText) findViewById(R.id.edittext_admin_answer3))
				.getText().toString();
		boolean ans3 = false;
		String answer4 = ((EditText) findViewById(R.id.edittext_admin_answer4))
				.getText().toString();
		boolean ans4 = false;

		RadioGroup rg = ((RadioGroup) findViewById(R.id.radiogroup_admin_correct_answer));
		int index = rg.indexOfChild(findViewById(rg.getCheckedRadioButtonId()));

		switch (index) {
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

		// Add answers to a list
		final List<Answer> answers = new ArrayList<Answer>(4);
		answers.add(new Answer(0, 0, answer1, ans1));
		answers.add(new Answer(0, 0, answer2, ans2));
		answers.add(new Answer(0, 0, answer3, ans3));
		answers.add(new Answer(0, 0, answer4, ans4));

		// Defines a thread to insert questions to the database
		Thread insertQuestionToDatabaseThread = new Thread(new Runnable() {
			@Override
			public void run() {
				HandleQuery.insertQuestionAndAnswer(question, catid, answers);
			}
		});
		// Validates the length of the question and answers
		if (question.length() > 5 && answer1.length() > 1
				&& answer2.length() > 1 && answer3.length() > 1
				&& answer4.length() > 1) {

			insertQuestionToDatabaseThread.start();
			Toast.makeText(local, "Done", Toast.LENGTH_SHORT).show();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, new AdminLoggedInFragment())
					.addToBackStack("Logged in").commit();
		} else {
			// Makes a toast-message if validation fails
			Toast.makeText(local, "Please fill in all fields",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void deleteQuestionsListViewLoaded(final List<Question> result) {
		final EditText inputSearch = (EditText) findViewById(R.id.edittext_admin_search);

		inputSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				questionArrayAdapter.getFilter().filter(s);
			}

			@Override
			public void afterTextChanged(Editable s) {
			}

		});
		questionlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(final AdapterView<?> parent, View view,
					final int position, final long id) {
				selectedQuestionId = ((Question) parent
						.getItemAtPosition(position)).getQuestionid();
				// Makes an alertdialog
				new AlertDialog.Builder(local)
						.setTitle("Delete question")
						.setMessage(
								"Are you sure you want to delete this question?")
						// If user wanna delete question
						.setPositiveButton(android.R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// Continue with delete
										questionArrayAdapter
												.remove(questionArrayAdapter
														.getItem(position));
										questionArrayAdapter.getFilter()
												.filter("");
										inputSearch.setText("");
										questionArrayAdapter
												.notifyDataSetChanged();
										Thread deleteQuestionFromDatabaseThread = new Thread(
												new Runnable() {
													@Override
													public void run() {
														HandleQuery
																.deleteQuestion(selectedQuestionId);
													}
												});
										deleteQuestionFromDatabaseThread
												.start();
									}
								})
						// If user wanna cancel
						.setNegativeButton(android.R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// Do nothing
									}
								}).setIcon(android.R.drawable.ic_dialog_alert)
						.show();
			}
		});
	}

	/*
	 * Asynctask - Validating login information.
	 */
	private class CheckLoginInfo extends AsyncTask<String[], Void, Boolean> {

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
			if (HandleQuery.validateUser(username, password)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// If validation is ok, show AdminLoggedInFragment
			if (result == true) {
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.container, new AdminLoggedInFragment())
						.addToBackStack("Logged in").commit();
			}
			// else show a toast-message
			else {
				Toast.makeText(local, "Incorrect login", Toast.LENGTH_SHORT)
						.show();
			}
			Dialog.dismiss();
		}
	}

	/*
	 * Asynctask - Adds items to the spinner with categories from the database
	 */
	public class FillCategorySpinner extends
			AsyncTask<Void, Void, List<Category>> {

		@Override
		protected List<Category> doInBackground(Void... params) {
			// Gets all categories and add them to a list
			List<Category> categories;
			try {
				categories = HandleQuery.getAllCategories();
				return categories;
			} catch (NullPointerException e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<Category> result) {
			// If we got categories
			if (result != null) {
				categorySpinner = (Spinner) findViewById(R.id.spinner_admin_newquestion_category);
				ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(
						AdminActivity.this,
						android.R.layout.simple_spinner_item, result);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				categorySpinner.setAdapter(adapter);
			}
			// Else write a toast-message
			else {
				Toast.makeText(AdminActivity.this,
						"Something went wrong loading data", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	/*
	 * Asynctask - Deletes a question from the list view
	 */
	public class DeleteQuestionsListView extends
			AsyncTask<Void, Void, List<Question>> {
		private ProgressDialog Dialog = new ProgressDialog(local);

		@Override
		protected void onPreExecute() {
			Dialog.setMessage("Getting questions..");
			Dialog.show();
		}

		@Override
		protected List<Question> doInBackground(Void... params) {
			// Gets all questions from the database
			List<Question> questions;
			try {
				questions = HandleQuery.getAllQuestions();
				return questions;
			} catch (NullPointerException e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(final List<Question> result) {
			// If we got any questions, we add them to the listview
			if (result != null) {
				// Get ListView
				questionlist = (ListView) findViewById(R.id.listview_admin_delete_question);

				// Create custom list adapter
				questionArrayAdapter = new ArrayAdapter<Question>(local,
						android.R.layout.simple_list_item_1, result);

				// Set list adapter for the ListView
				questionlist.setAdapter(questionArrayAdapter);

				deleteQuestionsListViewLoaded(result);
				// Else we write a toast-message
			} else {
				Toast.makeText(AdminActivity.this,
						"Something went wrong loading data", Toast.LENGTH_SHORT)
						.show();
			}
			Dialog.hide();
		}
	}

	/**
	 * A fragment for adding new questions to database
	 */
	public static class NewQuestionFragment extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_admin_newquestion, container, false);
			return rootView;
		}
	}

	/**
	 * A fragment for deleting questions from database
	 */
	public static class DeleteQuestionFragment extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_admin_delete_question, container, false);
			return rootView;
		}
	}

	/**
	 * A fragment containing a menu for admins who are logged in
	 */
	public static class AdminLoggedInFragment extends Fragment {
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
