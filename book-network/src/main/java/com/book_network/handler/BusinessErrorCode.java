package com.book_network.handler;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.Getter;

public enum BusinessErrorCode 
{
	
	NO_CODE(0, NOT_IMPLEMENTED, "No code"),
	INCORRECT_CURRENT_PASSWORD(300,BAD_REQUEST, "Current  pssword is incorrect"),
	NEW_PASSWORD_DOES_NOT_MATCH(301,BAD_REQUEST, "The new password does not match"),
	ACCOUNT_lOCKED(302, FORBIDDEN, "User account is locked"),
	ACCOUNT_DISABLED(303, FORBIDDEN, "User account is diabled"),
	BAD_CREDENTIALS(304, FORBIDDEN, "Login and / or password is incorrect");
	@Getter
	private final int code;
	@Getter
	private final String description;
	@Getter
	private final HttpStatus httpStatus;
	
	private BusinessErrorCode(int code, HttpStatus httpStatus, String description) {
		this.code = code;
		this.description = description;
		this.httpStatus = httpStatus;
	}
	
	
	
}
