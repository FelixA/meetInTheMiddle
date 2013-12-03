package com.example.meetinthemiddle.personenverwaltung;

public class Person {
	public Person() {
	}
public Person(String vorname, String nachname, String email,
			 String password) {
		super();
		this.vorname = vorname;
		this.nachname = nachname;
		this.email = email;
		Kontaktliste_fk = 1;
		this.password = password;
	}
private String vorname;
private String nachname;
private String email;
private int Kontaktliste_fk = 1;
private String password;
public String getVorname() {
	return vorname;
}
public void setVorname(String vorname) {
	this.vorname = vorname;
}
public String getNachname() {
	return nachname;
}
public void setNachname(String nachname) {
	this.nachname = nachname;
}
public String getEmail() {
	return email;
}
public void setEmail(String email) {
	this.email = email;
}
public int getKontaktliste_fk() {
	return Kontaktliste_fk;
}
public void setKontaktliste_fk(int kontaktliste_fk) {
	Kontaktliste_fk = kontaktliste_fk;
}
public String getPassword() {
	return password;
}
public void setPassword(String password) {
	this.password = password;
}
@Override
public String toString() {
	return "Person [vorname=" + vorname + ", nachname=" + nachname + ", email="
			+ email + ", password=" + password + "]";
}
}
