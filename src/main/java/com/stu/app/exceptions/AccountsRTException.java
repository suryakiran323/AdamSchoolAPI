package com.stu.app.exceptions;

import org.springframework.http.HttpStatus;



public class AccountsRTException  extends RuntimeException {

	private static final long serialVersionUID = -3578710730813801715L;

	private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
	private final String message ;

    public AccountsRTException(HttpStatus httpStatus, String message) {
    	this.message = message;
    	this.httpStatus = httpStatus;
    }

    public AccountsRTException( String message) {
    	this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
	public String getMessage() {
		return message;
	}

}
