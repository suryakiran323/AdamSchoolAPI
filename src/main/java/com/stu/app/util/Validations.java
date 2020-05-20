package com.stu.app.util;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import lombok.extern.log4j.Log4j2;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.stu.app.dto.SignupDTO;
import com.stu.app.dto.StudentDTO;
import com.stu.app.dto.UserCredentialsDTO;
import com.stu.app.dto.UserDTO;
import com.stu.app.exceptions.AccountsRTException;


@Component
@Log4j2
public class Validations {

	 /**
	   * method to validate user email password or otp based on request
	   *
	   * @param userCredentialsDTO
	   * @param locale
	   */
	  public void validateSignInObj(@Valid UserCredentialsDTO userCredentialsDTO) {
		  log.info(userCredentialsDTO);
		  
	    if ((userCredentialsDTO.getPassword() != null && !userCredentialsDTO.getPassword().isEmpty())) {
	      validateNormalField(userCredentialsDTO.getPassword(), "password");
	    } else {
	      validateNormalField(userCredentialsDTO.getPassword(), "password or email");
	    }
	  }
	  
	  /**
	   * validate whether input string value is valid are not
	   *
	   * @param input   - the input string to check
	   * @param msgName - for error message
	   * @param locale  - for message translation
	   */
	  public void validateNormalField(Object input, String msgName) {
	    Map<String, Object> object = new HashMap<>();
	    log.info("input string : " + input);
	    if (input instanceof String) {
	      String inputString = (String) input;
	      if (StringUtils.isBlank(inputString)) {
	        object.put(msgName, "REQUIRED");
	      } else if (inputString.length() > 250) {
	        object.put(msgName, "TOO_LONG");
	      }
	    } else {
	      object.put(msgName, "Invalid_INPUT_FORMAT");
	    }
	    if (object.size() != 0) {
	      throw new AccountsRTException(HttpStatus.BAD_REQUEST, object.get(msgName).toString());
	    }
	  }

	public void validateSignupObj(@Valid SignupDTO postDTO) {
		 if ((postDTO.getSecretkey() != null && !postDTO.getSecretkey().isEmpty())) {
		      validateNormalField(postDTO.getSecretkey(), "password");
		    } else {
		      validateNormalField(postDTO.getSecretkey(), "password or email");
		    }
		 if (StringUtils.isBlank(postDTO.getEmail())) {
			  throw new AccountsRTException(HttpStatus.BAD_REQUEST,  "Email is required");
		 } 
		 if (StringUtils.isBlank(postDTO.getUsertype())) {
			  throw new AccountsRTException(HttpStatus.BAD_REQUEST,  "User type is not defined");
		  } 
	}

	public void validateStudentObj(@Valid StudentDTO postDTO) {
		if(StringUtils.isBlank(postDTO.getFirstName())){
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,  "FIrst Name is required");
		}
		if(StringUtils.isBlank(postDTO.getLastName())){
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,  "Last Name is required");
		}if(postDTO.getCourseId()!=null && postDTO.getCourseId()>0){
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,  "Please select class from the dropdown");
		}if(postDTO.getParentId()!=null && postDTO.getParentId()>0){
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,  "Please select parent name from the dropdown");
		}
	}

	public void validateUserObj(@Valid SignupDTO postDTO) {
		// TODO Auto-generated method stub
		
	}
	
	public void validateUserObj(@Valid UserDTO postDTO) {
		// TODO Auto-generated method stub
		
	}
	
}
