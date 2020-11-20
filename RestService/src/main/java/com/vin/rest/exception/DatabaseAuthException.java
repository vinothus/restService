package com.vin.rest.exception;

public class DatabaseAuthException  extends RuntimeException{
private static final long serialVersionUID = 1L;

	
	public DatabaseAuthException(String message) {
		super(message);
	}
	
	public DatabaseAuthException(String message, Throwable t) {
		super(message, t);
	}
}
