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

/**
 * @author Admin
 *Student Class course details
 */
@Entity
@Table(name = "examdetails")
@Data
public class ExamDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;
	
	@Column(name = "name")
	String name;
	
	@ManyToOne
	@JoinColumn(name = "courseId")
	Course course;
	
	@Column(name = "description")
	String description;
	
	@Column(name = "type")
	String type;
	
	@Column(name = "conductdtm")
	Date conductDtm;
	
	@Column(name = "createDtm")
	Date createDtm;
	
	@Column(name = "updateDtm")
	Date updateDtm;
	
}
