package com.example.meetinthemiddle;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class DisplayMeetingsActivity extends Activity implements
		OnClickListener, OnMenuItemClickListener {
	private PopupMenu popupMenuKindOf;
	private PopupMenu popupMenuTime;
	private PopupMenu popupMenuKindOfTransportation;
	private String kindofString;
	private String kindofTransportationString;
	private Long id;

	private final static int ONE = 1;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_meetings);
		id = -1L;
		kindofString = "";
		kindofTransportationString = "";
		createPopups();
		

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if(extras.getLong("ContactId") != 0L){
			    id = extras.getLong("ContactId");
				TextView contact = (TextView) findViewById(R.id.meetings_profile_View);
				contact.setText("HURTZ");
				
			}
		}
		
		findViewById(R.id.meetings_kindOfTransportation_button)
				.setOnClickListener(this);
		findViewById(R.id.meetings_kindOf_button).setOnClickListener(this);
		findViewById(R.id.meetings_time_button).setOnClickListener(this);
	}

	@SuppressLint("NewApi")
	private void createPopups() {
		popupMenuKindOf = new PopupMenu(this,
				findViewById(R.id.meetings_kindOf_button));
		popupMenuKindOf.inflate(R.layout.menu_kindof);
		popupMenuKindOf.setOnMenuItemClickListener(this);

		popupMenuTime = new PopupMenu(this,
				findViewById(R.id.meetings_time_button));
		popupMenuTime.getMenu().add(Menu.NONE, ONE, Menu.NONE, "Item 1");
		popupMenuTime.setOnMenuItemClickListener(this);

		popupMenuKindOfTransportation = new PopupMenu(this,
				findViewById(R.id.meetings_kindOfTransportation_button));
		popupMenuKindOfTransportation
				.inflate(R.layout.menu_kindoftransportation);
		popupMenuKindOfTransportation.setOnMenuItemClickListener(this);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		TextView kindof = (TextView) findViewById(R.id.meetings_kindOf_View);
		TextView time = (TextView) findViewById(R.id.meetings_time_view);
		TextView kindofTransportation = (TextView) findViewById(R.id.meetings_kindOfTransportation_View);
		switch (item.getItemId()) {
		case R.id.kindof_bar:
			kindof.setText(item.getTitle());
			kindofString = item.getTitle().toString();
			break;
		case R.id.kindof_Cinema:
			kindof.setText(item.getTitle());
			kindofString = item.getTitle().toString();
			break;
		case R.id.kindof_disco:
			kindof.setText(item.getTitle());
			kindofString = item.getTitle().toString();
			break;
		case R.id.kindof_Restaurant:
			kindof.setText(item.getTitle());
			kindofString = item.getTitle().toString();
			break;
		case R.id.kindoftransportation_bike:
			kindofTransportation.setText(item.getTitle());
			kindofTransportationString = item.getTitle().toString();
			kindofString = item.getTitle().toString();
			break;
		case R.id.kindoftransportation_car:
			kindofTransportation.setText(item.getTitle());
			kindofTransportationString = item.getTitle().toString();
			kindofString = item.getTitle().toString();
			break;
		case R.id.kindoftransportation_foot:
			kindofTransportation.setText(item.getTitle());
			kindofTransportationString = item.getTitle().toString();
			kindofString = item.getTitle().toString();
			break;
		case R.id.kindoftransportation_public:
			kindofTransportation.setText(item.getTitle());
			kindofTransportationString = item.getTitle().toString();
			kindofString = item.getTitle().toString();
			break;
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
			popupMenuTime.show();
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

}
