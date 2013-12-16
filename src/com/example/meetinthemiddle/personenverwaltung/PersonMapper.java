package com.example.meetinthemiddle.personenverwaltung;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import com.example.meetinthemiddle.personenverwaltung.domain.Person;

 
public class PersonMapper implements RowMapper<Person>{
	 
	     /*implement abstract method for declaring mapping
	     *between POJO attributes and relational table attributes
	     */
	    public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
	        Person person=new Person();
	        person.setFirstName(rs.getString("VORNAME"));
	        person.setLastName(rs.getString("NACHNAME"));
	        person.setBirthday(rs.getDate("GEBURTSDATUM"));
	        person.setPhone(rs.getString("TELEFONNR"));
	        person.setEmail(rs.getString("EMAIL"));
	        person.setPassword(rs.getString("PASSWORD"));
	        person.setInterests(rs.getString("INTERESSEN"));
	        return person;
	    }
	}
