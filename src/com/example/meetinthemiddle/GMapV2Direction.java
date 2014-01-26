package com.example.meetinthemiddle;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.util.Log;

public class GMapV2Direction {
public final static String MODE_DRIVING = "driving";
public final static String MODE_WALKING = "walking";
public final static String MODE_TRANSIT = "transit";

public GMapV2Direction() {
}

public Document getDocument(LatLng start, LatLng end, String mode) {
	String url = "";
	if (mode == "driving" || mode == "walking")
	{
		url = "http://maps.googleapis.com/maps/api/directions/xml?"
            + "origin=" + start.latitude + "," + start.longitude
            + "&destination=" + end.latitude + "," + end.longitude
            + "&sensor=false&units=metric&mode="+mode;
		Log.i("driving oder walking", ""+url);
	}
	else if (mode == "MODE_DRIVING" || mode == "MODE_WALKING")
	{
		url = "http://maps.googleapis.com/maps/api/directions/xml?"
            + "origin=" + start.latitude + "," + start.longitude
            + "&destination=" + end.latitude + "," + end.longitude
            + "&sensor=false&units=metric&mode="+mode;
		Log.i("driving oder walking", ""+url);

	}
	else if (mode == "transit")
	{
		//1390046044492 -- Should: 1343605500
		long timeInMillis = System.currentTimeMillis();
		//Works: http://maps.googleapis.com/maps/api/directions/json?origin=49.01642889,8.38976915&destination=49.142696,9.212487&sensor=false&arrival_time=2321&mode=transit
		Log.i("Argumente: ", ""+start + " : " + end + " : " + mode);
		url = "http://maps.googleapis.com/maps/api/directions/xml?"
	            + "origin=" + start.latitude + "," + start.longitude
	            + "&destination=" + end.latitude + "," + end.longitude
	            + "&sensor=false&arrival_time=1390046044&mode="+mode;
	}
	else if (mode == "MODE_TRANSIT")
	{
		//1390046044492 -- Should: 1343605500
		long timeInMillis = System.currentTimeMillis();
		Log.i("GMapDirection 2", ""+mode+ ": "+timeInMillis);
		//Works: http://maps.googleapis.com/maps/api/directions/json?origin=49.01642889,8.38976915&destination=49.142696,9.212487&sensor=false&arrival_time=2321&mode=transit
		Log.i("Argumente: ", ""+start + " : " + end + " : " + mode);
		url = "http://maps.googleapis.com/maps/api/directions/xml?"
	            + "origin=" + start.latitude + "," + start.longitude
	            + "&destination=" + end.latitude + "," + end.longitude
	            + "&sensor=false&arrival_time=1390046044&mode="+mode;
		Log.i("GMapDirection 2: URL", ""+url);
	}
    try {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(url);
        HttpResponse response = httpClient.execute(httpPost, localContext);
        InputStream in = response.getEntity().getContent();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder();
        Document doc = builder.parse(in);
        return doc;
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}

public String getDurationText(Document doc) {
    try {

        NodeList nl1 = doc.getElementsByTagName("duration");
        Node node1 = nl1.item(0);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "text"));
        Log.i("DurationText", node2.getTextContent());
        return node2.getTextContent();
    } catch (Exception e) {
        return "0";
    }
}

public String getDurationValue(Document doc) {
    try {
    	String tagname = "duration";
    	NodeList nodes = doc.getElementsByTagName(tagname);
        Node relevantNode = nodes.item(nodes.getLength()-1);
        NodeList relevantChildNodes = relevantNode.getChildNodes();
        Node durationNode = relevantChildNodes.item(getNodeIndex(relevantChildNodes, "text"));
		Log.i("DurationValue", durationNode.getTextContent());
        return durationNode.getTextContent();
    } catch (Exception e) {
        return "N/A";
    }
}

public LatLng getLocationTrainStationValue(Document doc) {
    try {
    	String tagname = "arrival_stop";
    	NodeList nodes = doc.getElementsByTagName(tagname);
        Node relevantNode = nodes.item(nodes.getLength()-1);
        NodeList relevantChildNodes = relevantNode.getChildNodes();
    	String tagname2 = "location";
    	nodes = doc.getElementsByTagName(tagname2);
    	Node relNodes = nodes.item(nodes.getLength()-1);
    	NodeList relevantChildNotesLoc = relNodes.getChildNodes();
    	Node latNode = relevantChildNotesLoc.item(getNodeIndex(relevantChildNotesLoc, "lat"));
    	Node lngNode = relevantChildNotesLoc.item(getNodeIndex(relevantChildNotesLoc, "lng"));
    	Log.i(":::latNode:::", ""+latNode.getTextContent());
    	Log.i(":::lngNode:::", ""+lngNode.getTextContent());
    	double lngDbl = Double.parseDouble(lngNode.getTextContent());
    	double latDbl = Double.parseDouble(latNode.getTextContent());    	
		return new LatLng(latDbl,lngDbl);
    } catch (Exception e) {
        LatLng exceptStation = new LatLng(48.994523,8.400593);
    	return exceptStation;
    }
}

