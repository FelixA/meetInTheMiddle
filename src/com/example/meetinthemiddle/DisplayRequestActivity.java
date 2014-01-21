package com.example.meetinthemiddle;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.example.meetinthemiddle.meetingverwaltung.dao.MeetingDao;
import com.example.meetinthemiddle.meetingverwaltung.domain.Meeting;
import com.example.meetinthemiddle.personenverwaltung.dao.PersonDao;
import com.example.meetinthemiddle.personenverwaltung.domain.Person;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.content.Intent;
import android.os.Build;

public class DisplayRequestActivity extends Activity implements
		OnClickListener, OnMenuItemClickListener {
	private PopupMenu popupMenuKindOf;
	private PopupMenu popupMenuKindOfTransportation;
	private String kindofString;
	private String kindofTransportationString;
	private Long kindofId;
	private Long kindofTransportationId;
	TextView kindof;
	TextView time;
	TextView kindofTransportation;
	private TextView displayTime;
	private ImageButton pickTime;
	private MeetingDao meetingDao;
	private PersonDao personDao;
	private Meeting meeting;
	private Person person;
	private int pHour;
	private int pMinute;
	/**
	 * This integer will uniquely define the dialog to be used for displaying
	 * time picker.
	 */
	static final int TIME_DIALOG_ID = 0;

	private final static int ONE = 1;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_request);
		meetingDao = new MeetingDao(this);
		personDao = new PersonDao(this);
		kindofString = "";
		kindofTransportationString = "";
		kindofId = -1L;
		kindofTransportationId = -1L;
		
		//Meeting ID holen
		Bundle extras = getIntent().getExtras();
	    Long id = extras.getLong("MeetingId");
		meeting = meetingDao.findMeetingById(id);	    
	    
		kindof = (TextView) findViewById(R.id.meetings_kindOf_View);
		kindofTransportation = (TextView) findViewById(R.id.meetings_kindOfTransportation_View);
		/** Capture our View elements */
		displayTime = (TextView) findViewById(R.id.meetings_time_view);
		pickTime = (ImageButton) findViewById(R.id.meetings_time_button);
		TextView contact = (TextView) findViewById(R.id.meetings_profile_View);
		
		displayTime.setText(meeting.getUhrzeit().toString());
		kindof.setText(meeting.getLokalitaet_fk().toString());
		person = personDao.findPersonById(meeting.getPers1_fk());
		contact.setText(person.getFirstName() + " " + person.getLastName());
		/** Get the current time */
		final Calendar cal = Calendar.getInstance(new Locale("CET"));
		pHour = cal.get(Calendar.HOUR_OF_DAY);
		pMinute = cal.get(Calendar.MINUTE);

		/** Display the current time in the TextView */
		updateDisplay();

		createPopups();

		findViewById(R.id.meetings_kindOfTransportation_button).setOnClickListener(this);
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
		popupMenuKindOf = new PopupMenu(this,
				findViewById(R.id.meetings_kindOf_button));
		popupMenuKindOf.inflate(R.layout.menu_kindof);
		popupMenuKindOf.setOnMenuItemClickListener(this);

		popupMenuKindOfTransportation = new PopupMenu(this,
				findViewById(R.id.meetings_kindOfTransportation_button));
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
				"meetings_kindOfTransportation_button")) {
			popupMenuKindOfTransportation.show();
		}
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

	private class CreateMeetingTask extends AsyncTask<Void, Void, Void> {

		@SuppressWarnings("deprecation")
		@Override
		protected Void doInBackground(Void... arg0) {
			Bundle extras = getIntent().getExtras();
		    Long id = extras.getLong("MeetingId");
			meetingDao.update(id, 5, "abc", 234L, + pHour + ":" + pMinute + " Uhr mit dir treffen");
			return null;
		}
	}

	public void sendInvitation(View view) throws ParseException {
		CreateMeetingTask createMeetingTask = new CreateMeetingTask();
		createMeetingTask.execute();
	}
	
}
