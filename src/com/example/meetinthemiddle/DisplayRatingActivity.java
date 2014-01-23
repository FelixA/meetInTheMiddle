package com.example.meetinthemiddle;

import java.util.concurrent.ExecutionException;

import com.example.meetinthemiddle.locationverwaltung.dao.LocationDao;
import com.example.meetinthemiddle.meetingverwaltung.dao.MeetingDao;
import com.example.meetinthemiddle.meetingverwaltung.domain.Meeting;
import com.example.meetinthemiddle.personenverwaltung.dao.PersonDao;
import com.example.meetinthemiddle.personenverwaltung.domain.Person;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Toast;

public class DisplayRatingActivity extends Activity {

	private RatingBar ratingBar;
	private Button button;
	private MeetingDao meetingDao;
	private PersonDao personDao;
	private LocationDao locationDao;
	private Integer ratingInt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_rating);
		ShowMeetingTask showMeetingTask = new ShowMeetingTask();
		meetingDao = new MeetingDao(this);
		personDao = new PersonDao(this);
		locationDao = new LocationDao(this);
		addListenerOnRatingBar();
		addListenerOnButton();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			Long id = extras.getLong("MeetingId");
			;
		}
	}

	public void acceptAndShare(View view) throws InterruptedException, ExecutionException {
		ShowMeetingTask showMeetingTask = new ShowMeetingTask();
		String textToShare = showMeetingTask.execute().get();
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("text/plain");
		share.putExtra(Intent.EXTRA_TEXT, textToShare);

		startActivity(Intent.createChooser(share,
				"Title of the dialog the system will open"));
	}

	public void addListenerOnRatingBar() {

		ratingBar = (RatingBar) findViewById(R.id.RatingBar02);

		// if rating value is changed,
		// display the current rating value in the result (textview)
		// automatically
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				ratingInt = (int) rating;
						//String.valueOf(rating);
				;
			}
		});
	}

	public void addListenerOnButton() {

		ratingBar = (RatingBar) findViewById(R.id.RatingBar02);
		button = (Button) findViewById(R.id.button);

		// if click on me, then display the current rating value.
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Toast.makeText(DisplayRatingActivity.this,
						String.valueOf(ratingBar.getRating()),
						Toast.LENGTH_SHORT).show();
				// In DB Schreiben
			}

		});

	}

	private class ShowMeetingTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			Bundle extras = getIntent().getExtras();
			Long id = extras.getLong("MeetingId");
			System.out.println(id);
			Meeting meeting = meetingDao.findMeetingById(id);
			Person p = personDao.findPersonById(meeting.getPers2_fk());
			
			return "war mit " + p.getFirstName() + " in " + locationDao.findLocationById(meeting.getLokalitaet_fk()).getBeschreibung() + " und gibt dafür " + ratingInt;
		}
	}
}
