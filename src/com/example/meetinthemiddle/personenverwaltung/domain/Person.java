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
			String phone, String email, String password,
			String interests) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthday = birthday;
		this.phone = phone;
		this.email = email;
//		Wohnort_fk = wohnort_fk;
		this.password = password;
		this.interests = interests;
	}
	public Person() {
	}
@Element(required=false)
private Long id; 
@Element
private String firstName;
@Element
private String lastName;
@Element
private Date birthday;
@Element(required=false)
private String phone;
@Element
private String email;
@Element(required=false)
private Long Wohnort_fk;
@Element
private String password;
@Element(required=false)
private String interests;
public Long getId() {
	return id;
}
public void setId(Long id) {
	this.id = id;
}
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
public Long getWohnort_fk() {
	return Wohnort_fk;
}
public void setWohnort_fk(Long wohnort_fk) {
	Wohnort_fk = wohnort_fk;
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
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((birthday == null) ? 0 : birthday.hashCode());
	result = prime * result + ((email == null) ? 0 : email.hashCode());
	result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
	result = prime * result + ((id == null) ? 0 : id.hashCode());
	result = prime * result + ((interests == null) ? 0 : interests.hashCode());
	result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
	result = prime * result + ((password == null) ? 0 : password.hashCode());
	result = prime * result + ((phone == null) ? 0 : phone.hashCode());
	return result;
}
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	Person other = (Person) obj;
	if (Wohnort_fk != other.Wohnort_fk)
		return false;
	if (birthday == null) {
		if (other.birthday != null)
			return false;
	} else if (!birthday.equals(other.birthday))
		return false;
	if (email == null) {
		if (other.email != null)
			return false;
	} else if (!email.equals(other.email))
		return false;
	if (firstName == null) {
		if (other.firstName != null)
			return false;
	} else if (!firstName.equals(other.firstName))
		return false;
	if (id == null) {
		if (other.id != null)
			return false;
	} else if (!id.equals(other.id))
		return false;
	if (interests == null) {
		if (other.interests != null)
			return false;
	} else if (!interests.equals(other.interests))
		return false;
	if (lastName == null) {
		if (other.lastName != null)
			return false;
	} else if (!lastName.equals(other.lastName))
		return false;
	if (password == null) {
		if (other.password != null)
			return false;
	} else if (!password.equals(other.password))
		return false;
	if (phone == null) {
		if (other.phone != null)
			return false;
	} else if (!phone.equals(other.phone))
		return false;
	return true;
}
@Override
public String toString() {
	return "Person [id=" + id + ", firstName=" + firstName + ", lastName="
			+ lastName + ", birthday=" + birthday + ", phone=" + phone
			+ ", email=" + email + ", Wohnort_fk=" + Wohnort_fk + ", password="
			+ password + ", interests=" + interests + "]";
}


}
