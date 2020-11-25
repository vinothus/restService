package com.vin.validatior;

public interface Validator <T> {
	public boolean isValid(String... value);
	public String getErrorMsg(); 

}
