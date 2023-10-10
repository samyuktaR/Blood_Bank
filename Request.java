package com.example.bloodbank;

import java.util.Optional;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class Request {
	@Id
	@GeneratedValue
	
	private Integer id;
	
	private String bg;
	
	private String quantity;
	
	private String location;
	
	private String status;
	
	private String recipientid;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Recipient recipient;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Donor donor;
	
	public Integer getId() {
		return id;
	}
	
	public void setRequestId(Integer id) {
		this.id = id;
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
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getRecipientId() {
		return recipientid;
	}
	
	public void setRecipientId(String recipientid) {
		this.recipientid = recipientid;
	}
	
	public Recipient getRecipient() {
		return recipient;
	}
	
	public void setRecipient(Recipient recipient) {
		this.recipient = recipient;
	}
	
	public Donor getDonor() {
		return donor;
	}
	
	public void setDonor(Donor donor) {
		this.donor = donor;
	}
	
}
