package com.example.meetinthemiddle;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import com.example.meetinthemiddle.locationverwaltung.dao.LocationDao;
import com.example.meetinthemiddle.locationverwaltung.domain.Location;
import com.example.meetinthemiddle.meetingverwaltung.dao.MeetingDao;
import com.example.meetinthemiddle.meetingverwaltung.domain.Meeting;
import com.example.meetinthemiddle.personenverwaltung.dao.PersonDao;
import com.example.meetinthemiddle.personenverwaltung.domain.Person;
import com.google.android.gms.maps.model.LatLng;

import android.location.Criteria;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class DisplayRequestActivity extends Activity implements
		OnClickListener, OnMenuItemClickListener {
	private PopupMenu popupMenuKindOfTransportation;
	private String kindofTransportationString;
	private Long kindofTransportationId;
	TextView kindof;
	TextView time;
	TextView kindofTransportation;
	private TextView displayTime;
	private MeetingDao meetingDao;
	private LocationDao locationDao;
	private PersonDao personDao;
	private Meeting meeting;
	private Person person;
	private int pHour;
	private int pMinute;
	private FindMeetingTask findMeetingTask;
	private FindPersonTask findPersonTask;
	private FindLocationTask findLocationTask;
	private Location location;
	
	private LocationManager locationManager;
	private String provider;
	public LatLng aktPos;
	public String locationStr, locationPers1;
	
	/**
	 * This integer will uniquely define the dialog to be used for displaying
	 * time picker.
	 */
	static final int TIME_DIALOG_ID = 0;

	private final static int ONE = 1;
	private Long modePers2;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_request);
		meetingDao = new MeetingDao(this);
		personDao = new PersonDao(this);
		locationDao = new LocationDao(this);
		kindofTransportationString = "";
		kindofTransportationId = -1L;
		findMeetingTask = new FindMeetingTask();
		findPersonTask = new FindPersonTask();
		findLocationTask = new FindLocationTask();
		location = new Location();

		// Meeting ID holen
		Bundle extras = getIntent().getExtras();
		Long id = extras.getLong("MeetingId");
		try {
			meeting = findMeetingTask.execute(id).get();
			person = findPersonTask.execute(meeting.getPers1_fk()).get();
			location = findLocationTask.execute(meeting.getLokalitaet_fk())
					.get();

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		kindof = (TextView) findViewById(R.id.request_kindOf_View);
		kindofTransportation = (TextView) findViewById(R.id.request_kindOfTransportation_View);
		displayTime = (TextView) findViewById(R.id.request_time_view);
		TextView contact = (TextView) findViewById(R.id.request_profile_View);
		contact.setText(person.getFirstName());
		kindof.setText(location.getBeschreibung());

		displayTime.setText(meeting.getUhrzeit().toString());
		contact.setText(person.getFirstName() + " " + person.getLastName());
		/** Get the current time */
		final Calendar cal = Calendar.getInstance(new Locale("CET"));
		Date time = meeting.getUhrzeit();
		pHour = time.getHours();
		pMinute = time.getMinutes();
		/** Display the current time in the TextView */
		updateDisplay();

		createPopups();

		findViewById(R.id.request_kindOfTransportation_button)
				.setOnClickListener(this);
	}

	/** Updates the time in the TextView */
	private void updateDisplay() {
		displayTime.setText(new StringBuilder().append(pad(pHour)).append(":")
				.append(pad(pMinute)));
	}

	/** Add padding to numbers less than ten */
	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	@SuppressLint("NewApi")
	private void createPopups() {
		popupMenuKindOfTransportation = new PopupMenu(this,
				findViewById(R.id.request_kindOfTransportation_button));
		popupMenuKindOfTransportation
				.inflate(R.layout.menu_kindoftransportation);
		popupMenuKindOfTransportation.setOnMenuItemClickListener(this);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.kindoftransportation_bike) {
			kindofTransportation.setText(item.getTitle());
			kindofTransportationString = item.getTitle().toString();
			kindofTransportationId = 1L;
		} else if (itemId == R.id.kindoftransportation_car) {
			kindofTransportation.setText(item.getTitle());
			kindofTransportationString = item.getTitle().toString();
			kindofTransportationId = 2L;
		} else if (itemId == R.id.kindoftransportation_foot) {
			kindofTransportation.setText(item.getTitle());
			kindofTransportationString = item.getTitle().toString();
			kindofTransportationId = 3L;
		} else if (itemId == R.id.kindoftransportation_public) {
			kindofTransportation.setText(item.getTitle());
			kindofTransportationString = item.getTitle().toString();
			kindofTransportationId = 4L;
		}
		return false;
	}

	@Override
	public void onClick(View view) {
		if (getResources().getResourceEntryName(view.getId()).equals(
				"request_kindOfTransportation_button")) {
			popupMenuKindOfTransportation.show();
		}
	}
	
	public void sendBestaetigung(View view) throws InterruptedException, ExecutionException{
		UpdateMeetingTask updateMeetingTask = new UpdateMeetingTask();
		updateMeetingTask.execute();
		//TODO: ROUTENFUEHRUNG STARTEN
		// Benötige Positionsdaten von Person 1, bereits ausgelesen?
		
		FindLocation1IDTask findLocation1Task = new FindLocation1IDTask();
		Meeting meetingausdb = findLocation1Task.execute().get();
		String locationPers1 = meetingausdb.getLocationPers1();
		String locationPers2 = meetingausdb.getLocationPers2();
		long modePers1 = meetingausdb.getVerkehrsmittel_pers1_fk();
		long modePers2 = meetingausdb.getVerkehrsmittel_pers2_fk();
		long lokalitaet = meetingausdb.getLokalitaet_fk();
		Date uhrzeit = meetingausdb.getUhrzeit();
		
		Intent intentRouting = new Intent(DisplayRequestActivity.this, DisplayMap.class);
		intentRouting.putExtra("PositionPerson1", locationPers1);
		intentRouting.putExtra("PositionPerson2", locationPers2);
		intentRouting.putExtra("ModusPers1", modePers1);
		intentRouting.putExtra("ModusPers2", modePers2);
		intentRouting.putExtra("lokalitaet", lokalitaet);
		intentRouting.putExtra("Uhrzeit", uhrzeit);
		startActivity(intentRouting);
		
		Intent rating = new Intent(DisplayRequestActivity.this, DisplayRatingActivity.class);
		Bundle extras = getIntent().getExtras();
		Long id = extras.getLong("MeetingId");
		rating.putExtra("MeetingId", id);
		startActivity(rating);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_meetings, menu);
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

	private class FindMeetingTask extends AsyncTask<Long, Void, Meeting> {

		@Override
		protected Meeting doInBackground(Long... id) {
			Meeting meeting = new Meeting();
			meeting = meetingDao.findMeetingById(id[0]);
			return meeting;
		}

	}

	private class UpdateMeetingTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			Bundle extras = getIntent().getExtras();
			Long id = extras.getLong("MeetingId");
			System.out.println(kindofTransportationId);
			//TODO - Positionsdaten holen
			try
			{
				locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			    Criteria criteria = new Criteria();
			    provider = locationManager.getBestProvider(criteria, false);
			    android.location.Location location = locationManager.getLastKnownLocation(provider);
			    
			    if (location != null) {
				      System.out.println("Provider " + provider + " has been selected.");
				      System.out.println("location: "+location);
						double lat = location.getLatitude();
						double lng = location.getLongitude();
						System.out.println("lat/lng: "+lat+"/"+lng);
						locationStr = String.valueOf(lat)+","+String.valueOf(lng);
						Log.i("Location is: ", ""+locationStr);
			    }
			    else
			    {
			    	System.out.println("Location = null: "+location);
			    	Log.i("locationManager", "getPosition failed");
			    }
			}
			catch(Exception e)
			{
				Log.e("Error in Positionsermittlung", "DisplayMeetingsActivity");
			}
			meetingDao.update(id, -1, " ", "Das Treffen wurde bestaetigt",kindofTransportationId,locationStr);
			return null;
		}
	}

	private class FindPersonTask extends AsyncTask<Long, Void, Person> {

		@Override
		protected Person doInBackground(Long... params) {
			Person person = new Person();
			person = personDao.findPersonById(params[0]);
			return person;
		}

	}

	private class FindLocationTask extends AsyncTask<Long, Void, Location> {

		@Override
		protected Location doInBackground(Long... params) {
			return locationDao.findLocationById(params[0]);
		}

	}
	
	private class FindLocation1IDTask extends AsyncTask<Void, Void, Meeting>
	//TODO - getLocation from Pers1, als Variable kannst du die public String locationPers1 verwenden, die ist bereits oben definiert
	{

		@Override
		protected Meeting doInBackground(Void... params) {
			Bundle extras = getIntent().getExtras();
			Long id = extras.getLong("MeetingId");
			Meeting meeting = meetingDao.findMeetingById(id);
					return meeting;
		}
		
	}

}
