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
 *Messages Class
 */
@Entity
@Table(name = "messages")
@Data
public class Messages {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;
	
	@ManyToOne
	@JoinColumn(name ="fromId")
	Users fromUserId;
	
	@ManyToOne
	@JoinColumn(name ="toId")
	Users toUserId;
	@Column(name="message")
	String message;
	
	@Column(name= "viewInd")
	Boolean viewInd;
	
	@Column(name = "createDtm")
	Date createDtm;
	
	@Column(name = "updateDtm")
	Date updateDtm;
	
}
