package com.vin.processor;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UpperLowerParamProcessor implements ProcessParam {

	@Override
	public String doPreProcess(String... value) throws JsonParseException, JsonMappingException, IOException {
		 String paramoValue=value[0];
		 String apiKey=value[1];
		 String datasourceKey=value[2];
		 String serviceName=value[3];
		 String attrbMap=value[4];
		 String mapofVal=value[5];
		 String env=value[6];
		 ObjectMapper om=new ObjectMapper();
		 
		 Map<String,String> mapofValMap= om.readValue(mapofVal, new TypeReference<Map<String, String>>() {
			});
		 Map<String,Object> attrbMapMap= om.readValue(attrbMap, new TypeReference<Map<String, String>>() {
			});
		 Map<String,String> envObj=om.readValue(env, new TypeReference<Map<String, String>>() {
			});
		return paramoValue.toLowerCase();
	}

	@Override
	public String doPostProcess(String... value) throws JsonParseException, JsonMappingException, IOException {
		 String paramoValue=value[0];
		 String apiKey=value[1];
		 String datasourceKey=value[2];
		 String serviceName=value[3];
		 String attrbMap=value[4];
		 String mapofVal=value[5];
		 String env=value[6];
		 ObjectMapper om=new ObjectMapper();
		 
		 Map<String,Object> mapofValMap= om.readValue(mapofVal, new TypeReference<Map<String, String>>() {
			});
		 Map<String,Object> attrbMapMap= om.readValue(attrbMap, new TypeReference<Map<String, String>>() {
			});
		 Map<String,String> envObj=om.readValue(env, new TypeReference<Map<String, String>>() {
			});
		return paramoValue.toUpperCase();
	}

}
