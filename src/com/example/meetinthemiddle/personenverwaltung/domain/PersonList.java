package com.example.meetinthemiddle.personenverwaltung.domain;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "persons")
public class PersonList {

	@ElementList(inline = true, entry = "person")
	private List<Person> persons;

	public List<Person> getPersons() {
		return persons;
	}

	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}
	
	
}
