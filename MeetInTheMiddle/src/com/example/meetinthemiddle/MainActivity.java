package com.example.meetinthemiddle;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

	public final static String EXTRA_MESSAGE = "com.example.meetinthemiddle.EXTRA_MESSAGE";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	//Zeigt die Kontakte des jeweiligen Users.
	public void displayContacts(View view){
		Intent intent = new Intent(this, DisplayContactsActivity.class);
		startActivity(intent);
	}
	
	//Zeigt an den User geschickte Nachrichten.
	public void displayMessages(View view){
		Intent intent = new Intent(this, DisplayMessagesActivity.class);
		startActivity(intent);
	}
	
	//Zeigt den Bildschirm zur auswahl ob man ein meeting erstellen will, oder sich die historie einsehen.
	public void displayMeetings(View view){
		Intent intent = new Intent(this, DisplayMeetingsActivity.class);
		startActivity(intent);
	}
	
	//Zeigt das Profil des Users.
		public void displayProfile(View view){
			Intent intent = new Intent(this, DisplayProfileActivity.class);
			startActivity(intent);
		}
		
	//Zeigt die FAQ.
		 public void displayQuestions(View view){
		 	Intent intent = new Intent(this, DisplayQuestionsActivity.class);
			startActivity(intent);
		}
	//Zeigt die möglichen Einstellungen.
		 public void displaySettings(View view){
			Intent intent = new Intent(this, DisplaySettingsActivity.class);
			startActivity(intent);
		}
			
}
