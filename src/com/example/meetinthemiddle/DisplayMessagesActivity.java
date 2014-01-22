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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
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
		List<Map<String, String>> einladungenListe = new ArrayList<Map<String, String>>();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			Long id = extras.getLong("PersonId");

			try {
				meetingAnfragen = showMeetingAnfragenTask.execute(id).get();
				meetingEinladungen = showMeetingEinladungenTask.execute(id)
						.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		try {
			for (int i = 0; i < meetingEinladungen.size(); i++) {
				Map<String, String> personName = new HashMap<String, String>(2);
				Person person = new Person();
				showPersonFirstNameTask = new ShowPersonFirstNameTask();
				showLocationTask = new ShowLocationTask();
				person = showPersonFirstNameTask.execute(
						meetingEinladungen.get(i).getPers1_fk()).get();
				personName.put("zeile1", "Anfrage von " + person.getFirstName()+ " " + person.getLastName() + " um " + meetingEinladungen.get(i).getUhrzeit().getHours() + ":" + meetingEinladungen.get(i).getUhrzeit().getMinutes());
				personName.put("zeile2","in folgender Lokalitaet: "+ showLocationTask.execute(meetingEinladungen.get(i).getLokalitaet_fk())
										.get().getBeschreibung());
				einladungenListe.add(personName);
			}
		} catch (Exception e) {
		}

		for (int i = 0; i < meetingAnfragen.size(); i++) {
			Map<String, String> personName = new HashMap<String, String>(2);
			Person person = new Person();
			try {
				showPersonFirstNameTask = new ShowPersonFirstNameTask();
				person = showPersonFirstNameTask.execute(
						meetingAnfragen.get(i).getPers2_fk()).get();
				showLocationTask = new ShowLocationTask();
				personName.put("zeile1","Angefragt bei " + person.getFirstName() + " "+ person.getLastName() + " um " + meetingAnfragen.get(i).getUhrzeit().getHours() + ":" + meetingAnfragen.get(i).getUhrzeit().getMinutes());
				personName.put("zeile2","in folgender Lokalitaet: "+ showLocationTask.execute(meetingAnfragen.get(i).getLokalitaet_fk())
										.get().getBeschreibung());
				anfragenListe.add(personName);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			registerClickCallback();
		}

		anfragenAdapter = new SimpleAdapter(DisplayMessagesActivity.this,
				anfragenListe, android.R.layout.two_line_list_item,
				new String[] { "zeile1", "zeile2" }, new int[] {
						android.R.id.text1, android.R.id.text2 });
		anfragenList.setAdapter(anfragenAdapter);

		einladungenAdapter = new SimpleAdapter(DisplayMessagesActivity.this,
				einladungenListe, android.R.layout.two_line_list_item,
				new String[] { "zeile1", "zeile2" }, new int[] {
						android.R.id.text1, android.R.id.text2 });
		einladungenList.setAdapter(einladungenAdapter);
	}

	private void registerClickCallback() {
		final ListView anfragenList = (ListView) findViewById(R.id.messages_anfragenlist);
		anfragenList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent,
							View viewClicked, int position, long id) {
						System.out.println("bin in anfragenListListener");
						String text = anfragenList.getItemAtPosition(position).toString();
						String[] parts = text.split("=");
						
						String[] relevant = parts[2].split(" ");
						System.out.println(relevant[2]+ relevant[3]);
						String[] uhrzeit = relevant[5].split(":");
						System.out.println(uhrzeit[0] + uhrzeit[1]);
						String minute = uhrzeit[1].substring(0,
								uhrzeit[1].length() - 1);
						GetPersonByNameTask getPersonByNameTask= new GetPersonByNameTask();
						Long pers_id = -1L;
						try {
						pers_id = getPersonByNameTask.execute(relevant[2], relevant[3]).get().getId();
						System.out.println(pers_id);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						GetMeetingByPers2_FK_UhrzeitTask getMeetingByPers2_FK_UhrzeitTask = new GetMeetingByPers2_FK_UhrzeitTask();
						try {
							Long meeting_id = getMeetingByPers2_FK_UhrzeitTask.execute(pers_id.toString(), uhrzeit[0] , minute).get().getId();
							//TODO: INTENT MIT MEETING_ID ZU NEUER ACTIVITY
							System.out.println(meeting_id);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				});
		final ListView einladungenList = (ListView) findViewById(R.id.messagesInvitations_list);
		einladungenList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent,
					View viewClicked, int position, long id) {
				System.out.println("bin in einladungenListListener");
				String text = einladungenList.getItemAtPosition(position).toString();
				String[] parts = text.split("=");
				
				String[] relevant = parts[2].split(" ");
				System.out.println(relevant[2]+ relevant[3]);
				String[] uhrzeit = relevant[5].split(":");
				System.out.println(uhrzeit[0] + uhrzeit[1]);
				String minute = uhrzeit[1].substring(0,
						uhrzeit[1].length() - 1);
				GetPersonByNameTask getPersonByNameTask= new GetPersonByNameTask();
				Long pers_id = -1L;
				try {
				pers_id = getPersonByNameTask.execute(relevant[2], relevant[3]).get().getId();
				System.out.println(pers_id);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				GetMeetingByPers1_FK_UhrzeitTask getMeetingByPers1_FK_UhrzeitTask = new GetMeetingByPers1_FK_UhrzeitTask();
				try {
					Long meeting_id = getMeetingByPers1_FK_UhrzeitTask.execute(pers_id.toString(), uhrzeit[0] , minute).get().getId();
					//TODO: INTENT MIT MEETING_ID ZU REQUESTACTIVITY
					System.out.println(meeting_id);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	public class GetMeetingByPers2_FK_UhrzeitTask extends AsyncTask<String, Void, Meeting> {

		@Override
		protected Meeting doInBackground(String... params) {
		Meeting meeting = new Meeting();
		meeting = meetingDao.findMeetingByPers2_FK_Uhrzeit(params[0],params[1],params[2]);
			return meeting;
		}
	}
	public class GetMeetingByPers1_FK_UhrzeitTask extends AsyncTask<String, Void, Meeting> {

		@Override
		protected Meeting doInBackground(String... params) {
			Meeting meeting = new Meeting();
			meeting = meetingDao.findMeetingByPers1_FK_Uhrzeit(params[0],params[1],params[2]);
			return meeting;
		}
	}
	public class GetPersonByNameTask extends AsyncTask<String, Void, Person> {

		@Override
		protected Person doInBackground(String... name) {
			Person p = personDao.findPersonByFirstLastName(name[0], name[1]);
			return p;
		}
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

	public class ShowMeetingEinladungenTask extends
			AsyncTask<Long, Void, List<Meeting>> {
		@Override
		protected List<Meeting> doInBackground(Long... person2_FK) {
			return meetingDao.findMeetingByPers2_fk(person2_FK[0]);
		}
	}

	public class ShowMeetingAnfragenTask extends
			AsyncTask<Long, Void, List<Meeting>> {
		@Override
		protected List<Meeting> doInBackground(Long... person1_FK) {
			try {
				return meetingDao.findMeetingByPers1_fk(person1_FK[0]);

			} catch (Exception e) {
				return null;
			}
		}
	}
}
