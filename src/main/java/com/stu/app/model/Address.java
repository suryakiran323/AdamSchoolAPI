package com.stu.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "address")
@Data
public class Address {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;
	@Column(name = "address1")
	String address1;
	@Column(name = "address2")
	String address2;
	@Column(name = "city")
	String city;
	@Column(name = "state")
	String state;
	@Column(name = "postalCode")
	String postalCode;
	@Column(name = "country")
	String country;

}
