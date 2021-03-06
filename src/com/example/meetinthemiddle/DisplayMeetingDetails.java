package com.example.meetinthemiddle;

import com.example.meetinthemiddle.DisplayMap;
import com.example.meetinthemiddle.DisplayOverviewRouting;
import com.example.meetinthemiddle.R;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Location;

public class DisplayMeetingDetails extends android.support.v4.app.FragmentActivity implements android.location.LocationListener {

	  private LocationManager locationManager;
	  public String provider, name, tel, address;
	  public LatLng aktPos, middlePoint1;
	  double lat, lng;
	  


	  
	/** Called when the activity is first created. */

	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_display_meeting_details);

			    
	    try {
	    	Bundle extras = getIntent().getExtras();
	    	name = extras.getString("name");
	    	tel = extras.getString("tel");
	    	address = extras.getString("adresse");
	    	lat = extras.getDouble("lat");
	    	lng = extras.getDouble("lng");
	    	middlePoint1 = new LatLng(lat, lng);
	    	String durationPA = extras.getString("durationPersonA");
	    	String durationPB = extras.getString("durationPersonB");
	    	String distancePA = extras.getString("distancePersonA");
	    	String distancePB = extras.getString("distancePersonB");
		    TextView txtName = (TextView) findViewById(R.id.textName);
		    txtName.setText("Name: "+name);
		    TextView txtTel = (TextView) findViewById(R.id.textTel);
		    txtTel.setText("Telefon: "+tel);
		    TextView txtAddress = (TextView) findViewById(R.id.textAddress);
		    txtAddress.setText("Adresse: "+address);
		    System.out.println("Details: "+name+tel+address+lat+lng);
		    TextView txtDurationPA = (TextView) findViewById(R.id.textDurationPersonA);
		    TextView txtDurationPB = (TextView) findViewById(R.id.textDurationPersonB);
		    TextView txtDistancePA = (TextView) findViewById(R.id.textDistancePersonA);
		    TextView txtDistancePB = (TextView) findViewById(R.id.textDistancePersonB);
		    txtDurationPA.setText(durationPA);
		    txtDurationPB.setText(durationPB);
		    txtDistancePA.setText(distancePA);
		    txtDistancePB.setText(distancePB);
//		    CameraPosition cameraPosition = null;
//		    cameraPosition = new CameraPosition.Builder().target(middlePoint1).zoom(13).build();
//		    Log.i("cameraPosition", "Zoom (15)");
	    }
	    catch (Exception e)
	    {
	    	
	    }
	    loadActivity();

		
	  }
	  
	  private void loadActivity() {
		    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

	        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
	            Toast.makeText(this, "GPS ist aktiviert.", Toast.LENGTH_SHORT).show();
	            Criteria criteria = new Criteria();
	    	    provider = locationManager.getBestProvider(criteria, false);
	    	    Location location = locationManager.getLastKnownLocation(provider);
	    	    
	    	    if (location != null) {
	    		      System.out.println("Provider " + provider + " has been selected.");
	    		      onLocationChanged(location);
	    	    }
	    	    else
	    	    {
	    	    	System.out.println("Location = null: "+location);
;
	    	    }
	        }
	        else
	        {
	            showGPSDisabledAlertToUser();
	        }	
		}
	  
	  private void showGPSDisabledAlertToUser(){
	        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	        alertDialogBuilder.setMessage("GPS ist bislang deaktiviert. Wollen Sie es aktivieren?")
	        .setCancelable(false)
	        .setPositiveButton("GPS Einstellungen",
	                new DialogInterface.OnClickListener(){
	            public void onClick(DialogInterface dialog, int id){
	                Intent callGPSSettingIntent = new Intent(
	                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	                startActivity(callGPSSettingIntent);
	            }
	        });
	        alertDialogBuilder.setNegativeButton("Abbrechen",
	                new DialogInterface.OnClickListener(){
	            public void onClick(DialogInterface dialog, int id){
	                dialog.cancel();
	            }
	        });
	        AlertDialog alert = alertDialogBuilder.create();
	        alert.show();
	        
	    }

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub		
	}


	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		System.out.println("location: "+location);
		System.out.println(middlePoint1);

		System.out.println("ende");
		LatLng aktPos = middlePoint1;
	    GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
	            .getMap();

	    Marker aktuellePosition = map.addMarker(new MarkerOptions().position(aktPos)
	            .title("Treffpunkt"));
	    aktuellePosition.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

		
	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(aktPos, 12));
		
	}

	 	

} 