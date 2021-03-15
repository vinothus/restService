package com.vin.rest.dynamic;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vin.rest.exception.DatabaseAuthException;
import com.vin.validation.ServiceConstraintViolation;
import com.vin.validation.VinMap;

import vin.rest.common.Constant;

@Component
@Validated
@Controller
public class MultiServiceController {
	@Autowired
	MultiServiceImpl multiserviceImpl;
	@Autowired
	Validator validator;
	@MethodName(MethodName="addData")
	@CrossOrigin
	public ResponseEntity<List<Map<String,Map<String, Object>>>> addData(@PathVariable("service") String service,
			@RequestBody String params,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey,@RequestHeader(value="passToken", defaultValue = "none") String passToken,@RequestHeader Map<String,String> headers) throws Exception   {
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String,Map<String, String>>> inputList=new ArrayList<>();
		Map<String,Map<String, Object>> jsonMap = new HashMap<>();
		try {
			jsonMap = mapper.readValue(params, new TypeReference<Map<String,Map<String, Object>>>() {
			});
		} catch (IOException e) {
			Set<ConstraintViolation<HashMap>> constraintViolation =new HashSet<ConstraintViolation<HashMap>>();
			Map<String, String> errorMessages=new HashMap<String,String>();
			ConstraintViolation<HashMap> cv=new ServiceConstraintViolation<String,String>("Not a Valid JSON "," / "+service); 
			constraintViolation.add(cv);
			throw new ConstraintViolationException(constraintViolation);
		} // converts JSON to Map
		
		
		Map<String,Map<String, String>> map=new HashMap<>();
		
		for (Entry<String, Map<String, Object>> entry : jsonMap.entrySet()) {
		String serviceName=	entry.getKey();
		Map<String, Object> mapofData=entry.getValue();
		Map<String, String> mapofStrData=new HashMap<>();
		for (Entry<String,Object> entryofData : mapofData.entrySet()) {
		String attrbName=	entryofData.getKey();
		Object value=	entryofData.getValue();
			try {
				Map<String, String> innerMap=(Map<String, String>) value; 
				map.put(attrbName, innerMap);
			}catch(Exception e) {
				try {
					List<Map<String, String>> tmpList = (List<Map<String, String>>) value;
					for (Iterator iterator = tmpList.iterator(); iterator.hasNext();) {
						Map<String, String> maps = (Map<String, String>) iterator.next();
						Map<String, Map<String, String>> tmpMaps=new HashMap<>();
						tmpMaps.put(attrbName, maps);
						inputList.add(tmpMaps);
					}
				}catch(Exception ex) {
					mapofStrData.put(attrbName, String.valueOf(value))	;
				}
				
			}
		}
		map.put(serviceName, mapofStrData);
		}
			
			for (Entry<String, Map<String, String>> entry : map.entrySet()) {
				String serviceName=entry.getKey();
				 
				Map<String, String> serviceMap=entry.getValue();
				serviceMap.put("ServiceKey", service);
				serviceMap.put(Constant.REST_METHOD, Constant.POST_METHOD);
				serviceMap.put(Constant.VIN_SERVICE, serviceName);
				serviceMap.put(Constant.VIN_SERVICE_DS, dataStoreKey);
				serviceMap.put(Constant.VIN_SERVICE_APIKEY, apiKey);
			Set<ConstraintViolation<HashMap>> constraintViolation = validator
					.validate(new VinMap<String, String>(serviceMap));
			if (!constraintViolation.isEmpty()) {
				throw new ConstraintViolationException(constraintViolation);
			}
			
			}
			
			inputList.add(map);	
		return new ResponseEntity<List<Map<String,Map<String, Object>>>>(multiserviceImpl.insertMultiData(service, inputList, apiKey,  dataStoreKey,passToken),
				new HttpHeaders(), HttpStatus.OK);
	}
	@MethodName(MethodName="updateData")
	@CrossOrigin
	public ResponseEntity<List<Map<String,Map<String, Object>>>> updateData(@PathVariable("service") String service,
			@RequestBody String params,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey,@RequestHeader(value="passToken", defaultValue = "none") String passToken,@RequestHeader Map<String,String> headers) throws Exception   {
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String,Map<String, String>>> inputList=new ArrayList<>();
		Map<String,Map<String, Object>> jsonMap = new HashMap<>();
		try {
			jsonMap = mapper.readValue(params, new TypeReference<Map<String,Map<String, Object>>>() {
			});
		} catch (IOException e) {
			Set<ConstraintViolation<HashMap>> constraintViolation =new HashSet<ConstraintViolation<HashMap>>();
			Map<String, String> errorMessages=new HashMap<String,String>();
			ConstraintViolation<HashMap> cv=new ServiceConstraintViolation<String,String>("Not a Valid JSON "," / "+service); 
			constraintViolation.add(cv);
			throw new ConstraintViolationException(constraintViolation);
		} // converts JSON to Map
		
		
		Map<String,Map<String, String>> map=new HashMap<>();
		
		for (Entry<String, Map<String, Object>> entry : jsonMap.entrySet()) {
		String serviceName=	entry.getKey();
		Map<String, Object> mapofData=entry.getValue();
		Map<String, String> mapofStrData=new HashMap<>();
		for (Entry<String,Object> entryofData : mapofData.entrySet()) {
		String attrbName=	entryofData.getKey();
		Object value=	entryofData.getValue();
			try {
				Map<String, String> innerMap=(Map<String, String>) value; 
				map.put(attrbName, innerMap);
			}catch(Exception e) {
				try {
					List<Map<String, String>> tmpList = (List<Map<String, String>>) value;
					for (Iterator iterator = tmpList.iterator(); iterator.hasNext();) {
						Map<String, String> maps = (Map<String, String>) iterator.next();
						Map<String, Map<String, String>> tmpMaps=new HashMap<>();
						tmpMaps.put(attrbName, maps);
						inputList.add(tmpMaps);
					}
				}catch(Exception ex) {
					mapofStrData.put(attrbName, String.valueOf(value))	;
				}
				
			}
		}
		map.put(serviceName, mapofStrData);
		}
			
			for (Entry<String, Map<String, String>> entry : map.entrySet()) {
				String serviceName=entry.getKey();
				 
				Map<String, String> serviceMap=entry.getValue();
				serviceMap.put("ServiceKey", service);
				serviceMap.put(Constant.REST_METHOD, Constant.PUT_METHOD);
				serviceMap.put(Constant.VIN_SERVICE, serviceName);
				serviceMap.put(Constant.VIN_SERVICE_DS, dataStoreKey);
				serviceMap.put(Constant.VIN_SERVICE_APIKEY, apiKey);
			Set<ConstraintViolation<HashMap>> constraintViolation = validator
					.validate(new VinMap<String, String>(serviceMap));
			if (!constraintViolation.isEmpty()) {
				throw new ConstraintViolationException(constraintViolation);
			}
			
			}
		
			inputList.add(map);	
		 
		return new ResponseEntity<List<Map<String,Map<String, Object>>>>(multiserviceImpl.updateMultiData(service, inputList, apiKey,  dataStoreKey,passToken),
				new HttpHeaders(), HttpStatus.OK);
	}
	@MethodName(MethodName="getDatum")
	@CrossOrigin
	public ResponseEntity<List<Map<String,List<Map<String, Object>>>>> getDatum(@PathVariable("service") String service,
			@RequestParam   Map<String, String> params,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey,@RequestHeader(value="passToken", defaultValue = "none") String passToken,@RequestHeader Map<String,String> headers) throws DatabaseAuthException, Exception    {
		ObjectMapper mapper = new ObjectMapper();
		//List<Map<String,Map<String, String>>> jsonMap = new ArrayList<>();
		params.put("ServiceKey", service);
		params.put(Constant.REST_METHOD, Constant.GET_ALL_METHOD);
		params.put(Constant.VIN_SERVICE, service);
		params.put(Constant.VIN_SERVICE_DS, dataStoreKey);
		params.put(Constant.VIN_SERVICE_APIKEY, apiKey);
			Set<ConstraintViolation<HashMap>> constraintViolation = validator
					.validate(new VinMap<String, String>(params));
			if (!constraintViolation.isEmpty()) {
				throw new ConstraintViolationException(constraintViolation);
			}
			
			 
		return new ResponseEntity<List<Map<String,List<Map<String, Object>>>>>(multiserviceImpl.getMultiDataForParams(service, params, apiKey,  dataStoreKey,passToken),
				new HttpHeaders(), HttpStatus.OK);
	}
	@MethodName(MethodName="getData")
	@CrossOrigin
	public ResponseEntity<List<Object>> getData(@PathVariable("service") String service,
			@PathVariable("uniquekey") @Valid @NotNull String uniquekey,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey,@RequestHeader(value="passToken", defaultValue = "none") String passToken,@RequestHeader Map<String,String> headers) throws Exception {

		return new ResponseEntity<List<Object>>(multiserviceImpl.getMultiData(service, uniquekey, apiKey,  dataStoreKey,passToken),
				new HttpHeaders(), HttpStatus.OK);
	}
	@MethodName(MethodName="delData")
	@CrossOrigin
	public ResponseEntity<List<Map<String,Map<String, Object>>>> delData(@PathVariable("service") String service,
			@PathVariable("uniquekey") @Valid @NotNull String uniquekey,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey,@RequestHeader(value="passToken", defaultValue = "none") String passToken,@RequestHeader Map<String,String> headers) throws Exception {
		return new ResponseEntity<List<Map<String,Map<String, Object>>>>(multiserviceImpl.deleteMultiData(service, uniquekey, apiKey,  dataStoreKey,passToken),
				new HttpHeaders(), HttpStatus.OK);
	}	
	public static String getMethodName(String Name) {

		Method methods[] = GenericController.class.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			MethodName methodAnno = method.getDeclaredAnnotation(MethodName.class);
			if (methodAnno != null) {
				if (methodAnno.MethodName().equalsIgnoreCase(Name)) {
					return method.getName();
				}
			}
		}

