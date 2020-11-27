package com.vin.validatior;

public class EmailMustContainFirst implements Validator<String> {

	@Override
	public boolean isValid(String... value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getErrorMsg() {
		// TODO Auto-generated method stub
		return "Email $  Id must Contains First Name";
	}

}
