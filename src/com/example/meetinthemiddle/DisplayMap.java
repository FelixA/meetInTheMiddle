package com.example.meetinthemiddle;

import java.util.ArrayList;
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
 
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.view.View;
import org.w3c.dom.Document;


public class DisplayMap extends android.support.v4.app.FragmentActivity implements android.location.LocationListener{
	private LocationManager locationManager;
	private String provider, duration;
	//private mapsTask mTask = null;
	private progressTask prgsTask = null;
	private placesTask pTask = null;
	public Location location;
	String[] detailsArr = new String[10];
	public double distance;
	boolean checkWay = false;
	public LatLng aktPos, destPos, poi, middlePoint, searchPoint, medianPoint, trainStation;
	GoogleMap map;
    ArrayList<LatLng> markerPoints;
    TextView tvDistanceDuration;
    public PolylineOptions rectLine = null;
    public ProgressDialog barProgressDialog;
    public Handler updateBarHandler;
    ProgressBar bar = null;
    ProgressDialog ringProgressDialog;
	
	public void onCreate(Bundle savedInstanceState)
	   {
		System.out.println("OnCreate in DisplayMap.java");
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.activity_display_routingmap);
		    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		    // Define the criteria how to select the location provider -> use
		    // default
		    Criteria criteria = new Criteria();
		    provider = locationManager.getBestProvider(criteria, false);
		    Location location = locationManager.getLastKnownLocation(provider);
		    // Initialize the location fields
		    if (location != null) {
		      System.out.println("Provider " + provider + " has been selected.");
			    bar = (ProgressBar) this.findViewById(R.id.progressBar);
			    //new progressTask().execute();
			    centerMapOnMyLocation(location);

		      //addPolyLines();
		    } else {
		    	//Setzen der Werte für Felder in Layout
			    Toast.makeText(getApplicationContext(), "Lat + Lng konnten nicht ermittelt werden.", Toast.LENGTH_LONG).show();
		    }
		      //setIcon(detailsArr);
		    
	    	//ringProgressDialog = ProgressDialog.show(DisplayMap.this, "Please wait...", "Calculating Middlepoint...", true);
	    	//ringProgressDialog.setCancelable(true);
		    /*
		    final Button buttonProgress = (Button) findViewById(R.id.progressBar);
		    buttonProgress.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					launchRingDialog(v);
				}
			});
			
			    			<Button
			    android:id="@+id/progressButton"
			    android:layout_width="wrap_content"
			    android:layout_height="wrap_content"
			   	android:layout_marginLeft="40dip"
		        android:layout_marginRight="15dip"
			    android:text="Test Progress"
			    android:onClick="showMapRouting" />
			    
			    
		    */
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
    		Log.i("placesTask", "REQUEST");
    		int radius = 200;
    		//Weitere Suchmöglichkeiten "name"/"types"        opennow
    		String keyword = "cafe"; // Alternativ/Zusätzlich Suche über Name / Treffer auch wenn Suchwort nicht ausgeschrieben ist,  ?types=cafe|bakery
    		String types = "bar|restaurant"; //Parameter in URL anpassen
    		String rankingByDistance = "&rankby=distance"; //Parameter radius hierzu deaktivieren, Ergebnisse werden nach distanz geordnet
    		String rankingByProminence = "&rankby=prominence";
    		//String destPos = "49.005363,8.403747";//"49.009239,8.403974";
    		
    		//funktioniert: https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=49.016557,8.390047&rankby=prominence&radius=1000&types=movie_theater&sensor=false&key=AIzaSyCW2yIWAH8FtzCwhYKAazZnFIi6Fc71trA
    	    
