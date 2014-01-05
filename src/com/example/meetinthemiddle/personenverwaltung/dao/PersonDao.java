package com.example.meetinthemiddle.personenverwaltung.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
	
	public Person findPersonById(Long id){
		Person person = WebServiceClient.get(Person.class, "/rest/persons/" + id, context, Constants.DATE_FORMAT_JAXB);
		return person;
	}
	
	public List<Person> findContactsById(Long id){
		PersonList list = WebServiceClient.get(PersonList.class, "/rest/persons/" + id + "/contacts", context, Constants.DATE_FORMAT_JAXB);
		return list.getPersons();
	}
	  
	public void create(String firstName, String lastName,Date birthday, String phone, String email,
			 String password, String interests) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_JAXB, Locale.getDefault());
    	String format = sdf.format(birthday);
		Date birthdaynew = sdf.parse(format);
		System.out.println(" formatted bday: "+ birthdaynew);
		Person person = new Person(firstName, lastName, birthdaynew, phone, email, password, interests);
		WebServiceClient.post(person, "/rest/persons", context, Constants.DATE_FORMAT_JAXB);
	}
}
