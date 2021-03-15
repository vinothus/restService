package com.vin.rest.dynamic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vin.rest.exception.DatabaseAuthException;
import com.vin.rest.repository.EmployeeRepositaryImpl;
@Component
public class MultiServiceImpl {
	//@Autowired
	//JdbcTemplate jdbcTemplate;
	@Autowired
	private Environment env;
	@Autowired
	EmployeeRepositaryImpl singleServiceImpl;
	public static Map<String, List<MultiService>> MultiServiceMap= new ConcurrentHashMap<>();
	Map<String, JdbcTemplate> jdbcTemplateMap= new ConcurrentHashMap<>();
	Logger log = Logger.getLogger(MultiServiceImpl.class.getName());
	private JdbcTemplate setUserDataStore(String apiKey, String dataStoreKey,String passToken) {
		 JdbcTemplate userJdbcTemplate;
		 try {	DriverManagerDataSource dataSource = new DriverManagerDataSource();
		if(jdbcTemplateMap.get(dataStoreKey)==null)
		{
			if(dataStoreKey.equalsIgnoreCase("SYSTEM"))
		{
			
			dataSource.setDriverClassName(env.getProperty("sys.spring.datasource.driver-class-name"));
			dataSource.setUrl(env.getProperty("sys.spring.datasource.url"));
			dataSource.setUsername(env.getProperty("sys.spring.datasource.username"));
			dataSource.setPassword(env.getProperty("sys.spring.datasource.password"));
		     userJdbcTemplate=new JdbcTemplate();
		    userJdbcTemplate.setDataSource(dataSource);
		    jdbcTemplateMap.put(dataStoreKey, userJdbcTemplate);
		}
			else {
				JdbcTemplate JdbcTemplate = null;
				if (setUserDataStore(apiKey, "system","none") == null) {

					JdbcTemplate = setUserDataStore( apiKey, "system", passToken);

				} else {
					JdbcTemplate = setUserDataStore(apiKey, "system","none");
				}
				List<Map<String,Object>> DataStoreData=JdbcTemplate.queryForList("select dst.url as url ,dst.driver as driver from Datastore dst,  User us where dst.Datastore = ? and dst.uid = us.id and us.apikey = ? " ,new Object[] { dataStoreKey ,apiKey});
				
				   dataSource.setDriverClassName(DataStoreData.get(0).get("driver").toString());
				    dataSource.setUrl(DataStoreData.get(0).get("url").toString());
				    byte[] decodedBytes = Base64.getDecoder().decode(passToken);
				    String decodedString = new String(decodedBytes);
				    
				    dataSource.setUsername(decodedString.split(":")[0]);
				    dataSource.setPassword(decodedString.split(":")[1]);
				    userJdbcTemplate=new JdbcTemplate();
				    userJdbcTemplate.setDataSource(dataSource);
				    jdbcTemplateMap.put(dataStoreKey, userJdbcTemplate);
			}	
		
		}else
		{
			userJdbcTemplate=jdbcTemplateMap.get(dataStoreKey);
		}
	}catch(Exception e)
	{
		log.warning(e.getMessage());
		throw new DatabaseAuthException("Exception during database Authentication");
	}
		return userJdbcTemplate;
	}
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
			List<Map<String, Map<String, String>>> jsonMap,String apiKey, String dataStoreKey,String passToken) throws Exception {
		List<MultiService> serviceComponent= MultiServiceMap.get(service);
		if(serviceComponent==null)
		{
			arrangeMultServiceGD(service,apiKey,dataStoreKey,passToken);
			 serviceComponent= MultiServiceMap.get(service);
		}
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, Map<String, Object>>> returnData=new ArrayList<>();
		
		for (Iterator<Map<String, Map<String, String>>> iterator1 = jsonMap.iterator(); iterator1.hasNext();) {
			Map<String, Map<String, String>> multiServiceData = (Map<String, Map<String, String>>) iterator1.next();
			
		
		for (Iterator<MultiService> iterator = serviceComponent.iterator(); iterator.hasNext();) {
			MultiService multiService = (MultiService) iterator.next();
			if(multiService.getServiceType().equals(ServiceType.SINGLE)) {
				String parentService=null;
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
											if (parentService == null) {
												parentService = serviceParam[0];
											}
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
									if (parentService == null) {
										parentService = serviceParam[0];
									}
								}
							}
					
						}
					}
				}
					}
				
					if (data != null) {
						Map<String, Object> dataReturn = singleServiceImpl.insertData(singleService, data, apiKey,  multiService.getDataSourceKey(),passToken);

						Map<String, String> datareturn = new HashMap<>();
						String params = mapper.writeValueAsString(dataReturn);
						datareturn = mapper.readValue(params, new TypeReference<Map<String, String>>() {
						});
						
						Map<String, Map<String, Object>> returnMap=new HashMap<>();
						//data.putAll(datareturn);
						Map<String, Map<String, Object>> parentMap=null;
						for (Iterator iterator2 = returnData.iterator(); iterator2.hasNext();) {
							Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) iterator2.next();
							for (Entry<String, Map<String, Object>> entry : map.entrySet())  
							 {
								if(entry.getKey().equals(parentService)) {
									// parent serviec already in 
									parentMap=map;
								}
							 }
							
						}
						if (parentMap != null && parentService != null) {

							returnData.remove(parentMap);
							//parentMap.put(singleService, dataReturn);
							parentMap.get(parentService).put(singleService, dataReturn);
							returnData.add(parentMap);
						}else {
						returnMap.put(singleService, dataReturn);
						returnData.add(returnMap);
						}
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
			List<Map<String, Map<String, String>>> jsonMap,String apiKey, String dataStoreKey,String passToken) throws Exception {
		List<MultiService> serviceComponent= MultiServiceMap.get(service);
		if(serviceComponent==null)
		{
			arrangeMultServiceGD(service,apiKey,dataStoreKey,passToken);
			serviceComponent= MultiServiceMap.get(service);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, Map<String, Object>>> returnData=new ArrayList<>();
		
		for (Iterator<Map<String, Map<String, String>>> iterator1 = jsonMap.iterator(); iterator1.hasNext();) {
			Map<String, Map<String, String>> multiServiceData = (Map<String, Map<String, String>>) iterator1.next();
		
		
		for (Iterator<MultiService> iterator = serviceComponent.iterator(); iterator.hasNext();) {
			MultiService multiService = (MultiService) iterator.next();
			if(multiService.getServiceType().equals(ServiceType.SINGLE)) {
				String parentService=null;
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
											if (parentService == null) {
												parentService = serviceParam[0];
											}
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
									if (parentService == null) {
										parentService = serviceParam[0];
									}
								}
							}
					
						}
					}
				}}
				if (data != null) {
				Map<String, Object> dataReturn=singleServiceImpl.updateData(singleService, data, apiKey,  multiService.getDataSourceKey(),passToken);
				
				 Map<String, String> datareturn=new HashMap<>();
				String params = mapper.writeValueAsString(dataReturn);
				datareturn = mapper.readValue(params, new TypeReference<Map<String, String>>() {
				});
				Map<String, Map<String, Object>> returnMap=new HashMap<>();
				//data.putAll(datareturn);
				Map<String, Map<String, Object>> parentMap=null;
				for (Iterator iterator2 = returnData.iterator(); iterator2.hasNext();) {
					Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) iterator2.next();
					for (Entry<String, Map<String, Object>> entry : map.entrySet())  
					 {
						if(entry.getKey().equals(parentService)) {
							// parent serviec already in 
							parentMap=map;
						}
					 }
					
				}
				if (parentMap != null && parentService != null) {

					returnData.remove(parentMap);
					parentMap.get(parentService).put(singleService, dataReturn);
					//parentMap.put(singleService, dataReturn);
					returnData.add(parentMap);
				}else {
				returnMap.put(singleService, dataReturn);
				returnData.add(returnMap);
				}
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

	private void arrangeMultServiceGD(String serviceName,String apiKey, String dataStoreKey,String passToken) throws JsonParseException, JsonMappingException, IOException {
		List<Map<String, Object>> serviceDatum = null ;
		try {
		serviceDatum= setUserDataStore(apiKey, dataStoreKey, passToken).queryForList("select ms.id as id, s.serviceName as serviceName, ms.multiservicename as multiservicename, ms.priority as priority ,ms.type,relationwithparam,ds.name from Multi_Service ms , Service s , Datastore ds where multiservicename= '"+serviceName+"'  and s.id=ms.service_id and ds.id=s.dsid");
		}catch(Exception e)
		{
		singleServiceImpl.createSysTable();	
		serviceDatum= setUserDataStore(apiKey, dataStoreKey, passToken).queryForList("select ms.id as id, s.serviceName as serviceName, ms.multiservicename as multiservicename, ms.priority as priority ,ms.type,relationwithparam,ds.name from Multi_Service ms , Service s , Datastore ds where multiservicename= '"+serviceName+"'  and s.id=ms.service_id and ds.id=s.dsid");
		}
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
			service.setDataSourceKey(String.valueOf((map.get("name"))));
			serviceList.add(service);
			multiServiceName=(map.get("multiservicename"));
		}
		
		serviceList.sort(new MultiService());
		if(multiServiceName!=null)
		{
			MultiServiceMap.put(multiServiceName, serviceList);
		}
	}
	public List<Map<String, List<Map<String, Object>>>> getMultiDataForParams(String service, Map<String, String> params,String apiKey, String dataStoreKey,String passToken) throws DatabaseAuthException, Exception {
		List<MultiService> serviceComponent= MultiServiceMap.get(service);
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, List<Map<String, Object>>>> returnData=new ArrayList<>();
		if(serviceComponent==null)
		{
			arrangeMultServiceGD(service,apiKey,dataStoreKey,passToken);
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
						List<Map<String, Object>> dataReturn=singleServiceImpl.getDataForParams(singleService, new HashMap<String, String>(), apiKey,  multiService.getDataSourceKey(),passToken);
						retObj.put(singleService, dataReturn);
						returnData.add(retObj);
						
					}
					 
					
					else if(relation.contains(",")&&relation.contains(".")) {
						String[] serviceListParam=relation.split(",");	
						Map<String, String> tempParamsFor2=new  HashMap<>();
						List<String> tempParamsFor3=new  ArrayList<>();
						Map<String, String> tempParamsFor4=new  HashMap<>();
						for (int i = 0; i < serviceListParam.length; i++) {
							
							String serviceRelation=serviceListParam[i];
							String[] serviceParam=serviceRelation.split("\\.");
							if(serviceParam.length==3) {
								tempParamsFor3.add(serviceRelation);
								if (tempParamsFor4.get(serviceParam[0])==null) {
									tempParamsFor4.put(serviceParam[0], serviceParam[1] + ":" + serviceParam[1]);
								} else {
									tempParamsFor4.put(serviceParam[0], tempParamsFor4.get(serviceParam[0]) + ","
											+ serviceParam[1] + ":" + serviceParam[1]);
								}
							}
							else if(serviceParam.length==2)
							{
								
								tempParamsFor2.put(serviceParam[1], params.get(serviceParam[0]));
							}

						}
							for (Iterator iterator2 = returnData.iterator(); iterator2.hasNext();) {
								Map<String, List<Map<String, Object>>> map = (Map<String, List<Map<String, Object>>>) iterator2.next();
								
								if(map.size()>0)
								{
									Map<String, List<Map<String, String>>> hashMapStr=new HashMap<>();
									String mapStr = mapper.writeValueAsString(map);
									hashMapStr = mapper.readValue(mapStr, new TypeReference<Map<String, List<Map<String, String>>>>() {
									});
									 for (Map.Entry<String,String> entry1 : tempParamsFor4.entrySet())  
									 {
										String serviceOnConfig= entry1.getKey();
										String servicekeyWithparamKey= entry1.getValue();
										 
										 
										 for (Map.Entry<String,List<Map<String, String>>> entry : hashMapStr.entrySet())  
											{
												if(entry.getKey().equalsIgnoreCase(serviceOnConfig))
														{
													List<Map<String, String>> listObj=entry.getValue(); 
													for (Iterator iterator3 = listObj.iterator(); iterator3
															.hasNext();) {
														Map<String, String> map2 = (Map<String, String>) iterator3.next();
														 Map<String, String> tempParams=new  HashMap<>();
														 if(servicekeyWithparamKey.contains(","))
														 {
															 for (int i = 0; i < servicekeyWithparamKey.split(",").length; i++) {
																 tempParams.put(servicekeyWithparamKey.split(",")[i].split(":")[1],(String) (map2.get(servicekeyWithparamKey.split(",")[i].split(":")[0])));
															}
														 }else {
															 tempParams.put(servicekeyWithparamKey.split(":")[1],(String) (map2.get(servicekeyWithparamKey.split(":")[0]))); 
														 }
														 tempParams.putAll(tempParamsFor2);
														 tempParamsFor2=new  HashMap<>();
														 String paramForSingleServiceFMParentSer=(String) map2.get(servicekeyWithparamKey.split(":")[0]);
														List<Map<String, Object>> dataReturn=singleServiceImpl.getDataForParams(singleService, tempParams, apiKey,  multiService.getDataSourceKey(),passToken);
														sumazationList.addAll(dataReturn);
														//retObj.put(singleService, sumazationList);
														if(map.get(serviceOnConfig)!=null)
														{
															//bolean 
															for (Iterator iterator5 = map.get(serviceOnConfig).iterator(); iterator5
																	.hasNext();) {
																Map<String, Object> map3 = (Map<String, Object>) iterator5
																		.next();
																if(String.valueOf(map3.get(servicekeyWithparamKey.split(":")[0])).equalsIgnoreCase(paramForSingleServiceFMParentSer))
																		{
																	
																	map3.put(singleService, dataReturn);
																		}
															//	if(map3.get(singleService)!=null) {
															//	 	List<Map<String,Object>> listofObj= (List<Map<String, Object>>) map3.get(singleService);
															//	 	listofObj.addAll(sumazationList);
															//	} 
																
																
															}
														}
													}
													
													//returnData.add(retObj);
														}
												
											}
										 
										 
									 }
									 
									/*if(tempParamsFor3.size()>0)
									{
										for (Iterator iterator4 = tempParamsFor3.iterator(); iterator4.hasNext();) {
											String string = (String) iterator4.next();
											String[] serviceParam=string.split("\\.");
										for (Map.Entry<String,List<Map<String, String>>> entry : hashMapStr.entrySet())  
										{
											if(entry.getKey().equalsIgnoreCase(serviceParam[0]))
													{
												List<Map<String, String>> listObj=entry.getValue(); 
												for (Iterator iterator3 = listObj.iterator(); iterator3
														.hasNext();) {
													Map<String, String> map2 = (Map<String, String>) iterator3.next();
													 Map<String, String> tempParams=new  HashMap<>();
													 tempParams.put(serviceParam[2],(String) (map2.get(serviceParam[1])));
													 String paramForSingleServiceFMParentSer=(String) map2.get(serviceParam[1]);
													List<Map<String, Object>> dataReturn=singleServiceImpl.getDataForParams(singleService, tempParams, apiKey,  dataStoreKey,passToken);
													sumazationList.addAll(dataReturn);
													//retObj.put(singleService, sumazationList);
													if(map.get(serviceParam[0])!=null)
													{
														//bolean 
														for (Iterator iterator5 = map.get(serviceParam[0]).iterator(); iterator5
																.hasNext();) {
															Map<String, Object> map3 = (Map<String, Object>) iterator5
																	.next();
															if(String.valueOf(map3.get(serviceParam[1])).equalsIgnoreCase(paramForSingleServiceFMParentSer))
																	{
																
																map3.put(singleService, dataReturn);
																	}
														//	if(map3.get(singleService)!=null) {
														//	 	List<Map<String,Object>> listofObj= (List<Map<String, Object>>) map3.get(singleService);
														//	 	listofObj.addAll(sumazationList);
														//	} 
															
															
														}
													}
												}
												
												//returnData.add(retObj);
													}
											
										}
									}
										
										
										/*
										 * if(hashMapStr.get(serviceParam[0])!=null&&hashMapStr.get(serviceParam[0]).get
										 * (0)!=null&&hashMapStr.get(serviceParam[0]).get(0).get(serviceParam[1])!=null)
										 * {params.put(serviceParam[2],(String)
										 * (hashMapStr.get(serviceParam[0]).get(0).get(serviceParam[1]))); }
										 */
								//	}
									//else
										if(tempParamsFor2.size()>0)
									{
										  
										List<Map<String, Object>> dataReturn=singleServiceImpl.getDataForParams(singleService, tempParamsFor2, apiKey,  multiService.getDataSourceKey(),passToken);
										sumazationList.addAll(dataReturn);
										retObj.put(singleService, sumazationList);
								 
									}
									else {
										
										List<Map<String, Object>> dataReturn=singleServiceImpl.getDataForParams(singleService, params, apiKey,  multiService.getDataSourceKey(),passToken);
										sumazationList.addAll(dataReturn);
										retObj.put(singleService, sumazationList);
										
									}
								}
							}
							if(returnData.size()==0&&tempParamsFor3.size()>0) {
								 Map<String, String> tempParams=new  HashMap<>();
								List<Map<String, Object>> dataReturn=singleServiceImpl.getDataForParams(singleService, tempParams, apiKey,  multiService.getDataSourceKey(),passToken);
								sumazationList.addAll(dataReturn);
								retObj.put(singleService, sumazationList);
							}else if(returnData.size()==0&&tempParamsFor2.size()>0) {
								 
								List<Map<String, Object>> dataReturn=singleServiceImpl.getDataForParams(singleService, tempParamsFor2, apiKey,  multiService.getDataSourceKey(),passToken);
								sumazationList.addAll(dataReturn);
								retObj.put(singleService, sumazationList);
							}
							if(retObj.size()>0)
							{
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
												 Map<String, String> tempParams=new  HashMap<>();
												 tempParams.put(serviceParam[2],(String) (map2.get(serviceParam[1])));
												 String paramForSingleServiceFMParentSer=(String) map2.get(serviceParam[1]);
												List<Map<String, Object>> dataReturn=singleServiceImpl.getDataForParams(singleService, tempParams, apiKey,  multiService.getDataSourceKey(),passToken);
												sumazationList.addAll(dataReturn);
												//retObj.put(singleService, sumazationList);
												if(map.get(serviceParam[0])!=null)
												{
													//bolean 
													for (Iterator iterator4 = map.get(serviceParam[0]).iterator(); iterator4
															.hasNext();) {
														Map<String, Object> map3 = (Map<String, Object>) iterator4
																.next();
														if(String.valueOf(map3.get(serviceParam[1])).equalsIgnoreCase(paramForSingleServiceFMParentSer))
																{
															
															map3.put(singleService, dataReturn);
																}
													//	if(map3.get(singleService)!=null) {
													//	 	List<Map<String,Object>> listofObj= (List<Map<String, Object>>) map3.get(singleService);
													//	 	listofObj.addAll(sumazationList);
													//	} 
														
														
													}
												}
											}
											
											//returnData.add(retObj);
												}
										
									}
								}else if(serviceParam.length==2)
								{
								 
									 Map<String, String> tempParams=new  HashMap<>();
									 tempParams.put(serviceParam[1], params.get(serviceParam[0]));
									List<Map<String, Object>> dataReturn=singleServiceImpl.getDataForParams(singleService, tempParams, apiKey,  multiService.getDataSourceKey(),passToken);
									sumazationList.addAll(dataReturn);
									retObj.put(singleService, sumazationList);
							 
								}
								
								else {
									 Map<String, String> tempParams=new  HashMap<>();
									List<Map<String, Object>> dataReturn=singleServiceImpl.getDataForParams(singleService, tempParams, apiKey,  multiService.getDataSourceKey(),passToken);
									sumazationList.addAll(dataReturn);
									retObj.put(singleService, sumazationList);
								
								}
							}
						}
						if(returnData.size()==0&&serviceParam.length==3) {
							 Map<String, String> tempParams=new  HashMap<>();
							List<Map<String, Object>> dataReturn=singleServiceImpl.getDataForParams(singleService, tempParams, apiKey,  multiService.getDataSourceKey(),passToken);
							sumazationList.addAll(dataReturn);
							retObj.put(singleService, sumazationList);
						}else if(returnData.size()==0&&serviceParam.length==2) {
							 Map<String, String> tempParams=new  HashMap<>();
							 tempParams.put(serviceParam[1], params.get(serviceParam[0]));
							List<Map<String, Object>> dataReturn=singleServiceImpl.getDataForParams(singleService, tempParams, apiKey,  multiService.getDataSourceKey(),passToken);
							sumazationList.addAll(dataReturn);
							retObj.put(singleService, sumazationList);
						}
						if (retObj.size() > 0) {
							returnData.add(retObj);
						}
					}else {
						Map<String, String> tempParams=new  HashMap<>();
						List<Map<String, Object>> dataReturn=singleServiceImpl.getDataForParams(singleService, tempParams, apiKey,  multiService.getDataSourceKey(),passToken);
						sumazationList.addAll(dataReturn);
						retObj.put(singleService, sumazationList);
						returnData.add(retObj);
					}
					}else {
						Map<String, String> tempParams=new  HashMap<>();
						List<Map<String, Object>> dataReturn=singleServiceImpl.getDataForParams(singleService, tempParams, apiKey,  multiService.getDataSourceKey(),passToken);
						sumazationList.addAll(dataReturn);
						retObj.put(singleService, sumazationList);
						returnData.add(retObj);
					}
			}
			}
		
		return returnData;
	}

	public List<Object> getMultiData(String service, @Valid @NotNull String uniquekey,String apiKey, String dataStoreKey,String passToken) throws Exception  {
		List<MultiService> serviceComponent= MultiServiceMap.get(service);
		ObjectMapper mapper = new ObjectMapper();
		List<Object> returnData=new ArrayList<>();
		if(serviceComponent==null)
		{
			arrangeMultServiceGD(service,apiKey,dataStoreKey,passToken);
			 serviceComponent= MultiServiceMap.get(service);
			 if(serviceComponent!=null) {
				 serviceComponent.sort(new MultiService());
			 }
		}else {serviceComponent.sort(new MultiService());}
		for (Iterator<MultiService> iterator = serviceComponent.iterator(); iterator.hasNext();) {
			Map<String, Object> retObj=new HashMap<>();
			MultiService multiService = (MultiService) iterator.next();
			if(multiService.getServiceType().equals(ServiceType.SINGLE)) {
				String singleService=multiService.getServiceName();
				String relation=multiService.getRelationwithParam();
				if(relation!=null)
					{
					if(relation.contains("none"))
					{
						
						Map<String, Object> dataReturn=singleServiceImpl.getData(singleService, uniquekey, apiKey,  multiService.getDataSourceKey(),passToken);
						retObj.put(singleService,dataReturn);
						returnData.add(retObj);	
					}
					
					
					
					else if(relation.contains(",")) {
						String[] serviceListParam=relation.split(",");	
						boolean retrived=false;
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
							{	
								
							}
							if(serviceParam.length==3) {
								if(datareturn.get(serviceParam[0])!=null&& datareturn.get(serviceParam[0]).get(serviceParam[1])!=null)
								{
									String key = (String) datareturn.get(serviceParam[0]).get(serviceParam[1]);
									if (key != null) {
										uniquekey = key;
										Map<String, Object> dataReturnTmp = singleServiceImpl.getData(singleService,
												uniquekey, apiKey,  multiService.getDataSourceKey(),passToken);
										retObj.put(singleService, dataReturnTmp);
										//returnData.add(retObj);
										Map<String, Map<String, Object>> parentObj = null;
										for (Iterator iterator2 = returnData.iterator(); iterator2.hasNext();) {
											Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) iterator2
													.next();
											if(map.get(serviceParam[0])!=null) {
												parentObj=map;
											break;
											}
										}
										if(parentObj!=null) {
											parentObj.get(serviceParam[0]).putAll(retObj);
											returnData.remove(parentObj);
											returnData.add(parentObj);
										}
										retrived=true;
										break;
									}/* else {
										Map<String, Object> dataReturnTmp = singleServiceImpl.getData(singleService,
												uniquekey, apiKey,  multiService.getDataSourceKey(),passToken);
										retObj.put(singleService, dataReturnTmp);
										returnData.add(retObj);
										break;
									}*/

								}
								
							}
							
						
						}
						if(!retrived) {
							Map<String, Object> dataReturn = singleServiceImpl.getData(singleService,
									uniquekey, apiKey,  multiService.getDataSourceKey(),passToken);
							retObj.put(singleService, dataReturn);
							returnData.add(retObj);
							 
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
						{	 	Map<String, Object> dataReturnTmp = singleServiceImpl.getData(singleService,
												uniquekey, apiKey,  multiService.getDataSourceKey(),passToken);
										retObj.put(singleService, dataReturnTmp);
										returnData.add(retObj);
										break;
						}
						if(serviceParam.length==3) {
							
							if(datareturn.get(serviceParam[0])!=null&& datareturn.get(serviceParam[0]).get(serviceParam[1])!=null)
							{
								String key = (String) datareturn.get(serviceParam[0]).get(serviceParam[1]);
								if (key != null) {
									uniquekey = key;
									//Map<String, Object> dataReturnTmp = singleServiceImpl.getData(singleService,
									//		uniquekey, apiKey,  multiService.getDataSourceKey(),passToken);
									Map<String,String> params=new HashMap<>();
									params.put(serviceParam[2], uniquekey);
								List<Map<String,Object>> dataChild=	singleServiceImpl.getDataForParams(singleService,
											params, apiKey,  multiService.getDataSourceKey(),passToken);
									
									retObj.put(singleService,dataChild );
									//returnData.add(retObj);
									Map<String, Map<String, Object>> parentObj = null;
									for (Iterator iterator2 = returnData.iterator(); iterator2.hasNext();) {
										Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) iterator2
												.next();
										if(map.get(serviceParam[0])!=null) {
											parentObj=map;
										break;
										}
									}
									if(parentObj!=null) {
										parentObj.get(serviceParam[0]).putAll(retObj);
										returnData.remove(parentObj);
										returnData.add(parentObj);
									}
									break;
								} else {
									Map<String, Object> dataReturnTmp = singleServiceImpl.getData(singleService,
											uniquekey, apiKey,  multiService.getDataSourceKey(),passToken);
									retObj.put(singleService, dataReturnTmp);
									returnData.add(retObj);
									break;
								}

							}
						}
						
					}else {
						Map<String, Object> dataReturn=singleServiceImpl.getData(singleService, uniquekey, apiKey,  multiService.getDataSourceKey(),passToken);
						retObj.put(singleService,dataReturn);
						returnData.add(retObj);
					}
					}else
					{
						Map<String, Object> dataReturn=singleServiceImpl.getData(singleService, uniquekey, apiKey,  multiService.getDataSourceKey(),passToken);
						retObj.put(singleService,dataReturn);
						returnData.add(retObj);
						
					}
				}
			}
		
		return returnData;
	}

	public List<Map<String, Map<String, Object>>> deleteMultiData(String service, @Valid @NotNull String uniquekey,String apiKey, String dataStoreKey,String passToken) throws Exception {
		List<MultiService> serviceComponent= MultiServiceMap.get(service);
		ObjectMapper mapper = new ObjectMapper();
		if(serviceComponent==null)
		{
			arrangeMultServiceGD(service,apiKey,dataStoreKey,passToken);
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
						Map<String, Object> dataReturn=singleServiceImpl.deleteData(singleService, uniquekey, apiKey,  multiService.getDataSourceKey(),passToken);
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
							Map<String, Object> dataReturn=singleServiceImpl.deleteData(singleService, uniquekey, apiKey,  multiService.getDataSourceKey(),passToken);
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
						Map<String, Object> dataReturn=singleServiceImpl.deleteData(singleService, uniquekey, apiKey,  multiService.getDataSourceKey(),passToken);
						retObj.put(singleService,dataReturn);
						returnData.add(retObj);
					}
					}
				}
			}
		
		return returnData;
	}

}
