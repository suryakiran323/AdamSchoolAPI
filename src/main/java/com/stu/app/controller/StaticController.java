package com.stu.app.controller;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stu.app.service.StaticService;
import com.stu.app.util.AppResponse;

@RestController
@RequestMapping("/api/st/")
@Log4j2
public class StaticController {
	@Autowired
	StaticService staticService;
	
	@GetMapping("country")
	public ResponseEntity<AppResponse> getCountries(@RequestParam("k") String keyword, HttpServletRequest request){
		log.info("getting countries data for :"+keyword);
		 return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS, staticService.getCountries(keyword)), HttpStatus.OK);
	}
	@GetMapping("/state")
	public ResponseEntity<AppResponse> getStates(@RequestParam("c") String country, @RequestParam("k") String keyword, HttpServletRequest request){
		log.info("getting getStates data for :"+country+keyword);
		 return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS, staticService.getStates(country, keyword)), HttpStatus.OK);
	}
	
	@GetMapping("/city")
	public ResponseEntity<AppResponse> getCities(@RequestParam("c") String country,@RequestParam("s") String state, @RequestParam("k") String keyword, HttpServletRequest request){
		log.info("getting getCities data for :"+state+keyword);
		 return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS, staticService.getCities(country, state, keyword)), HttpStatus.OK);
	}
	
	@GetMapping("/courses")
	public ResponseEntity<AppResponse> getCourses(@RequestParam("k") String keyword, HttpServletRequest request){
		log.info("getting getCourses data for :" + keyword);
		 return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS, staticService.getCourses( keyword)), HttpStatus.OK);
	}
	
	@GetMapping("/courseInfo")
	public ResponseEntity<AppResponse> getCourseInfo(@RequestParam("k") String keyword, HttpServletRequest request){
		log.info("getting getCourses data for :" + keyword);
		 return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS, staticService.getCourseInfo( keyword)), HttpStatus.OK);
	}
	
}
