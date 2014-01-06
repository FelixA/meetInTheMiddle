package com.example.meetinthemiddle.locationverwaltung.domain;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "locations")
public class LocationList {

	@ElementList(inline = true, entry = "location")
	private List<Location> locations;

	public List<Location> getLocations() {
		return locations;
	}

	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}

}
