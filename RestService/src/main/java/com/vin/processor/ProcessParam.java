package com.vin.processor;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface ProcessParam {

	public String doPreProcess(String...params) throws JsonParseException, JsonMappingException, IOException;
	public String doPostProcess(String...params) throws JsonParseException, JsonMappingException, IOException;
}
