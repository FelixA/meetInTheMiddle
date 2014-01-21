package com.example.meetinthemiddle;

import java.util.ArrayList;
import java.util.List;

import com.example.meetinthemiddle.DisplayContactsActivity.ShowPersonFirstNameTask;
import com.example.meetinthemiddle.locationverwaltung.dao.LocationDao;
import com.example.meetinthemiddle.locationverwaltung.domain.Location;
import com.example.meetinthemiddle.meetingverwaltung.dao.MeetingDao;
import com.example.meetinthemiddle.meetingverwaltung.domain.Meeting;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;

public class DisplayMessagesActivity extends Activity {

	private List<String> personFirstNames;
	private List<String> lokalitaeten;
	private List<Meeting> meetings;

	private ShowPersonFirstNameTask showPersonFirstNameTask;
	private ShowLocationTask showLocationTask;
	private ShowMeetingEinladungenTask showMeetingEinladungenTask;
	private ShowMeetingAnfragenTask showMeetingAnfragenTask;

	private MeetingDao meetingDao;
	private LocationDao locationDao;

	private ListView messages;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_messages);

		personFirstNames = new ArrayList<String>();
		lokalitaeten = new ArrayList<String>();
		meetings = new ArrayList<Meeting>();

		messages = (ListView) findViewById(R.id.messages_list);

		meetingDao = new MeetingDao(this);
		locationDao = new LocationDao(this);

		showPersonFirstNameTask = new ShowPersonFirstNameTask();
		showLocationTask = new ShowLocationTask();
		showMeetingEinladungenTask = new ShowMeetingEinladungenTask();
		showMeetingAnfragenTask = new ShowMeetingAnfragenTask();
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			Long id = extras.getLong("PersonId");

//			personFirstNames.add(arg0);
//			lokalitaeten.add(object);
//			meetings.add(object);
		}

	}

	public class ShowLocationTask extends AsyncTask<Long, Void, Location> {
		@Override
		protected Location doInBackground(Long... locationId) {
			return null;
			
		}
	}
	public class ShowPersonFirstNameTask extends AsyncTask<Long, Void, Location> {
		@Override
		protected Location doInBackground(Long... locationId) {
			return null;
			
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
