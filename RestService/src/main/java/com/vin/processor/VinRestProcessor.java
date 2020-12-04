package com.vin.processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vin.rest.repository.EmployeeRepositaryImpl;
import com.vin.validation.ParamsValidator;

import static com.vin.validation.ParamsValidator.dsidMap;
import static com.vin.validation.ParamsValidator.userServiceTableMap;
import static com.vin.validation.ParamsValidator.userServiceAttrTableMap;
import static com.vin.validation.ParamsValidator.reflecClass;
import vin.rest.common.Constant;
@Component
public class VinRestProcessor implements Processor<String,Object> {

	public static Map<String,List<Map<String, Object>>> userProcessorAttrMap= new ConcurrentHashMap<>();
	
	@Autowired
	EmployeeRepositaryImpl employeeRepositaryImpl;
	
	@Autowired
	private Environment env;
	
	Logger log = Logger.getLogger(VinRestProcessor.class.getName());
	
	@Override
	public Map<String, String> doPreProcess(Map<String, String> param, String... value) {
		
		String serviceName =value[2];
		String dataStoreKey = value[1];
		String apiKey = value[0];
		String dsid = null;
		if(userServiceAttrTableMap.size()==0)
		{
			fillServiceAttrbMap(apiKey);
		}
		if(userProcessorAttrMap.size()==0)
		{
			fillVinProcessorMap(apiKey);
		}
		Map<String, String> returnMap=new HashMap<>();
		returnMap.putAll(param);
		if(serviceName!=null&&dataStoreKey!=null&&apiKey!=null) {
			if (dsidMap.get(dataStoreKey + ":" + apiKey) == null) {
				dsid = employeeRepositaryImpl.getdsidFordsName(dataStoreKey);
				dsidMap.put(dataStoreKey + ":" + apiKey, dsid);
			} else {
				dsid = dsidMap.get(dataStoreKey + ":" + apiKey);
			}
			Map<String, String> params = new HashMap<String, String>();
			params.put(Constant.SERVICENAME, serviceName);
			params.put(Constant.DSID, dsid);
			List<Map<String, Object>> cachedServiceObj = userServiceTableMap.get(serviceName + "" + dsid);
			List<Map<String, Object>> resultObj = null;
			JdbcTemplate jdbcTem = null;
			if (cachedServiceObj == null) {
				jdbcTem = employeeRepositaryImpl.setUserDataStore(apiKey, "system", "none");
				try {
					resultObj = jdbcTem.queryForList(
							"select * from Service where dsid= ? and  uid = ( select id   from User where   apikey =  ? ) and serviceName =? ",
							new Object[] { dsid, apiKey, serviceName });
				} catch (Exception e) {
					generateServiceTable();
					resultObj = jdbcTem.queryForList(
							"select * from Service where dsid= ? and  uid = ( select id   from User where   apikey =  ? ) and serviceName =? ",
							new Object[] { dsid, apiKey, serviceName });
				}
				if (resultObj.size() > 0) {
					if(userServiceTableMap.get(serviceName + "" + dsid)==null)
					{
						userServiceTableMap.put(serviceName + "" + dsid, resultObj);
					}else
					{
						userServiceTableMap.get(serviceName + "" + dsid).addAll(resultObj);
					}
				}

			} else {
				resultObj = new ArrayList<Map<String, Object>>();
				resultObj.addAll(cachedServiceObj);
			}
			String service_id = null;
			if (resultObj.size() > 0) {
				for (Iterator iterator = resultObj.iterator(); iterator.hasNext();) {
					Map<String, Object> map = (Map<String, Object>) iterator.next();
					if(serviceName.equalsIgnoreCase((String) (map.get("serviceName"))))
					{
						service_id = String.valueOf(resultObj.get(0).get("id"));
						break;
					}
					
				}
				
			}
	if(service_id!=null) {
			params.put(Constant.service_id, service_id);
			List<Map<String, Object>> cachedServicAttreObj = userServiceAttrTableMap.get(serviceName + "" + dsid);
			List<Map<String, Object>> resultObjattr = null;
			if (cachedServicAttreObj == null) {
				if (jdbcTem == null) {
					jdbcTem = employeeRepositaryImpl.setUserDataStore(apiKey, "system", "none");
					try {
						resultObjattr = jdbcTem.queryForList("select * from Service_Attr where service_id =? ",
								new Object[] { service_id });
					} catch (Exception e) {
						generateServiceTable();
						resultObjattr = jdbcTem.queryForList("select * from Service_Attr where service_id =? ",
								new Object[] { service_id });
					}
					if (resultObjattr.size() > 0) {
						if(userServiceAttrTableMap.get(serviceName + "" + dsid)==null)
						{
							userServiceAttrTableMap.put(serviceName + "" + dsid, resultObjattr);
							
						}else
						{
							userServiceAttrTableMap.get(serviceName + "" + dsid) .addAll(resultObjattr);
						}
					}
				} else {
					try {
						resultObjattr = jdbcTem.queryForList("select * from Service_Attr where service_id =? ",
								new Object[] { service_id });

					} catch (Exception e) {
						generateServiceTable();
						resultObjattr = jdbcTem.queryForList("select * from Service_Attr where service_id =? ",
								new Object[] { service_id });
					}
					if (resultObjattr.size() > 0) {

						if(userServiceAttrTableMap.get(serviceName + "" + dsid)==null)
						{
							userServiceAttrTableMap.put(serviceName + "" + dsid, resultObjattr);
							
						}else
						{
							userServiceAttrTableMap.get(serviceName + "" + dsid) .addAll(resultObjattr);
						}
					
					}
				}

			} else {
				resultObjattr = new ArrayList<Map<String, Object>>();
				resultObjattr.addAll(cachedServicAttreObj);
			}	
			for (Iterator<Map<String, Object>> iterator = resultObjattr.iterator(); iterator.hasNext();) {
				 
				String attrname;
				String attrid;
	            String paramAttrbvalue="";
	            String attrIsProcessor;
	            String serviceId;
				Map<String, Object> map = (Map<String, Object>) iterator.next();
				attrname= String.valueOf(map.get("attrName"));
				attrid=String.valueOf(map.get("id"));
				attrIsProcessor= String.valueOf(map.get("attrIsProcessor"));
				paramAttrbvalue=String.valueOf(param.get(attrname));
				serviceId=String.valueOf(param.get("service_id"));
				for (Map.Entry<String, String> entry : param.entrySet()) {
					if(entry.getKey().equalsIgnoreCase(attrname)&&(attrIsProcessor!=null)&&(attrIsProcessor.equalsIgnoreCase("yes")||attrIsProcessor.equalsIgnoreCase("1")||attrIsProcessor.equalsIgnoreCase("true")))
					{

						if (userProcessorAttrMap.get(attrid+":"+serviceId) == null) {
							List<Map<String, Object>> obj = employeeRepositaryImpl
									.setUserDataStore(apiKey, "system", "none").queryForList(
											"select * from VinProcessor where attr_id = ? ", new Object[] { attrid });
							if (obj != null && obj.size() > 0) {
								userProcessorAttrMap.put(attrid+":"+serviceId, obj);
							}
						}else
						{
							boolean isPresent=false;
							List<Map<String, Object>> obj = userProcessorAttrMap.get(attrid+":"+serviceId);
							 for (Iterator<Map<String, Object>> iterator2 = resultObjattr.iterator(); iterator2.hasNext();) {
								Map<String, Object> processorMap = (Map<String, Object>) iterator2.next();
								if((String.valueOf(processorMap.get("attr_id"))+":"+processorMap.get("service_id")).equalsIgnoreCase(attrid+":"+serviceId))
								{
									isPresent=true;
									break;
									
								}
								
							}
							if(!isPresent) 
							{
								List<Map<String, Object>> tmpobj = employeeRepositaryImpl
										.setUserDataStore(apiKey, "system", "none").queryForList(
												"select * from VinProcessor where attr_id = ? ", new Object[] { attrid });
								userProcessorAttrMap.get(attrid+":"+serviceId).addAll(tmpobj);
							}
						}

						List<Map<String, Object>> obj = userProcessorAttrMap.get(attrid+":"+serviceId);
						if (obj != null && obj.size() > 0) {
							for (Iterator<Map<String, Object>> iterator2 = obj.iterator(); iterator2.hasNext();) {
								Map<String, Object> vinProcessorMapmap = (Map<String, Object>) iterator2.next();
								String className = (String) vinProcessorMapmap.get("classname");
								if (className != null&&!className.equalsIgnoreCase("null")) {
								try {
									
								String	preProcessedValue=	dopreProcess(apiKey, dataStoreKey, serviceName,  className,
											map, param, paramAttrbvalue, env);	
									if(preProcessedValue!=null&&!preProcessedValue.equalsIgnoreCase("null"))
									{
										returnMap.put(entry.getKey(), preProcessedValue);	
									}
								}catch(Exception e) {
									
								}
									
								}

							}

						}
					}
			}
			}
	}
		}
		return returnMap;
	}

