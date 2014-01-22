package com.example.meetinthemiddle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.example.meetinthemiddle.DisplayContactsActivity.ShowPersonFirstNameTask;
import com.example.meetinthemiddle.locationverwaltung.dao.LocationDao;
import com.example.meetinthemiddle.locationverwaltung.domain.Location;
import com.example.meetinthemiddle.meetingverwaltung.dao.MeetingDao;
import com.example.meetinthemiddle.meetingverwaltung.domain.Meeting;
import com.example.meetinthemiddle.personenverwaltung.dao.PersonDao;
import com.example.meetinthemiddle.personenverwaltung.domain.Person;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;

public class DisplayMessagesActivity extends Activity {

	private List<String> personFirstNames;
	private List<String> lokalitaeten;
	private List<Meeting> meetingEinladungen;
	private List<Meeting> meetingAnfragen;


	private ShowPersonFirstNameTask showPersonFirstNameTask;
	private ShowLocationTask showLocationTask;
	private ShowMeetingEinladungenTask showMeetingEinladungenTask;
	private ShowMeetingAnfragenTask showMeetingAnfragenTask;

	private MeetingDao meetingDao;
	private LocationDao locationDao;
	private PersonDao personDao;
	
	public SimpleAdapter anfragenAdapter;
public SimpleAdapter einladungenAdapter;

	private ListView einladungenList;
	private ListView anfragenList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_messages);

		personFirstNames = new ArrayList<String>();
		lokalitaeten = new ArrayList<String>();
		meetingEinladungen = new ArrayList<Meeting>();
		meetingAnfragen = new ArrayList<Meeting>();
		einladungenList = (ListView) findViewById(R.id.messagesInvitations_list);
		anfragenList = (ListView) findViewById(R.id.messages_anfragenlist);

		meetingDao = new MeetingDao(this);
		locationDao = new LocationDao(this);
		personDao = new PersonDao(this);

		showPersonFirstNameTask = new ShowPersonFirstNameTask();
		showLocationTask = new ShowLocationTask();
		showMeetingEinladungenTask = new ShowMeetingEinladungenTask();
		showMeetingAnfragenTask = new ShowMeetingAnfragenTask();
		
		List<Map<String, String>> anfragenListe = new ArrayList<Map<String, String>>();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			Long id = extras.getLong("PersonId");

			try {
				meetingAnfragen = showMeetingAnfragenTask.execute(id).get();
				meetingEinladungen = showMeetingAnfragenTask.execute(id).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (int i = 0; i < meetingAnfragen.size(); i++) {
			Map<String, String> personName = new HashMap<String, String>(2);
			personName.put("zeile1", "Anfrage von " + showPersonFirstNameTask.execute(meetingAnfragen.get(i).getPers1_fk()));
			personName.put("zeile2", "in folgender Lokalitaet: " + showLocationTask.execute(meetingAnfragen.get(i).getLokalitaet_fk()));
			anfragenListe.add(personName);
		}
		
		anfragenAdapter = new SimpleAdapter(DisplayMessagesActivity.this, anfragenListe,
				android.R.layout.two_line_list_item, new String[] {
						"zeile1", "zeile2" }, new int[] {
						android.R.id.text1, android.R.id.text2 });
		anfragenList.setAdapter(anfragenAdapter);
	}

	public class ShowLocationTask extends AsyncTask<Long, Void, Location> {
		@Override
		protected Location doInBackground(Long... locationId) {
			return locationDao.findLocationById(locationId[0]);
		}
	}
	public class ShowPersonFirstNameTask extends AsyncTask<Long, Void, Person> {
		@Override
		protected Person doInBackground(Long... personId) {
			return personDao.findPersonById(personId[0]);	
		}
	}
	public class ShowMeetingEinladungenTask extends AsyncTask<Long, Void, List<Meeting>> {
		@Override
		protected List<Meeting> doInBackground(Long... person1_FK) {
			return meetingDao.findMeetingByPers1_fk(person1_FK[0]);	
		}
	}
	public class ShowMeetingAnfragenTask extends AsyncTask<Long, Void, List<Meeting>> {
		@Override
		protected List<Meeting> doInBackground(Long... person2_FK) {
			return meetingDao.findMeetingByPers2_fk(person2_FK[0]);	
		}
	}
}
