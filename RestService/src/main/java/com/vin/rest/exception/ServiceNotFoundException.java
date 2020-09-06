package com.vin.rest.exception;

public class ServiceNotFoundException  extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public ServiceNotFoundException(String message) {
		super(message);
	}
	
	public ServiceNotFoundException(String message, Throwable t) {
		super(message, t);
	}
}
