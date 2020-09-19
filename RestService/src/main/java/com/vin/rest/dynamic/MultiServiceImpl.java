package com.vin.rest.dynamic;

import java.io.IOException;
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
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vin.rest.repository.EmployeeRepositaryImpl;
@Component
public class MultiServiceImpl {
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	EmployeeRepositaryImpl singleServiceImpl;
	public static Map<String, List<MultiService>> MultiServiceMap= new ConcurrentHashMap<>();
	static {init();}
	public static void init()
	{
		List<MultiService> serviceList =new ArrayList<>();
	
		MultiService service=new MultiService();
		service.setId(1);
		service.setServiceType(ServiceType.SINGLE);
		service.setServiceName("tbl student");
		//service.setRelationwithParam("..");
		serviceList.add(service);
		
		MultiService service1=new MultiService();
		service1.setId(1);
		service1.setServiceType(ServiceType.SINGLE);
		service1.setServiceName("tbl employees");
		//service.setRelationwithParam("..");
		serviceList.add(service1);
		
		MultiServiceMap.put("student", serviceList);
		
	}
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
				if(relation!=null)
				{
					if(relation.contains("."))
				{
					String[] serviceParam=relation.split(".");
					data.put(serviceParam[2], multiServiceData.get(serviceParam[1]).get(serviceParam[0]));
				}
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
				if(relation!=null)
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

	public List<Map<String, List<Map<String, Object>>>> getMultiDataForParams(String service, Map<String, String> params) {
		List<MultiService> serviceComponent= MultiServiceMap.get(service);
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, List<Map<String, Object>>>> returnData=new ArrayList<>();
		
		for (Iterator<MultiService> iterator = serviceComponent.iterator(); iterator.hasNext();) {
			Map<String, List<Map<String, Object>>> retObj=new HashMap<>();
			MultiService multiService = (MultiService) iterator.next();
			if(multiService.getServiceType().equals(ServiceType.SINGLE)) {
				String singleService=multiService.getServiceName();
				String relation=multiService.getRelationwithParam();
				if(relation!=null)
					{
					if(relation.contains("."))
					{
						String[] serviceParam=relation.split(".");
						for (Iterator iterator2 = returnData.iterator(); iterator2.hasNext();) {
							Map<String, List<Map<String, Object>>> map = (Map<String, List<Map<String, Object>>>) iterator2.next();
							if(map.size()>0)
							params.put(serviceParam[2],(String) (map.get(serviceParam[1]).get(0).get(serviceParam[0])));
						}
						List<Map<String, Object>> dataReturn=singleServiceImpl.getDataForParams(singleService, params);
					}else if(relation.contains(":")) {
						String[] serviceParam=relation.split(":");
						params.put(serviceParam[0],(String)params.get(serviceParam[1]));
						List<Map<String, Object>> dataReturn=singleServiceImpl.getDataForParams(singleService, params);
						retObj.put(singleService, dataReturn);
						returnData.add(retObj);
					}
					}else {
						List<Map<String, Object>> dataReturn=singleServiceImpl.getDataForParams(singleService, params);
						retObj.put(singleService, dataReturn);
						returnData.add(retObj);
					}
			}
			}
		
		return returnData;
	}

	public List<Map<String, Map<String, Object>>> getMultiData(String service, @Valid @NotNull String uniquekey) throws Exception  {
		List<MultiService> serviceComponent= MultiServiceMap.get(service);
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, Map<String, Object>>> returnData=new ArrayList<>();
		for (Iterator<MultiService> iterator = serviceComponent.iterator(); iterator.hasNext();) {
			Map<String, Map<String, Object>> retObj=new HashMap<>();
			MultiService multiService = (MultiService) iterator.next();
			if(multiService.getServiceType().equals(ServiceType.SINGLE)) {
				String singleService=multiService.getServiceName();
				String relation=multiService.getRelationwithParam();
				if(relation!=null)
					{
					if(relation.contains("."))
					{
						
						String[] serviceParam=relation.split(".");
						for (Iterator iterator2 = returnData.iterator(); iterator2.hasNext();) {
							Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) iterator2.next();
							if(map.size()>0)
								{
								Map<String, Map<String, Object>> datareturn=new HashMap<>();
								String params = mapper.writeValueAsString(map);
								datareturn = mapper.readValue(params, new TypeReference<Map<String, Map<String, Object>>>() {
								});
								
										String key=(String) datareturn.get(serviceParam[0]).get(serviceParam[1]);
										if(key!=null)
										{
											uniquekey=key;
										}
								}
						}
						Map<String, Object> dataReturn=singleServiceImpl.deleteData(singleService, uniquekey);
						retObj.put(singleService,dataReturn);
						returnData.add(retObj);
					}
					}
				}
			}
		
		return returnData;
	}

	public List<Map<String, Map<String, Object>>> deleteMultiData(String service, @Valid @NotNull String uniquekey) throws Exception {
		List<MultiService> serviceComponent= MultiServiceMap.get(service);
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, Map<String, Object>>> returnData=new ArrayList<>();
		for (Iterator<MultiService> iterator = serviceComponent.iterator(); iterator.hasNext();) {
			Map<String, Map<String, Object>> retObj=new HashMap<>();
			MultiService multiService = (MultiService) iterator.next();
			if(multiService.getServiceType().equals(ServiceType.SINGLE)) {
				String singleService=multiService.getServiceName();
				String relation=multiService.getRelationwithParam();
				if(relation!=null)
					{
					if(relation.contains("."))
					{
						
						String[] serviceParam=relation.split(".");
						for (Iterator iterator2 = returnData.iterator(); iterator2.hasNext();) {
							Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) iterator2.next();
							if(map.size()>0)
								{
								Map<String, Map<String, Object>> datareturn=new HashMap<>();
								String params = mapper.writeValueAsString(map);
								datareturn = mapper.readValue(params, new TypeReference<Map<String, Map<String, Object>>>() {
								});
								
										String key=(String) datareturn.get(serviceParam[0]).get(serviceParam[1]);
										if(key!=null)
										{
											uniquekey=key;
										}
								}
						}
						Map<String, Object> dataReturn=singleServiceImpl.deleteData(singleService, uniquekey);
						retObj.put(singleService,dataReturn);
						returnData.add(retObj);
					}
					}
				}
			}
		
		return returnData;
	}

}
