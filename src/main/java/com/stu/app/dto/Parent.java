package com.stu.app.dto;

import lombok.Data;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Component
@Data
@JsonInclude(value=Include.NON_NULL)
public class Parent {
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
	String secretkey;
	String usertype;
}
