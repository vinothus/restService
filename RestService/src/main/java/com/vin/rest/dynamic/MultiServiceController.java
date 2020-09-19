package com.vin.rest.dynamic;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import com.vin.validation.ServiceConstraintViolation;
import com.vin.validation.VinMap;

@Component
@Validated
@Controller
public class MultiServiceController {
	@Autowired
	MultiServiceImpl multiserviceImpl;
	@Autowired
	Validator validator;
	public ResponseEntity<List<Map<String,Map<String, Object>>>> addData(@PathVariable("service") String service,
			@RequestBody String params)   {
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
		
		return new ResponseEntity<List<Map<String,Map<String, Object>>>>(multiserviceImpl.insertMultiData(service, jsonMap),
				new HttpHeaders(), HttpStatus.OK);
	}

	public ResponseEntity<List<Map<String,Map<String, Object>>>> updateData(@PathVariable("service") String service,
			@RequestBody String params)   {
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
		 
		return new ResponseEntity<List<Map<String,Map<String, Object>>>>(multiserviceImpl.updateMultiData(service, jsonMap),
				new HttpHeaders(), HttpStatus.OK);
	}
	public ResponseEntity<List<Map<String,List<Map<String, Object>>>>> getDatum(@PathVariable("service") String service,
			@RequestParam   Map<String, String> params)    {
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String,Map<String, String>>> jsonMap = new ArrayList<>();
		params.put("ServiceKey", service);
			Set<ConstraintViolation<HashMap>> constraintViolation = validator
					.validate(new VinMap<String, String>(params));
			if (!constraintViolation.isEmpty()) {
				throw new ConstraintViolationException(constraintViolation);
			}
			
			 
		return new ResponseEntity<List<Map<String,List<Map<String, Object>>>>>(multiserviceImpl.getMultiDataForParams(service, params),
				new HttpHeaders(), HttpStatus.OK);
	}

	public ResponseEntity<List<Map<String,Map<String, Object>>>> getData(@PathVariable("service") String service,
			@PathVariable("uniquekey") @Valid @NotNull String uniquekey) throws Exception {

		return new ResponseEntity<List<Map<String,Map<String, Object>>>>(multiserviceImpl.getMultiData(service, uniquekey),
				new HttpHeaders(), HttpStatus.OK);
	}

	public ResponseEntity<List<Map<String,Map<String, Object>>>> delData(@PathVariable("service") String service,
			@PathVariable("uniquekey") @Valid @NotNull String uniquekey) throws Exception {
		return new ResponseEntity<List<Map<String,Map<String, Object>>>>(multiserviceImpl.deleteMultiData(service, uniquekey),
				new HttpHeaders(), HttpStatus.OK);
	}	
	
}
