package com.stu.app.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "user_type")
@Data
public class Users {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;
	
	@Column(name = "type")
	String type; // type can be a parent/faculty/admin
	
	@Column(name = "first_name")
	String firstName;
	
	@Column(name = "last_name")
	String LastName;
	
	@Column(name = "middle_name")
	String middleName;
	
	@Column(name = "email")
	String email;
	
	@Column(name = "company")
	String company;
	
	@Column(name = "designation")
	String designation;
	
	@ManyToOne
	@JoinColumn(name = "addressid")
	Address address;
	
	@Column(name = "password")
	String password;
	
	@Column(name = "status")
	String status;
	
	@Column(name = "createDtm")
	Date createDtm;
	
	@Column(name = "updateDtm")
	Date updateDtm;
}
