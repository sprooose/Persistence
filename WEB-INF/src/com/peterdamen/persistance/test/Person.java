package com.peterdamen.persistance.test;

import java.sql.Date;

import com.peterdamen.persistance.Persistable;

public class Person extends Persistable {

	String firstname;
	String lastname;
	Date   dob;
	String gender;
	Boolean cool;
	
	public Boolean getCool() {
		return cool;
	}
	public void setCool(Boolean cool) {
		this.cool = cool;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	
	public String toString() {
		
		return firstname + " " + lastname + " " + dob + " " + cool;
		
	}
}
