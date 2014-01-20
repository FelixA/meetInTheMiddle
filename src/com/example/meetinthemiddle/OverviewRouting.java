package com.example.meetinthemiddle;

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

public class OverviewRouting extends android.support.v4.app.FragmentActivity implements android.location.LocationListener {
	private TextView latituteField;
	  private TextView longitudeField;
	  private LocationManager locationManager;
	  private String provider;
	  public LatLng aktPos;

	  
	/** Called when the activity is first created. */

	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.overview_routing);
	    latituteField = (TextView) findViewById(R.id.TextView02);
	    longitudeField = (TextView) findViewById(R.id.TextView04);	    
	    // Get the location manager
	    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    // Define the criteria how to select the location provider -> use
	    // default
	    Criteria criteria = new Criteria();
	    provider = locationManager.getBestProvider(criteria, false);
	    Location location = locationManager.getLastKnownLocation(provider);
	    // Initialize the location fields
	    System.out.println("Prüfung location");
	    if (location != null) {
	      System.out.println("Provider " + provider + " has been selected.");
	      onLocationChanged(location);
	    } else {
	    	//Setzen der Werte für Felder in Layout
	      latituteField.setText("Location not available");
	      longitudeField.setText("Location not available");
	    }
		boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// check if enabled and if not send user to the GSP settings
		// Better solution would be to display a dialog and suggesting to 
		// go to the settings
		if (!enabled) {
			//Alternativ Button einbinden, über den man GPS manuell aktivieren kann.
			String Text = "Ihr GPS ist bislang nicht aktiviert. Um eine korrekte Routenführung zu gewährleisten, aktivieren Sie ihr GPS bitte in den Einstellungen ihres Geräts.";
			Toast.makeText( getApplicationContext(), Text, Toast.LENGTH_SHORT).show();
			AlertDialog.Builder alert_box=new AlertDialog.Builder(this);
			alert_box.setIcon(R.drawable.house_flag);
			alert_box.setMessage("Ihr GPS ist bislang nicht aktiviert. Um eine korrekte Routenführung zu gewährleisten, aktivieren Sie ihr GPS bitte in den Einstellungen ihres Geräts.");
			alert_box.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			   
			   @Override
			   public void onClick(DialogInterface dialog, int which) {
				   Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				   startActivity(intent);
			   }
			  });
			alert_box.setNegativeButton("No", new DialogInterface.OnClickListener() {
			   
			   @Override
			   public void onClick(DialogInterface dialog, int which) {
			    // TODO Auto-generated method stub
			    Toast.makeText(getApplicationContext(), "GPS weiterhin nicht aktiviert", Toast.LENGTH_LONG).show();
			   }
			  });
			alert_box.show();
		}
		onLocationChanged(location);
	  }

   /*   private void centerMapOnMyLocation(Location location) {
  	    double lat = (location.getLatitude());
  	    double lng = (location.getLongitude());
		LatLng aktPos = new LatLng(lat, lng);
  	    GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
	            .getMap();
	        Marker aktuellePosition = map.addMarker(new MarkerOptions().position(aktPos)
	            .title("Aktuelle Position"));
	        // Move the camera instantly to aktPos with a zoom of 15.		    
		    //map.moveCamera(CameraUpdateFactory.newLatLngZoom(aktPos, 12));		    
    	}
	 */ 
	  public void showMapRouting(View view){
		    Toast.makeText(getApplicationContext(), "showMapRouting", Toast.LENGTH_LONG).show();
	         Intent i = new Intent(OverviewRouting.this, DisplayMap.class);
	         startActivity(i);
	  }
	  
	  /* Request updates at startup */
	  @Override
	  protected void onResume() {
	    super.onResume();
	    locationManager.requestLocationUpdates(provider, 400, 1, this);
	  }

	  /* Remove the locationlistener updates when Activity is paused */
	  @Override
	  protected void onPause() {
	    super.onPause();
	    locationManager.removeUpdates(this);
	  }
	  

	  @Override
	  public void onLocationChanged(Location location) {
	    double lat = (location.getLatitude());
	    double lng = (location.getLongitude());
		latituteField.setText(String.valueOf(lat));
		longitudeField.setText(String.valueOf(lng));
		LatLng aktPos = new LatLng(lat, lng);
  	    GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
	            .getMap();
	        Marker aktuellePosition = map.addMarker(new MarkerOptions().position(aktPos)
	            .title("Aktuelle Position"));
	        // Move the camera instantly to aktPos with a zoom of 15.
	        map.moveCamera(CameraUpdateFactory.newLatLngZoom(aktPos, 12));
		   /* CameraPosition cameraPosition = new CameraPosition.Builder().target(
		    		aktPos).zoom(12).build();
		    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		    map.getUiSettings().setCompassEnabled(true);
		    map.getUiSettings().setMyLocationButtonEnabled(true);
		    */
	  }

	  @Override
	  public void onStatusChanged(String provider, int status, Bundle extras) {
	    // TODO Auto-generated method stub

	  }

	  @Override
	  public void onProviderEnabled(String provider) {
	    Toast.makeText(this, "Enabled new provider " + provider,
	        Toast.LENGTH_SHORT).show();

	  }

	  @Override
	  public void onProviderDisabled(String provider) {
	    Toast.makeText(this, "Disabled provider " + provider,
	        Toast.LENGTH_SHORT).show();
	  }
	} 