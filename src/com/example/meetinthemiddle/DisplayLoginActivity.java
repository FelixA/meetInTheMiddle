package com.example.meetinthemiddle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.example.meetinthemiddle.personenverwaltung.PersonMapper;
import com.example.meetinthemiddle.personenverwaltung.domain.Person;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class DisplayLoginActivity extends Activity {
	DriverManagerDataSource dmdataSource = new DriverManagerDataSource();
	DataSource dataSource;
	String url = "jdbc:oracle:thin:@iwi-w-vm-dbo.hs-karlsruhe.de:1521:oracledbwi";
	String driver = "oracle.jdbc.driver.OracleDriver";
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	// private static final String[] DUMMY_CREDENTIALS = new String[] {
	// "felix@felix", "test" };
	private List<String> DUMMY_CREDENTIALS = new ArrayList<String>();
	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;
	private List<Person> person;

	private String dbEmail;
	private String dbPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private JdbcTemplate jdbcTemplate;
	private PersonMapper personMapper;

	@Autowired(required = true)
	public void setDataSource(DataSource ds) {
		dataSource = ds;
	}

	public void create(String firstName, String lastName, String email,
			String kontaktliste, String password) {
		JdbcTemplate insert = new JdbcTemplate(dataSource);
		insert.update(
				"INSERT INTO PERSON (ID, VORNAME, NACHNAME,EMAIL,KONTAKTLISTE_FK,PASSWORD) VALUES(?,?,?,?,?,?)",
				new Object[] { 2, firstName, lastName, email, kontaktliste,
						password });
	}

	public List<Person> validate(String email, String password) {
		System.out.println("HERE I AM");
		JdbcTemplate select = new JdbcTemplate(dataSource);
		return select
				.query("Select EMAIL, PASSWORD from Person where EMAIL = ? AND PASSWORD = ?);",
						personMapper);
		// System.out.println(person.toString());

		// select.execute("Select EMAIL, PASSWORD from Person where EMAIL = ? AND PASSWORD = ?);");
	}

	public List<Person> selectAll() {
		JdbcTemplate select = new JdbcTemplate(dataSource);
		return select.query("select * from PERSON", new PersonMapper());
	}

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
		setDataSource(dmdataSource);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		System.out.println("Created");

		// DUMMY_CREDENTIALS.add("felix@felix");
		// DUMMY_CREDENTIALS.add("test");
		super.onCreate(savedInstanceState);
		setDbConnection();

		setContentView(R.layout.activity_display_login);

		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.display_login, menu);
		return true;
	}

	/**
	 * Gives the possibility to register the account.
	 */
	public void attemptRegistration() {
		displayRegistrationActivity(mLoginFormView, mEmail);
	}
	/**
	 * Attempts to sign in the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	public void displayRegistrationActivity(View view, String email){
		Intent intent = new Intent(this, DisplayRegistrationActivity.class);
		startActivity(intent);
	}
	
	public void displayMainActivity(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				// Simulate network access.
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return false;
			}
			person = selectAll();
			System.out.println("hallo" + person.get(0).getEmail());
			for (int i = 0; i <= person.size(); i++) {
				if (person.get(i).getEmail().equals(mEmail)) {

					// Account exists, return true if the password matches.
					if (person.get(i).getPassword().equals(mPassword)) {
						return true;
					}
				}
			}
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				displayMainActivity(mLoginFormView);
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
