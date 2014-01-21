package com.example.meetinthemiddle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meetinthemiddle.locationverwaltung.dao.LocationDao;
import com.example.meetinthemiddle.locationverwaltung.domain.Location;
import com.example.meetinthemiddle.meetingverwaltung.dao.MeetingDao;
import com.example.meetinthemiddle.meetingverwaltung.domain.Meeting;
import com.example.meetinthemiddle.personenverwaltung.dao.PersonDao;
import com.example.meetinthemiddle.personenverwaltung.domain.Person;
import com.example.meetinthemiddle.placesverwaltung.dao.PlaceDao;
import com.example.meetinthemiddle.placesverwaltung.domain.Place;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.OpenGraphAction;
import com.facebook.model.OpenGraphObject;
import com.facebook.widget.FacebookDialog;
import com.google.android.gcm.GCMRegistrar;
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
	private List<String> firstPersons;
	private List<String> secondPersons;
	private List<String> places;
	private List<String> locations;
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
	private UiLifecycleHelper uiHelper;
//	private MyListTask myListTask;
	

    
	private Session.StatusCallback callback = new Session.StatusCallback()
	{
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			// TODO Auto-generated method stub	
		}
    };

     

	public final static String EXTRA_MESSAGE = "com.example.meetinthemiddle.EXTRA_MESSAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
		meetings = new ArrayList<Meeting>();
		firstPersons = new ArrayList<String>();
		secondPersons = new ArrayList<String>();
		locations = new ArrayList<String>();
		places = new ArrayList<String>();
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
				firstPersons.add(showFirstPersonTask.execute(meetings.get(i).getPers1_fk()).get().getFirstName());
				showSecondPersonTask = new ShowSecondPersonTask();
				secondPersons.add(showSecondPersonTask.execute(meetings.get(i).getPers2_fk()).get().getFirstName());
				showPlaceTask = new ShowPlaceTask();
				places.add(showPlaceTask.execute(meetings.get(i).getOrt_fk()).get().getStadtname());
				showLocationTask = new ShowLocationTask();
				locations.add(showLocationTask.execute(meetings.get(i).getLokalitaet_fk()).get().getBeschreibung());
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		myListTask = new MyListTask();
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();

//		myListTask.execute(firstPersons, secondPersons, places, locations);
//		populateListView();

	     SimpleAdapter adapter = new SimpleAdapter(this, data,android.R.layout.list_content, null, null);

	}

//	private void populateListView() {
//		ArrayAdapter<Meeting> adapter = new MyListAdapter();
//		ListView list = (ListView) findViewById(R.id.pastMeetingsView);
//		list.setAdapter(adapter);
//	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
	        @Override
	        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
	            Log.e("Activity", String.format("Error: %s", error.toString()));
	        }

	        @Override
	        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
	            Log.i("Activity", "Success!");
	        }
	    });
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
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
			meetingFirstPersonView.setText(firstPersons.get(position));

			TextView meetingSecondPersonView = (TextView) meetings_view.findViewById(R.id.meeting_secondPersonName);
			meetingSecondPersonView.setText(secondPersons.get(position));
			
			TextView meetingLocationView = (TextView) meetings_view.findViewById(R.id.meeting_locationName);
			meetingLocationView.setText(locations.get(position));
			
			TextView meetingPlaceView = (TextView) meetings_view.findViewById(R.id.meeting_placeName);
			meetingPlaceView.setText(places.get(position).toString());
			
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
	
	public void share(View view){
		//Define Notification Manager
		NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

		//Define sound URI
		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
		.setSound(soundUri); //This sets the sound to play
		//Display notification
		notificationManager.notify(0, mBuilder.build());
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		// Vibrate for 500 milliseconds
		v.vibrate(500);


		FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
        .setLink("https://developers.facebook.com/android")
		.setDescription("Felix war mit Lukas in Karlsruhe in Hindu Tempel")
        .build();
		uiHelper.trackPendingDialogCall(shareDialog.present());
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
		Intent intent = new Intent(this, DisplayMeetingFunctionsActivity.class);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    Long id = extras.getLong("PersonId");
		    System.out.println(id);
			intent.putExtra("PersonId", id);
		}
		startActivity(intent);
	}

	// Zeigt das Profil des Users.
	public void displayProfile(View view) {
		Intent intent = new Intent(this, DisplayProfileActivity.class);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    Long id = extras.getLong("PersonId");
		    System.out.println(id);
			intent.putExtra("PersonId", id);
		}
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
//	public class MyListTask extends AsyncTask<List<String>, Integer, List<String>>{
//		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
//
//		@Override
//		protected List<String> doInBackground(List<String>... arg0) {
//			// TODO Auto-generated method stub
//			return mylist;
//		}
//		 @Override
//		    protected void onPostExecute(List<String> result) {
//			 ArrayAdapter<Meeting> adapter = new MyListAdapter();
//				ListView list = (ListView) findViewById(R.id.pastMeetingsView);
//				list.setAdapter(adapter);
//		        list.setTextFilterEnabled(true);
//		    }
//		
//	}
}
