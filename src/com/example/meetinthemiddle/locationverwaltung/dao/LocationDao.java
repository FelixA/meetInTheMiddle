package com.example.meetinthemiddle.locationverwaltung.dao;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import android.content.Context;

import com.example.meetinthemiddle.locationverwaltung.domain.Location;
import com.example.meetinthemiddle.locationverwaltung.domain.LocationList;
import com.example.meetinthemiddle.util.Constants;
import com.example.meetinthemiddle.util.WebServiceClient;

public class LocationDao {
		private Context context;
		private String url = "/rest/locations/";

		public LocationDao(Context ctx) {
			this.context = ctx;
		}

		public List<Location> selectAll() {
			LocationList list = WebServiceClient.get(LocationList.class,
					"/rest/locations", context, Constants.DATE_FORMAT_JAXB);

			return list.getLocations();
		}

		public Location findLocationById(Long id) {
			Location location = WebServiceClient.get(Location.class, url
					+ id, context, Constants.DATE_FORMAT_JAXB);
			return location;
		}

		public void create(String beschreibung) throws ParseException {
	  
			Location location = new Location(beschreibung);
			WebServiceClient.post(location, url, context, Constants.DATE_FORMAT_JAXB);
		}
		public void update(String beschreibung){
			Location location = new Location(beschreibung);
			WebServiceClient.put(location, url, context, Constants.DATE_FORMAT_JAXB);

		}
}
