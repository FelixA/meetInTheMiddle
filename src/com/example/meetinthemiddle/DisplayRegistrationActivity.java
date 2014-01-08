package com.example.meetinthemiddle;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Logger;

import com.example.meetinthemiddle.personenverwaltung.dao.PersonDao;
import com.example.meetinthemiddle.personenverwaltung.domain.Person;
import com.example.meetinthemiddle.util.ConvertToMD5;
import com.example.meetinthemiddle.util.WebServiceClient;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.DatePicker;

public class DisplayRegistrationActivity extends Activity {
	@SuppressWarnings("unused")
	private static final String LOG_TAG = DisplayRegistrationActivity.class
			.getSimpleName();
	private PersonDao personDao;
	
	// Values used for registration process.
		private String mFirstName;
		private String mLastName;
		private String mEmail;
		private String mPassword;
		private String mInterests;
		private String mPhone;
		private Date mBirthday;
		private View mRegistrationFormView;
		private View mRegistrationStatusView;
		private TextView mRegistrationStatusMessageView;
		
		
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
		 * Keep track of the registration task to ensure we can cancel it if requested.
		 */
		private UserRegistrationTask mRegistrationTask = null;
		
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
		if (mRegistrationTask != null) {
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
		mRegistrationTask = new UserRegistrationTask();
		mRegistrationTask.execute((Void) null);
		}
	}
	

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		//TODO:Progressbar
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
		try {
			personDao.create(mFirstName,mLastName, mBirthday, mPhone, mEmail, converter.md5(mPassword), mInterests);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	@Override
	protected void onPostExecute(final Boolean success) {
		mRegistrationTask = null;
		showProgress(false);

		if (success) {
			Log.v("DisplayRegistrationTask","Herzlichen Glückwunsch, Ihr Konto wurde angelegt");
			displayLoginActivity(mRegistrationFormView);
		} 
			else {
			Log.e(DisplayRegistrationActivity.class.getName(),"Konto konnte nicht angelegt werden");
		}
	}

	@Override
	protected void onCancelled() {
		mRegistrationTask = null;
		showProgress(false);
	}
}
}