		return null;
	}
	
	public Map<String,Object> doValidation(List<Map<String,Map<String, String>>> data, String service,String apiKey, String dataStoreKey)
	{
		Map<String,Map<String, String>> validData=new HashMap<>();
		Map<String,Map<String, String>> inValidData=new HashMap<>();
		Map<String,Set<ConstraintViolation<HashMap>>> errorData=new HashMap<>();
		Map<String,Object> retData=new HashMap<>();
		for (Iterator iterator = data.iterator(); iterator.hasNext();) {
			Map<String, Map<String, String>> map = (Map<String, Map<String, String>>) iterator.next();
			 for (Entry<String, Map<String, String>> entry : map.entrySet()) {
			      System.out.println("Key : " + entry.getKey() + " value : " + entry.getValue());
			      String serviceName=entry.getKey();
			      Map<String, String> jsonMap = entry.getValue();
			      jsonMap.put(Constant.VIN_SERVICE, serviceName);
					jsonMap.put(Constant.VIN_SERVICE_DS, dataStoreKey);
					jsonMap.put(Constant.VIN_SERVICE_APIKEY, apiKey);
					Set<ConstraintViolation<HashMap>> constraintViolation = validator
							.validate(new VinMap<String, String>(jsonMap));
					if (!constraintViolation.isEmpty()) {
						inValidData.put(serviceName, jsonMap)	;
						errorData.put(serviceName, constraintViolation)	;
					}else
					{
						validData.put(serviceName, jsonMap)	;
					}
			    }
			
		}
		retData.put(Constant.VALIDDATA, validData);
		retData.put(Constant.INVALIDDATA, inValidData);
		retData.put(Constant.ERRORDATA, errorData);
		return retData;
	}
}
