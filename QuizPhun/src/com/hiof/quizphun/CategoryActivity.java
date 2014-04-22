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
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.hiof.adapter.CustomCategoryAdapter;
import com.hiof.database.HandleQuery;
import com.hiof.objects.Category;

public class CategoryActivity extends ActionBarActivity {
	
	private List<Category> categories = new ArrayList<Category>();
	public CategoryActivity local;
	private int count = 0;
	private Session session = null;
	private static String userName;
	TextView userLoggedIn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		setTitle("Select category");
		local = this;
		try{
			session = Session.getActiveSession();
			System.out.println("Session i category" + session.toString());
			if(session != null && session.isOpened()){
				makeMeRequest(session);
			}
			else {
				userName = getIntent().getStringExtra("USERNAME");
			}
		}catch(NullPointerException e){
			System.out.println("Session i category , ingen facebook session");
		}
	}
	
	 @Override
	    protected void onResume() {
	        super.onResume();
	        new FillCategoryListView().execute();
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
			if (session != null) {
				try {
					session = Session.getActiveSession();
					session.closeAndClearTokenInformation();
				} catch (NullPointerException e) {
					System.out.println("performLogout(): User was logged in with email");
				}
			}
			Intent i = new Intent(this, MainActivity.class);
			startActivity(i);
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void makeMeRequest(final Session session) {
		// Make an API call to get user data and define a
		// new callback to handle the response.
		Request request = Request.newMeRequest(session,
				new Request.GraphUserCallback() {

					@Override
					public void onCompleted(GraphUser user, Response response) {
						// If the response is successful
						if (session == Session.getActiveSession()) {
							if (user != null) {
								System.out.println("Session" + user.getFirstName());
								userName = user.getFirstName();
								userLoggedIn = (TextView)findViewById(R.id.textview_userloggedin);
								userLoggedIn.setText("Player : " + userName);
							}
						}
					}
				});
		request.executeAsync();
	}
	
	private void startQuiz(int categoryid, String category) {
		Intent i = new Intent(this, QuizActivity.class);
		i.putExtra("USERNAME", userName);
		i.putExtra("CATEGORYID", categoryid);
		i.putExtra("CATEGORYNAME", category);
		startActivity(i);
	}

	
	private class FillCategoryListView extends AsyncTask<Void, Void, List<Category>> {
		private ProgressDialog Dialog = new ProgressDialog(local);
		private ListView categoryItems;
		
		@Override
	    protected void onPreExecute(){
			Dialog.setMessage("Loading categories..");
			Dialog.show();
	    }
		
		@Override
		protected List<Category> doInBackground(Void... params) {
			try {
				new HandleQuery();
				categories = HandleQuery.getAllCategories();
				return categories;
				
			} catch (NullPointerException e) {
				System.out.println("AsyncTask in CategoryActivity returns null while trying to get all categories from database");
				return null;
			}
		}

		@Override
		protected void onPostExecute(final List<Category> result) {
			if(result!=null){
				// Get ListView
				categoryItems = (ListView) findViewById(R.id.listview_category);
				
				// Create custom list adapter
				CustomCategoryAdapter adapter = new CustomCategoryAdapter(local, result);
				
				// Set list adapter for the ListView
				categoryItems.setAdapter(adapter);
				categoryItems.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						System.out.println("Clicked "+result.get(position));
						startQuiz(result.get(position).getCategoryid(), result.get(position).getCategoryname());
					}
				});
			}else{
				if(++count<=3){
					Toast.makeText(local, "Something went wrong, reloading ("+count+" of 3 attempts)...", Toast.LENGTH_SHORT).show();
					new CountDownTimer(2000, 1000) {
						@Override
						public void onTick(long millisUntilFinished) {
							// Do nothing
						}
						@Override
						public void onFinish() {
							new FillCategoryListView().execute();
						}
					  }.start();
				}else{
					//There is something wrong, send user to start
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

		TextView userLoggedIn;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_category,
					container, false);
			return rootView;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			userLoggedIn = (TextView) getView().findViewById(R.id.textview_userloggedin);
			userLoggedIn.setText("Player : " + userName);
		}		
	}
	
	

}
