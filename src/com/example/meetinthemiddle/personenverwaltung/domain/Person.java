package com.example.meetinthemiddle.personenverwaltung.domain;

import static com.example.meetinthemiddle.util.Constants.NAMESPACE_PERSONENVERWALTUNG;

import java.util.Date;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;
@Root
@Namespace(reference=NAMESPACE_PERSONENVERWALTUNG)
public class Person {
	public Person(String firstName, String lastName, Date birthday,
			String phone, String email, int kontaktliste_fk, String password,
			String interests) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthday = birthday;
		this.phone = phone;
		this.email = email;
		Kontaktliste_fk = kontaktliste_fk;
		this.password = password;
		this.interests = interests;
	}
	public Person() {
	}
@Element
private String firstName;
@Element
private String lastName;
@Element
private Date birthday;
@Element
private String phone;
@Element
private String email;
@Element
private int Wohnort_fk;
@Element
private int privateinstellungen_fk;
@Element
private int Kontaktliste_fk = 1;
@Element
private String password;
@Element
private String interests;

public String getFirstName() {
	return firstName;
}
public void setFirstName(String firstName) {
	this.firstName = firstName;
}
public String getLastName() {
	return lastName;
}
public void setLastName(String lastName) {
	this.lastName = lastName;
}
public Date getBirthday() {
	return birthday;
}
public void setBirthday(Date birthday) {
	this.birthday = birthday;
}
public String getPhone() {
	return phone;
}
public void setPhone(String phone) {
	this.phone = phone;
}
public String getEmail() {
	return email;
}
public void setEmail(String email) {
	this.email = email;
}
public int getWohnort_fk() {
	return Wohnort_fk;
}
public void setWohnort_fk(int wohnort_fk) {
	Wohnort_fk = wohnort_fk;
}
public int getPrivateinstellungen_fk() {
	return privateinstellungen_fk;
}
public void setPrivateinstellungen_fk(int privateinstellungen_fk) {
	this.privateinstellungen_fk = privateinstellungen_fk;
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
public String getInterests() {
	return interests;
}
public void setInterests(String interests) {
	this.interests = interests;
}
@Override
public String toString() {
	return "Person [firstName=" + firstName + ", lastName=" + lastName
			+ ", birthday=" + birthday + ", phone=" + phone + ", email="
			+ email + ", Wohnort_fk=" + Wohnort_fk
			+ ", privateinstellungen_fk=" + privateinstellungen_fk
			+ ", Kontaktliste_fk=" + Kontaktliste_fk + ", password=" + password
			+ ", interests=" + interests + "]";
}

}
