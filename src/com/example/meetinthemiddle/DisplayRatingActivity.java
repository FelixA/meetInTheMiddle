package com.example.meetinthemiddle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Toast;

public class DisplayRatingActivity extends Activity {
	
	private RatingBar ratingBar;
	private Button button;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_rating);
		
		addListenerOnRatingBar();
		addListenerOnButton();
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    Long id = extras.getLong("MeetingId");;
		}
	}
	public void acceptAndShare(View view){
		String message = "Text I want to share.";
				Intent share = new Intent(Intent.ACTION_SEND);
				share.setType("text/plain");
				share.putExtra(Intent.EXTRA_TEXT, message);

				startActivity(Intent.createChooser(share, "Title of the dialog the system will open"));
	}

	  public void addListenerOnRatingBar() {
		  
			ratingBar = (RatingBar) findViewById(R.id.RatingBar02);
		 
			//if rating value is changed,
			//display the current rating value in the result (textview) automatically
			ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
				public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
					String Rating = String.valueOf(rating);;
				}
			});
		  }	
	
	  public void addListenerOnButton() {
		  
			ratingBar = (RatingBar) findViewById(R.id.RatingBar02);
			button = (Button) findViewById(R.id.button);
		 
			//if click on me, then display the current rating value.
			button.setOnClickListener(new OnClickListener() {
		 
				@Override
				public void onClick(View v) {
		 
					Toast.makeText(DisplayRatingActivity.this,
						String.valueOf(ratingBar.getRating()),
							Toast.LENGTH_SHORT).show();
					//In DB Schreiben
				}
		 
			});
		 
		  }	
	
}

