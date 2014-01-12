package com.example.meetinthemiddle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.example.meetinthemiddle.personenverwaltung.dao.PersonDao;
import com.example.meetinthemiddle.personenverwaltung.domain.Person;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;

public class DisplayProfileActivity extends Activity {

	PersonDao personDao;
	List<Person> persons;
	Person person;
	TextView profileView;
	
	public ShowProfileTask showProfileTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_profile);
		// Show the Up button in the action bar.
		setupActionBar();
		
		persons = new ArrayList<Person>();
		person = new Person();
		personDao = new PersonDao(this);
		showProfileTask = new ShowProfileTask();
		try {
			person = showProfileTask.execute((Void) null).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		showPersonViews();
		
	}

	private void showPersonViews() {
//		TextView personFirstNameView = (TextView) profileView.findViewById(R.id.profile_firstName)
//		personFirstNameView.setText(person.getFirstName());
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class ShowProfileTask extends AsyncTask<Void, Void, Person>{

		@Override
		protected Person doInBackground(Void... arg0) {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				Long id = extras.getLong("ContactId");
				Person person = new Person();
				return person = personDao.findPersonById(id);
			}
			return null;
		}
		
	}

}
