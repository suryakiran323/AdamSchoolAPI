package com.stu.app.dto;

import java.util.Date;

import lombok.Data;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Component
@Data
@JsonInclude(value=Include.NON_NULL)
public class MsgDTO {
	Integer fromUserId;
	String fromName;
	Integer toUserId;
	String toName;
	Integer studentId;
	String studentName;
	String message;
	Boolean viewInd;
	Date createDtm;
}
