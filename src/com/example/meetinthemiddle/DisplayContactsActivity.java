package com.example.meetinthemiddle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.example.meetinthemiddle.personenverwaltung.dao.PersonDao;
import com.example.meetinthemiddle.personenverwaltung.domain.Person;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayContactsActivity extends Activity {
	@SuppressWarnings("unused")
	private static final String LOG_TAG = DisplayContactsActivity.class
			.getSimpleName();

	PersonDao personDao;
	private List<Person> persons;

	private ShowPersonTask showPersonTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_contacts);

		persons = new ArrayList<Person>();
		personDao = new PersonDao(this);
		showPersonTask = new ShowPersonTask();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			Long id = extras.getLong("PersonId");
			try {
				persons = showPersonTask.execute(id).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		populateListView();
		registerClickCallback();
	}

	private void populateListView() {
		ArrayAdapter<Person> adapter = new MyListAdapter();
		ListView list = (ListView) findViewById(R.id.contacts_view);
		list.setAdapter(adapter);
	}

	private class MyListAdapter extends ArrayAdapter<Person> {
		public MyListAdapter() {
			super(DisplayContactsActivity.this, R.layout.contacts_view, persons);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View contacts_view = convertView;
			// Make sure a view is present
			if (contacts_view == null) {
				contacts_view = getLayoutInflater().inflate(
						R.layout.contacts_view, parent, false);
			}

			Person currentPerson = persons.get(position);

			// TODO: Bilder vom User
			// ImageView imageView = (ImageView)
			// contacts_view.findViewById(R.id.contacts_personImage);
			// imageView.setImageResource(currentPerson.getIconID());

			TextView contactPersonFirstNameView = (TextView) contacts_view
					.findViewById(R.id.contacts_firstName);
			contactPersonFirstNameView.setText(currentPerson.getFirstName()
					+ " ");

			TextView contactPersonSecondNameView = (TextView) contacts_view
					.findViewById(R.id.contacts_lastName);
			contactPersonSecondNameView.setText(currentPerson.getLastName());

			return contacts_view;
		}
	}

	private void registerClickCallback() {
		ListView list = (ListView) findViewById(R.id.contacts_view);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked,
					int position, long id) {
				Person contact = persons.get(position);
				Intent intent = new Intent(DisplayContactsActivity.this, DisplayProfileActivity.class);
				intent.putExtra("ContactId", contact.getId());
				startActivity(intent);
				
//				Toast.makeText(DisplayContactsActivity.this, message, Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_contacts, menu);
		return true;
	}

	public class ShowPersonTask extends AsyncTask<Long, Void, List<Person>> {

		@Override
		protected List<Person> doInBackground(Long... personId) {
			persons = new ArrayList<Person>();
			System.out.println(personId[0]);
			persons = personDao.findContactsById(personId[0]);
			return persons;
		}

	}
}
