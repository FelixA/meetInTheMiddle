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
	private ShowMeetingTask showMeetingTask;

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
		showMeetingTask = new ShowMeetingTask();

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
	public class ShowMeetingTask extends AsyncTask<Long, Void, Location> {
		@Override
		protected Location doInBackground(Long... locationId) {
			return null;
			
		}
	}
}
