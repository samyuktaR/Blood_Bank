package com.example.bloodbank;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity // This tells Hibernate to make a table out of this class
public class Donor {
	@Id
	@GeneratedValue
	
	private Integer id;

	private String name;

	private String age;

	private String bg;

	private String location;

	private String mobile;

	private String email;

	private String lastdonated;
	
	public Integer getId() {
	    return id;
	  }
	  
	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getBg() {
		return bg;
	}

	public void setBg(String bg) {
		this.bg = bg;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLastdonated() {
		return lastdonated;
	}

	public void setLastdonated(String lastdonated) {
		this.lastdonated = lastdonated;
	}
}

