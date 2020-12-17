package com.vin.validatior;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vin.rest.repository.EmployeeRepositaryImpl;
@Component
public class ConnectionValidator implements Validator<String> {

	@Autowired
	EmployeeRepositaryImpl employeeRepositaryImpl;
	
	String errorMsg="";
	@Override
	public boolean isValid(String... value) throws JsonParseException, JsonMappingException, IOException {
		 String passToken=value[0];
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
			try {
				EmployeeRepositaryImpl.jdbcTemplateMap.remove(datasourceKey);
				JdbcTemplate template =employeeRepositaryImpl.setUserDataStore(apiKey, datasourceKey, passToken);
				return template.getDataSource().getConnection().isValid(10);
			} catch (Exception e) {
				errorMsg=e.getMessage();
				return false;
			}
	}

	@Override
	public String getErrorMsg() {
		// TODO Auto-generated method stub
		return errorMsg;
	}

}
