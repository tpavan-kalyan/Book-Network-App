package com.book_network.exceptions;

import static com.book_network.exceptions.BusinessErrorCode.ACCOUNT_DISABLED;
import static com.book_network.exceptions.BusinessErrorCode.ACCOUNT_lOCKED;
import static com.book_network.exceptions.BusinessErrorCode.BAD_CREDENTIALS;
import static org.springframework.http.HttpStatus.*;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.mail.MessagingException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(LockedException.class)
	public ResponseEntity<ExceptionResponse> handleException(LockedException exception){
		return ResponseEntity
				.status(UNAUTHORIZED)
					.body(
						ExceptionResponse.builder()
						.businessErrorCode(ACCOUNT_lOCKED.getCode())
						.businessErrornDescription(ACCOUNT_lOCKED.getDescription())
						.error(exception.getMessage())
						.build()
						
						);
	
		
	}
	
	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<ExceptionResponse> handleException(DisabledException exception){
		return ResponseEntity
				.status(UNAUTHORIZED)
					.body(
						ExceptionResponse.builder()
							.businessErrorCode(ACCOUNT_DISABLED.getCode())
							.businessErrornDescription(ACCOUNT_DISABLED.getDescription())
							.error(exception.getMessage())
							.build()
						
						);
		
		
	}
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException exception){
		return ResponseEntity
				.status(UNAUTHORIZED)
				.body(
						ExceptionResponse.builder()
						.businessErrorCode(BAD_CREDENTIALS.getCode())
						.businessErrornDescription(BAD_CREDENTIALS.getDescription())
						.error(BAD_CREDENTIALS.getDescription())
						.build()
						
						);
		
		
	}
	@ExceptionHandler(MessagingException.class)
	public ResponseEntity<ExceptionResponse> handleException(MessagingException exception){
		return ResponseEntity
				.status(INTERNAL_SERVER_ERROR)
				.body(
						ExceptionResponse
						.builder()
						.error(exception.getMessage())
						.build()
						
						);
		
		
	}
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException exception){
		Set<String> errors = new HashSet<>();
		
		exception.getBindingResult().getAllErrors()
						.forEach(error -> {
							var errorMessage = error.getDefaultMessage();
							errors.add(errorMessage);
						});
		
		return ResponseEntity
				.status(BAD_REQUEST)
				.body(
						ExceptionResponse.builder()
						.validatationErrors(errors)
						.build()
						
						);
		
		
	}
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponse> handleException(Exception exception){
		
		//Log the exception
		exception.printStackTrace();
		
		return ResponseEntity
				.status(INTERNAL_SERVER_ERROR)
				.body(
						ExceptionResponse.builder()
						.businessErrornDescription("Internal error, contact the admin")
						.error(exception.getMessage())
						.build()
						
						);
		
		
	}
	
}
