package com.stu.app.dto;

import lombok.Data;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
/**
 * @author Admin
 * Faculty/Parent sign up DTO to capture ui form data
 */
@Component
@Data
@JsonInclude(value=Include.NON_NULL)
public class SignupDTO {
	Integer key;
	String firstName;
	String lastName;
	String middleName;
	String email;
	String relation;
	String designation;
	String facultySubject;
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
