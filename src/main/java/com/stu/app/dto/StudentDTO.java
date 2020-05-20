package com.stu.app.dto;

import java.util.Date;

import lombok.Data;

import org.springframework.stereotype.Component;
@Component
@Data
public class StudentDTO {
	Integer key;
	String firstName;
	String lastName;
	String middleName;
	String email;
	Date dob;
	String gender;
	Integer courseId;//selected course ID
	Integer parentId;
	String statusStr;
	String courseName;
	String parentName;
	
}