public String getDistanceText(Document doc) {
    /*
     * while (en.hasMoreElements()) { type type = (type) en.nextElement();
     * 
     * }
     */

    try {
        NodeList nl1;
        nl1 = doc.getElementsByTagName("distance");

        Node node1 = nl1.item(nl1.getLength() - 1);
        NodeList nl2 = null;
        nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "value"));
        Log.d("DistanceText", node2.getTextContent());
        return node2.getTextContent();
    } catch (Exception e) {
        return "-1";
    }
}

public int getDistanceValue(Document doc) {
    try {
        NodeList nl1 = doc.getElementsByTagName("distance");
        Node node1 = null;
        node1 = nl1.item(nl1.getLength() - 1);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "value"));
        Log.i("DistanceValue", node2.getTextContent());
        return Integer.parseInt(node2.getTextContent());
    } catch (Exception e) {
        return -1;
    }

}

public String getStartAddress(Document doc) {
    try {
        NodeList nl1 = doc.getElementsByTagName("start_address");
        Node node1 = nl1.item(0);
        Log.i("StartAddress", node1.getTextContent());
        return node1.getTextContent();
    } catch (Exception e) {
        return "-1";
    }

}

public String getEndAddress(Document doc) {
    try {
        NodeList nl1 = doc.getElementsByTagName("end_address");
        Node node1 = nl1.item(0);
        Log.i("StartAddress", node1.getTextContent());
        return node1.getTextContent();
    } catch (Exception e) {
        return "-1";        
}
}
public String getCopyRights(Document doc) {
    try {
        NodeList nl1 = doc.getElementsByTagName("copyrights");
        Node node1 = nl1.item(0);
        Log.i("CopyRights", node1.getTextContent());
        return node1.getTextContent();
    } catch (Exception e) {
    return "-1";
    }

}

public ArrayList<LatLng> getDirection(Document doc) {
    NodeList nl1, nl2, nl3;
    ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
    nl1 = doc.getElementsByTagName("step");
    if (nl1.getLength() > 0) {
        for (int i = 0; i < nl1.getLength(); i++) {
            Node node1 = nl1.item(i);
            nl2 = node1.getChildNodes();

            Node locationNode = nl2
                    .item(getNodeIndex(nl2, "start_location"));
            nl3 = locationNode.getChildNodes();
            Node latNode = nl3.item(getNodeIndex(nl3, "lat"));
            double lat = Double.parseDouble(latNode.getTextContent());
            Node lngNode = nl3.item(getNodeIndex(nl3, "lng"));
            double lng = Double.parseDouble(lngNode.getTextContent());
            listGeopoints.add(new LatLng(lat, lng));

            locationNode = nl2.item(getNodeIndex(nl2, "polyline"));
            nl3 = locationNode.getChildNodes();
            latNode = nl3.item(getNodeIndex(nl3, "points"));
            ArrayList<LatLng> arr = decodePoly(latNode.getTextContent());
            for (int j = 0; j < arr.size(); j++) {
                listGeopoints.add(new LatLng(arr.get(j).latitude, arr
                        .get(j).longitude));
            }

            locationNode = nl2.item(getNodeIndex(nl2, "end_location"));
            nl3 = locationNode.getChildNodes();
            latNode = nl3.item(getNodeIndex(nl3, "lat"));
            lat = Double.parseDouble(latNode.getTextContent());
            lngNode = nl3.item(getNodeIndex(nl3, "lng"));
            lng = Double.parseDouble(lngNode.getTextContent());
            listGeopoints.add(new LatLng(lat, lng));
        }
    }

    return listGeopoints;
}

private int getNodeIndex(NodeList nl, String nodename) {
    for (int i = 0; i < nl.getLength(); i++) {
        if (nl.item(i).getNodeName().equals(nodename))
            return i;
    }
    return -1;
}

private ArrayList<LatLng> decodePoly(String encoded) {
    ArrayList<LatLng> poly = new ArrayList<LatLng>();
    int index = 0, len = encoded.length();
    int lat = 0, lng = 0;
    while (index < len) {
        int b, shift = 0, result = 0;
        do {
            b = encoded.charAt(index++) - 63;
            result |= (b & 0x1f) << shift;
            shift += 5;
        } while (b >= 0x20);
        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
        lat += dlat;
        shift = 0;
        result = 0;
        do {
            b = encoded.charAt(index++) - 63;
            result |= (b & 0x1f) << shift;
            shift += 5;
        } while (b >= 0x20);
        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
        lng += dlng;

        LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
        poly.add(position);
    }
    return poly;
}
}