package com.example.meetinthemiddle.personenverwaltung.dao;

import java.sql.Date;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.Context;

import com.example.meetinthemiddle.personenverwaltung.domain.Person;
import com.example.meetinthemiddle.personenverwaltung.domain.PersonList;
import com.example.meetinthemiddle.util.Constants;
import com.example.meetinthemiddle.util.WebServiceClient;

public class PersonDao {
	
	private Context context;
	
	public PersonDao(Context ctx) {
		this.context = ctx;
	}
	
	
	public List<Person> selectAll() {
		PersonList list = WebServiceClient.get(PersonList.class, "/rest/persons", context, Constants.DATE_FORMAT_JAXB);
		
		return list.getPersons();
	}
	  
	public void create(String firstName, String lastName,Date birthday, String phone, String email,
			Integer kontaktliste, String password, String interests) {
		Person person = new Person(firstName, lastName, birthday, phone, email, kontaktliste, password, interests);
		WebServiceClient.post(person, "/rest/persons/create", context);
	}

	public List<Person> validate(String email, String password) {
		// TODO
		return null;
	}
}
