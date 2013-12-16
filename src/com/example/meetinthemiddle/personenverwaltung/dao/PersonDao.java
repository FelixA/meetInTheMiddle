package com.example.meetinthemiddle.personenverwaltung.dao;

import java.sql.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.example.meetinthemiddle.personenverwaltung.PersonMapper;
import com.example.meetinthemiddle.personenverwaltung.domain.Person;

// Spring framework auf tomcat installieren
// Ueberpruefen:
// Gibts bibliotheken fuer REST?
public class PersonDao {
	
	DataSource dataSource;
	
	@Autowired(required = true)
	public void setDataSource(DataSource ds) {
		dataSource = ds;
	}
	private JdbcTemplate jdbcTemplate;  
	  
	public List<Person> selectAll() {
		JdbcTemplate select = new JdbcTemplate(dataSource);
		return select.query("select * from PERSON", new PersonMapper());
	}
	  
	public void create(String firstName, String lastName,Date birthday, String phone, String email,
			String kontaktliste, String password, String interests) {
		JdbcTemplate insert = new JdbcTemplate(dataSource);
		insert.update(
				"INSERT INTO PERSON (ID, VORNAME, NACHNAME,GEBURTSDATUM,TELEFONNR,EMAIL,KONTAKTLISTE_FK,PASSWORD,INTERESSEN) VALUES(SEQUENCE_PERSON_PK.NEXTVAL,?,?,?,?,?,?,?,?)",
				new Object[] { firstName, lastName, birthday, phone, email, kontaktliste, 
						password, interests });
	}

	public List<Person> validate(String email, String password) {
		JdbcTemplate select = new JdbcTemplate(dataSource);
		return select
				.query("Select EMAIL, PASSWORD from Person where EMAIL = ? AND PASSWORD = ?);",
						new PersonMapper());
	}
}
