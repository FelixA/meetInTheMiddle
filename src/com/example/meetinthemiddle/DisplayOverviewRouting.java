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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Location;

// Aufruf und Darstellung Ergebniss Klasse
//http://developer.android.com/guide/faq/commontasks.html#opennewscreen

public class DisplayOverviewRouting extends android.support.v4.app.FragmentActivity implements android.location.LocationListener {
	private TextView latituteField;
	  private TextView longitudeField;
	  private LocationManager locationManager;
	  private String provider;
	  public LatLng aktPos;

	  
	/** Called when the activity is first created. */

	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_display_overview_routing);
	   
	    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
		      latituteField.setText("Location not available");
		      longitudeField.setText("Location not available");
	    }
		//onLocationChanged(location);

	    
	    
	    
	  }
//
//
//	@Override
//	public void onLocationChanged(Location location) {
//		double lat = (location.getLatitude());
//	    double lng = (location.getLongitude());
//		latituteField.setText(String.valueOf(lat));
//		longitudeField.setText(String.valueOf(lng));
//		LatLng aktPos = new LatLng(lat, lng);
//	    GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
//	            .getMap();
//	        Marker aktuellePosition = map.addMarker(new MarkerOptions().position(aktPos)
//	            .title("Aktuelle Position"));
//	        // Move the camera instantly to aktPos with a zoom of 15.
//	        map.moveCamera(CameraUpdateFactory.newLatLngZoom(aktPos, 12));
//		
//	}
//

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
		double lat = location.getLatitude();
		double lng = location.getLongitude();
		System.out.println("lat/lng: "+lat+"/"+lng);
		//latituteField.setText(String.valueOf(lat));
		//longitudeField.setText(String.valueOf(lng));
		System.out.println("ende");
		LatLng aktPos = new LatLng(lat, lng);
	    GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
	            .getMap();
	    Marker aktuellePosition = map.addMarker(new MarkerOptions().position(aktPos)
	            .title("Aktuelle Position"));
		
	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(aktPos, 12));
		
	}
	  
	  public void showMapRouting(View view){
		    Toast.makeText(getApplicationContext(), "showMapRouting", Toast.LENGTH_LONG).show();
	         Intent i = new Intent(DisplayOverviewRouting.this, DisplayMap.class);
	         startActivity(i);
	  }
	 	

} 