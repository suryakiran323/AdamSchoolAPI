package com.stu.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "student_subject")
@Data
	
public class StudentSubject {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;
	
	@ManyToOne
	@JoinColumn(name = "subjectid")
	Subject subject;
	
	@ManyToOne
	@JoinColumn(name = "studentid")
	Student student;
	
	@Column(name="status")
	String status;
}
