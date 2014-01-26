package com.example.meetinthemiddle;

import java.util.ArrayList;
import java.util.Date;

import com.example.meetinthemiddle.meetingverwaltung.domain.Meeting;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
 
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.UserDictionary.Words;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.view.View;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class DisplayMap extends android.support.v4.app.FragmentActivity implements android.location.LocationListener{
	private LocationManager locationManager;
	private String provider, duration, namePoint, telNumber, address;
	//private mapsTask mTask = null;
	private progressTask prgsTask = null;
	private placesTask pTask = null;
	public Location location;
	String[] detailsArr = new String[10];
	public double distance;
	boolean checkWay = false;
	public LatLng aktPos, destPos, poi, middlePoint, searchPoint, medianPoint, trainStation, pTaskPOI;
	GoogleMap map;
    ArrayList<LatLng> markerPoints;
    TextView tvDistanceDuration;
    public PolylineOptions rectLine = null;
    public ProgressDialog barProgressDialog;
    public Handler updateBarHandler;
    ProgressBar bar = null;
    ProgressDialog ringProgressDialog;
    public boolean beta = false;
    public String details="";
	public final String rankingByDistance = "&rankby=distance"; //Parameter radius hierzu deaktivieren, Ergebnisse werden nach distanz geordnet
	public final String rankingByProminence = "&rankby=prominence";
	double distancePersonA, distancePersonB;
	String durationPersonB, durationPersonA;

    //Offizielle Parameter aus Übergabe
    String locationPers1, locationPers2, uhrzeit, mode, mode2, types;
    long modePers1, modePers2, lokalitaet;
    
	
	public void onCreate(Bundle savedInstanceState)
	   {
		System.out.println("OnCreate in DisplayMap.java");
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.activity_display_routingmap);
	      
	      /*
	       * Übergebene Parameter aus DisplayRequestActivity und DisplayMessagesActivity
	       */			

	      System.out.println("DisplayMap: Fehlerquelle 9");
			Bundle extras = getIntent().getExtras();
			locationPers1 = extras.getString("PositionPerson1");
			locationPers2 = extras.getString("PositionPerson2");
			modePers1 = extras.getLong("ModusPerson1");
			modePers2 = extras.getLong("ModusPerson2");
			lokalitaet = extras.getLong("lokalitaet");
			uhrzeit = extras.getString("Uhrzeit");
			Log.e("DisplayMap/Übergabeparameter: ", "Parameter okay? "+locationPers1+", "+locationPers2+", "+modePers1+", "+modePers2+", "+lokalitaet+", "+uhrzeit);
			formatParameters();
			Log.e("DisplayMap/Übergabeparameter: ", "Parameter okay nach Formatierung? "+aktPos+", "+destPos+", "+mode+", "+mode2+", "+types+", "+uhrzeit);
		    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		    //loadActivity();
		    
		    Criteria criteria = new Criteria();
		    provider = locationManager.getBestProvider(criteria, false);
		    Location location = locationManager.getLastKnownLocation(provider);
		    // Initialize the location fields
		    if (location != null) {
		      System.out.println("Provider " + provider + " has been selected.");
			    bar = (ProgressBar) this.findViewById(R.id.progressBar);
			    //new progressTask().execute();
			    centerMapOnMyLocation(location);

		    } else {
		    	//Setzen der Werte für Felder in Layout
			    Toast.makeText(getApplicationContext(), "Lat + Lng konnten nicht ermittelt werden.", Toast.LENGTH_LONG).show();
		    }
		    try {
		   final Button buttonDetails = (Button) findViewById(R.id.detailsBtn);
		    buttonDetails.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i = new Intent(DisplayMap.this,
							DisplayMeetingDetails.class);
					try
					{						
						if (durationPersonA == "")
						{
							durationPersonA = "Zeit konnte nicht ermittelt werden";
						}
						if (durationPersonB == "")
						{
							durationPersonB = "Zeit konnte nicht ermittelt werden";
						}
						
						if (namePoint == "" || namePoint == null)
						{
							namePoint = "Konnte nicht ermittelt werden";
						}
						if (telNumber == "" || telNumber == null)
						{
							telNumber = "Konnte nicht ermittelt werden";
						}
						if (address == "" || address == null)
						{
							address = "Konnte nicht ermittelt werden";
						}
						try
						{
							i.putExtra("durationPersonA", durationPersonA);
							i.putExtra("durationPersonB", durationPersonB);
							i.putExtra("distancePersonA", distancePersonA);
							i.putExtra("distancePersonA", distancePersonB);
	
							System.out.println("Mockdaten");
							i.putExtra("name", namePoint);
							i.putExtra("tel", telNumber);
							i.putExtra("adresse", address);
							System.out.println("Konvertierung latlng");
							double lat = middlePoint.latitude;
							double lng = middlePoint.longitude;
							System.out.println("Übergabe an extras");
							i.putExtra("lat", lat);
							i.putExtra("lng", lng);
							System.out.println("StartActivity");
							startActivity(i);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					catch (Exception e)
					{
						Log.e("DisplayMap", "Crash nach Click auf buttonDetails.");
						e.printStackTrace();
					}
				}});
		    }
		    catch(Exception e)
		    {
		    	Log.e("onClick Details", "Problem");
		    	e.printStackTrace();
		    }
		    
            final Button button = (Button) findViewById(R.id.startrouting);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr="+aktPos.latitude+","+aktPos.longitude+"&daddr="+middlePoint.latitude+","+middlePoint.longitude));
        		    startActivity(intent);
                }});               
	   }
	
	public void setIcon(String[] detailsArr) {
		InputStream in = null;
		URL url = null;
		try
		{
			System.out.println("IconURL: "+detailsArr[3]);
		    url = new URL(detailsArr[3]);
		}
		catch(MalformedURLException e)
		{
			Log.e("setIcon", "Problems with URL");
		}
		try
		{
		    System.out.println("Icon aus setIcon(): "+detailsArr[3]);
		    URLConnection urlConn = url.openConnection();
		    Log.i("setIcon", "openConnection abgeschlossen");
		    HttpURLConnection httpConn = (HttpURLConnection) urlConn;
		    httpConn.connect();
		    Log.i("setIcon", "Verbindung abgeschlossen");
		    in = httpConn.getInputStream();
		}
		catch (IOException e)
		{
			System.out.println("Icon will nicht so wie ich :(");
		    e.printStackTrace();
		}
		Bitmap bmpimg = BitmapFactory.decodeStream(in);
		//ImageView iv = ((ImageView) findViewById(R.id.imageViewPOI));
		//iv.setImageBitmap(bmpimg);
		
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public class placesTask extends AsyncTask<Void, Void, String>{
    	@Override
    	protected String doInBackground(Void... params) {
    		try
    		{
    		Log.i("############placesTask", "REQUEST");
    		//funktioniert: https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=49.016557,8.390047&rankby=prominence&radius=1000&types=movie_theater&sensor=false&key=AIzaSyCW2yIWAH8FtzCwhYKAazZnFIi6Fc71trA
    	    int radius = (int) (distance/3);
    		String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/xml?location="
    				+ middlePoint.latitude + "," + middlePoint.longitude + rankingByDistance +"&radius="+radius
    				+ "&types=" + types + "&sensor=false&key=AIzaSyCmpO5pMHGahkcZg5TqAkXQ_P1xiNE6VKs";//AIzaSyCW2yIWAH8FtzCwhYKAazZnFIi6Fc71trA";

    	    Log.i("meetingPoint", url);    	    

    	    HttpURLConnection conn = null;
    	    StringBuilder sbResults = new StringBuilder();
    	    String reference = "";
    	    try
    	    {
    	    	//Muss auf den Knoten navigieren
    	        try {
    	            HttpClient httpClient = new DefaultHttpClient();
    	            HttpContext localContext = new BasicHttpContext();
    	            HttpPost httpPost = new HttpPost(url);
    	            HttpResponse response = httpClient.execute(httpPost, localContext);
    	            InputStream in = response.getEntity().getContent();
    	            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
    	                    .newDocumentBuilder();
    	            Document doc = builder.parse(in);
    	            
    	            NodeList nl1, nl2, nl3;
    	            String urlProminence = "";
    	            try 
    	            {
    	            	nl1 = doc.getElementsByTagName("PlaceSearchResponse");
    	            	Node responseNode = nl1.item(0);
    	            	nl2 = responseNode.getChildNodes();
    	            	Node statusNode = nl2.item(getNodeIndex(nl2, "status"));
    	            	String resultsStr = statusNode.getTextContent();
    	            	System.out.println("Results in URL? " +resultsStr );
    	            	if (resultsStr == "ZERO_RESULTS");
    	            	{
    	            		urlProminence = "https://maps.googleapis.com/maps/api/place/nearbysearch/xml?location="
    	            				+ middlePoint.latitude + "," + middlePoint.longitude + rankingByProminence +"&radius="+radius
    	            				+ "&types=" + types + "&sensor=false&key=AIzaSyCmpO5pMHGahkcZg5TqAkXQ_P1xiNE6VKs";//AIzaSyCW2yIWAH8FtzCwhYKAazZnFIi6Fc71trA";
    	            		System.out.println("UrlProminence: "+urlProminence);
        	            	httpClient = new DefaultHttpClient();
            	            localContext = new BasicHttpContext();
            	            httpPost = new HttpPost(urlProminence);
            	            response = httpClient.execute(httpPost, localContext);
            	            in = response.getEntity().getContent();
            	            builder = DocumentBuilderFactory.newInstance()
            	                    .newDocumentBuilder();
            	            doc = builder.parse(in);
            	            System.out.println("Doc ist gefüllt nach zweiten Versuch mit Prominence-Faktor");
    	            	}
    	            }
    	            catch(Exception e)
    	            {
    	            	System.out.println("Zero Results");
    	            }
    	            
    	            
    	            nl1 = null;
    	            nl2 = null;
    	            System.out.println("Doc ist gefüllt");
        	        nl1 = doc.getElementsByTagName("location");
        	        System.out.println("Länge Node Nl1 = "+nl1.getLength());
        	        Node locationNode = nl1.item(0);
        	        nl2 = locationNode.getChildNodes();
        	        Node latNode = nl2.item(getNodeIndex(nl2, "lat"));
        	        double lat = Double.parseDouble(latNode.getTextContent());
        	        Node lngNode = nl2.item(getNodeIndex(nl2, "lng"));
        	        double lng = Double.parseDouble(lngNode.getTextContent());
        	        System.out.println("lat/lng:"+lat+"/"+lng);
        	        nl3 = doc.getElementsByTagName("reference");
        	        System.out.println("Anzahl Elemente reference: "+nl3.getLength());
        	        Node referenceNode = nl3.item(0);
        	        reference = referenceNode.getTextContent();
        	        pTaskPOI = new LatLng(lat, lng);

      	        } catch (Exception e) {
    	            e.printStackTrace();
    	        }

    	    	//Mittels Reference auf Details zugreifen
    	    	String urlDetails = "https://maps.googleapis.com/maps/api/place/details/xml"
    	    	+ "?reference="+reference 
    	    	+ "&sensor=false&key=AIzaSyCmpO5pMHGahkcZg5TqAkXQ_P1xiNE6VKs";//AIzaSyCW2yIWAH8FtzCwhYKAazZnFIi6Fc71trA";
    	    	//Log.i("urlDetails", ""+urlDetails);
    	    	detailsArr = null;
    	    	try
    	    	{

    	    		System.out.println("GetDetails: "+urlDetails);
    	    		HttpClient httpClient = new DefaultHttpClient();
    	            HttpContext localContext = new BasicHttpContext();
    	            HttpPost httpPost = new HttpPost(urlDetails);
    	            HttpResponse response = httpClient.execute(httpPost, localContext);
    	            InputStream in = response.getEntity().getContent();
    	            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
    	                    .newDocumentBuilder();
    	            Document doc = builder.parse(in);
    	            System.out.println("Doc2 ist gefüllt");
    	            NodeList nl1, nl2;
        	        nl1 = doc.getElementsByTagName("result");
        	        System.out.println("Länge Node Nl1 = "+nl1.getLength());
        	        Node resultNode = nl1.item(0);
        	        nl2 = resultNode.getChildNodes();
        	        System.out.println("placesTask : Access Details");
        	        Node nameNode = nl2.item(getNodeIndex(nl2, "name"));
        	        namePoint= nameNode.getTextContent();
        	        System.out.println("Name: "+namePoint);
        	        Node telNode = nl2.item(getNodeIndex(nl2, "formatted_phone_number"));
        	        telNumber = telNode.getTextContent();
        	        System.out.println("Name: "+telNumber);
        	        Node addNode = nl2.item(getNodeIndex(nl2, "formatted_address"));
        	        address = addNode.getTextContent();
        	        System.out.println("Name: "+address);

        	        
        	        details = details+namePoint+";"+telNumber+";"+address;
        	        System.out.println("Name/Tel/Address: " +details);
        	        return details;
        	        
        	        //Müssen noch übergeben werden
    	    	}
    	    	catch(MalformedURLException ex)
    	    	{
    	    		Log.e("placesTask", ""+ex.getMessage());
        	    } catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    	    }
    	    catch(Exception ex)
    	    {
    	    	Log.e("placesTask", "Fehler beim auslesen");
    	    }
    		}
    		catch(Exception e)
    		{
    			Log.e("DoinBackground", "Error");
    			e.printStackTrace();
    		}
    	    return "";
    	
    	}
    	
		protected void onPostExecute() {		
    	}
    	
    	@Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
    
    private int getNodeIndex(NodeList nl, String nodename) {
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeName().equals(nodename))
                return i;
        }
        return -1;
    }
    
    public String getDetailsPlace()
    {
		//funktioniert: https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=49.016557,8.390047&rankby=prominence&radius=1000&types=movie_theater&sensor=false&key=AIzaSyCW2yIWAH8FtzCwhYKAazZnFIi6Fc71trA
	    
		String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/xml?location="
				+ middlePoint.latitude + "," + middlePoint.longitude + rankingByDistance 
				+ "&types=" + types + "&sensor=false&key=AIzaSyCW2yIWAH8FtzCwhYKAazZnFIi6Fc71trA";

		Log.i("public String getDetailsPlace", "Start: MeetingPoint: "+url);

	    HttpURLConnection conn = null;
	    StringBuilder sbResults = new StringBuilder();
	    String reference = "";
	    try
	    {
	    	//Muss auf den Knoten navigieren
	        try {
	            HttpClient httpClient = new DefaultHttpClient();
	            HttpContext localContext = new BasicHttpContext();
	            HttpPost httpPost = new HttpPost(url);
	            HttpResponse response = httpClient.execute(httpPost, localContext);
	            InputStream in = response.getEntity().getContent();
	            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
	                    .newDocumentBuilder();
	            Document doc = builder.parse(in);
	            
	            NodeList nl1, nl2, nl3;
	            String urlProminence = "";
	            try 
	            {
	            	/*nl1 = doc.getElementsByTagName("result");//PlaceSearchResponse");
	            	Node responseNode = nl1.item(0);
	            	nl2 = responseNode.getChildNodes();
	            	Node statusNode = nl2.item(getNodeIndex(nl2, "status"));
	            	String resultsStr = statusNode.getTextContent();
	            	System.out.println("Results in URL? " +resultsStr );
	            	
	            	*/
	            	String resultsStr = "";
	            	try
	            	{
	            	nl1 = doc.getElementsByTagName("location");
	    	        System.out.println("Länge Node Nl1 = "+nl1.getLength());
	    	        Node locationNode = nl1.item(0);
	    	        nl2 = locationNode.getChildNodes();
	    	        Node latNode = nl2.item(getNodeIndex(nl2, "lat"));
	    	        double lat = Double.parseDouble(latNode.getTextContent());
	    	        Node lngNode = nl2.item(getNodeIndex(nl2, "lng"));
	    	        double lng = Double.parseDouble(lngNode.getTextContent());
	    	        System.out.println("lat/lng:"+lat+"/"+lng);
	    	        nl3 = doc.getElementsByTagName("reference");
	    	        System.out.println("Anzahl Elemente reference: "+nl3.getLength());
	    	        Node referenceNode = nl3.item(0);
	    	        reference = referenceNode.getTextContent();
	    	        pTaskPOI = new LatLng(lat, lng);
	            	}
	            	catch(Exception e)
	            	{
	            		Log.e("getBarsEtc...", "failed");
	            		resultsStr = "ZERO_RESULTS";
	            	}
	            	
	            	
	            	if (resultsStr == "ZERO_RESULTS");
	            	{
	            		urlProminence = "https://maps.googleapis.com/maps/api/place/nearbysearch/xml?location="
	            				+ middlePoint.latitude + "," + middlePoint.longitude + rankingByProminence +"&radius=5000"
	            				+ "&types=" + types + "&sensor=false&key=AIzaSyCW2yIWAH8FtzCwhYKAazZnFIi6Fc71trA";
	            		System.out.println("UrlProminence: "+urlProminence);
    	            	httpClient = new DefaultHttpClient();
        	            localContext = new BasicHttpContext();
        	            httpPost = new HttpPost(urlProminence);
        	            response = httpClient.execute(httpPost, localContext);
        	            in = response.getEntity().getContent();
        	            builder = DocumentBuilderFactory.newInstance()
        	                    .newDocumentBuilder();
        	            doc = builder.parse(in);
        	            System.out.println("Doc ist gefüllt nach zweiten Versuch mit Prominence-Faktor");
	            	}
	            	
	            }
	            catch(Exception e)
	            {
	            	System.out.println("Zero Results");
	            }
	            
	            
	            nl1 = null;
	            nl2 = null;
	            System.out.println("Doc ist gefüllt");
    	        nl1 = doc.getElementsByTagName("location");
    	        System.out.println("Länge Node Nl1 = "+nl1.getLength());
    	        Node locationNode = nl1.item(0);
    	        nl2 = locationNode.getChildNodes();
    	        Node latNode = nl2.item(getNodeIndex(nl2, "lat"));
    	        double lat = Double.parseDouble(latNode.getTextContent());
    	        Node lngNode = nl2.item(getNodeIndex(nl2, "lng"));
    	        double lng = Double.parseDouble(lngNode.getTextContent());
    	        System.out.println("lat/lng:"+lat+"/"+lng);
    	        nl3 = doc.getElementsByTagName("reference");
    	        System.out.println("Anzahl Elemente reference: "+nl3.getLength());
    	        Node referenceNode = nl3.item(0);
    	        reference = referenceNode.getTextContent();
    	        pTaskPOI = new LatLng(lat, lng);

  	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	    	//Mittels Reference auf Details zugreifen
	    	String urlDetails = "https://maps.googleapis.com/maps/api/place/details/xml"
	    	+ "?reference="+reference 
	    	+ "&sensor=false&key=AIzaSyCW2yIWAH8FtzCwhYKAazZnFIi6Fc71trA";//AIzaSyCW2yIWAH8FtzCwhYKAazZnFIi6Fc71trA"; 
	    	//Log.i("urlDetails", ""+urlDetails);
	    	detailsArr = null;
	    	try
	    	{
	    		//Adresse
	    		//Telefonnummer
	    		//Name
	    		
	    		System.out.println("GetDetails: "+urlDetails);
	    		HttpClient httpClient = new DefaultHttpClient();
	            HttpContext localContext = new BasicHttpContext();
	            HttpPost httpPost = new HttpPost(urlDetails);
	            HttpResponse response = httpClient.execute(httpPost, localContext);
	            InputStream in = response.getEntity().getContent();
	            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
	                    .newDocumentBuilder();
	            Document doc = builder.parse(in);
	            System.out.println("Doc2 ist gefüllt");
	            NodeList nl1, nl2;
    	        nl1 = doc.getElementsByTagName("result");
    	        System.out.println("Länge Node Nl1 = "+nl1.getLength());
    	        Node resultNode = nl1.item(0);
    	        nl2 = resultNode.getChildNodes();
    	        System.out.println("placesTask : Access Details");
    	        Node nameNode = nl2.item(getNodeIndex(nl2, "name"));
    	        namePoint= nameNode.getTextContent();
    	        System.out.println("Name: "+namePoint);
    	        Node telNode = nl2.item(getNodeIndex(nl2, "formatted_phone_number"));
    	        telNumber = telNode.getTextContent();
    	        System.out.println("Name: "+telNumber);
    	        Node addNode = nl2.item(getNodeIndex(nl2, "formatted_address"));
    	        address = addNode.getTextContent();
    	        System.out.println("Name: "+address);

    	        
    	        details = details+namePoint+";"+telNumber+";"+address;
    	        System.out.println("Name/Tel/Address: " +details);
    	        return details;
    	        
    	        //Müssen noch übergeben werden
	    	}
	    	catch(MalformedURLException ex)
	    	{
	    		Log.e("placesTask", ""+ex.getMessage());
    	    } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    catch(Exception ex)
	    {
	    	Log.e("placesTask", "Fehler beim auslesen");
	    }
	    return "";
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
    
  
    //Umbenennung mapsTask in progressTask
			    
    public class progressTask extends AsyncTask<Void, Void, PolylineOptions> {

        @Override
        protected void onPreExecute() {

        	bar.setVisibility(View.VISIBLE);
        }
    	
    	@Override
		protected PolylineOptions doInBackground(Void... params) {
	    Location location = locationManager.getLastKnownLocation(provider);
	    	//double lat = (location.getLatitude());
	  	    //double lng = (location.getLongitude());
	  	    //aktPos = new LatLng(lat, lng);
	  	    ArrayList<LatLng> directionPoint = null;
	  	    
	  	    //System.out.println("Aktuelle Position: "+lat+"/"+lng);

	  	    
	  	    //destPos = new LatLng(47.999008,7.842104);//Heilbronn(49.142696,9.212487);//Kriegstraße(49.005363,8.403747);//(47.996642,7.841449);//(49.142696 , 9.212487);Science-Center//(49.011373 , 8.364624);Philippsstraße//(48.543433,7.976418);Appenweiher//Freiburg(47.996642,7.841449);//Mannheim(49.480617,8.469086);//Baden-Baden//Heilbronn//Durlach(48.999197,8.47445);//Kriegstraße(49.005363,8.403747);//(49.009239, 8.403974);
	  	    GMapV2Direction md = new GMapV2Direction();
	  	    System.out.println("Pooblem: "+aktPos + ", "+destPos + mode + " "+ mode2);
		    /*
	  	     * Parameter-Übergabe laufen, Auto oder öffentliche Verkehrsmittel
	  	     * String mode wird übergeben von vorangegangener Methode
	  	     */
	  	    Document doc = null;
	  	    //String mode = "driving";
	  	    //String mode2 = "walking";
	  	    
	  	    if (checkWay==false)
			{
			  	    if(mode == "walking")
			  	    {
			  	    	if (mode2 == "walking")
			  	    	{
			  	    		doc = md.getDocument(aktPos, destPos,GMapV2Direction.MODE_WALKING);
			  	    	}
			  	    	else
			  	    	{
			  	    		if (mode2 == "driving")
			  	    		{
			  	    			doc = md.getDocument(aktPos, destPos, GMapV2Direction.MODE_DRIVING);
			  	    		}
			  	    		else
			  	    		{
			  	    			//Test ob schneller mit Fuß muss noch gemacht werden
			  	    			doc = md.getDocument(aktPos, destPos, GMapV2Direction.MODE_TRANSIT);
			  	    		}
			  	    	}
			  	    }
			  	    else if(mode == "driving")
			  	    {
			  	    	doc = md.getDocument(aktPos, destPos,GMapV2Direction.MODE_DRIVING);
			  	    }
			  	    else if (mode == "transit")
			  	    { 
			  	    	doc = md.getDocument(aktPos, destPos,GMapV2Direction.MODE_WALKING);
			  	    	String durationWalking = md.getDurationValue(doc);		  	    	
			  	    	doc = md.getDocument(aktPos, destPos, GMapV2Direction.MODE_TRANSIT);
			  	    	String durationTransit = md.getDurationValue(doc);
			  	    	double durationWalkingDbl = calculateDuration(durationWalking);
			  	    	double durationTransitDbl = calculateDuration(durationTransit);
			  	    	if (durationWalkingDbl<=durationTransitDbl)
			  	    	{
			  	    		Log.i("Transit vs. Walking", "WALKING: durationWalkingDbl<durationTransitDbl"+durationWalkingDbl + " / "+durationTransitDbl);
			  	    		mode = "walking";
			  	    		beta = true;
			  	    		
			  			    //Toast.makeText(getApplicationContext(), "Schneller und flexibler zu Fuß auf der Strecke :)", Toast.LENGTH_LONG).show();
			  	    	}
			  	    	else
			  	    	{
			  	    		Log.i("Transit vs. Walking", "TRANSIT: durationWalkingDbl<durationTransitDbl"+durationWalkingDbl + " / "+durationTransitDbl);
			  	    	}
			  	    }

		  	    Log.i("Transit vs. Walking", "Neuer Mode = "+mode);
		  	    distance = md.getDistanceValue(doc);
				
				/*
					Formatierung beachten, wenn "hours" dabei ist.
					01-17 18:20:00.418: E/AndroidRuntime(17478): Caused by: java.lang.NumberFormatException: Invalid double: "1 hour 25"
				 */
				String durationStr = null;
				double durationCpl = 0;
				duration = String.valueOf(md.getDurationValue(doc));
				Log.i("Duration1", ""+duration);
				System.out.println("AUFRUF CALCULATE");
				durationCpl = calculateDuration(duration);
				System.out.println("CalculateValue = "+durationCpl);
				
				directionPoint = md.getDirection(doc);
				Log.i("Anzahl der Punkte in DirectionPoints", ""+directionPoint.size());
				int anzPunkte = directionPoint.size();
				int median = directionPoint.size()/2;
				medianPoint = directionPoint.get(median);
				System.out.println("Hier::::::::::::::::::");
				if (mode == "walking")
				{
					doc = md.getDocument(aktPos, medianPoint,GMapV2Direction.MODE_WALKING);
				}
				else if (mode == "driving")
				{
					doc = md.getDocument(aktPos, medianPoint,GMapV2Direction.MODE_DRIVING);
				} 
				else if (mode == "transit")
				{	
					/*TeilStrecke, in dem Beispiel bis Umsteigplatz "Bietigheim-Bissingen"
					 * anstelle medianPoint location der Umsteigestelle und keine Optimierung des Mittelpunkts
					 */
					doc = md.getDocument(aktPos, medianPoint, GMapV2Direction.MODE_TRANSIT);
					trainStation = md.getLocationTrainStationValue(doc);
					Log.i("testLocation", ""+trainStation);
					searchPoint = trainStation;
				}
				
				if (mode == "walking" || mode == "driving")
				{
					String durationPart = String.valueOf(md.getDurationValue(doc));
					Log.i("DurationPart1", ""+durationPart);
					//String durationPartStr = durationPart.substring(0, durationPart.indexOf(" mins", 0));
					//Eingefügt
					double durationPartDbl = 0;
					durationPartDbl = calculateDuration(durationPart);
					
					int halbeStrecke = (int) (durationCpl/2);
					//double durationPartDbl = Double.parseDouble(durationPartStr);
					Log.i("DurationPart", durationPart);
					Log.i("Median Lat/Lng", ""+directionPoint.get(median));
					Log.i("Checkway=False", "Front of While-Loop,/n "+durationCpl + "/" + halbeStrecke + "/" + durationPartDbl);
					
					if (halbeStrecke == durationPartDbl)
					{
						Log.i("halbeStrecke == durationPartDbl", "Start");
						if(mode == "walking")
						{
							doc = md.getDocument(aktPos, directionPoint.get(median),GMapV2Direction.MODE_WALKING);
						}
						else if (mode == "driving")
						{
							doc = md.getDocument(aktPos, directionPoint.get(median),GMapV2Direction.MODE_DRIVING);
						}
						else if (mode == "transit")
						{
							doc = md.getDocument(aktPos, directionPoint.get(median),GMapV2Direction.MODE_TRANSIT);
						}
						durationPart = String.valueOf(md.getDurationValue(doc));
						Log.i("DurationPart2", ""+durationPart);
						durationPartDbl = 0;
						durationPartDbl = calculateDuration(durationPart);
						Log.i("Checkway=False ", "> "+ durationCpl + "/" + halbeStrecke + "/" + durationPartDbl);
						searchPoint = directionPoint.get(median);
	
					}
					else
					{
						int helpMedian1 = 0;
						int helpMedian2 = 0;
						helpMedian1 = (anzPunkte/100)*10;
						helpMedian2 = (anzPunkte/100)*90;
						while (halbeStrecke!= durationPartDbl)
						{
							if (halbeStrecke > (durationPartDbl))
							{ 
								//System.out.println("Mittelpunktstrecke kleiner als Hälfte komplette Strecke, Erhöhung der Koordinaten");
								//Wenn Dauer Teilstrecke von StartPos zu median kleiner als durationCpl, dann median inkrementieren
								Log.i("halbeStrecke>durationPartDbl", "HalbeStrecke: "+halbeStrecke + ", durationPartDbl: "+durationPartDbl + ", helpMedian1 = "+helpMedian1);
								median = median + 20;//((anzPunkte/100)*2); //anstelle 20
								helpMedian1 = helpMedian1+8;
								helpMedian2 = helpMedian2+8;
								if(mode == "walking")
								{
									if (mode2 != "walking")
									{
										doc = md.getDocument(aktPos, directionPoint.get(helpMedian1),GMapV2Direction.MODE_WALKING);
										searchPoint = directionPoint.get(helpMedian1);
									}
									else
									{
										doc = md.getDocument(aktPos, directionPoint.get(median),GMapV2Direction.MODE_WALKING);
										searchPoint = directionPoint.get(median);
									}
								}
								else if(mode == "driving")
								{
									if (mode2 != "walking")
									{
										doc = md.getDocument(aktPos, directionPoint.get(median),GMapV2Direction.MODE_DRIVING);
										searchPoint = directionPoint.get(median);
									}
									else
									{
										doc = md.getDocument(aktPos, directionPoint.get(helpMedian2),GMapV2Direction.MODE_DRIVING);
										searchPoint = directionPoint.get(helpMedian2);

									}
								}
								else if (mode == "transit")
								{
									//Nicht Suche über MEdian, sondern über Haltestelle
									
									doc = md.getDocument(aktPos, directionPoint.get(median),GMapV2Direction.MODE_TRANSIT);
									searchPoint = directionPoint.get(median);

								}
								
								durationPart = String.valueOf(md.getDurationValue(doc));
								Log.i("DurationPart3", ""+durationPart+ ", doc: "+doc);
								durationPartDbl = 0;
								durationPartDbl = calculateDuration(durationPart);
								if (durationPartDbl == 0.0 || (durationPartDbl+1) == halbeStrecke)
								{
									Log.e("halbeStrecke>durationPArtDbl", "=0.0");
									break;
								}
								Log.i("Checkway=False ", "> "+ durationCpl + "/" + halbeStrecke + "/" + durationPartDbl);

							}
							else if (halbeStrecke < durationPartDbl)
							{
								//System.out.println("Mittelpunktstrecke größer als Hälfte komplette Strecke, Verkleinerung der Koordinaten");
								//median--;
								Log.i("halbeStrecke<durationPartDbl", "HalbeStrecke: "+halbeStrecke + ", durationPartDbl: "+durationPartDbl+ ", helpMedian1 = "+helpMedian1);
								int test = median;
								median = median-20;//((anzPunkte/100)*2);
								helpMedian1 = helpMedian1-4;
								helpMedian2 = helpMedian2-4;
								
								if(mode == "walking")
								{
									if (mode2 != "walking")
									{
										doc = md.getDocument(aktPos, directionPoint.get(helpMedian1),GMapV2Direction.MODE_WALKING);
										searchPoint = directionPoint.get(helpMedian1);
									}
									else
									{
										doc = md.getDocument(aktPos, directionPoint.get(median),GMapV2Direction.MODE_WALKING);
										searchPoint = directionPoint.get(median);

									}
								}
								else if(mode == "driving")
								{
									if (mode2 != "walking")
									{
										doc = md.getDocument(aktPos, directionPoint.get(median),GMapV2Direction.MODE_DRIVING);
										searchPoint = directionPoint.get(median);
									}
									else
									{
										doc = md.getDocument(aktPos, directionPoint.get(helpMedian2),GMapV2Direction.MODE_DRIVING);
										searchPoint = directionPoint.get(helpMedian2);

									}
								}
								else if (mode == "transit")
								{
									//Nicht Suche über MEdian, sondern über Haltestelle
									
									doc = md.getDocument(aktPos, directionPoint.get(median),GMapV2Direction.MODE_TRANSIT);
									searchPoint = directionPoint.get(median);

								}

								durationPart = String.valueOf(md.getDurationValue(doc));
								durationPartDbl = 0;
								durationPartDbl = calculateDuration(durationPart);
								if (durationPartDbl == 0.0 || (durationPartDbl-1) == halbeStrecke)
								{
									Log.e("halbeStrecke>durationPArtDbl", "=0.0");
									break;
								}
								Log.i("Checkway=False ", "< "+ durationCpl + "/" + halbeStrecke + "/" + durationPartDbl);
							}
							else
							{
								System.out.println("Weder kleiner noch größer -> =");
								searchPoint = directionPoint.get(median);
								Log.i("searchPoint1", ""+searchPoint);
								break;
							}
							System.out.println("SearchPoint2");
							//searchPoint = directionPoint.get(median);
							Log.i("searchPoint2", ""+searchPoint);
						}
						System.out.println("SearchPoint3");
						//searchPoint = directionPoint.get(median);
						Log.i("searchPoint3", ""+searchPoint);
					}
				}
				middlePoint = searchPoint;
				
				
				
				//pTask = new placesTask();
				//pTask.execute((Void) null);
				getDetailsPlace();
				try
				{
					if (pTaskPOI != null)
					{
						System.out.println("Neuer Mittelpunkt anhand Places");
						middlePoint = pTaskPOI;
					}
				}
				catch(Exception ex)
				{
					Log.e("Mittelpunktermittlung","Konvertierung schiefgelaufen");
				}
				/*
				 * Zur Ermittlung korrekter Linie 
				 */
				if (mode == "walking")
				{
					doc = md.getDocument(aktPos,middlePoint,GMapV2Direction.MODE_WALKING);
				}
				else if (mode == "driving")
				{
					doc = md.getDocument(aktPos, middlePoint,GMapV2Direction.MODE_DRIVING);
				}
				else if (mode == "transit")
				{
					doc = md.getDocument(aktPos, middlePoint,GMapV2Direction.MODE_TRANSIT);
				}
				directionPoint = md.getDirection(doc);
				durationPersonA = md.getDurationValue(doc);
				distancePersonA = md.getDistanceValue(doc);
				Log.i("Mittelpunkt", ""+middlePoint);
				System.out.println("Setzen des Mittelpunkts anhand errechneten Mittelpunkts ");
				/*
				 * 
				 * Abrufen der nächstgelegenen exquisiten Absteige über pTask
				 * 
				 */
//Alter Aufruf pTask
				System.out.println("+++++++++++++++++Zeichnen der 1. Linie");
				rectLine = new PolylineOptions().width(7).color(
				        Color.BLUE);
				System.out.println("DP1: "+directionPoint);
				for (int i = 0; i < directionPoint.size(); i++) {
					rectLine.add(directionPoint.get(i));
					//Hier Linie nur bis Mittelpunkt zeichnen
				}
				System.out.println("Rectline: "+rectLine);
				checkWay = true;
				Log.i("Mittelpunkt und Streckenpunkte ermittelt", "checkWay = true" + " Übergabe middlePoint: "+middlePoint);
			}
			else //Ermittlung des zweiten Weges
			{
				Log.i("#######", "##########");
				Log.i("Ermittlung des zweiten Weges", "Parameter: aktPos/MiddlePoint/destPos"+ aktPos + "/" + middlePoint + "  / "+ destPos);
	
				if (mode == "driving")
				{
					doc = md.getDocument(middlePoint, destPos,GMapV2Direction.MODE_DRIVING);
				}
				else if (mode == "walking")
				{
					doc = md.getDocument(middlePoint, destPos,GMapV2Direction.MODE_WALKING);
				}
				else if (mode == "transit")
				{
					Log.i("Test Transit", "middlePoint/destPos"+middlePoint+ " / " + destPos);
					doc = md.getDocument(middlePoint, destPos,GMapV2Direction.MODE_TRANSIT);
				}
				
				directionPoint = md.getDirection(doc);
				// Änderung Anfang
				durationPersonB = md.getDurationValue(doc);
				distancePersonB = md.getDistanceValue(doc);
				//Änderung Ende
				System.out.println("+++++++++++++++++Zeichnen der 2. Linie");
				rectLine = new PolylineOptions().width(7).color(Color.GREEN);
				System.out.println("DP2: "+directionPoint);
				for (int i = 0; i < directionPoint.size(); i++) {
				    rectLine.add(directionPoint.get(i));
				    //Hier muss noch Änderung rein mit Ermittlung nur bis Mittelpunkt
				}
				checkWay = false;
			}

			return rectLine;

		}
	
		@Override
	    protected void onPostExecute(PolylineOptions rectLine) {
			try{
				Log.i("onPostExecute", ""+aktPos + "/" + middlePoint + "/" + destPos);
			    GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			    //TextView txtDuration = (TextView) findViewById(R.id.TextViewTime);
			    //txtDuration.setText(duration);
			    
			    //TextView txtDistance = (TextView) findViewById(R.id.TextView02);
			    //txtDistance.setText(distance+" m"); // txt.setText(result);
			    System.out.println("Setzen der Marker");
			    Marker destinationPos = map.addMarker(new MarkerOptions().position(destPos)
			    		.title("Startpunkt des Empfängers"));
			    destinationPos.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
			    Marker aktuellePosition = map.addMarker(new MarkerOptions().position(aktPos)
			            .title("Startpunkt des Erstellers"));
			    Marker middle = map.addMarker(new MarkerOptions().position(middlePoint)
			            .title("Optimaler Mittelpunkt"));
			    System.out.println("Setzen der Linie++++++");
			    System.out.println(rectLine);
			    Polyline polylin = map.addPolyline(rectLine);
			    middle.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
			    bar.setVisibility(View.GONE);
			    System.out.println("CameraPosition");
			 // Move the camera instantly to aktPos with a zoom of 15.		    
			    //map.moveCamera(CameraUpdateFactory.newLatLngZoom(aktPos, 12));
			    CameraPosition cameraPosition = null;
			    if (distance < 1000)
			    {
			    	 cameraPosition = new CameraPosition.Builder().target(
					    		aktPos).zoom(15).build();
					    Log.i("cameraPosition", "Zoom (15)");
			    }
			    else if (distance < 1500)
			    {
				    cameraPosition = new CameraPosition.Builder().target(
				    		aktPos).zoom(14).build();
				    Log.i("cameraPosition", "Zoom (14)");
			    }
			    else if (distance < 5000)
			    {
				    cameraPosition = new CameraPosition.Builder().target(
				    		aktPos).zoom(13).build();
				    Log.i("cameraPosition", "Zoom (13)");
			    }
			    else if (distance < 30000)
			    {
				    cameraPosition = new CameraPosition.Builder().target(
				    		aktPos).zoom(12).build();
				    Log.i("cameraPosition", "Zoom (12)");
			    }
			    else if (distance > 30000 && distance < 60000)
			    {
				    cameraPosition = new CameraPosition.Builder().target(
				    		aktPos).zoom(11).build();
				    Log.i("cameraPosition", "Zoom (11)");
			    }
			    else if (distance > 60000 && distance < 80000)
			    {
			    	cameraPosition = new CameraPosition.Builder().target(
				    		aktPos).zoom(10).build();
				    Log.i("cameraPosition", "Zoom (10)");
			    }
			    else if (distance < 100000)
			    {
			    	cameraPosition = new CameraPosition.Builder().target(
				    		aktPos).zoom(9).build();
				    Log.i("cameraPosition", "Zoom (9)");
			    }
			    else if (distance >= 100000)
			    {
			    	cameraPosition = new CameraPosition.Builder().target(
				    		aktPos).zoom(8).build();
				    Log.i("cameraPosition", "Zoom (8)");
			    }
			    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			    map.getUiSettings().setCompassEnabled(true);
			    map.getUiSettings().setMyLocationButtonEnabled(true);
			    System.out.println("Ende PostExecute mit Setzen der Marker und TextView");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	    }


        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
    
    public double calculateDuration(String duration)
    {
    	try
    	{
    	double value = 0;
		Log.i("Aufruf CalculateDuration", "Duration: "+duration);

    	if (duration.contains("day"))
		{
			//01-19 11:42:39.011: I/System.out(30930): Contains Days1 day 4 hours
			System.out.println("Contains Days"+duration);
		    //Toast.makeText(getApplicationContext(), "Von einer so langen Strecke gibt es nur Blasen an den Füßen ;-)", Toast.LENGTH_LONG).show();
		    int indTest = duration.indexOf(" day", 0);
		    String s = duration.substring(0, indTest);
		    System.out.println("indTest/s:"+indTest+"/"+s);
			int day = Integer.parseInt(duration.substring(0, duration.indexOf(" day", 0)));
			System.out.println("Day: "+day);
			int hour = 0;
			int helpInd1 = 0;
			int helpInd2 = 0;
			try
			{
				if (duration.contains("days"))
				{
					helpInd1 = duration.indexOf("days", 0);
					helpInd1 = helpInd1+5; //Ab dem 6. Element nach "hours" beginnt der Text für die Minuten
					System.out.println("helpInd1 bei duration: "+helpInd1+" : "+duration );
					hour = Integer.parseInt(duration.substring(helpInd1, duration.indexOf(" hour", 0)));
				}
				else
				{
					helpInd2 = duration.indexOf("day", 0);
					helpInd2 = helpInd2+4;
					System.out.println("helpIndex2 bei duration: "+helpInd2+" : "+duration );
					hour = Integer.parseInt(duration.substring(helpInd2, duration.indexOf(" hour", 0)));
				}
			}
			catch(Exception ex)
			{
				Log.e("Keine Stundenangabe", "0");
				hour = 0;
			}
			System.out.println("Hour: "+hour);
			value = (day*24*60) + (hour*60);
			System.out.println("Day/hour/value: "+day+"/"+hour+"/"+value );
		}
		else if (duration.contains("hour"))
		{
			//getHour
			int hour = 0;
			int min = 0;
			int helpIndex1 = 0;
			int helpIndex2 = 0;
			if (duration.contains("hours"))
			{
				hour = Integer.parseInt(duration.substring(0, duration.indexOf(" hours", 0))); 				
			}
			else
			{
				hour = Integer.parseInt(duration.substring(0, duration.indexOf(" hour", 0))); 
			}
			try
			{
				//01-19 11:02:22.263: I/DurationValue(24203): 1 hour 51 mins
				//12 hour 51 mins
				//13hours 13 mins
				if (duration.contains("hours"))
				{
					helpIndex1 = duration.indexOf("hours", 0);
					helpIndex1 = helpIndex1+6; //Ab dem 6. Element nach "hours" beginnt der Text für die Minuten
					System.out.println("helpIndex1 bei duration: "+helpIndex1+" : "+duration );
					min = Integer.parseInt(duration.substring(helpIndex1, duration.indexOf(" min", 0)));
				}
				else
				{
					helpIndex2 = duration.indexOf("hour", 0);
					helpIndex2 = helpIndex2+5;
					System.out.println("helpIndex2 bei duration: "+helpIndex2+" : "+duration );
					min = Integer.parseInt(duration.substring(helpIndex2, duration.indexOf(" min", 0)));
				}
			}
			catch(Exception ex)
			{
				Log.e("Keine Minutenangabe", "0");
				min = 0;
			}									
			value = (hour * 60) + min;
		}
		else
		{
			String durationStr = duration.substring(0, duration.indexOf(" min", 0));
			value = Double.parseDouble(durationStr);
		}
		return value;
    	}
    	catch(Exception e)
    	{
    		Log.e("calculateDuration", "ERROR: "+e);
    		return 0.0;
    	}
    }
    
    
    public void launchRingDialog(View view) {
    	final ProgressDialog ringProgressDialog = ProgressDialog.show(DisplayMap.this, "Please wait...", "Calculating Middlepoint...", true);
    	ringProgressDialog.setCancelable(true);
    	new Thread(new Runnable() {
    		@Override
    		public void run() {
    			try {
    				Thread.sleep(5000);
    				
    				centerMapOnMyLocation(location);
    				
    			}
    			catch(Exception e)
    			{
    				Log.e("launchRingDialog",""+e);
    			}
    			ringProgressDialog.dismiss();
    		}
    	}).start();
    }
    
    
	public void centerMapOnMyLocation(Location location) {
		
		Log.i("centerMapOnMyLocation", "Aufruf placesTask/AsyncTask");

		System.out.println("Erster Aufruf");
		prgsTask = new progressTask();
		prgsTask.execute((Void) null);
		System.out.println("Zweiter Aufruf");
		prgsTask = new progressTask();
		prgsTask.execute((Void) null);	
		System.out.println("Rückgabe zweiter Aufruf");
		Log.i("Start Navigation", "Intent");
		        
	}

	  public void showViewbefore(View view){
	    Toast.makeText(getApplicationContext(), "showViewBefore", Toast.LENGTH_LONG).show();
         Intent i = new Intent(DisplayMap.this, DisplayOverviewRouting.class);
         startActivity(i);
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
		
	}
	
	
	public void formatParameters()
	{
		if (modePers1 == 1)
		{
			mode = "walking";
		}
		else if (modePers1 == 2)
		{
			mode = "transit";
		}
		else
		{
			mode = "driving";
		}
		if (modePers2 == 1)
		{
			mode2 = "walking";
		}
		else if (modePers2 == 2)
		{
			mode2 = "transit";
		}
		else
		{
			mode2 = "driving";
		}
		if (lokalitaet == 1)
		{
			types = "cafe";
		}
		else if (lokalitaet == 2)
		{
			types = "night_cafe";
		}
		else if (lokalitaet == 3)
		{
			types = "bar";
		}
		else if (lokalitaet == 4)
		{
			types = "hindu_temple";
		}
		else if (lokalitaet == 5)
		{
			types = "museum";
		}
		else if (lokalitaet == 6)
		{
			types = "park";
		}
		else if (lokalitaet == 7)
		{
			types = "restaurant";
		}
		else
		{
			types = "movie_theater";
		}
		
		try
		{
			String lat1 = locationPers1.substring(0, (locationPers1.indexOf(",")-1));
			System.out.println("lat1: "+lat1);
			String lng1 = locationPers1.substring(locationPers1.indexOf(",")+1, locationPers1.length());
			lat1 = lat1.replaceAll(" ", "");
			lng1 = lng1.replaceAll(" ", "");
			System.out.println("lng1: "+lng1);
			double latPers1 = Double.parseDouble(lat1);
			double lngPers1 = Double.parseDouble(lng1);
			String lat2 = locationPers2.substring(0, (locationPers2.indexOf(",")-1));
			System.out.println("lat2: "+lat2);
			String lng2 = locationPers2.substring(locationPers2.indexOf(",")+1, locationPers2.length());
			lat2 = lat2.replaceAll(" ", "");
			lng2 = lng2.replaceAll(" ", "");
			System.out.println("lng2: "+lng2);
			double latPers2 = Double.parseDouble(lat2);
			double lngPers2 = Double.parseDouble(lng2);	
			
			System.out.println("lat1, lng1, lat2, lng2: "+latPers1 + " " + lngPers1 + " " + latPers2 + " " + lngPers2);
			aktPos = new LatLng(latPers1, lngPers1);
			destPos = new LatLng(latPers2, lngPers2);
			//destPos = new LatLng(49.010132 , 8.386655);
		}
		catch(Exception e)
		{
			Log.e("formatParameters"," ... failed");
		}
	}


}
