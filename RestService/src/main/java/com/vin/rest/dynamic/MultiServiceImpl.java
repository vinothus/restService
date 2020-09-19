package com.vin.rest.dynamic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vin.rest.repository.EmployeeRepositaryImpl;

public class MultiServiceImpl {
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	EmployeeRepositaryImpl singleServiceImpl;
	public static Map<String, List<MultiService>> MultiServiceMap= new ConcurrentHashMap<>();
	
	
	public List<Map<String, Map<String, Object>>> insertMultiData(String service,
			List<Map<String, Map<String, String>>> jsonMap) throws Exception {
		List<MultiService> serviceComponent= MultiServiceMap.get(service);
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, Map<String, Object>>> returnData=new ArrayList<>();
		
		for (Iterator<Map<String, Map<String, String>>> iterator1 = jsonMap.iterator(); iterator1.hasNext();) {
			Map<String, Map<String, String>> multiServiceData = (Map<String, Map<String, String>>) iterator1.next();
			Map<String, Map<String, Object>> returnMap=new HashMap<>();
		
		for (Iterator<MultiService> iterator = serviceComponent.iterator(); iterator.hasNext();) {
			MultiService multiService = (MultiService) iterator.next();
			if(multiService.getServiceType().equals(ServiceType.SINGLE)) {
				String singleService=multiService.getServiceName();
				Map<String, String> data=multiServiceData.get(singleService);
				String relation=multiService.getRelationwithParam();
				if(relation.contains("."))
				{
					String[] serviceParam=relation.split(".");
					data.put(serviceParam[2], multiServiceData.get(serviceParam[1]).get(serviceParam[0]));
				}
				Map<String, Object> dataReturn=singleServiceImpl.insertData(singleService, data);
				
				 Map<String, String> datareturn=new HashMap<>();
				String params = mapper.writeValueAsString(dataReturn);
				datareturn = mapper.readValue(params, new TypeReference<Map<String, String>>() {
				});
				data.putAll(datareturn);
				returnMap.put(singleService, dataReturn);
				returnData.add(returnMap);
				
			} else if(multiService.getServiceType().equals(ServiceType.MULTIPLE))
			{
				String singleService=multiService.getServiceName();
				//not implemented
				
			}
			
		}
		}
		
		return returnData;
	}

	public List<Map<String, Map<String, Object>>> updateMultiData(String service,
			List<Map<String, Map<String, String>>> jsonMap) throws Exception {
		List<MultiService> serviceComponent= MultiServiceMap.get(service);
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, Map<String, Object>>> returnData=new ArrayList<>();
		
		for (Iterator<Map<String, Map<String, String>>> iterator1 = jsonMap.iterator(); iterator1.hasNext();) {
			Map<String, Map<String, String>> multiServiceData = (Map<String, Map<String, String>>) iterator1.next();
			Map<String, Map<String, Object>> returnMap=new HashMap<>();
		
		for (Iterator<MultiService> iterator = serviceComponent.iterator(); iterator.hasNext();) {
			MultiService multiService = (MultiService) iterator.next();
			if(multiService.getServiceType().equals(ServiceType.SINGLE)) {
				String singleService=multiService.getServiceName();
				Map<String, String> data=multiServiceData.get(singleService);
				String relation=multiService.getRelationwithParam();
				if(relation.contains("."))
				{
					String[] serviceParam=relation.split(".");
					data.put(serviceParam[2], multiServiceData.get(serviceParam[1]).get(serviceParam[0]));
				}
				Map<String, Object> dataReturn=singleServiceImpl.updateData(singleService, data);
				
				 Map<String, String> datareturn=new HashMap<>();
				String params = mapper.writeValueAsString(dataReturn);
				datareturn = mapper.readValue(params, new TypeReference<Map<String, String>>() {
				});
				data.putAll(datareturn);
				returnMap.put(singleService, dataReturn);
				returnData.add(returnMap);
				
			} else if(multiService.getServiceType().equals(ServiceType.MULTIPLE))
			{
				String singleService=multiService.getServiceName();
				//not implemented
				
			}
			
		}
		}
		
		return returnData;
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
