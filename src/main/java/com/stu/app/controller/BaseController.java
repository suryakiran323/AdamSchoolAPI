package com.stu.app.controller;

import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stu.app.util.AppResponse;

@RestController
@RequestMapping("/api/")
@Log4j2
public class BaseController {
	@GetMapping("status")
	public ResponseEntity<AppResponse> status(){
		log.debug("Getting status call> to verify api is working or not");
		 return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS, "OK"), HttpStatus.OK);
	}
	
}
