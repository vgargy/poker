package com.games.poker.exceptions;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends PokerException {

	private static final long serialVersionUID = 9185623947642673153L;

	public AuthenticationException(String message) {
		super(message);
	}
	
	public HttpStatus getHttpStatus() {
		return HttpStatus.UNAUTHORIZED;
	}

}
