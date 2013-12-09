package com.example.meetinthemiddle;

import com.example.meetinthemiddle.DisplayLoginActivity.UserLoginTask;
import com.example.meetinthemiddle.personenverwaltung.domain.Person;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class DisplayRegistrationActivity extends Activity {
	// Values used for registration process.
		private String mEmail;
		private String mPassword;
		private Person person;
		private View mRegistrationFormView;
		
		/**
		 * Keep track of the login task to ensure we can cancel it if requested.
		 */
		private UserLoginTask mAuthTask = null;
		

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_registration);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registration, menu);
		return true;
	}
	
	public void displayMainActivity(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}


/**
 * Represents an asynchronous registration task to register
 * the user.
 */
public class UserRegistrationTask extends AsyncTask<Void, Void, Boolean> {
	@Override
	protected Boolean doInBackground(Void... params) {
		return null;
	
	}


	@Override
	protected void onPostExecute(final Boolean success) {
		mAuthTask = null;
//		showProgress(false);

		if (success) {
			System.out.println("Herzlichen Glückwunsch, Ihr Konto wurde angelegt");
			displayMainActivity(mRegistrationFormView);
		} 
			else {
			System.out.println("Fehler");
		}
	}

	@Override
	protected void onCancelled() {
		mAuthTask = null;
//		showProgress(false);
	}
}
}