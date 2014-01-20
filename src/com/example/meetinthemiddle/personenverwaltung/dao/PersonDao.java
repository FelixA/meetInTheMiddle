package com.example.meetinthemiddle.personenverwaltung.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.util.Log;

import com.example.meetinthemiddle.meetingverwaltung.domain.Meeting;
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
			 String password, String interests, String androidId) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_JAXB, Locale.getDefault());
    	String format = sdf.format(birthday);
		Date birthdaynew = sdf.parse(format);
		Log.v(PersonDao.class.getName()," formatted bday: "+ birthdaynew);
		Person person = new Person(firstName, lastName, birthdaynew, phone, email, password, interests, androidId);
		WebServiceClient.post(person, "/rest/persons", context, Constants.DATE_FORMAT_JAXB);
	}
	
	public void update(Long id, String firstName, String lastName,Date birthday, String phone, String email,
			  String interests){
		Person person = new Person(firstName,lastName,birthday, phone,email, interests);
		person.setId(id);
		WebServiceClient.put(person, "/rest/persons", context, Constants.DATE_FORMAT_JAXB);
	}
	public void update(Long id, String firstName, String lastName,Date birthday, String phone, String email, String password,
			 String interests, String androidId){
		Person person = new Person(firstName,lastName,birthday, phone,email,password, interests, androidId);
		person.setId(id);
		WebServiceClient.put(person, "/rest/persons", context, Constants.DATE_FORMAT_JAXB);
	}


	public Person findPersonByFirstLastName(String firstName, String lastName) {
		Person person = WebServiceClient.get(Person.class, "/rest/persons/" + firstName + "/" + lastName, context, Constants.DATE_FORMAT_JAXB);
		return person;
	}


	public void createKontakt(Long long1, Long long2) {
		WebServiceClient.postNewContact(long1,long2, "/rest/persons/contacts/"+ long1 + "/" + long2, context, Constants.DATE_FORMAT_JAXB);
	}
}
