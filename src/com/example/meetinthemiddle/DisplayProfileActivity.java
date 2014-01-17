package com.example.meetinthemiddle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.example.meetinthemiddle.personenverwaltung.dao.PersonDao;
import com.example.meetinthemiddle.personenverwaltung.domain.Person;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class DisplayProfileActivity extends Activity {

	PersonDao personDao;
	List<Person> persons;
	Person person;
	EditText personFirstNameView;
	EditText personLastNameView;
	EditText personAgeView;
	EditText personEmailView;
	EditText personPhoneView;
	EditText personInterestsView;
	
	public ShowProfileTask showProfileTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_profile);
		// Show the Up button in the action bar.
//		setupActionBar();
		
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
		System.out.println(person);
		showPersonViews();
		
	}

	private void showPersonViews() {
		personFirstNameView = (EditText) findViewById(R.id.profile_first_name);
		personFirstNameView.setEnabled(false);
		personFirstNameView.setText(person.getFirstName());
		personLastNameView = (EditText) findViewById(R.id.profile_last_name);
		personLastNameView.setEnabled(false);
		personLastNameView.setText(person.getLastName());
		personAgeView = (EditText) findViewById(R.id.profile_age);
		personAgeView.setEnabled(false);
		Date age = new Date();
		long millis = age.getTime()-person.getBirthday().getTime();
		long year = (long) (millis/31536000000L);
		personAgeView.setText(String.valueOf(year));
	    personEmailView = (EditText) findViewById(R.id.profile_mail);
		personEmailView.setEnabled(false);
		personEmailView.setText(person.getEmail());
		personPhoneView = (EditText) findViewById(R.id.profile_phone);
		personPhoneView.setEnabled(false);
		personPhoneView.setText(person.getPhone());
		personInterestsView = (EditText) findViewById(R.id.profile_interests);
		personInterestsView.setEnabled(false);
		personInterestsView.setText(person.getInterests());
	}

//	/**
//	 * Set up the {@link android.app.ActionBar}, if the API is available.
//	 */
//	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
//	private void setupActionBar() {
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//			getActionBar().setDisplayHomeAsUpEnabled(true);
//		}
//	}

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
	
	public void changeProfile(View view){
		Button btn = (Button) findViewById(R.id.profile_change);
		if(!btn.getText().equals("Speichern")){
		personFirstNameView = (EditText) findViewById(R.id.profile_first_name);
		personFirstNameView.setEnabled(true);
		personLastNameView.setEnabled(true);
		personEmailView.setEnabled(true);
		personPhoneView.setEnabled(true);
		personInterestsView.setEnabled(true);
		btn.setText("Speichern");
		return;
		}
		PersonUpdateTask personUpdateTask = new PersonUpdateTask();
		String firstname = (personFirstNameView.getText().toString());
		String lastname = (personLastNameView.getText().toString());
		String email = (personEmailView.getText().toString());
		String phone = (personPhoneView.getText().toString());
		String interests = (personInterestsView.getText().toString());
		personUpdateTask.execute(firstname,lastname,email,phone,interests);
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			Long id = extras.getLong("PersonId");
			Intent intent = new Intent(this,MainActivity.class);
			intent.putExtra("PersonId", id);
			startActivity(intent);
		}
	}
	
	private class ShowProfileTask extends AsyncTask<Void, Void, Person>{

		@Override
		protected Person doInBackground(Void... arg0) {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				if(extras.getLong("PersonId")!= -0L){
				Long id = extras.getLong("PersonId");
				Person person = new Person();
				return person = personDao.findPersonById(id);
				}
				if(extras.getLong("ContactId")!=0L){
					Long id = extras.getLong("ContactId");
					Person person = new Person();
					return person = personDao.findPersonById(id);
				}
			}
			return null;
		}
		
	}
	private class PersonUpdateTask extends AsyncTask<String, Void, Void>{

		@Override
		protected Void doInBackground(String... params) {
			personDao.update(person.getId(), params[0], params[1], person.getBirthday(), params[2], params[3],person.getPassword(), params[4]);
			return null;
		}

		
	}

}
