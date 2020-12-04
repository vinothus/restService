package com.vin.validatior;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface Validator <T> {
	public boolean isValid(String... value) throws JsonParseException, JsonMappingException, IOException;
	public String getErrorMsg(); 

}
