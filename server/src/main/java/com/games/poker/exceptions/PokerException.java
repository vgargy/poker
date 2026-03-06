package com.games.poker.exceptions;

import org.springframework.http.HttpStatus;

public class PokerException extends RuntimeException {


	private static final long serialVersionUID = -1621977726123135861L;

	public PokerException(String message) {
		super(message);
	}
	
	public HttpStatus getHttpStatus() {
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}

}
