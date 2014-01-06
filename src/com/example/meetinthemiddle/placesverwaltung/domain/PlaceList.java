package com.example.meetinthemiddle.placesverwaltung.domain;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;


@Root(name = "places")
public class PlaceList {

	@ElementList(inline = true, entry = "place")
	private List<Place> places;

	public List<Place> getPlaces() {
		return places;
	}

	public void setPlaces(List<Place> places) {
		this.places = places;
	}

}
