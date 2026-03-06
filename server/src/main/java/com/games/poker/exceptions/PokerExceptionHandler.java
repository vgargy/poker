package com.games.poker.exceptions;

import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.games.poker.response.WebResponse;

@ControllerAdvice
public class PokerExceptionHandler {

	@ExceptionHandler
	public ResponseEntity<?> handleException(Exception ex, WebRequest request) {
		return buildResponse(ex);
	}

	@ExceptionHandler(PokerException.class)
	public ResponseEntity<?> handlePokerException(PokerException ex, WebRequest request) {
		return buildResponse(ex);
	}

	private ResponseEntity<WebResponse<?>> buildResponse(Throwable throwable) {
		HttpStatus httpStatus = (throwable instanceof PokerException) ?
				((PokerException) throwable).getHttpStatus() : HttpStatus.INTERNAL_SERVER_ERROR;

		Throwable rootCause = NestedExceptionUtils.getMostSpecificCause(throwable);
		String exceptionMessage = rootCause != null ? rootCause.getMessage()
				: "Error while processing your request";

		return ResponseEntity.status(httpStatus).body(new WebResponse<>(null, exceptionMessage));
	}

}
