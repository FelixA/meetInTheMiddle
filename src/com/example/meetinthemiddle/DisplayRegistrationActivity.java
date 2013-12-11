package com.example.meetinthemiddle;

import java.util.logging.Logger;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.example.meetinthemiddle.DisplayLoginActivity.UserLoginTask;
import com.example.meetinthemiddle.personenverwaltung.dao.PersonDao;
import com.example.meetinthemiddle.personenverwaltung.domain.Person;
import com.example.meetinthemiddle.util.Constants;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.text.Editable;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.DatePicker;

public class DisplayRegistrationActivity extends Activity {
	private static final Logger logger = Logger.getLogger( DisplayRegistrationActivity.class.getName() );
	private PersonDao personDao;
	DriverManagerDataSource dmdataSource;
	String url = Constants.uri;
	String driver = Constants.driver;
	
	// Values used for registration process.
		private String mFirstName;
		private String mLastName;
		private String mEmail;
		private String mPassword;
		private String mInterests;
		private String mPhone;
		private Person person;
		private View mRegistrationFormView;
		
		private int year;
		private int month;
		private int day;
		
		private DatePicker dpResult;

		
		// UI references.
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
		
		public void setDbConnection() {
			try {
				Class.forName(oracle.jdbc.driver.OracleDriver.class.getName());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			dmdataSource.setDriverClassName(driver);
			dmdataSource.setUrl(url);
			dmdataSource.setUsername("eBW13Db02");
			dmdataSource.setPassword("eBW13Db");
			personDao.setDataSource(dmdataSource);
		}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_registration);
		personDao = new PersonDao();
		dmdataSource = new DriverManagerDataSource();
		setDbConnection();
		
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
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mInterests = mInterestsView.getText().toString();
		mPhone = mPhoneView.getText().toString();
		
		// Show a progress spinner, and kick off a background task to
		// perform the user login attempt.
//		mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
//		showProgress(true);
		mAuthTask = new UserRegistrationTask();
		mAuthTask.execute((Void) null);
	}
	
	public void displayLoginActivity(View view) {
		Intent intent = new Intent(this, DisplayLoginActivity.class);
		startActivity(intent);
	}

	private DatePickerDialog.OnDateSetListener datePickerListener 
    = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;
			// set selected date into textview
			StringBuilder bla;
			bla = new StringBuilder().append(month + 1)
					.append("-").append(day).append("-").append(year)
					.append(" ");
			// set selected date into datepicker also
			dpResult.init(year, month, day, null);

}
};

/**
 * Represents an asynchronous registration task to register
 * the user.
 */
public class UserRegistrationTask extends AsyncTask<Void, Void, Boolean> {
	@Override
	protected Boolean doInBackground(Void... params) {
		personDao.create("Felix", "Albert", mEmail, "1", mPassword);
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