package com.book_network.exceptions;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExceptionResponse {
	
	private Integer businessErrorCode;
	private String businessErrornDescription;
	private String error;
	private Set<String> validatationErrors;
	private Map<String, String> errors;
	
}
