package com.stu.app.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
@Component
@Data
@JsonInclude(value=Include.NON_NULL)
public class StudentDTO {
	Integer key;
	String firstName;
	String lastName;
	String middleName;
	Date dob;
	String relation;
	String gender;
	Integer courseId;//selected course ID
	List<String> subjects;
	String subject;
	Integer parentId;
	String status;
	String courseName;
	Parent parentInfo;
	
	
}
