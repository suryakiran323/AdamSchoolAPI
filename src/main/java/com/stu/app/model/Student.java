package com.stu.app.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "student")
@Data
@EqualsAndHashCode
public class Student {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;
	
	@Column(name = "first_name")
	String firstName;
	
	@Column(name = "last_name") 
	String lastName;
	
	@Column(name = "middle_name")
	String middleName;
	
	@Column(name = "email")
	String email;
	
	@Column(name = "dob")
	Date dob;
	
	@Column(name = "gender")
	String gender;
	
	@ManyToOne
	@JoinColumn(name = "courseid")
	Course course;
	
	@ManyToOne
	@JoinColumn(name = "parentid")
	Users parent;
	
	@Column(name="relation")
	String relation;
	
	@Column(name = "status")
	String status;
	
	
	@Column(name = "createDtm")
	Date createDtm;
	
	@Column(name = "updateDtm")
	Date updateDtm;
	
	@OneToMany(mappedBy = "student", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private Set<StudentSubject> studentSubjects = new HashSet<>();
	
}
