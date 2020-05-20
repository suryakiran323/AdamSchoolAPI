package com.stu.app.dto;

import lombok.Data;

import org.springframework.stereotype.Component;
/**
 * @author Admin
 * Parent signup from data capturing via this Dto
 */
@Component
@Data
public class SignupDTO {
	Integer key;
	String firstName;
	String lastName;
	String middleName;
	String email;
	String relation;
	String designation;
	String address1;
	String address2;
	String city;
	String state;
	String postalCode;
	String country;
	String maincource;
	String secretkey;
	String usertype;
	
}
