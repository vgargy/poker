package com.games.poker.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidDataException extends PokerException {


	private static final long serialVersionUID = -1542472319328993211L;

	public InvalidDataException(String message) {
		super(message);
	}

	public HttpStatus getHttpStatus() {
		return HttpStatus.BAD_REQUEST;
	}

}
