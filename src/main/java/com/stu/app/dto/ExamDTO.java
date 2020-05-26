package com.stu.app.dto;

import java.util.Date;

import lombok.Data;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
@Component
@Data
@JsonInclude(value=Include.NON_NULL)
public class ExamDTO {
	Integer key;
	String name;
	Integer classId;
	String description;
	String type;
	String className;
	Date conductDtm;
	Date createDtm;
	Date updateDtm;
}
