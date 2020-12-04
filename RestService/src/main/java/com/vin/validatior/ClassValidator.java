package com.vin.validatior;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClassValidator implements Validator<String>{

	@Override
	public boolean isValid(String... value) throws JsonParseException, JsonMappingException, IOException {
		 String paramoValue=value[0];
		 String apiKey=value[1];
		 String datasourceKey=value[2];
		 String serviceName=value[3];
		 String attrbMap=value[4];
		 String mapofVal=value[5];
		 String env=value[6];
		 ObjectMapper om=new ObjectMapper();
		 boolean returnVal=true;
		 Map<String,String> mapofValMap= om.readValue(mapofVal, new TypeReference<Map<String, String>>() {
			});
		 Map<String,String> attrbMapMap= om.readValue(attrbMap, new TypeReference<Map<String, String>>() {
			});
		 Map<String,String> envObj=om.readValue(env, new TypeReference<Map<String, String>>() {
			});
			if (paramoValue != null && !paramoValue.equalsIgnoreCase("null")) {
				try {
					com.vin.validatior.Validator<String> validator = (com.vin.validatior.Validator) Class
							.forName(paramoValue).newInstance();
				} catch (Exception e) {
					return false;
				}
			}
		return true;
	}

	@Override
	public String getErrorMsg() {
		// TODO Auto-generated method stub
		return "Not a Valid Class or Not Present in Current run time ";
	}

}
