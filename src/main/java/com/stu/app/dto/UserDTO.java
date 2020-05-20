package com.stu.app.dto;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
@Component
@Data
@JsonInclude(value=Include.NON_NULL)
public class UserDTO {
	Integer key;
	
	String firstName;
	String LastName;
	String middleName;
	String email;
	String designation;
	String address1;
	String address2;
	String city;
	String state;
	String postalCode;
	String country;
	String type;
	Date createDtm;
	
	Date updateDtm;
}
