package com.example.meetinthemiddle.personenverwaltung.dao;

import org.springframework.jdbc.core.JdbcTemplate;

import com.example.meetinthemiddle.personenverwaltung.domain.Person;

// Spring framework auf tomcat installieren
// Ueberpruefen:
// Gibts bibliotheken fuer REST?
public class PersonDao {
	private JdbcTemplate jdbcTemplate;  
	  
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {  
	    this.jdbcTemplate = jdbcTemplate;  
	}  
	  
	public int savePerson(Person e){  
	    String query="insert into person values('"+e.getVorname()+"','"+e.getNachname()+"','"+ e.getPassword() + "','" + e.getKontaktliste_fk() + "','" + e.getPassword()+"')";  
	    return jdbcTemplate.update(query);  
	}  
//	public int updateEmployee(Employee e){  
//	    String query="update employee set   
//	    name='"+e.getName()+"',salary='"+e.getSalary()+"' where id='"+e.getId()+"' ";  
//	    return jdbcTemplate.update(query);  
//	}  
//	public int deleteEmployee(Employee e){  
//	    String query="delete from employee where id='"+e.getId()+"' ";  
//	    return jdbcTemplate.update(query);  
//	}  
}
