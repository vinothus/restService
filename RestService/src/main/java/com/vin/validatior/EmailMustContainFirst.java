package com.vin.validatior;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.springframework.core.env.Environment;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EmailMustContainFirst implements Validator<String> {

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
		 String keys=(String) envObj.get("EmailMustContainFirst.keys");
			if (keys != null && !keys.contains(",")) {
				if (paramoValue.contains((String) mapofValMap.get(keys))) {
					return true;
				} else {
					return false;
				}
			} else {
				String[] array = keys.split(",");
				for (int i = 0; i < array.length; i++) {
					String stringKey = array[i];
					if (stringKey != null) {
						if (paramoValue.contains((String) mapofValMap.get(stringKey))) {

						} else {
							returnVal = false;
						}
					}
				}
			}
			if(!returnVal)
			{
				return returnVal;
			}
	return true;	 
	}

	@Override
	public String getErrorMsg() {
		// TODO Auto-generated method stub
		return "Email $  Id must Contains First Name";
	}

}
