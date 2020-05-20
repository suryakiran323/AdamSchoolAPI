package com.stu.app.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stu.app.dto.StudentDTO;
import com.stu.app.service.StudentService;
import com.stu.app.util.AppResponse;
import com.stu.app.util.Validations;

@RestController
@RequestMapping("/api/stu/")
public class StudentController {

	  @Autowired
	  Validations validations;
	  @Autowired
	  StudentService studentService;
	  /**
	 * @param postDTO
	 * @param request
	 * @return
	 */
	@PostMapping("create")
	  public ResponseEntity<AppResponse> createStudent(@Valid @RequestBody StudentDTO postDTO,  HttpServletRequest request) {
		validations.validateStudentObj(postDTO);
	    
	    return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS, studentService.createStudent(postDTO, request)), HttpStatus.OK);
	  }
	
		
	  /**
	 * @param postDTO
	 * @param request
	 * @return
	 */
	@PutMapping("student")
	  public ResponseEntity<AppResponse> updateStudent(@Valid @RequestBody StudentDTO postDTO,  HttpServletRequest request) {
		validations.validateStudentObj(postDTO);
	    return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS, studentService.updateStudent(postDTO, request)), HttpStatus.OK);
	  }  
	/**
	 * @param request
	 * @return
	 */
	@GetMapping("student/{userId}")
	  public ResponseEntity<AppResponse> getStudentId(@PathVariable("userId") Integer id, HttpServletRequest request) {
	    return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS, studentService.getStudentById(id, request)), HttpStatus.OK);
	  } 
	 
	
	@GetMapping("students/{parentId}")
	  public ResponseEntity<AppResponse> getUsers(@PathVariable("parentId") Integer parentId, 
			  @RequestParam(required=false, name="clourseName"  ) String courseName,
			  @RequestParam(required=false, name="name") String name,
			  HttpServletRequest request) {
	    return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS, studentService.getStudents(parentId,courseName+'%', name+'%', request)), HttpStatus.OK);
	  }  
	  
}
