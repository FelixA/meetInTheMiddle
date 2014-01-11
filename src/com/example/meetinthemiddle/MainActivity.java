package com.example.meetinthemiddle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import com.example.meetinthemiddle.locationverwaltung.dao.LocationDao;
import com.example.meetinthemiddle.locationverwaltung.domain.Location;
import com.example.meetinthemiddle.meetingverwaltung.dao.MeetingDao;
import com.example.meetinthemiddle.meetingverwaltung.domain.Meeting;
import com.example.meetinthemiddle.personenverwaltung.dao.PersonDao;
import com.example.meetinthemiddle.personenverwaltung.domain.Person;
import com.example.meetinthemiddle.placesverwaltung.dao.PlaceDao;
import com.example.meetinthemiddle.placesverwaltung.domain.Place;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
/**
 * 
 * @author Felix
 *
 */
public class MainActivity extends Activity {
	//TODO: Treffen = 0 error
	@SuppressWarnings("unused")
	private static final String LOG_TAG = MainActivity.class.getSimpleName();

	MeetingDao meetingDao;
	private List<Meeting> meetings;
	private List<Person> firstPersons;
	private List<Person> secondPersons;
	private List<Place> places;
	private List<Location> locations;
	private Place place;
	private Person person;
	private Location location;
	PersonDao personDao;
	PlaceDao placeDao;
	LocationDao locationDao;
	
	private ShowMeetingsTask showMeetingsTask = null;
	private ShowFirstPersonTask showFirstPersonTask = null;
	private ShowSecondPersonTask showSecondPersonTask = null;
	private ShowPlaceTask showPlaceTask = null;
	private ShowLocationTask showLocationTask = null;

	public final static String EXTRA_MESSAGE = "com.example.meetinthemiddle.EXTRA_MESSAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		meetings = new ArrayList<Meeting>();
		firstPersons = new ArrayList<Person>();
		secondPersons = new ArrayList<Person>();
		locations = new ArrayList<Location>();
		places = new ArrayList<Place>();
		place = new Place();
		person = new Person();
		location = new Location();
		
		meetingDao = new MeetingDao(this);
		personDao = new PersonDao(this);
		placeDao = new PlaceDao(this);
		locationDao = new LocationDao(this);
		
		showMeetingsTask = new ShowMeetingsTask();
		try {
			meetings = showMeetingsTask.execute((Void) null).get();
			for(int i = 0; i<meetings.size();++i){
				showFirstPersonTask = new ShowFirstPersonTask();
				firstPersons.add(showFirstPersonTask.execute(meetings.get(i).getPers1_fk()).get());
				showSecondPersonTask = new ShowSecondPersonTask();
				secondPersons.add(showSecondPersonTask.execute(meetings.get(i).getPers2_fk()).get());
				showPlaceTask = new ShowPlaceTask();
				places.add(showPlaceTask.execute(meetings.get(i).getOrt_fk()).get());
				showLocationTask = new ShowLocationTask();
				locations.add(showLocationTask.execute(meetings.get(i).getLokalitaet_fk()).get());
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		populateListView();
	}

	private void populateListView() {
		ArrayAdapter<Meeting> adapter = new MyListAdapter();
		ListView list = (ListView) findViewById(R.id.pastMeetingsView);
		list.setAdapter(adapter);
	}

	private class MyListAdapter extends ArrayAdapter<Meeting> {
		public MyListAdapter() {
			super(MainActivity.this, R.layout.meetings_view, meetings);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View meetings_view = convertView;
			// Make sure a view is present
			if (meetings_view == null) {
				meetings_view = getLayoutInflater().inflate(
						R.layout.meetings_view, parent, false);
			}

			Meeting currentMeeting = meetings.get(position);

			// TODO: Bilder vom User
			// ImageView imageView = (ImageView)
			// contacts_view.findViewById(R.id.contacts_personImage);
			// imageView.setImageResource(currentPerson.getIconID());

			TextView meetingFirstPersonView = (TextView) meetings_view.findViewById(R.id.meeting_firstPersonName);
			meetingFirstPersonView.setText(firstPersons.get(position).getFirstName());

			TextView meetingSecondPersonView = (TextView) meetings_view.findViewById(R.id.meeting_secondPersonName);
			meetingSecondPersonView.setText(secondPersons.get(position).getFirstName());
			
			TextView meetingLocationView = (TextView) meetings_view.findViewById(R.id.meeting_locationName);
			meetingLocationView.setText(locations.get(position).getBeschreibung());
			
			TextView meetingPlaceView = (TextView) meetings_view.findViewById(R.id.meeting_placeName);
			meetingPlaceView.setText(places.get(position).getStadtname().toString());
			
			TextView meetingCommentView = (TextView) meetings_view.findViewById(R.id.meeting_comment);
			meetingCommentView.setText(currentMeeting.getKommentar().toString());
			return meetings_view;
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
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    Long id = extras.getLong("PersonId");
		    System.out.println(id);
			intent.putExtra("PersonId", id);
		}
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
	public class ShowMeetingsTask extends AsyncTask<Void, Void, List<Meeting>> {
		@Override
		protected List<Meeting> doInBackground(Void... params) {
			try {
				// Hole aus den extras die Id der gesuchten Person
				// Long meetingId = null;
				// Bundle extras = getIntent().getExtras();
				// if (extras != null) {
				// meetingId = extras.getLong("PersonId");
				// } else {
				// throw new Exception("Id ist nicht vorhanden");
				// }
				meetings = new ArrayList<Meeting>();
				meetings = meetingDao.selectAll();
				for (int i = 0; i < meetings.size(); i++) {
					Log.v(MainActivity.class.getName(), meetings.get(i)
							.toString());
					// return meetings;
				}
			} catch (Exception e) {
				try {
					throw new Exception("Keine Verbindung" + e);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			return meetings;
			// return false;
		}
	}
	public class ShowFirstPersonTask extends AsyncTask<Long, Void, Person>{
		@Override
		protected Person doInBackground(Long... firstPersonId) {
			person = new Person();
			person = personDao.findPersonById(firstPersonId[0]);
			return person;
		}
	}
	public class ShowSecondPersonTask extends AsyncTask<Long, Void, Person>{
		@Override
		protected Person doInBackground(Long... secondPersonId) {
			person = new Person();
			person = personDao.findPersonById(secondPersonId[0]);
			return person;
		}
	}
	
	public class ShowPlaceTask extends AsyncTask<Long, Void, Place>{
		@Override
		protected Place doInBackground(Long... placeId) {
			place = new Place();
			place = placeDao.findPlaceById(placeId[0]);
			return place;
		}
	}
	
	public class ShowLocationTask extends AsyncTask<Long, Void, Location>{

		@Override
		protected Location doInBackground(Long... locationId) {
			location = new Location();
			location = locationDao.findLocationById(locationId[0]);
			return location;
		}
		
	}
}