    		String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
    				+ middlePoint.latitude + "," + middlePoint.longitude + rankingByDistance 
    				+ "&types=" + types + "&sensor=false&key=AIzaSyCW2yIWAH8FtzCwhYKAazZnFIi6Fc71trA";
    		/*
    		String url = "https://maps.googleapis.com/maps/api/place/search/json?keyword=" + keyword
    		+ "&location=" + middlePoint.latitude+","+middlePoint.longitude + rankingByProminence//"&radius="+radius //middlepoint anstelle destPos
    		+ "&sensor=false&key=AIzaSyCW2yIWAH8FtzCwhYKAazZnFIi6Fc71trA";
    		*/
    	    Log.i("meetingPoint", url);    	    
    	    //Test
    	    //Deklaration
    	    HttpURLConnection conn = null;
    	    StringBuilder sbResults = new StringBuilder();
    	    try
    	    {
    	    	URL urlRequest = new URL(url);
    	    	conn = (HttpURLConnection) urlRequest.openConnection();
    	    	InputStreamReader inReader = new InputStreamReader(conn.getInputStream());
    	    	
    	    	int read;
    	    	char[] buff = new char[1024];
    	    	while ((read = inReader.read(buff)) != -1) {
    	    		sbResults.append(buff, 0, read);
    	    	}
    	    	//getLocationofPOI (Point of Interest)
    	    	poi = getLocationOfPOI(sbResults);
    	    	
    	    	//+14, um die reference im String zu überspringen
    	    	int index = sbResults.indexOf("reference", 1)+14;
    	    	String reference = sbResults.substring(index, index+211);
    	    	//Mittels Reference auf Details zugreifen
    	    	String urlDetails = "https://maps.googleapis.com/maps/api/place/details/json"
    	    	+ "?reference="+reference 
    	    	+ "&sensor=false&key=AIzaSyCW2yIWAH8FtzCwhYKAazZnFIi6Fc71trA";
    	    	//Log.i("urlDetails", ""+urlDetails);
    	    	try
    	    	{
    	    		HttpURLConnection conn2 = null;
    	    		StringBuilder sbDetailedResults = new StringBuilder();
    	    		URL urlDetailedRequest = new URL(urlDetails);
    	    		conn2 = (HttpURLConnection) urlDetailedRequest.openConnection();
    	    		InputStreamReader inDetailedReader = new InputStreamReader(conn2.getInputStream());
    	    		int readDetails;
        	    	char[] buffDetails = new char[1024];
        	    	while ((readDetails = inDetailedReader.read(buffDetails)) != -1) {
        	    		sbDetailedResults.append(buffDetails, 0, readDetails);
        	    	}
    	    		String[] detailsArr;
    	    		detailsArr = getDetails(sbDetailedResults);
    	    		//setIcon(detailsArr);
    	    	}
    	    	catch(MalformedURLException ex)
    	    	{
    	    		Log.e("placesTask", ""+ex.getMessage());
        	    } catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    	    }
    	    catch(MalformedURLException ex)
    	    {
    	    	Log.e("placesTask", ""+ex.getMessage());
    	    } catch (IOException e) {
				// TODO Auto-generated catch block
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
    
    public LatLng getLocationOfPOI(StringBuilder sbResults)
    {
    	try
    	{
	    	LatLng locPOI = null;
	    	int index1 = (sbResults.indexOf("<lat>", 1)+5);
	    	int index2 = (sbResults.indexOf("<lng>", 1)+5);
	    	String latPOIStr = sbResults.substring(index1, index1+8);
	    	String lngPOIStr = sbResults.substring(index2, index2+8);
	    	double latPOI = Double.parseDouble(latPOIStr);
	    	double lngPOI = Double.parseDouble(lngPOIStr);
	    	Log.i("LatPOI/LngPOI", ""+latPOIStr+"/"+lngPOIStr);
	    	locPOI = new LatLng(latPOI, lngPOI);
	    	Log.i("locPOI", ""+locPOI);
	    	return locPOI;
    	}
    	catch(Exception ex)
    	{
    		Log.e("getLocationOfPOI", "Can't format LatLng. :(");
    	}
		return aktPos;
    	
    }
    
    
    public String[] getDetails(StringBuilder sbDetailedResults)
    {
    	//String[] detailsArr = new String[10];
    	Log.i("++++GETDETAILS+++++", ""+sbDetailedResults);
    	try
    	{
	    	//Get Name of POI - First search for icon, then start search for substing from index of icon cause of several "name"
	    	int indexIcon = (sbDetailedResults.indexOf("international_phone_number", 1));
	    	System.out.println("IndexICON: "+indexIcon);
	    	String namePOI = sbDetailedResults.substring(((sbDetailedResults.indexOf("name", indexIcon))+9), sbDetailedResults.indexOf(",", (sbDetailedResults.indexOf("name", indexIcon)))-1);
	    	detailsArr[0] = namePOI;
	    	System.out.println("Array[0]:"+detailsArr[0]);
    	}
    	catch(Exception ex)
    	{
    		Log.e("getDetails","Can't find Name :(");
    	}

    	try
    	{
    		String telephonePOI = (sbDetailedResults.substring((sbDetailedResults.indexOf("international_phone_number", 1))+31, sbDetailedResults.indexOf(",", (sbDetailedResults.indexOf("international_phone_number", (sbDetailedResults.indexOf("international_phone_number", 1)))))-1));
	    	detailsArr[1] = telephonePOI;
	    	System.out.println("Array[1]:"+detailsArr[1]);
    	}
    	catch(Exception ex)
    	{
    		Log.e("getDetails", "Can't find telephone :(");
    	}
    	try
    	{
    		String addressPOI = (sbDetailedResults.substring((sbDetailedResults.indexOf("formatted_address", 1))+22, sbDetailedResults.indexOf("formatted_phone_number", (sbDetailedResults.indexOf("formatted_address")))-10));
	    	detailsArr[2] = addressPOI;
	    	System.out.println("Array[2]:"+detailsArr[2]);
    	}
    	catch(Exception ex)
    	{
    		Log.e("getDetails", "Can't find formatted address :(");
    	}
    	try
    	{
    		String iconPOI = (sbDetailedResults.substring((sbDetailedResults.indexOf("icon", 1))+9, sbDetailedResults.indexOf(",", (sbDetailedResults.indexOf("icon")))-1));
	    	detailsArr[3] = iconPOI;
	    	System.out.println("Array[3]: ICON"+detailsArr[3]);
    	}
    	catch(Exception ex)
    	{
    		Log.e("getDetails", "Can't find icon :(");
    	}
    	return detailsArr;
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
	    	double lat = (location.getLatitude());
	  	    double lng = (location.getLongitude());
	  	    aktPos = new LatLng(lat, lng);
	  	    ArrayList<LatLng> directionPoint = null;
	  	    
	  	    System.out.println("Aktuelle Position: "+lat+"/"+lng);
	  	    /* Zielkoordinaten manuell angegeben, nachfolgend auslesen über Google Places/JSON
	  	     * Auswahl nach Ranking am Zielort im Umkreis/radius von 200m
	  	     * aktuelles Ziel/destPos stellt Marktplatz dar
	  	    */
	  	    destPos = new LatLng(49.005363,8.403747);//(47.996642,7.841449);//(49.142696 , 9.212487);Science-Center//(49.011373 , 8.364624);Philippsstraße//(48.543433,7.976418);Appenweiher//Freiburg(47.996642,7.841449);//Mannheim(49.480617,8.469086);//Baden-Baden//Heilbronn//Durlach(48.999197,8.47445);//Kriegstraße(49.005363,8.403747);//(49.009239, 8.403974);
		    GMapV2Direction md = new GMapV2Direction();		    
		    /*
	  	     * Parameter-Übergabe laufen, Auto oder öffentliche Verkehrsmittel
	  	     * String mode wird übergeben von vorangegangener Methode
	  	     */
	  	    Document doc = null;
	  	    String mode = "driving";
	  	    if (checkWay==false)
			{
		  	    if(mode == "walking")
		  	    {
		  	    	doc = md.getDocument(aktPos, destPos,GMapV2Direction.MODE_WALKING);
		  	    }
		  	    else if(mode == "driving")
		  	    {
		  	    	doc = md.getDocument(aktPos, destPos,GMapV2Direction.MODE_DRIVING);
		  	    }
		  	    else if (mode == "transit")
		  	    { 
		  	    	doc = md.getDocument(aktPos, destPos, GMapV2Direction.MODE_TRANSIT);
		  	    }
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
						while (halbeStrecke!= durationPartDbl)
						{
							if (halbeStrecke > (durationPartDbl))
							{ 
								//System.out.println("Mittelpunktstrecke kleiner als Hälfte komplette Strecke, Erhöhung der Koordinaten");
								//Wenn Dauer Teilstrecke von StartPos zu median kleiner als durationCpl, dann median inkrementieren
								median++;
								median = median + 10;
								if(mode == "walking")
								{
									doc = md.getDocument(aktPos, directionPoint.get(median),GMapV2Direction.MODE_WALKING);
								}
								else if(mode == "driving")
								{
									doc = md.getDocument(aktPos, directionPoint.get(median),GMapV2Direction.MODE_DRIVING);
								}
								else if (mode == "transit")
								{
									//Nicht Suche über MEdian, sondern über Haltestelle
									
									doc = md.getDocument(aktPos, directionPoint.get(median),GMapV2Direction.MODE_TRANSIT);
								}
								
								durationPart = String.valueOf(md.getDurationValue(doc));
								Log.i("DurationPart3", ""+durationPart);
								durationPartDbl = 0;
								durationPartDbl = calculateDuration(durationPart);
								Log.i("Checkway=False ", "> "+ durationCpl + "/" + halbeStrecke + "/" + durationPartDbl);
		
							}
							else if (halbeStrecke < durationPartDbl)
							{
								//System.out.println("Mittelpunktstrecke größer als Hälfte komplette Strecke, Verkleinerung der Koordinaten");
								//median--;
								median = median-10;
								if (mode == "walking")
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
								Log.i("DurationPart4", ""+durationPart);
								durationPartDbl = 0;
								durationPartDbl = calculateDuration(durationPart);
								Log.i("Checkway=False ", "< "+ durationCpl + "/" + halbeStrecke + "/" + durationPartDbl);
							}
							else
							{
								System.out.println("Weder kleiner noch größer -> =");
								break;
							}
							searchPoint = directionPoint.get(median);
							Log.i("searchPoint", ""+searchPoint);
						}
					}
				}
				middlePoint = searchPoint;
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
				Log.i("Mittelpunkt", ""+middlePoint);
				System.out.println("Setzen des Mittelpunkts anhand errechneten Mittelpunkts ");
				/*
				 * 
				 * Abrufen der nächstgelegenen exquisiten Absteige über pTask
				 * 
				 */
				pTask = new placesTask();
				pTask.execute((Void) null);
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
			

			//Zeichnung zweite Strecke in anderer Farbe
		}
	
