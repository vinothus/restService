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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
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
			List<Map<String, Map<String, String>>> jsonMap,String apiKey, String dataStoreKey) throws Exception {
		List<MultiService> serviceComponent= MultiServiceMap.get(service);
		if(serviceComponent==null)
		{
			arrangeMultServiceGD(service);
			 serviceComponent= MultiServiceMap.get(service);
		}
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
					if(relation.equalsIgnoreCase("none")) {
						
					}
					
					else if(relation.contains(",")) {
						String[] serviceListParam=relation.split(",");	
						for (int i = 0; i < serviceListParam.length; i++) {
							
							String serviceRelation=serviceListParam[i];
							String[] serviceParam=serviceRelation.split("\\.");

							if(serviceParam.length==3)
							{
								if (data != null) {
									for (Iterator iterator2 = returnData.iterator(); iterator2.hasNext();) {
										Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) iterator2
												.next();
										
										String paramsStr = mapper.writeValueAsString(map);
										
										Map<String, Map<String, String>>  strMap =new HashMap<>();
										strMap = mapper.readValue(paramsStr, new TypeReference<Map<String, Map<String, String>>>() {
										});
										if(strMap.get(serviceParam[0])!=null&&strMap.get(serviceParam[0]).get(serviceParam[1])!=null)
										{
											data.put(serviceParam[2], strMap.get(serviceParam[0]).get(serviceParam[1]));
										}
									}
							
								}
							}
						
						}
						
					}
					else if(relation.contains("."))
				{
					String[] serviceParam=relation.split("\\.");
					if(serviceParam.length==3)
					{
						if (data != null) {
							for (Iterator iterator2 = returnData.iterator(); iterator2.hasNext();) {
								Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) iterator2
										.next();
								
								String paramsStr = mapper.writeValueAsString(map);
								
								Map<String, Map<String, String>>  strMap =new HashMap<>();
								strMap = mapper.readValue(paramsStr, new TypeReference<Map<String, Map<String, String>>>() {
								});
								if(strMap.get(serviceParam[0])!=null&&strMap.get(serviceParam[0]).get(serviceParam[1])!=null)
								{
									data.put(serviceParam[2], strMap.get(serviceParam[0]).get(serviceParam[1]));
								}
							}
					
						}
					}
				}
					}
				
					if (data != null) {
						Map<String, Object> dataReturn = singleServiceImpl.insertData(singleService, data, apiKey,  dataStoreKey);

						Map<String, String> datareturn = new HashMap<>();
						String params = mapper.writeValueAsString(dataReturn);
						datareturn = mapper.readValue(params, new TypeReference<Map<String, String>>() {
						});
						//data.putAll(datareturn);
						returnMap.put(singleService, dataReturn);
						returnData.add(returnMap);
					}
				
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
			List<Map<String, Map<String, String>>> jsonMap,String apiKey, String dataStoreKey) throws Exception {
		List<MultiService> serviceComponent= MultiServiceMap.get(service);
		if(serviceComponent==null)
		{
			arrangeMultServiceGD(service);
			serviceComponent= MultiServiceMap.get(service);
		}
		
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
					if(relation.equalsIgnoreCase("none")) {
						
					}
					else if(relation.contains(",")) {
						String[] serviceListParam=relation.split(",");	
						for (int i = 0; i < serviceListParam.length; i++) {
							
							String serviceRelation=serviceListParam[i];
							String[] serviceParam=serviceRelation.split("\\.");

							if(serviceParam.length==3)
							{
								if (data != null) {
									for (Iterator iterator2 = returnData.iterator(); iterator2.hasNext();) {
										Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) iterator2
												.next();
										
										String paramsStr = mapper.writeValueAsString(map);
										
										Map<String, Map<String, String>>  strMap =new HashMap<>();
										strMap = mapper.readValue(paramsStr, new TypeReference<Map<String, Map<String, String>>>() {
										});
										if(strMap.get(serviceParam[0])!=null&&strMap.get(serviceParam[0]).get(serviceParam[1])!=null)
										{
											data.put(serviceParam[2], strMap.get(serviceParam[0]).get(serviceParam[1]));
										}
									}
							
								}
							}
						
						}
						
					}
					
					else if(relation.contains("."))
				{
					String[] serviceParam=relation.split("\\.");
					if(serviceParam.length==3)
					{
						if (data != null) {
							for (Iterator iterator2 = returnData.iterator(); iterator2.hasNext();) {
								Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) iterator2
										.next();
								
								String paramsStr = mapper.writeValueAsString(map);
								
								Map<String, Map<String, String>>  strMap =new HashMap<>();
								strMap = mapper.readValue(paramsStr, new TypeReference<Map<String, Map<String, String>>>() {
								});
								if(strMap.get(serviceParam[0])!=null&&strMap.get(serviceParam[0]).get(serviceParam[1])!=null)
								{
									data.put(serviceParam[2], strMap.get(serviceParam[0]).get(serviceParam[1]));
								}
							}
					
						}
					}
				}}
				if (data != null) {
				Map<String, Object> dataReturn=singleServiceImpl.updateData(singleService, data, apiKey,  dataStoreKey);
				
				 Map<String, String> datareturn=new HashMap<>();
				String params = mapper.writeValueAsString(dataReturn);
				datareturn = mapper.readValue(params, new TypeReference<Map<String, String>>() {
				});
				//data.putAll(datareturn);
				returnMap.put(singleService, dataReturn);
				returnData.add(returnMap);
				}
				
			} else if(multiService.getServiceType().equals(ServiceType.MULTIPLE))
			{
				String singleService=multiService.getServiceName();
				//not implemented
				
			}
			
		}
		}
		
		return returnData;
	}

	private void arrangeMultServiceGD(String serviceName) throws JsonParseException, JsonMappingException, IOException {
		List<Map<String, Object>> serviceDatum = jdbcTemplate.queryForList("select ms.id as id, s.serviceName as serviceName, ms.multiservicename as multiservicename, ms.priority as priority ,ms.type,relationwithparam from Multi_Service ms , Service s where multiservicename= '"+serviceName+"'  and s.id=ms.service_id ");
		List<Map<String, String>> serviceStrDatum=new ArrayList<>();
		 ObjectMapper mapper = new ObjectMapper();
			String params = mapper.writeValueAsString(serviceDatum);
			serviceStrDatum = mapper.readValue(params, new TypeReference<List<Map<String, String>>>() {
			});
			List<MultiService> serviceList =new ArrayList<>();
			String multiServiceName=null;
		for (Iterator<Map<String, String>> iterator = serviceStrDatum.iterator(); iterator.hasNext();) {
			Map<String, String> map = (Map<String, String>) iterator.next();
			
			
			MultiService service=new MultiService();
			service.setId(Integer.parseInt(map.get("id")));
			service.setPriproty(Integer.parseInt(map.get("priority")));
			if(map.get("type").equalsIgnoreCase("single"))
			{
				service.setServiceType(ServiceType.SINGLE);
			}else
			{
				service.setServiceType(ServiceType.MULTIPLE);
			}
			
			service.setServiceName(map.get("serviceName"));
			service.setRelationwithParam(map.get("relationwithparam"));
			serviceList.add(service);
			multiServiceName=(map.get("multiservicename"));
		}
		
		serviceList.sort(new MultiService());
		if(multiServiceName!=null)
		{
			MultiServiceMap.put(multiServiceName, serviceList);
		}
	}
	public List<Map<String, List<Map<String, Object>>>> getMultiDataForParams(String service, Map<String, String> params,String apiKey, String dataStoreKey) throws JsonParseException, JsonMappingException, IOException {
		List<MultiService> serviceComponent= MultiServiceMap.get(service);
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, List<Map<String, Object>>>> returnData=new ArrayList<>();
		if(serviceComponent==null)
		{
			arrangeMultServiceGD(service);
			 serviceComponent= MultiServiceMap.get(service);
		}
		for (Iterator<MultiService> iterator = serviceComponent.iterator(); iterator.hasNext();) {
			Map<String, List<Map<String, Object>>> retObj=new HashMap<>();
			MultiService multiService = (MultiService) iterator.next();
			List<Map<String, Object>> sumazationList=new ArrayList<>(); 
			if(multiService.getServiceType().equals(ServiceType.SINGLE)) {
				String singleService=multiService.getServiceName();
				String relation=multiService.getRelationwithParam();
				if(relation!=null)
					{
					if(relation.contains("none"))
					{
						List<Map<String, Object>> dataReturn=singleServiceImpl.getDataForParams(singleService, new HashMap<String, String>(), apiKey,  dataStoreKey);
						retObj.put(singleService, dataReturn);
						returnData.add(retObj);
						
					}
					else if(relation.contains(",")&&relation.contains(":"))
					{
						String[] serviceListParam=relation.split(",");
						for (int i = 0; i < serviceListParam.length; i++) {
							String relationlpstr = serviceListParam[i];
							

							String[] serviceParam=relationlpstr.split(":");
							params.put(serviceParam[0],(String)params.get(serviceParam[1]));
							List<Map<String, Object>> dataReturn=singleServiceImpl.getDataForParams(singleService, params, apiKey,  dataStoreKey);
							sumazationList.addAll(dataReturn);
							retObj.put(singleService, sumazationList);
							returnData.add(retObj);
						
							
						}
					}
					
					else if(relation.contains(",")&&relation.contains(".")) {
						String[] serviceListParam=relation.split(",");	
						for (int i = 0; i < serviceListParam.length; i++) {
							
							String serviceRelation=serviceListParam[i];
							String[] serviceParam=serviceRelation.split("\\.");

							for (Iterator iterator2 = returnData.iterator(); iterator2.hasNext();) {
								Map<String, List<Map<String, Object>>> map = (Map<String, List<Map<String, Object>>>) iterator2.next();
								
								if(map.size()>0)
								{
									Map<String, List<Map<String, String>>> hashMapStr=new HashMap<>();
									String mapStr = mapper.writeValueAsString(map);
									hashMapStr = mapper.readValue(mapStr, new TypeReference<Map<String, List<Map<String, String>>>>() {
									});
									if(serviceParam.length==3)
									{
										for (Map.Entry<String,List<Map<String, String>>> entry : hashMapStr.entrySet())  
										{
											if(entry.getKey().equalsIgnoreCase(serviceParam[0]))
													{
												List<Map<String, String>> listObj=entry.getValue(); 
												for (Iterator iterator3 = listObj.iterator(); iterator3
														.hasNext();) {
													Map<String, String> map2 = (Map<String, String>) iterator3.next();
													params.put(serviceParam[2],(String) (map2.get(serviceParam[1])));
													List<Map<String, Object>> dataReturn=singleServiceImpl.getDataForParams(singleService, params, apiKey,  dataStoreKey);
													sumazationList.addAll(dataReturn);
													retObj.put(singleService, sumazationList);
												}
												
												//returnData.add(retObj);
													}
											
										}
										
										
										/*
										 * if(hashMapStr.get(serviceParam[0])!=null&&hashMapStr.get(serviceParam[0]).get
										 * (0)!=null&&hashMapStr.get(serviceParam[0]).get(0).get(serviceParam[1])!=null)
										 * {params.put(serviceParam[2],(String)
										 * (hashMapStr.get(serviceParam[0]).get(0).get(serviceParam[1]))); }
										 */
									}else {
										
										List<Map<String, Object>> dataReturn=singleServiceImpl.getDataForParams(singleService, params, apiKey,  dataStoreKey);
										sumazationList.addAll(dataReturn);
										retObj.put(singleService, sumazationList);
										
									}
								}
							}
							returnData.add(retObj);
						
						}
						
					}
					
					else if(relation.contains("."))
					{
						String[] serviceParam=relation.split("\\.");
						for (Iterator iterator2 = returnData.iterator(); iterator2.hasNext();) {
							Map<String, List<Map<String, Object>>> map = (Map<String, List<Map<String, Object>>>) iterator2.next();
							
							if(map.size()>0)
							{
								Map<String, List<Map<String, String>>> hashMapStr=new HashMap<>();
								String mapStr = mapper.writeValueAsString(map);
								hashMapStr = mapper.readValue(mapStr, new TypeReference<Map<String, List<Map<String, String>>>>() {
								});
								if(serviceParam.length==3)
								{
									for (Map.Entry<String,List<Map<String, String>>> entry : hashMapStr.entrySet())  
									{
										if(entry.getKey().equalsIgnoreCase(serviceParam[0]))
												{
											List<Map<String, String>> listObj=entry.getValue(); 
											for (Iterator iterator3 = listObj.iterator(); iterator3
													.hasNext();) {
												Map<String, String> map2 = (Map<String, String>) iterator3.next();
												params.put(serviceParam[2],(String) (map2.get(serviceParam[1])));
												List<Map<String, Object>> dataReturn=singleServiceImpl.getDataForParams(singleService, params, apiKey,  dataStoreKey);
												sumazationList.addAll(dataReturn);
												retObj.put(singleService, sumazationList);
											}
											
											//returnData.add(retObj);
												}
										
									}
								}else {
									
									List<Map<String, Object>> dataReturn=singleServiceImpl.getDataForParams(singleService, params, apiKey,  dataStoreKey);
									sumazationList.addAll(dataReturn);
									retObj.put(singleService, sumazationList);
								
								}
							}
						}
						returnData.add(retObj);
					}else if(relation.contains(":")) {
						String[] serviceParam=relation.split(":");
						params.put(serviceParam[0],(String)params.get(serviceParam[1]));
						List<Map<String, Object>> dataReturn=singleServiceImpl.getDataForParams(singleService, params, apiKey,  dataStoreKey);
						sumazationList.addAll(dataReturn);
						retObj.put(singleService, sumazationList);
						returnData.add(retObj);
					}else {
						List<Map<String, Object>> dataReturn=singleServiceImpl.getDataForParams(singleService, params, apiKey,  dataStoreKey);
						sumazationList.addAll(dataReturn);
						retObj.put(singleService, sumazationList);
						returnData.add(retObj);
					}
					}else {
						List<Map<String, Object>> dataReturn=singleServiceImpl.getDataForParams(singleService, params, apiKey,  dataStoreKey);
						sumazationList.addAll(dataReturn);
						retObj.put(singleService, sumazationList);
						returnData.add(retObj);
					}
			}
			}
		
		return returnData;
	}

	public List<Map<String, Map<String, Object>>> getMultiData(String service, @Valid @NotNull String uniquekey,String apiKey, String dataStoreKey) throws Exception  {
		List<MultiService> serviceComponent= MultiServiceMap.get(service);
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, Map<String, Object>>> returnData=new ArrayList<>();
		if(serviceComponent==null)
		{
			arrangeMultServiceGD(service);
			 serviceComponent= MultiServiceMap.get(service);
		}
		for (Iterator<MultiService> iterator = serviceComponent.iterator(); iterator.hasNext();) {
			Map<String, Map<String, Object>> retObj=new HashMap<>();
			MultiService multiService = (MultiService) iterator.next();
			if(multiService.getServiceType().equals(ServiceType.SINGLE)) {
				String singleService=multiService.getServiceName();
				String relation=multiService.getRelationwithParam();
				if(relation!=null)
					{
					if(relation.contains("none"))
					{
						Map<String, Object> dataReturn=singleServiceImpl.getData(singleService, uniquekey, apiKey,  dataStoreKey);
						retObj.put(singleService,dataReturn);
						returnData.add(retObj);	
					}
					
					
					
					else if(relation.contains(",")) {
						String[] serviceListParam=relation.split(",");	
						for (int i = 0; i < serviceListParam.length; i++) {
							
							String serviceRelation=serviceListParam[i];
							String[] serviceParam=serviceRelation.split("\\.");
							Map<String, Map<String, String>> datareturn=new HashMap<>();
							for (Iterator iterator2 = returnData.iterator(); iterator2.hasNext();) {
								Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) iterator2.next();
								if(map.size()>0)
									{
								
									String params = mapper.writeValueAsString(map);
									datareturn = mapper.readValue(params, new TypeReference<Map<String, Map<String, String>>>() {
									});
									}
							}
							if(serviceParam.length==2)
							{	if(datareturn.get(serviceParam[0])!=null&& datareturn.get(serviceParam[0]).get(serviceParam[1])!=null)
									{
										String key = (String) datareturn.get(serviceParam[0]).get(serviceParam[1]);
										if (key != null) {
											uniquekey = key;
											Map<String, Object> dataReturn = singleServiceImpl.getData(singleService,
													uniquekey, apiKey,  dataStoreKey);
											retObj.put(singleService, dataReturn);
											returnData.add(retObj);
										} else {
											Map<String, Object> dataReturn = singleServiceImpl.getData(singleService,
													uniquekey, apiKey,  dataStoreKey);
											retObj.put(singleService, dataReturn);
											returnData.add(retObj);
										}

									}
							}
							
						
						}
						
					}
					
					else if(relation.contains("."))
					{
						
						String[] serviceParam=relation.split("\\.");
						Map<String, Map<String, String>> datareturn=new HashMap<>();
						for (Iterator iterator2 = returnData.iterator(); iterator2.hasNext();) {
							Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) iterator2.next();
							if(map.size()>0)
								{
							
								String params = mapper.writeValueAsString(map);
								datareturn = mapper.readValue(params, new TypeReference<Map<String, Map<String, String>>>() {
								});
								}
						}
						if(serviceParam.length==2)
						{	if(datareturn.get(serviceParam[0])!=null&& datareturn.get(serviceParam[0]).get(serviceParam[1])!=null)
								{
									String key = (String) datareturn.get(serviceParam[0]).get(serviceParam[1]);
									if (key != null) {
										uniquekey = key;
										Map<String, Object> dataReturn = singleServiceImpl.getData(singleService,
												uniquekey, apiKey,  dataStoreKey);
										retObj.put(singleService, dataReturn);
										returnData.add(retObj);
									} else {
										Map<String, Object> dataReturn = singleServiceImpl.getData(singleService,
												uniquekey, apiKey,  dataStoreKey);
										retObj.put(singleService, dataReturn);
										returnData.add(retObj);
									}

								}
						}
						
					}else {
						Map<String, Object> dataReturn=singleServiceImpl.getData(singleService, uniquekey, apiKey,  dataStoreKey);
						retObj.put(singleService,dataReturn);
						returnData.add(retObj);
					}
					}else
					{
						Map<String, Object> dataReturn=singleServiceImpl.getData(singleService, uniquekey, apiKey,  dataStoreKey);
						retObj.put(singleService,dataReturn);
						returnData.add(retObj);
						
					}
				}
			}
		
		return returnData;
	}

	public List<Map<String, Map<String, Object>>> deleteMultiData(String service, @Valid @NotNull String uniquekey,String apiKey, String dataStoreKey) throws Exception {
		List<MultiService> serviceComponent= MultiServiceMap.get(service);
		ObjectMapper mapper = new ObjectMapper();
		if(serviceComponent==null)
		{
			arrangeMultServiceGD(service);
			 serviceComponent= MultiServiceMap.get(service);
		}
		List<Map<String, Map<String, Object>>> returnData=new ArrayList<>();
		for (Iterator<MultiService> iterator = serviceComponent.iterator(); iterator.hasNext();) {
			Map<String, Map<String, Object>> retObj=new HashMap<>();
			MultiService multiService = (MultiService) iterator.next();
			if(multiService.getServiceType().equals(ServiceType.SINGLE)) {
				String singleService=multiService.getServiceName();
				String relation=multiService.getRelationwithParam();
				if(relation!=null)
					{
					if(relation.contains("none"))
					{
						Map<String, Object> dataReturn=singleServiceImpl.deleteData(singleService, uniquekey, apiKey,  dataStoreKey);
						retObj.put(singleService,dataReturn);
						returnData.add(retObj);	
					}
					else if(relation.contains(",")) {
						String[] serviceListParam=relation.split(",");	
						for (int i = 0; i < serviceListParam.length; i++) {

							
							String[] serviceParam=serviceListParam[i].split("\\.");
							for (Iterator iterator2 = returnData.iterator(); iterator2.hasNext();) {
								Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) iterator2.next();
								if(map.size()>0)
									{
									Map<String, Map<String, String>> datareturn=new HashMap<>();
									String params = mapper.writeValueAsString(map);
									datareturn = mapper.readValue(params, new TypeReference<Map<String, Map<String, String>>>() {
									});
									
											String key=(String) datareturn.get(serviceParam[0]).get(serviceParam[1]);
											if(key!=null)
											{
												uniquekey=key;
											}
									}
							}
							Map<String, Object> dataReturn=singleServiceImpl.deleteData(singleService, uniquekey, apiKey,  dataStoreKey);
							retObj.put(singleService,dataReturn);
							returnData.add(retObj);
						
						}
						}
					else if(relation.contains("."))
					{
						
						String[] serviceParam=relation.split("\\.");
						for (Iterator iterator2 = returnData.iterator(); iterator2.hasNext();) {
							Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) iterator2.next();
							if(map.size()>0)
								{
								Map<String, Map<String, String>> datareturn=new HashMap<>();
								String params = mapper.writeValueAsString(map);
								datareturn = mapper.readValue(params, new TypeReference<Map<String, Map<String, String>>>() {
								});
								
										String key=(String) datareturn.get(serviceParam[0]).get(serviceParam[1]);
										if(key!=null)
										{
											uniquekey=key;
										}
								}
						}
						Map<String, Object> dataReturn=singleServiceImpl.deleteData(singleService, uniquekey, apiKey,  dataStoreKey);
						retObj.put(singleService,dataReturn);
						returnData.add(retObj);
					}
					}
				}
			}
		
		return returnData;
	}

}
