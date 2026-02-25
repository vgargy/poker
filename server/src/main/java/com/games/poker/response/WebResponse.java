package com.games.poker.response;

public class WebResponse<T> {
	
	private T data;
	private String message;
	
	public WebResponse(T data, String message) {
		setData(data);
		setMessage(message);
	}
	
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

}
