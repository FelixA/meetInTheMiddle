package com.example.meetinthemiddle;

import android.app.Activity;
import android.os.Bundle;

public class DisplayFAQ extends Activity {
	
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	System.out.println("Anlegung DisplayFAQ Klasse");
	setContentView(R.layout.activity_display_questions);
	}
}
