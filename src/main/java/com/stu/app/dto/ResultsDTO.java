package com.stu.app.dto;

import java.util.Date;

import lombok.Data;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
@Component
@Data
@JsonInclude(value=Include.NON_NULL)
public class ResultsDTO {
	Integer key;
	Integer examId;
	String examType;
	
	Integer studentId;
	String studentName;
	
	Integer english;
	Integer maths;
	Integer writing;
	Integer genAbility;
	Integer others;
	Date conductDtm;
}
