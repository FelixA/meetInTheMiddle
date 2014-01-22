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

public class DisplayMeetingsActivity extends Activity implements
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

	private int pHour;
	private int pMinute;
	/**
	 * This integer will uniquely define the dialog to be used for displaying
	 * time picker.
	 */
	static final int TIME_DIALOG_ID = 0;

	private final static int ONE = 1;

	/** Callback received when the user "picks" a time in the dialog */
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			pHour = hourOfDay;
			pMinute = minute;
			updateDisplay();
		}
	};

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_meetings);
		meetingDao = new MeetingDao(this);
		personDao = new PersonDao(this);
		kindofString = "";
		kindofTransportationString = "";
		kindofId = -1L;
		kindofTransportationId = -1L;

		kindof = (TextView) findViewById(R.id.meetings_kindOf_View);
		kindofTransportation = (TextView) findViewById(R.id.meetings_kindOfTransportation_View);
		/** Capture our View elements */
		displayTime = (TextView) findViewById(R.id.meetings_time_view);
		pickTime = (ImageButton) findViewById(R.id.meetings_time_button);

		/** Get the current time */
		final Calendar cal = Calendar.getInstance(new Locale("CET"));
		pHour = cal.get(Calendar.HOUR_OF_DAY);
		pMinute = cal.get(Calendar.MINUTE);

		/** Display the current time in the TextView */
		updateDisplay();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if (extras.getBoolean("true")) {
				if (!extras.getString("kindOf").contentEquals("")) {
					kindofString = extras.getString("kindOf");
					kindof.setText(extras.getString("kindOf"));
				}
				if (!extras.getString("kindOfTransportation").contentEquals("")) {
					kindofTransportationString = extras
							.getString("kindOfTransportation");
					kindofTransportation.setText(extras
							.getString("kindOfTransportation"));
				}
			}
			// if(!extras.getString("kindOf").contentEquals(null)||!extras.getString("kindOf").contentEquals(""))
			// kindof.setText(extras.getString("kindOf"));
			// if(!extras.getString("kindOfTransportation").isEmpty())
			// kindofTransportation.setText(extras.getString("kindOfTransportation"));
		}

		createPopups();

		if (extras != null) {
			if (extras.getBoolean("true")) {
				Person contactPerson = (Person) extras
						.getSerializable("Contact");
				TextView contact = (TextView) findViewById(R.id.meetings_profile_View);
				contact.setText(contactPerson.getFirstName() + " "
						+ contactPerson.getLastName());

			}
		}

		findViewById(R.id.meetings_kindOfTransportation_button)
				.setOnClickListener(this);
		findViewById(R.id.meetings_kindOf_button).setOnClickListener(this);
		findViewById(R.id.meetings_time_button).setOnClickListener(this);
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

	/** Create a new dialog for time picker */

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, mTimeSetListener, pHour, pMinute,
					true);
		}
		return null;
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
		if (itemId == R.id.kindof_Cafe) {
			kindof.setText(item.getTitle());
			kindofString = item.getTitle().toString();
			kindofId = 1L;
		} else if (itemId == R.id.kindof_night_cafe) {
			kindof.setText(item.getTitle());
			kindofString = item.getTitle().toString();
			kindofId = 2L;
		} else if (itemId == R.id.kindof_bar) {
			kindof.setText(item.getTitle());
			kindofString = item.getTitle().toString();
			kindofId = 3L;
		} else if (itemId == R.id.kindof_museum) {
			kindof.setText(item.getTitle());
			kindofString = item.getTitle().toString();
			kindofId = 4L;
		} else if (itemId == R.id.kindof_park) {
			kindof.setText(item.getTitle());
			kindofString = item.getTitle().toString();
			kindofId = 5L;
		} else if (itemId == R.id.kindof_restaurant) {
			kindof.setText(item.getTitle());
			kindofString = item.getTitle().toString();
			kindofId = 6L;
		} else if (itemId == R.id.kindof_movie_theater) {
			kindof.setText(item.getTitle());
			kindofString = item.getTitle().toString();
			kindofId = 7L;
		} else if (itemId == R.id.kindof_hindu_temple) {
			kindof.setText(item.getTitle());
			kindofString = item.getTitle().toString();
			kindofId = 8L;
		} else if (itemId == R.id.kindoftransportation_bike) {
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
				"meetings_kindOf_button")) {
			popupMenuKindOf.show();
		}
		if (getResources().getResourceEntryName(view.getId()).equals(
				"meetings_time_button")) {
			showDialog(TIME_DIALOG_ID);
		}
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

	// Zeigt die Kontakte des jeweiligen Users.
	public void displayContacts(View view) {
		Intent intent = new Intent(this, DisplayContactsActivity.class);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			Long id = extras.getLong("PersonId");
			System.out.println(id);
			intent.putExtra("PersonId", id);
			intent.putExtra("goToMeeting", true);
			System.out.println(kindofString);
			intent.putExtra("kindOf", kindofString);
			intent.putExtra("kindOfTransportation", kindofTransportationString);
		}
		startActivity(intent);
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

	private class CreateMeetingTask extends AsyncTask<Void, Void, Long> {

		@SuppressWarnings("deprecation")
		@Override
		protected Long doInBackground(Void... arg0) {
			Bundle extras = getIntent().getExtras();
			Person contact = (Person) extras.getSerializable("Contact");
			Person person = personDao
					.findPersonById(extras.getLong("PersonId"));
			/** Get the current time */
			Date date = new Date();
			date.setHours(pHour);
			date.setMinutes(pMinute);
			System.out.println(extras.getLong("PersonId") + " "
					+ contact.getId() + " " + date);
			try {
				// TODO: Location pers1, Location pers2
				meetingDao.create(extras.getLong("PersonId"), contact.getId(),
						date, kindofId, 15L, 4, kindofTransportationId,
						"BLubberBlubb", kindofTransportationId,
						person.getAndroidId(), contact.getAndroidId(),
						person.getFirstName() + " möchte sich um " + pHour
								+ ":" + pMinute + " Uhr mit dir treffen",
								"HIER BITTE LOCATION_PERS1","HIER BITTE LOCATION_PERS2");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return 1L;
		}
		@Override 
		protected void onPostExecute(Long id) {
			Bundle extras = getIntent().getExtras();
			Intent intent = new Intent(DisplayMeetingsActivity.this, MainActivity.class);
			if(extras != null){
				Long personId =extras.getLong("PersonId");
				intent.putExtra("PersonId", personId);
				System.out.println(id);
				startActivity(intent);
			}
		}
	}

	public void sendInvitation(View view) throws ParseException {
		CreateMeetingTask createMeetingTask = new CreateMeetingTask();
		createMeetingTask.execute();
	}

	public void openMeetingMap(View view) {
		Intent i = new Intent(DisplayMeetingsActivity.this,
				DisplayOverviewRouting.class);
		startActivity(i);
	}
}
