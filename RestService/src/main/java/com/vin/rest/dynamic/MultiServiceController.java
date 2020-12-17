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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	public ResponseEntity<List<Map<String,Map<String, Object>>>> addData(@PathVariable("service") String service,
			@RequestBody String params,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey,@RequestHeader(value="passToken", defaultValue = "none") String passToken) throws Exception   {
		ObjectMapper mapper = new ObjectMapper();

		List<Map<String,Map<String, String>>> jsonMap = new ArrayList<>();
		try {
			jsonMap = mapper.readValue(params, new TypeReference<List<Map<String,Map<String, Object>>>>() {
			});
		} catch (IOException e) {
			Set<ConstraintViolation<HashMap>> constraintViolation =new HashSet<ConstraintViolation<HashMap>>();
			Map<String, String> errorMessages=new HashMap<String,String>();
			ConstraintViolation<HashMap> cv=new ServiceConstraintViolation<String,String>("Not a Valid JSON "," / "+service); 
			constraintViolation.add(cv);
			throw new ConstraintViolationException(constraintViolation);
		} // converts JSON to Map
		for (Iterator<Map<String, Map<String, String>>> iterator = jsonMap.iterator(); iterator.hasNext();) {
			Map<String, Map<String, String>> map = iterator.next();
			
			for (Entry<String, Map<String, String>>entry : map.entrySet()) {
				String serviceName=entry.getKey();
				 
				Map<String, String> serviceMap=entry.getValue();
				serviceMap.put("ServiceKey", service);
			Set<ConstraintViolation<HashMap>> constraintViolation = validator
					.validate(new VinMap<String, String>(serviceMap));
			if (!constraintViolation.isEmpty()) {
				throw new ConstraintViolationException(constraintViolation);
			}
			
			}
		}
		
		return new ResponseEntity<List<Map<String,Map<String, Object>>>>(multiserviceImpl.insertMultiData(service, jsonMap, apiKey,  dataStoreKey,passToken),
				new HttpHeaders(), HttpStatus.OK);
	}
	@MethodName(MethodName="updateData")
	public ResponseEntity<List<Map<String,Map<String, Object>>>> updateData(@PathVariable("service") String service,
			@RequestBody String params,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey,@RequestHeader(value="passToken", defaultValue = "none") String passToken) throws Exception   {
		ObjectMapper mapper = new ObjectMapper();

		List<Map<String,Map<String, String>>> jsonMap = new ArrayList<>();
		try {
			jsonMap = mapper.readValue(params, new TypeReference<List<Map<String,Map<String, Object>>>>() {
			});
		} catch (IOException e) {
			Set<ConstraintViolation<HashMap>> constraintViolation =new HashSet<ConstraintViolation<HashMap>>();
			Map<String, String> errorMessages=new HashMap<String,String>();
			ConstraintViolation<HashMap> cv=new ServiceConstraintViolation<String,String>("Not a Valid JSON "," / "+service); 
			constraintViolation.add(cv);
			throw new ConstraintViolationException(constraintViolation);
		}// converts JSON to Map
		for (Iterator<Map<String, Map<String, String>>> iterator = jsonMap.iterator(); iterator.hasNext();) {
			Map<String, Map<String, String>> map = iterator.next();
			
			for (Entry<String, Map<String, String>>entry : map.entrySet()) {
				String serviceName=entry.getKey();
				 
				Map<String, String> serviceMap=entry.getValue();
				serviceMap.put("ServiceKey", service);
			Set<ConstraintViolation<HashMap>> constraintViolation = validator
					.validate(new VinMap<String, String>(serviceMap));
			if (!constraintViolation.isEmpty()) {
				throw new ConstraintViolationException(constraintViolation);
			}
			
			}
		}
		 
		return new ResponseEntity<List<Map<String,Map<String, Object>>>>(multiserviceImpl.updateMultiData(service, jsonMap, apiKey,  dataStoreKey,passToken),
				new HttpHeaders(), HttpStatus.OK);
	}
	@MethodName(MethodName="getDatum")
	public ResponseEntity<List<Map<String,List<Map<String, Object>>>>> getDatum(@PathVariable("service") String service,
			@RequestParam   Map<String, String> params,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey,@RequestHeader(value="passToken", defaultValue = "none") String passToken) throws JsonParseException, JsonMappingException, IOException    {
		ObjectMapper mapper = new ObjectMapper();
		//List<Map<String,Map<String, String>>> jsonMap = new ArrayList<>();
		//params.put("ServiceKey", service);
		//	Set<ConstraintViolation<HashMap>> constraintViolation = validator
		//			.validate(new VinMap<String, String>(params));
		//	if (!constraintViolation.isEmpty()) {
		//		throw new ConstraintViolationException(constraintViolation);
		//	}
			
			 
		return new ResponseEntity<List<Map<String,List<Map<String, Object>>>>>(multiserviceImpl.getMultiDataForParams(service, params, apiKey,  dataStoreKey,passToken),
				new HttpHeaders(), HttpStatus.OK);
	}
	@MethodName(MethodName="getData")
	public ResponseEntity<List<Map<String,Map<String, Object>>>> getData(@PathVariable("service") String service,
			@PathVariable("uniquekey") @Valid @NotNull String uniquekey,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey,@RequestHeader(value="passToken", defaultValue = "none") String passToken) throws Exception {

		return new ResponseEntity<List<Map<String,Map<String, Object>>>>(multiserviceImpl.getMultiData(service, uniquekey, apiKey,  dataStoreKey,passToken),
				new HttpHeaders(), HttpStatus.OK);
	}
	@MethodName(MethodName="delData")
	public ResponseEntity<List<Map<String,Map<String, Object>>>> delData(@PathVariable("service") String service,
			@PathVariable("uniquekey") @Valid @NotNull String uniquekey,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey,@RequestHeader(value="passToken", defaultValue = "none") String passToken) throws Exception {
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
