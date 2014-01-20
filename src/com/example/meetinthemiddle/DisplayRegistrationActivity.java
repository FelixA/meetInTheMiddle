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
import com.google.android.gcm.GCMRegistrar;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.DatePicker;
import android.widget.Toast;

/**
 * 
 * @author Felix
 * 
 */
public class DisplayRegistrationActivity extends Activity {
	// TODO: check for unique email
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

	// Replace the xxx with the project id generated from the Google console
	// when
	// you defined a Google APIs project.
	private static final String PROJECT_ID = "355205271798";

	// This tag is used in Log.x() calls
	private static final String TAG = "MainActivity";

	// This string will hold the lengthy registration id that comes
	// from GCMRegistrar.register()
	private String regId = "";

	// These strings are hopefully self-explanatory
	private String registrationStatus = "Not yet registered";
	private String broadcastMessage = "No broadcast message";

	// This intent filter will be set to filter on the string
	// "GCM_RECEIVED_ACTION"
	IntentFilter gcmFilter;

	// textviews used to show the status of our app's registration, and the
	// latest
	// broadcast message.
	TextView tvRegStatusResult;
	TextView tvBroadcastMessage;

	// This broadcastreceiver instance will receive messages broadcast
	// with the action "GCM_RECEIVED_ACTION" via the gcmFilter

	// A BroadcastReceiver must override the onReceive() event.
	private BroadcastReceiver gcmReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			broadcastMessage = intent.getExtras().getString("gcm");

			if (broadcastMessage != null) {
				// display our received message
				tvBroadcastMessage.setText(broadcastMessage);
			}
		}
	};

	public static final String EXTRA_EMAIL = "com.example.android.registration.extra.EMAIL";
	static final int DATE_DIALOG_ID = 999;

	/**
	 * Keep track of the registration task to ensure we can cancel it if
	 * requested.
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

		// mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
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

		tvBroadcastMessage = (TextView) findViewById(R.id.tv_message);
		tvRegStatusResult = (TextView) findViewById(R.id.tv_reg_status_result);

		// Create our IntentFilter, which will be used in conjunction with a
		// broadcast receiver.
		gcmFilter = new IntentFilter();
		gcmFilter.addAction("GCM_RECEIVED_ACTION");


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

	public void attemptRegistration() {
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
		mBirthday = new Date(mBirthdayView.getYear() - 1900,
				mBirthdayView.getMonth(), mBirthdayView.getDayOfMonth());

		boolean cancel = false;
		View focusView = null;

		// TODO: Check vor valid values

		// Show a progress spinner, and kick off a background task to
		// perform the user login attempt.
		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			mRegistrationStatusMessageView
					.setText(R.string.registration_progress_spinner);
			showProgress(true);
			mRegistrationTask = new UserRegistrationTask();
			mRegistrationTask.execute((Void) null);
		}
	}

	// This registerClient() method checks the current device, checks the
	// manifest for the appropriate rights, and then retrieves a registration id
	// from the GCM cloud. If there is no registration id, GCMRegistrar will
	// register this device for the specified project, which will return a
	// registration id.
	public String registerClient() {

		try {
			// Check that the device supports GCM (should be in a try / catch)
			GCMRegistrar.checkDevice(this);

			// Check the manifest to be sure this app has all the required
			// permissions.
			GCMRegistrar.checkManifest(this);

			// Get the existing registration id, if it exists.
			regId = GCMRegistrar.getRegistrationId(this);

			if (regId.equals("")) {

				registrationStatus = "Registering...";
				System.out.println(registrationStatus);
				tvRegStatusResult.setText(registrationStatus);

				// register this device for this project
				GCMRegistrar.register(this, PROJECT_ID);
				regId = GCMRegistrar.getRegistrationId(this);

				registrationStatus = "Registration Acquired";

				// This is actually a dummy function. At this point, one
				// would send the registration id, and other identifying
				// information to your server, which should save the id
				// for use when broadcasting messages.
				sendRegistrationToServer();

			} else {

				registrationStatus = "Already registered";
			}
		} catch (Exception e) {

			e.printStackTrace();
			registrationStatus = e.getMessage();
		}
		Log.d(TAG, registrationStatus);
		tvRegStatusResult.setText(registrationStatus);
		// This is part of our CHEAT. For this demo, you'll need to
		// capture this registration id so it can be used in our demo web
		// service.
		Log.d(TAG, regId);
		return regId;
	}

	private void sendRegistrationToServer() {
		// This is an empty placeholder for an asynchronous task to post the
		// registration
		// id and any other identifying information to your server.
	}

	// If the user changes the orientation of his phone, the current activity
	// is destroyed, and then re-created. This means that our broadcast message
	// will get wiped out during re-orientation.
	// So, we save the broadcastmessage during an onSaveInstanceState()
	// event, which is called prior to the destruction of the activity.
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {

		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putString("BroadcastMessage", broadcastMessage);

	}

	// When an activity is re-created, the os generates an
	// onRestoreInstanceState()
	// event, passing it a bundle that contains any values that you may have put
	// in during onSaveInstanceState()
	// We can use this mechanism to re-display our last broadcast message.

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {

		super.onRestoreInstanceState(savedInstanceState);

		broadcastMessage = savedInstanceState.getString("BroadcastMessage");
		tvBroadcastMessage.setText(broadcastMessage);

	}

	// If our activity is paused, it is important to UN-register any
	// broadcast receivers.
	@Override
	protected void onPause() {

		unregisterReceiver(gcmReceiver);
		super.onPause();
	}

	// When an activity is resumed, be sure to register any
	// broadcast receivers with the appropriate intent
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(gcmReceiver, gcmFilter);

	}

	// NOTE the call to GCMRegistrar.onDestroy()
	@Override
	public void onDestroy() {

		GCMRegistrar.onDestroy(this);

		super.onDestroy();
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// TODO:Progressbar
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
							mRegistrationStatusView
									.setVisibility(show ? View.VISIBLE
											: View.GONE);
						}
					});

			mRegistrationStatusView.setVisibility(View.VISIBLE);
			mRegistrationStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mRegistrationStatusView
									.setVisibility(show ? View.GONE
											: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mRegistrationStatusView.setVisibility(show ? View.VISIBLE
					: View.GONE);
			mRegistrationFormView
					.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	public void displayLoginActivity(View view) {
		Intent intent = new Intent(this, DisplayLoginActivity.class);
		startActivity(intent);
	}

	/**
	 * Represents an asynchronous registration task to register the user.
	 */
	public class UserRegistrationTask extends AsyncTask<Void, Void, Boolean> {
		ConvertToMD5 converter = new ConvertToMD5();

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				personDao.create(mFirstName, mLastName, mBirthday, mPhone,
						mEmail, converter.md5(mPassword), mInterests,registerClient());
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
				Toast.makeText(DisplayRegistrationActivity.this, "Herzlichen Glückwunsch, Ihr Konto wurde angelegt", Toast.LENGTH_LONG).show();
//				Log.v("DisplayRegistrationTask",
//						"Herzlichen Glückwunsch, Ihr Konto wurde angelegt");
				displayLoginActivity(mRegistrationFormView);
			} else {
				Log.e(DisplayRegistrationActivity.class.getName(),
						"Konto konnte nicht angelegt werden");
			}
		}

		@Override
		protected void onCancelled() {
			mRegistrationTask = null;
			showProgress(false);
		}
	}
}