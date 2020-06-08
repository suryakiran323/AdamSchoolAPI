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
@Table(name = "examresults")
@Data
public class ExamResults {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;
	
	@ManyToOne
	@JoinColumn(name = "examid")
	ExamDetails examDetails;
	
	@ManyToOne
	@JoinColumn(name = "studentId")
	Student student;
	
	@ManyToOne
	@JoinColumn(name = "subjectId")
	Subject subject;
	
	@Column(name = "marks")
	Integer marks;
	
	@Column(name = "createDtm")
	Date createDtm;
	
	@Column(name = "updateDtm")
	Date updateDtm;
	
}
