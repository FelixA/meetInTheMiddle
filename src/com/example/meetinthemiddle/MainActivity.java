package com.example.meetinthemiddle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import com.example.meetinthemiddle.DisplayRegistrationActivity.UserRegistrationTask;
import com.example.meetinthemiddle.personenverwaltung.dao.PersonDao;
import com.example.meetinthemiddle.personenverwaltung.domain.Person;
import com.example.meetinthemiddle.personenverwaltung.domain.PersonList;
import com.example.meetinthemiddle.util.ConvertToMD5;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	@SuppressWarnings("unused")
	private static final String LOG_TAG = MainActivity.class.getSimpleName();

	PersonDao personDao;
	private List<Person> persons;

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private ShowContactsTask showContactsTask = null;

	public final static String EXTRA_MESSAGE = "com.example.meetinthemiddle.EXTRA_MESSAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		persons = new ArrayList<Person>();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		personDao = new PersonDao(this);
		showContactsTask = new ShowContactsTask();
		try {
			persons = showContactsTask.execute((Void) null).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		populatePersonList();
		populateListView();
	}

//	private void populatePersonList() {
//		persons.add(new Person("Felix","Albert",new Date(),"12345","fwfew@cdew","dewdew","interessen"));	
//	}

	private void populateListView() {
		ArrayAdapter<Person> adapter = new MyListAdapter();
		ListView list = (ListView) findViewById(R.id.pastMeetingsView);
		list.setAdapter(adapter);
	}

	private class MyListAdapter extends ArrayAdapter<Person> {
		public MyListAdapter() {
			super(MainActivity.this, R.layout.contacts_view, persons);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			 View contacts_view = convertView;
			 //Make sure a view is present
			 if(contacts_view == null){
				 contacts_view = getLayoutInflater().inflate(R.layout.contacts_view, parent, false);
			 }
			
			Person currentPerson = persons.get(position);
			
			//TODO: Bilder vom User
//			ImageView imageView = (ImageView) contacts_view.findViewById(R.id.contacts_personImage);
//			imageView.setImageResource(currentPerson.getIconID());
			
			TextView personNameView = (TextView) contacts_view.findViewById(R.id.contacts_personName);
			personNameView.setText(currentPerson.getFirstName());
			
			TextView interestsView = (TextView) contacts_view.findViewById(R.id.contacts_personInteressen);
			interestsView.setText(currentPerson.getInterests());
			return contacts_view;
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// Zeigt die Kontakte des jeweiligen Users.
	public void displayContacts(View view) {
		Intent intent = new Intent(this, DisplayContactsActivity.class);
		startActivity(intent);
	}

	// Zeigt an den User geschickte Nachrichten.
	public void displayMessages(View view) {
		Intent intent = new Intent(this, DisplayMessagesActivity.class);
		startActivity(intent);
	}

	// Zeigt den Bildschirm zur auswahl ob man ein meeting erstellen will, oder
	// sich die historie einsehen.
	public void displayMeetings(View view) {
		Intent intent = new Intent(this, DisplayMeetingsActivity.class);
		startActivity(intent);
	}

	// Zeigt das Profil des Users.
	public void displayProfile(View view) {
		Intent intent = new Intent(this, DisplayProfileActivity.class);
		startActivity(intent);
	}

	// Zeigt die FAQ.
	public void displayQuestions(View view) {
		Intent intent = new Intent(this, DisplayQuestionsActivity.class);
		startActivity(intent);
	}

	// Zeigt die möglichen Einstellungen.
	public void displaySettings(View view) {
		Intent intent = new Intent(this, DisplaySettingsActivity.class);
		startActivity(intent);
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class ShowContactsTask extends AsyncTask<Void, Void, List<Person>> {
		@Override
		protected List<Person> doInBackground(Void... params) {
			try {
				Long personId = null;
				Bundle extras = getIntent().getExtras();
				if (extras != null) {
					personId = extras.getLong("PersonId");
				} else {
					throw new Exception("Id ist nicht vorhanden");
				}
				persons = new ArrayList<Person>();
				persons = personDao.findContactsById(personId);
				for (int i = 0; i < persons.size(); i++) {
					Log.v(MainActivity.class.getName(), persons.get(i)
							.toString());
					return persons;
				}

				LinearLayout linear = (LinearLayout) findViewById(R.layout.activity_main);
				TextView[] txt = new TextView[3];

				for (int i = 0; i < txt.length; i++) {
					txt[i] = new TextView(MainActivity.this);
					txt[i].setText("text " + i);
					txt[i].setLayoutParams(new LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
					linear.addView(txt[i]);
				}
			} catch (RuntimeException e) {
				Log.e(MainActivity.class.getName(),
						"Es konnte keine Verbindung hergestellt werden" + e);
//				return false;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return persons;

//			return false;
		}
	}
}
