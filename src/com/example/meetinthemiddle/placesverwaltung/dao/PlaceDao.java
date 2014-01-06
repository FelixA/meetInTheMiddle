package com.example.meetinthemiddle.placesverwaltung.dao;

import java.text.ParseException;
import java.util.List;
import android.content.Context;

import com.example.meetinthemiddle.placesverwaltung.domain.Place;
import com.example.meetinthemiddle.placesverwaltung.domain.PlaceList;
import com.example.meetinthemiddle.util.Constants;
import com.example.meetinthemiddle.util.WebServiceClient;

public class PlaceDao {
private Context context;
private String uri = "/rest/places/";
	
	public PlaceDao(Context ctx) {
		this.context = ctx;
	}
	
	
	public List<Place> selectAll() {
		PlaceList list = WebServiceClient.get(PlaceList.class, uri, context, Constants.DATE_FORMAT_JAXB);
		
		return list.getPlaces();
	}
	
	public Place findPlaceById(Long id){
		Place place = WebServiceClient.get(Place.class, uri + id, context, Constants.DATE_FORMAT_JAXB);
		return place;
	}
	
	  
	public void create(String stadtname, String plz) throws ParseException {
      
		Place place = new Place(stadtname,plz);
		WebServiceClient.post(place, uri, context, Constants.DATE_FORMAT_JAXB);
	}
}
