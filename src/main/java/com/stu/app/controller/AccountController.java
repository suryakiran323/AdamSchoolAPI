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

import com.stu.app.config.Constants;
import com.stu.app.dto.UserDTO;
import com.stu.app.dto.SignInResponseDTO;
import com.stu.app.dto.SignupDTO;
import com.stu.app.dto.UserCredentialsDTO;
import com.stu.app.exceptions.AccountsRTException;
import com.stu.app.service.AuthenticationService;
import com.stu.app.util.AppResponse;
import com.stu.app.util.Validations;

@RestController
@RequestMapping("/api/account/")
public class AccountController {
	
	  @Autowired
	  AuthenticationService authenticationService;

	  @Autowired
	  Validations validations;

	  /**
	   * controller for user sign in
	   *
	   * @param userCredentialsDTO - {@link UserCredentialsDTO} info to sign in the user
	   * @param request            - to get request information
	   * @param locale             - for message translation
	   * @return {@link SignInResponseDTO} as NexcoResponse
	   */
	  @PostMapping("login")
	  public ResponseEntity<AppResponse> authentication(@Valid @RequestBody UserCredentialsDTO userCredentialsDTO, HttpServletRequest request) {
	   try{
		   validations.validateSignInObj(userCredentialsDTO);
		   return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS, authenticationService.userSignin(userCredentialsDTO, request)), HttpStatus.OK);
	   	}catch(AccountsRTException ex ){
	    	return new ResponseEntity<>(new AppResponse(AppResponse.FAIL, ex.getMessage()), ex.getHttpStatus()); 
	    }
	  }
	  
	  /**
	 * @param postDTO
	 * @param request
	 * @return
	 */
	@PostMapping("parentsignup")
	  public ResponseEntity<AppResponse> createUser(@Valid @RequestBody SignupDTO postDTO,  HttpServletRequest request) {
	    postDTO.setUsertype(Constants.User.PARENT);
	    try{
	    	validations.validateSignupObj(postDTO);
		    return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS, authenticationService.createUser(postDTO, request)), HttpStatus.OK);
		 }catch(AccountsRTException ex ){
	    	return new ResponseEntity<>(new AppResponse(AppResponse.FAIL, ex.getMessage()), ex.getHttpStatus()); 
	    }
	  }
	
	 /**
		 * @param postDTO
		 * @param request
		 * @return
		 */
		@PostMapping("facultysignup")
		  public ResponseEntity<AppResponse> createfactutyUser(@Valid @RequestBody SignupDTO postDTO,  HttpServletRequest request) {
		    postDTO.setUsertype(Constants.User.FACULTY);
			validations.validateSignupObj(postDTO);
		    try{
		    	return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS, authenticationService.createUser(postDTO, request)), HttpStatus.OK);
		    }catch(AccountsRTException ex ){
		    	return new ResponseEntity<>(new AppResponse(AppResponse.FAIL, ex.getMessage()), ex.getHttpStatus()); 
		    }
		  }
		
	  /**
	 * @param postDTO
	 * @param request
	 * @return
	 */
	@PutMapping("user")
	  public ResponseEntity<AppResponse> updateUser(@Valid @RequestBody UserDTO agentData,  HttpServletRequest request) {
	    validations.validateUserObj(agentData);
	    try{
		    return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS, authenticationService.updateUser(agentData, request)), HttpStatus.OK);
		 }catch(AccountsRTException ex ){
	    	return new ResponseEntity<>(new AppResponse(AppResponse.FAIL, ex.getMessage()), ex.getHttpStatus()); 
	    }
	  } 
	
	 /**
	 * @param User DTO to create user in the system as type Faculty
	 * @param request
	 * @return
	 */
	@PostMapping("createfaculty")
	  public ResponseEntity<AppResponse> createfactuty(@Valid @RequestBody SignupDTO postDTO,  HttpServletRequest request) {
	    postDTO.setUsertype(Constants.User.FACULTY);
		validations.validateSignupObj(postDTO);
	    try{
	    	return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS, authenticationService.createUser(postDTO, request)), HttpStatus.OK);
	    }catch(AccountsRTException ex ){
	    	return new ResponseEntity<>(new AppResponse(AppResponse.FAIL, ex.getMessage()), ex.getHttpStatus()); 
	    }
	  }
	
	/**
	 * @param request
	 * @return
	 */
	@GetMapping("user/{userId}")
	  public ResponseEntity<AppResponse> getFacultyId(@PathVariable("userId") Integer id, HttpServletRequest request) {
	    return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS, authenticationService.getUserById(id, request)), HttpStatus.OK);
	  } 
	  /**
	 * @param request
	 * @return
	 */
	@GetMapping("users")
	  public ResponseEntity<AppResponse> getUsers(HttpServletRequest request) {
	    return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS, authenticationService.getUsers(null, request)), HttpStatus.OK);
	  }  
	
	 /**
	 * @param request
	 * @return
	 */
	  @GetMapping("faculties")
	  public ResponseEntity<AppResponse> getFacultyUsers(HttpServletRequest request) {
	    return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS, authenticationService.getUsers(Constants.User.FACULTY ,request)), HttpStatus.OK);
	  }  
		
		
	 /**
	 * @param request
	 * @return
	 */
	  @GetMapping("parents")
	  public ResponseEntity<AppResponse> getParentUsers(HttpServletRequest request) {
	    return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS, authenticationService.getUsers(Constants.User.PARENT, request)), HttpStatus.OK);
	  }  
	  /**
	 * @param request
	 * @return
	 */
	  @GetMapping("activate")
	  public ResponseEntity<AppResponse> getActivate(@RequestParam("token") Integer token,  HttpServletRequest request) {
	    return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS, authenticationService.(Constants.User.PARENT, request)), HttpStatus.OK);
	  } 
  
}
