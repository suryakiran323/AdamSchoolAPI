package com.stu.app;

import com.stu.app.config.Constants;


public class TestString {

	public static void main(String[] args) {
		String message = "Dear Parent,\nyour account is created successfully\n Click on the below link to activate.\n\n<a href='{actlink}'>Activate your account</a>"
				+ "\n\n Regards,\nAdams school,\n Administrator.";
		
		message = message.replace("{actlink}", Constants.ACTIVATE_URL+1);
		System.out.println(message);
		
	}
}