		@Override
	    protected void onPostExecute(PolylineOptions rectLine) {
			Log.i("onPostExecute", ""+aktPos + "/" + middlePoint + "/" + destPos);
		    GoogleMap map = ((MapFragment) getFragmentManager()
                 .findFragmentById(R.id.map)).getMap();
		    System.out.println("MapFragment/");
		    TextView txtDuration = (TextView) findViewById(R.id.TextViewTime);
		    txtDuration.setText(duration);
		    TextView txtDistance = (TextView) findViewById(R.id.TextView02);
		    txtDistance.setText(distance+" m"); // txt.setText(result);
		    System.out.println("Setzen der Marker");
		    Marker destinationPos = map.addMarker(new MarkerOptions().position(destPos)
		    		.title("Ziel"));
		    destinationPos.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
		    Marker aktuellePosition = map.addMarker(new MarkerOptions().position(aktPos)
		            .title("Aktuelle Position"));
		    Marker middle = map.addMarker(new MarkerOptions().position(middlePoint)
		            .title("Mittelpunkt"));
		    System.out.println("Setzen der Linie++++++");
		    System.out.println(rectLine);
		    Polyline polylin = map.addPolyline(rectLine);
		    middle.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
		    bar.setVisibility(View.GONE);
		    System.out.println("CameraPosition");
		 // Move the camera instantly to aktPos with a zoom of 15.		    
		    //map.moveCamera(CameraUpdateFactory.newLatLngZoom(aktPos, 12));
		    CameraPosition cameraPosition = null;
		    if (distance < 1500)
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
		    
		    //Abfrage über Button
		    //Error-Code abfragen, danach Bewertungsactivity starten

	    }


        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
    
    public double calculateDuration(String duration)
    {
    	Log.i("AUFRUF calculateDuration", "Aufruf");
    	double value = 0;
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
			System.out.println("Hour/min/durationPart: "+hour+"/"+min+"/"+value );
		}
		else
		{
			String durationStr = duration.substring(0, duration.indexOf(" min", 0));
			value = Double.parseDouble(durationStr);
			System.out.println("Min==durationPart: "+value);
		}
		System.out.println("Ende calculateDuration");
		return value;
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
		//mTask = new mapsTask();
		//mTask.execute((Void) null);
		/*
		 * 		System.out.println("Setzen des Mittelpunkts anhand errechneten Mittelpunkts ");
		pTask = new placesTask();
		pTask.execute((Void) null);
		 */
		System.out.println("Zweiter Aufruf");
		prgsTask = new progressTask();
		prgsTask.execute((Void) null);
		//mTask = new mapsTask();
		//mTask.execute((Void) null);		
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


}
