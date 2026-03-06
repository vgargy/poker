package com.games.poker.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends PokerException {

	private static final long serialVersionUID = 7153522936640515803L;

	public NotFoundException(String message) {
		super(message);
	}

	public HttpStatus getHttpStatus() {
		return HttpStatus.NOT_FOUND;
	}

}
