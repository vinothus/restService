package com.vin.rest.dynamic;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.vin.rest.repository.EmployeeRepositaryImpl;

public class MultiServiceImpl {
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	EmployeeRepositaryImpl employeeRepositaryImpl;
	public static Map<String, List<MultiService>> MultiServiceMap= new ConcurrentHashMap<>();
	
	
	public List<Map<String, Map<String, Object>>> insertMultiData(String service,
			List<Map<String, Map<String, String>>> jsonMap) {
		List<MultiService> serviceComponent= MultiServiceMap.get(service);
		for (Iterator iterator = serviceComponent.iterator(); iterator.hasNext();) {
			MultiService multiService = (MultiService) iterator.next();
			
		}
		
		return null;
	}

	public List<Map<String, Map<String, Object>>> updateMultiData(String service,
			List<Map<String, Map<String, String>>> jsonMap) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Map<String, Map<String, Object>>> getMultiDataForParams(String service, String params) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Map<String, Map<String, Object>>> getMultiData(String service, @Valid @NotNull String uniquekey) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Map<String, Map<String, Object>>> deleteMultiData(String service, @Valid @NotNull String uniquekey) {
		// TODO Auto-generated method stub
		return null;
	}

}
