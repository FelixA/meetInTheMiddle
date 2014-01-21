package com.example.meetinthemiddle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.example.meetinthemiddle.personenverwaltung.dao.PersonDao;
import com.example.meetinthemiddle.personenverwaltung.domain.Person;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.SearchView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayContactsActivity extends Activity {
	@SuppressWarnings("unused")
	private static final String LOG_TAG = DisplayContactsActivity.class
			.getSimpleName();
	private PersonDao personDao;
	private List<String> contactFirstNames;
	private List<String> contactLastNames;

	private List<String> possibleContactFirstNames;
	private List<String> possibleContactLastNames;

	private ShowPersonFirstNameTask showPersonFirstNameTask;
	private ShowPersonLastNameTask showPersonLastNameTask;

	private ShowPossibleContactsFirstNameTask showPossibleContactsFirstNameTask;
	private ShowPossibleContactsLastNameTask showPossibleContactsLastNameTask;

	private ListView contactPersons;

	private ListView possibleContactPersons;
	public SimpleAdapter simpleAdapter;
	public SimpleAdapter possible_contacts_adapter;

	// Search EditText
	EditText inputSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_contacts);

		contactFirstNames = new ArrayList<String>();
		contactLastNames = new ArrayList<String>();

		possibleContactPersons = (ListView) findViewById(R.id.contacts_possibleContacts_List);
		contactPersons = (ListView) findViewById(R.id.contacts_List);
		contactPersons.setTextFilterEnabled(true);

		possibleContactFirstNames = new ArrayList<String>();
		possibleContactLastNames = new ArrayList<String>();

		personDao = new PersonDao(this);

		showPersonFirstNameTask = new ShowPersonFirstNameTask();
		showPersonLastNameTask = new ShowPersonLastNameTask();

		showPossibleContactsFirstNameTask = new ShowPossibleContactsFirstNameTask();
		showPossibleContactsLastNameTask = new ShowPossibleContactsLastNameTask();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			Long id = extras.getLong("PersonId");

			try {
				contactFirstNames = showPersonFirstNameTask.execute(id).get();
				
				contactLastNames = showPersonLastNameTask.execute(id).get();
				possibleContactFirstNames = showPossibleContactsFirstNameTask
						.execute().get();
				possibleContactLastNames = showPossibleContactsLastNameTask
						.execute().get();
				if (contactFirstNames == null) {
					System.out.println("Noch keine Kontakte");
					return;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch(Exception e){
				contactFirstNames.add("Nobody");
			}
		}

		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		List<Map<String, String>> possibleContacts = new ArrayList<Map<String, String>>();
		List<String> realPossibleContactFirstNames = new ArrayList<String>();
		List<String> realPossibleContactLastNames = new ArrayList<String>();


	        for(String firstName : possibleContactFirstNames) {
	            if(!contactFirstNames.contains(firstName)) {
	            	realPossibleContactFirstNames.add(firstName);
	            }
	        }
	        for(String lastName : possibleContactLastNames) {
	            if(!contactLastNames.contains(lastName)) {
	            	realPossibleContactLastNames.add(lastName);
	            }
	        }


		for (int i = 0; i < contactFirstNames.size(); i++) {
			Map<String, String> personName = new HashMap<String, String>(2);
			personName.put("firstName", contactFirstNames.get(i));
			personName.put("lastName", contactLastNames.get(i));
			data.add(personName);
		}
		
		for (int i = 0; i < realPossibleContactFirstNames.size(); i++) {
			Map<String, String> personName = new HashMap<String, String>(2);
			personName.put("firstName", realPossibleContactFirstNames.get(i));
			personName.put("lastName", realPossibleContactLastNames.get(i));
			possibleContacts.add(personName);	
		}

		simpleAdapter = new SimpleAdapter(DisplayContactsActivity.this, data,
				android.R.layout.two_line_list_item, new String[] {
						"firstName", "lastName" }, new int[] {
						android.R.id.text1, android.R.id.text2 });
		contactPersons.setAdapter(simpleAdapter);
		possible_contacts_adapter = new SimpleAdapter(
				DisplayContactsActivity.this, possibleContacts,
				android.R.layout.two_line_list_item, new String[] {
						"firstName", "lastName" }, new int[] {
						android.R.id.text1, android.R.id.text2 });
		possibleContactPersons.setAdapter(possible_contacts_adapter);

		inputSearch = (EditText) findViewById(R.id.contacts_searchText);
		inputSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				DisplayContactsActivity.this.simpleAdapter.getFilter().filter(
						cs);
				DisplayContactsActivity.this.possible_contacts_adapter
						.getFilter().filter(cs);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable cs) {

			}
		});

		contactPersons.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				TextView text1 = (TextView) v.findViewById(android.R.id.text1);
				TextView text2 = (TextView) v.findViewById(android.R.id.text2);
			}
		});
		registerClickCallback();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				Intent intent = new Intent(this, MainActivity.class);
				intent.putExtra("PersonId", extras.getLong("PersonId"));
				startActivity(intent);
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	public class ShowPersonFirstNameTask extends
			AsyncTask<Long, Void, List<String>> {

		@Override
		protected List<String> doInBackground(Long... personId) {
			List<Person> persons = new ArrayList<Person>();
			contactFirstNames = new ArrayList<String>();

			try{
			persons = personDao.findContactsById(personId[0]);
			if(persons.size()!=0){
			for (int i = 0; i < persons.size(); ++i) {
				contactFirstNames.add(persons.get(i).getFirstName());
				
			}
			}}catch(Exception e){
				contactFirstNames.add("noch kein Kontakt vorhanden");
			}
			
			return contactFirstNames;
		}
	}

	private void registerClickCallback() {
		final ListView possibleContactsList = (ListView) findViewById(R.id.contacts_possibleContacts_List);
		possibleContactsList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent,View viewClicked, int position, long id) {
						System.out.println("bin in possibleContactsListListener");
						AddNewFriendTask addNewFriendTask = new AddNewFriendTask();
						Bundle extras = getIntent().getExtras();
						if (extras != null) {
							Long personId = extras.getLong("PersonId");
							TextView textView = (TextView) viewClicked
									.findViewById(R.id.contacts_firstName);

							String text = possibleContactsList
									.getItemAtPosition(position).toString();
							System.out.println(text);
							String[] parts = text.split("=");
							for (String part : parts) {
								System.out.println(part);
							}
							String[] bla = parts[1].split(",");
							String firstName = bla[0];
							String lastName = parts[2].substring(0,
									parts[2].length() - 1);
							System.out.println(firstName + " " + lastName);

							GetContactIdTask getContactIdTask = new GetContactIdTask();
							try {
								addNewFriendTask.execute(
										personId,
										getContactIdTask.execute(firstName,
												lastName).get());
								Toast.makeText(getApplicationContext(), "Kontakt " + firstName + " " + lastName + " wurde angelegt.", Toast.LENGTH_LONG).show();

							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ExecutionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						finish();
						startActivity(getIntent());
					}
					
				});

		ListView list = (ListView) findViewById(R.id.contacts_List);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked,
					int position, long id) {
				
				System.out.println("bin in ListListener");

				Bundle extras = getIntent().getExtras();
				if (extras.getBoolean("goToMeeting")) {
					TextView textView = (TextView) viewClicked.findViewById(R.id.contacts_firstName);

					String text = possibleContactsList.getItemAtPosition(position).toString();
					System.out.println(text);
					String[] parts = text.split("=");
					for (String part : parts) {
						System.out.println(part);
					}
					String[] bla = parts[1].split(",");
					String firstName = bla[0];
					String lastName = parts[2].substring(0,
							parts[2].length() - 1);
					System.out.println(firstName + " " + lastName);

					GetContactByNameTask getContactByNameTask = new GetContactByNameTask();
					
					Intent intentNew = new Intent(DisplayContactsActivity.this,
							DisplayMeetingsActivity.class);
					Long personId = extras.getLong("PersonId");
					System.out.println(personId);
					System.out.println("kind " + extras.getString("kindOf"));
					System.out.println("transport "
							+ extras.getString("kindOfTransportation"));
					intentNew.putExtra("PersonId", personId);
					 try {
						intentNew.putExtra("Contact", getContactByNameTask.execute(firstName,lastName).get());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					intentNew.putExtra("kindOf", extras.getString("kindOf"));
					intentNew.putExtra("kindOfTransportation",
							extras.getString("kindOfTransportation"));
					intentNew.putExtra("true", true);

					startActivity(intentNew);
					return;
				}

				String contactFirstName = contactFirstNames.get(position);
				String contactLastName = contactLastNames.get(position);
				GetContactIdTask getContactIdTask = new GetContactIdTask();
				Long contactId = -1L;
				try {
					contactId = getContactIdTask.execute(contactFirstName,
							contactLastName).get();
					System.out.println(contactId);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent intent = new Intent(DisplayContactsActivity.this,
						DisplayProfileActivity.class);
				System.out.println("Starte Activity Profil");
				intent.putExtra("ContactId", contactId);
				startActivity(intent);

			}
		});
	}

	public class AddNewFriendTask extends AsyncTask<Long, Void, List<String>> {

		@Override
		protected List<String> doInBackground(Long... persons) {
			personDao.createKontakt(persons[0], persons[1]);
			return null;
		}

	}

	public class ShowPersonLastNameTask extends
			AsyncTask<Long, Void, List<String>> {

		@Override
		protected List<String> doInBackground(Long... personId) {
			List<Person> personsLastNames = new ArrayList<Person>();
			contactFirstNames = new ArrayList<String>();
			contactLastNames = new ArrayList<String>();

			personsLastNames = personDao.findContactsById(personId[0]);
			try{
			for (int i = 0; i < personsLastNames.size(); ++i) {
				contactFirstNames.add(personsLastNames.get(i).getFirstName());
				contactLastNames.add(personsLastNames.get(i).getLastName());
			}}catch(Exception e){
				contactLastNames.add("Bitte jemanden aus der Liste unten hinzufügen");
			}
			
			return contactLastNames;
		}
	}

	public class GetContactIdTask extends AsyncTask<String, Void, Long> {

		@Override
		protected Long doInBackground(String... name) {
			Long id = personDao.findPersonByFirstLastName(name[0], name[1])
					.getId();
			return id;
		}
	}
	public class GetContactByNameTask extends AsyncTask<String, Void, Person> {

		@Override
		protected Person doInBackground(String... name) {
			Person p = personDao.findPersonByFirstLastName(name[0], name[1]);
			return p;
		}
	}

	public class ShowPossibleContactsFirstNameTask extends
			AsyncTask<Void, Void, List<String>> {

		@Override
		protected List<String> doInBackground(Void... params) {
			List<Person> contactNames = new ArrayList<Person>();
			possibleContactFirstNames = new ArrayList<String>();
			contactNames = personDao.selectAll();
			for (int i = 0; i < contactNames.size(); ++i) {
				possibleContactFirstNames.add(contactNames.get(i)
						.getFirstName());
			}
			return possibleContactFirstNames;
		}
	}

	public class ShowPossibleContactsLastNameTask extends
			AsyncTask<Void, Void, List<String>> {
		@Override
		protected List<String> doInBackground(Void... params) {
			List<Person> contactNames = new ArrayList<Person>();
			possibleContactLastNames = new ArrayList<String>();
			contactNames = personDao.selectAll();
			for (int i = 0; i < contactNames.size(); ++i) {
				possibleContactLastNames.add(contactNames.get(i).getLastName());
			}
			return possibleContactLastNames;
		}
	}
}
