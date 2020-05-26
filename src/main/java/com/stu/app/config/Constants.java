package com.stu.app.config;

public class Constants {
	public static final String ACTIVATE_URL = "http://localhost:8080/api/account/activate?token=";
	public enum Response {
			OK(1),
			ERROR(2),
			UserExists(3),
			UserDoesNotExists(4),
			RecordExists(5);
			
			private final int value;

			Response(int value) {
				this.value = value;
			}

			public int getValue() {
				return value;
			}
		}
	 
	public enum Status {
			ACTIVE(1),
			INACTIVE(2),
			DELETE(3),
			COMPLETED(4),
			PENDING(5);			
			private final int value;

			Status(int value) {
				this.value = value;
			}

			public int getValue() {
				return value;
			}
		}
	public static final class User {
		public static final String STUDENT = "STUDENT"; 
		public static final String FACULTY = "FACULTY"; 
		public static final String ADMIN = "ADMIN"; 
		public static final String PARENT = "PARENT"; 
	}
}
