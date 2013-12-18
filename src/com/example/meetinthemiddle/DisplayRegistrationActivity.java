package com.example.meetinthemiddle;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.Calendar;
import java.util.logging.Logger;

import com.example.meetinthemiddle.DisplayLoginActivity.UserLoginTask;
import com.example.meetinthemiddle.personenverwaltung.dao.PersonDao;
import com.example.meetinthemiddle.personenverwaltung.domain.Person;
import com.example.meetinthemiddle.util.Constants;
import com.example.meetinthemiddle.util.ConvertToMD5;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.text.Editable;
import android.view.Menu;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.DatePicker;

public class DisplayRegistrationActivity extends Activity {
	private static final Logger logger = Logger.getLogger( DisplayRegistrationActivity.class.getName() );
	private PersonDao personDao;
	
	// Values used for registration process.
		private String mFirstName;
		private String mLastName;
		private String mEmail;
		private String mPassword;
		private String mInterests;
		private String mPhone;
		private Date mBirthday;
		private Person person;
		private View mRegistrationFormView;
		private View mRegistrationStatusView;
		private TextView mRegistrationStatusMessageView;
		
		private int year;
		private int month;
		private int day;
		
		// UI references.
		private EditText mFirstNameView;
		private EditText mLastNameView;
		private EditText mEmailView;
		private EditText mPasswordView;
		private EditText mInterestsView;
		private EditText mPhoneView;
		private DatePicker mBirthdayView;
		
	public static final String EXTRA_EMAIL = "com.example.android.registration.extra.EMAIL";
	static final int DATE_DIALOG_ID = 999;

		
		/**
		 * Keep track of the login task to ensure we can cancel it if requested.
		 */
		private UserRegistrationTask mAuthTask = null;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_registration);
		personDao = new PersonDao(this);
		
		// Get the EMail
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);
		
//		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setText(mPassword);
		
		mInterestsView = (EditText) findViewById(R.id.interests);
		mInterestsView.setText(mInterests);
		
		mPhoneView = (EditText) findViewById(R.id.phone);
		mPhoneView.setText(mPhone);
		
		mFirstNameView = (EditText) findViewById(R.id.firstName);
		mFirstNameView.setText(mFirstName);
		
		mLastNameView = (EditText) findViewById(R.id.lastName);
		mLastNameView.setText(mLastName);
		
		mBirthdayView = (DatePicker) findViewById(R.id.birthday);
//		(DatePicker) findViewById (R.id.birthday);
//		mBirthdayView.set
		
		mRegistrationStatusView = findViewById(R.id.registration_status);
		mRegistrationStatusMessageView = (TextView) findViewById(R.id.registration_status_message);
		
		findViewById(R.id.btnRegister).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptRegistration();
					}
				});
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registration, menu);
		return true;
	}
	
	public void attemptRegistration(){
		if (mAuthTask != null) {
			return;
		}
		
		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);
		
		// Store values at the time of the login attempt.
		mFirstName = mFirstNameView.getText().toString();
		mLastName = mLastNameView.getText().toString();
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mInterests = mInterestsView.getText().toString();
		mPhone = mPhoneView.getText().toString();
		mBirthday = new Date(mBirthdayView.getYear() - 1900, mBirthdayView.getMonth(), mBirthdayView.getDayOfMonth());
		
		
		boolean cancel = false;
		View focusView = null;
		
		//TODO: Check vor valid values
		
		// Show a progress spinner, and kick off a background task to
		// perform the user login attempt.
		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
		mRegistrationStatusMessageView.setText(R.string.registration_progress_spinner);
		showProgress(true);
		mAuthTask = new UserRegistrationTask();
		mAuthTask.execute((Void) null);
		}
	}
	

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		System.out.println("Progress bar should be seen now");
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mRegistrationStatusView.setVisibility(View.VISIBLE);
			mRegistrationStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mRegistrationStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mRegistrationStatusView.setVisibility(View.VISIBLE);
			mRegistrationStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mRegistrationStatusView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mRegistrationStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mRegistrationFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	public void displayLoginActivity(View view) {
		Intent intent = new Intent(this, DisplayLoginActivity.class);
		startActivity(intent);
	}

/**
 * Represents an asynchronous registration task to register
 * the user.
 */
public class UserRegistrationTask extends AsyncTask<Void, Void, Boolean> {
	ConvertToMD5 converter = new ConvertToMD5();
	@Override
	protected Boolean doInBackground(Void... params) {
		personDao.create(mFirstName,mLastName, new java.sql.Date(mBirthday.getTime()), mPhone, mEmail, "1", converter.md5(mPassword), mInterests);
		return true;
	}

	@Override
	protected void onPostExecute(final Boolean success) {
		mAuthTask = null;
//		showProgress(false);

		if (success) {
			System.out.println("Herzlichen Glückwunsch, Ihr Konto wurde angelegt");
			displayLoginActivity(mRegistrationFormView);
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