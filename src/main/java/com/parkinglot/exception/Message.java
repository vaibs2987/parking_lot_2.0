
package com.parkinglot.exception;


public enum Message {
	PROCESSING_ERROR("Processing Error "),
	PARKING_ALREADY_EXIST("Sorry Parking Already Created, It cannot be created."), 
	PARKING_NOT_EXIST_ERROR("Sorry, Parking does not exist"),
	INVALID_FILE("Invalid File"),
	INVALID_VALUE("{variable} value is incorrect"),
	INVALID_REQUEST("Invalid Request");
	
	private String message;

	private Message(String message) {
		this.message = message;
	}
	

	public String getMessage() {
		return message;
	}
}
