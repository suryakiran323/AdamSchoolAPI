package com.stu.app.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stu.app.dto.MsgDTO;
import com.stu.app.service.MsgService;
import com.stu.app.util.AppResponse;
import com.stu.app.util.Validations;

@RestController
@RequestMapping("/api/")
@Log4j2
public class BaseController {
	@Autowired
	Validations validations;
	@Autowired
	MsgService msgService;
	
	@GetMapping("status")
	public ResponseEntity<AppResponse> status(){
		log.debug("Getting status call> to verify api is working or not");
		 return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS, "OK"), HttpStatus.OK);
	}
	
	/**
	 * @param postDTO
	 * @param request
	 * @return
	 */
	@PostMapping("send")
	public ResponseEntity<AppResponse> sendMsg(
			@Valid @RequestBody MsgDTO postDTO, HttpServletRequest request) {
		try{
		validations.validateMsgObj(postDTO);

		return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS,
				msgService.sendMsg(postDTO, request)), HttpStatus.OK);
		}catch( Exception e){
			return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS,
					"Invalid Data"), HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("msgs")
	public ResponseEntity<AppResponse> getMessage(HttpServletRequest request){
		return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS,
				msgService.getMessages(null, request)), HttpStatus.OK);
	}
	@GetMapping("msgs/{touser}")
	public ResponseEntity<AppResponse> getMessage(@PathVariable("touser") Integer touser, HttpServletRequest request){
		return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS,
				msgService.getMessages(touser,request)), HttpStatus.OK);
	}
	@GetMapping("markread/{msgid}")
	public ResponseEntity<AppResponse> updateMsg(@PathVariable("msgid")Integer msgId,  HttpServletRequest request){
		return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS,
				msgService.updateMsg(msgId)), HttpStatus.OK);
	}
	
	@GetMapping("feedback/{stu}")
	public ResponseEntity<AppResponse> getStuFeedbacks(@PathVariable("stu") Integer stuid, HttpServletRequest request){
		return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS,
				msgService.getStuFeedbacks(stuid, request)), HttpStatus.OK);
	} 
}