	private void fillServiceAttrbMap(String apiKey) {
	     try {
	 		JdbcTemplate jdbcTem = employeeRepositaryImpl.setUserDataStore(apiKey, "system", "none");
	 		String Query="select ser.serviceName as name, ser.dsid as dsid , sa.attrName as attrName , sa.id as id , sa.colName as colName ,sa.attrEnable as attrEnable ,sa.attrBuName as attrBuName ,sa.attrBuIcon as attrBuIcon ,sa.attrCusValidation as attrCusValidation,sa.attrminLength as attrminLength,sa.attrMaxLength as attrMaxLength,sa.attrRegXvalidation as attrRegXvalidation,sa.attrIsMandatory as attrIsMandatory , sa.attrIsProcessor as attrIsProcessor from Service ser , Service_Attr sa where ser.id=sa.service_id ";
	 		List<Map<String,Object>> cachedObject=jdbcTem.queryForList(Query);
	 		for (Iterator iterator = cachedObject.iterator(); iterator.hasNext();) {
	 			Map<String, Object> map = (Map<String, Object>) iterator.next();
	 			
	 			if (userServiceAttrTableMap.get(map.get("name") + "" + map.get("dsid")) == null) {
	 				List<Map<String, Object>> object = new ArrayList<Map<String, Object>>();
	 				object.add(map);
	 				userServiceAttrTableMap.put(map.get("name") + "" + map.get("dsid"), object);

	 			} else {
	 				userServiceAttrTableMap.get(map.get("name") + "" + map.get("dsid")).add(map);

	 			}
	 			
	 			
	 			
	 		}
	      }catch (Exception e)
	     {
	    	  generateServiceTable(); 
	 	}
	 	
	 		
	 	}
	private void fillVinProcessorMap(String apiKey) {
		try {
		List<Map<String, Object>> obj = employeeRepositaryImpl
		.setUserDataStore(apiKey, "system", "none").queryForList(
				"select * from VinProcessor ");
		for (Iterator<Map<String, Object>> iterator = obj.iterator(); iterator.hasNext();) {
			Map<String, Object> map = (Map<String, Object>) iterator.next();
			if(userProcessorAttrMap.get(map.get("attr_id")+":"+map.get("service_id"))==null)
			{
			List<Map<String, Object>> cachedObj=new ArrayList<Map<String, Object>>();
			cachedObj.add(map);
			userProcessorAttrMap.put(String.valueOf(map.get("attr_id"))+":"+map.get("service_id"),cachedObj);
			}else
			{
				userProcessorAttrMap.get(String.valueOf(map.get("attr_id"))+":"+map.get("service_id")).add(map);
			}
		}
		}catch(Exception e)
		{
			generateServiceTable();
		}
		
	}
	private String dopreProcess(String apiKey,String datasourceKey,String service , String processorClassName,
			Map<String, Object> Attrbmap, Map<String, String> mapofVal, String valueFmParam,Environment env) throws InstantiationException,IOException, IllegalAccessException, ClassNotFoundException, JsonProcessingException {
		String retValue = null;
		if(reflecClass.get(processorClassName)==null)
		{
			reflecClass.put(processorClassName, (com.vin.processor.ProcessParam) Class
				.forName(processorClassName).newInstance());
		}
		if(reflecClass.get(processorClassName)!=null)
		{
		com.vin.processor.ProcessParam processor = (com.vin.processor.ProcessParam) reflecClass.get(processorClassName);
		ObjectMapper om=new ObjectMapper();
		 retValue=processor.doPreProcess(valueFmParam,apiKey,datasourceKey,service,om.writeValueAsString(Attrbmap),om.writeValueAsString(mapofVal),om.writeValueAsString(ParamsValidator.getAllKnownProperties(env)));
		}
		if(retValue==null)
		{
			return valueFmParam;
		}
		return retValue;
	}
	private String dopostProcess(String apiKey,String datasourceKey,String service , String processorClassName,
			Map<String, Object> Attrbmap, Map<String, Object> mapofVal, String valueFmParam,Environment env) throws InstantiationException,IOException, IllegalAccessException, ClassNotFoundException, JsonProcessingException {
		String retValue = null;
		if(reflecClass.get(processorClassName)==null)
		{
			reflecClass.put(processorClassName, (com.vin.processor.ProcessParam) Class
				.forName(processorClassName).newInstance());
		}
		if(reflecClass.get(processorClassName)!=null)
		{
		com.vin.processor.ProcessParam processor = (com.vin.processor.ProcessParam) reflecClass.get(processorClassName);
		ObjectMapper om=new ObjectMapper();
		 retValue=processor.doPostProcess(valueFmParam,apiKey,datasourceKey,service,om.writeValueAsString(Attrbmap),om.writeValueAsString(mapofVal),om.writeValueAsString(ParamsValidator.getAllKnownProperties(env)));
		}
		if(retValue==null)
		{
			return valueFmParam;
		}
		return retValue;
	}
	@Override
	public Map<String, Object> doPostProcess(Map<String, Object> param, String... value) {
		
		String serviceName =value[2];
		String dataStoreKey = value[1];
		String apiKey = value[0];
		String dsid = null;
		if(userServiceAttrTableMap.size()==0)
		{
			fillServiceAttrbMap(apiKey);
		}
		Map<String, Object> returnMap=new HashMap<>();
		returnMap.putAll(param);
		if(serviceName!=null&&dataStoreKey!=null&&apiKey!=null) {
			if (dsidMap.get(dataStoreKey + ":" + apiKey) == null) {
				dsid = employeeRepositaryImpl.getdsidFordsName(dataStoreKey);
				dsidMap.put(dataStoreKey + ":" + apiKey, dsid);
			} else {
				dsid = dsidMap.get(dataStoreKey + ":" + apiKey);
			}
			Map<String, String> params = new HashMap<String, String>();
			params.put(Constant.SERVICENAME, serviceName);
			params.put(Constant.DSID, dsid);
			List<Map<String, Object>> cachedServiceObj = userServiceTableMap.get(serviceName + "" + dsid);
			List<Map<String, Object>> resultObj = null;
			JdbcTemplate jdbcTem = null;
			if (cachedServiceObj == null) {
				jdbcTem = employeeRepositaryImpl.setUserDataStore(apiKey, "system", "none");
				try {
					resultObj = jdbcTem.queryForList(
							"select * from Service where dsid= ? and  uid = ( select id   from User where   apikey =  ? ) and serviceName =? ",
							new Object[] { dsid, apiKey, serviceName });
				} catch (Exception e) {
					generateServiceTable();
					resultObj = jdbcTem.queryForList(
							"select * from Service where dsid= ? and  uid = ( select id   from User where   apikey =  ? ) and serviceName =? ",
							new Object[] { dsid, apiKey, serviceName });
				}
				if (resultObj.size() > 0) {
					if(userServiceTableMap.get(serviceName + "" + dsid)==null)
					{
						userServiceTableMap.put(serviceName + "" + dsid, resultObj);
					}else
					{
						userServiceTableMap.get(serviceName + "" + dsid).addAll(resultObj);
					}
				}

			} else {
				resultObj = new ArrayList<Map<String, Object>>();
				resultObj.addAll(cachedServiceObj);
			}
			String service_id = null;
			if (resultObj.size() > 0) {
				for (Iterator iterator = resultObj.iterator(); iterator.hasNext();) {
					Map<String, Object> map = (Map<String, Object>) iterator.next();
					if(serviceName.equalsIgnoreCase((String) (map.get("serviceName"))))
					{
						service_id = String.valueOf(resultObj.get(0).get("id"));
						break;
					}
					
				}
				
			}
	if(service_id!=null) {
			params.put(Constant.service_id, service_id);
			List<Map<String, Object>> cachedServicAttreObj = userServiceAttrTableMap.get(serviceName + "" + dsid);
			List<Map<String, Object>> resultObjattr = null;
			if (cachedServicAttreObj == null) {
				if (jdbcTem == null) {
					jdbcTem = employeeRepositaryImpl.setUserDataStore(apiKey, "system", "none");
					try {
						resultObjattr = jdbcTem.queryForList("select * from Service_Attr where service_id =? ",
								new Object[] { service_id });
					} catch (Exception e) {
						generateServiceTable();
						resultObjattr = jdbcTem.queryForList("select * from Service_Attr where service_id =? ",
								new Object[] { service_id });
					}
					if (resultObjattr.size() > 0) {
						if(userServiceAttrTableMap.get(serviceName + "" + dsid)==null)
						{
							userServiceAttrTableMap.put(serviceName + "" + dsid, resultObjattr);
							
						}else
						{
							userServiceAttrTableMap.get(serviceName + "" + dsid) .addAll(resultObjattr);
						}
					}
				} else {
					try {
						resultObjattr = jdbcTem.queryForList("select * from Service_Attr where service_id =? ",
								new Object[] { service_id });

					} catch (Exception e) {
						generateServiceTable();
						resultObjattr = jdbcTem.queryForList("select * from Service_Attr where service_id =? ",
								new Object[] { service_id });
					}
					if (resultObjattr.size() > 0) {

						if(userServiceAttrTableMap.get(serviceName + "" + dsid)==null)
						{
							userServiceAttrTableMap.put(serviceName + "" + dsid, resultObjattr);
							
						}else
						{
							userServiceAttrTableMap.get(serviceName + "" + dsid) .addAll(resultObjattr);
						}
					
					}
				}

			} else {
				resultObjattr = new ArrayList<Map<String, Object>>();
				resultObjattr.addAll(cachedServicAttreObj);
			}	
			for (Iterator<Map<String, Object>> iterator = resultObjattr.iterator(); iterator.hasNext();) {
				 
				String attrname;
				String attrid;
	            String paramAttrbvalue="";
	            String serviceId;
	            String 	attrIsProcessor;
				Map<String, Object> map = (Map<String, Object>) iterator.next();
				attrname= String.valueOf(map.get("attrName"));
				attrid=String.valueOf(map.get("id"));
				paramAttrbvalue=String.valueOf(param.get(attrname));
				serviceId=String.valueOf(param.get("service_id"));
				attrIsProcessor= String.valueOf(map.get("attrIsProcessor"));
				for (Map.Entry<String, Object> entry : param.entrySet()) {
					if(entry.getKey().equalsIgnoreCase(attrname)&&(attrIsProcessor!=null)&&(attrIsProcessor.equalsIgnoreCase("yes")||attrIsProcessor.equalsIgnoreCase("1")||attrIsProcessor.equalsIgnoreCase("true")))
					{

						if (userProcessorAttrMap.get(attrid+":"+serviceId) == null) {
							List<Map<String, Object>> obj = employeeRepositaryImpl
									.setUserDataStore(apiKey, "system", "none").queryForList(
											"select * from VinProcessor where attr_id = ? ", new Object[] { attrid });
							if (obj != null && obj.size() > 0) {
								userProcessorAttrMap.put(attrid+":"+serviceId, obj);
							}
						}else
						{
							boolean isPresent=false;
							List<Map<String, Object>> obj = userProcessorAttrMap.get(attrid+":"+serviceId);
							 for (Iterator<Map<String, Object>> iterator2 = resultObjattr.iterator(); iterator2.hasNext();) {
								Map<String, Object> processorMap = (Map<String, Object>) iterator2.next();
								if((String.valueOf(processorMap.get("attr_id"))+":"+processorMap.get("service_id")).equalsIgnoreCase(attrid+":"+serviceId))
								{
									isPresent=true;
									break;
									
								}
								
							}
							if(!isPresent) 
							{
								List<Map<String, Object>> tmpobj = employeeRepositaryImpl
										.setUserDataStore(apiKey, "system", "none").queryForList(
												"select * from VinProcessor where attr_id = ? ", new Object[] { attrid });
								userProcessorAttrMap.get(attrid+":"+serviceId).addAll(tmpobj);
							}
						}

						List<Map<String, Object>> obj = userProcessorAttrMap.get(attrid+":"+serviceId);
						if (obj != null && obj.size() > 0) {
							for (Iterator iterator2 = obj.iterator(); iterator2.hasNext();) {
								Map<String, Object> vinProcessorMapmap = (Map<String, Object>) iterator2.next();
								String className = (String) vinProcessorMapmap.get("classname");
								if (className != null&&!className.equalsIgnoreCase("null")) {
								try {
									
								String	preProcessedValue=	dopostProcess(apiKey, dataStoreKey, serviceName,  className,
											map, param, paramAttrbvalue, env);	
									if(preProcessedValue!=null&&!preProcessedValue.equalsIgnoreCase("null"))
									{
										returnMap.put(entry.getKey(), preProcessedValue);	
									}
								}catch(Exception e) {
									
								}
									
								}

							}

						}
					}
			}
			}
	}
		}
		return returnMap;
	}

	public void generateServiceTable() {
		employeeRepositaryImpl.createSysTable();
	}

	 

}
