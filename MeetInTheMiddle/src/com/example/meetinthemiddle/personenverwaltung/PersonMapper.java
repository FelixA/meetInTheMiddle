package com.example.meetinthemiddle.personenverwaltung;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

 
public class PersonMapper implements RowMapper<Person>{
	 
	     /*implement abstract method for declaring mapping
	     *between POJO attributes and relational table attributes
	     */
	    public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
	        Person person=new Person();
	        person.setVorname(rs.getString("VORNAME"));
	        person.setNachname(rs.getString("NACHNAME"));
	        person.setEmail(rs.getString("EMAIL"));
	        person.setPassword(rs.getString("PASSWORD"));
	        return person;
	    }
	}
