package com.sabba.mancalademo.controller.errorhandling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ErrorHandlingController
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandlingController.class);

	@ExceptionHandler
	@ResponseBody
	public ResponseEntity<Exception> handleException(Exception e)
	{
		LOGGER.error("An unexpected error occurred during request", e);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.body(e);
	}
}
