package com.example.meetinthemiddle;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class DisplayMeetingFunctionsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_meeting_functions);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_meeting_functions, menu);
		return true;
	}
	
	// Zeigt den Bildschirm zum erstellen eines Treffens
		public void displayMeetings(View view) {
			Intent intent = new Intent(this, DisplayMeetingsActivity.class);
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
			    Long id = extras.getLong("PersonId");
			    System.out.println(id);
				intent.putExtra("PersonId", id);
			}
			startActivity(intent);
		}
		
		public void meetingHistory(View view) {
			Intent intent = new Intent(this, DisplayRatingActivity.class);
//			Bundle extras = getIntent().getExtras();
//			if (extras != null) {
//			    Long id = extras.getLong("PersonId");
//			    System.out.println(id);
//				intent.putExtra("PersonId", id);
//			}
			startActivity(intent);
		}


}
