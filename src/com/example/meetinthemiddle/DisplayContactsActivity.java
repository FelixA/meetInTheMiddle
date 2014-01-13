package com.example.meetinthemiddle;

import java.util.ArrayList;
import java.util.List;
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
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayContactsActivity extends Activity{
	@SuppressWarnings("unused")
	private static final String LOG_TAG = DisplayContactsActivity.class
			.getSimpleName();

	private PersonDao personDao;
	private List<Person> persons;

	private ShowPersonTask showPersonTask;
	
	private ListView contactsView;
	private SearchView searchView;
	ArrayAdapter<Person> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_contacts);
		ViewGroup relativeLayout = (ViewGroup) findViewById(R.id.contacts_layout_id);

		
		
//		//prepare the SearchView
//        searchView = (SearchView) findViewById(R.id.searchContacts);
// 
//        //Sets the default or resting state of the search field. If true, a single search icon is shown by default and
//        // expands to show the text field and other buttons when pressed. Also, if the default state is iconified, then it
//        // collapses to that state when the close button is pressed. Changes to this property will take effect immediately.
//        //The default value is true.
//        searchView.setIconifiedByDefault(false);

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
		
		// Get the intent, verify the action and get the query
	    Intent intent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      doMySearch(query);
	    }
 
		populateListView();
		registerClickCallback();
		
		
	}

	private void doMySearch(String query) {
		System.out.println("HEREENIORVCNOIVMR");
		adapter = new MyListAdapter();
		List<Person> personsResult = new ArrayList<Person>();
		ListView list = (ListView) findViewById(R.id.contacts_view);
		list.setAdapter(adapter);
		
    	for(int i=0; i<persons.size();i++){
    		if(query.toUpperCase().contains(persons.get(i).getFirstName().toUpperCase()) || query.toUpperCase().contains(persons.get(i).getLastName().toUpperCase())){
    			personsResult.add(persons.get(i));
    		}
    	}
	
		Toast.makeText(DisplayContactsActivity.this, personsResult.get(0).toString(), 0).show();
    }
	
	private void populateListView() {
		adapter = new MyListAdapter();
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
				
				Bundle extras = getIntent().getExtras();
				if(extras.getBoolean("goToMeeting")){
					Intent intentNew = new Intent(DisplayContactsActivity.this, DisplayMeetingsActivity.class);
					 Long personId = extras.getLong("PersonId");
					 System.out.println(personId);
					 intentNew.putExtra("PersonId", personId);
					intentNew.putExtra("ContactId", contact.getId());
					startActivity(intentNew);
				}
			}
		});
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
