package com.vin.rest.dynamic;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.vin.rest.exception.GlobalExceptionHandler;
import com.vin.rest.exception.RecordNotFoundException;
import com.vin.rest.exception.ServiceNotFoundException;
import com.vin.rest.model.EmployeeEntity;
import com.vin.rest.repository.EmployeeRepositaryImpl;
import com.vin.rest.service.EmployeeService;
import com.vin.validation.ParamMapValidator;
import com.vin.validation.VinMap;

import vin.rest.common.Constant;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
@Component
@Validated
@Controller
public class GenericController {

	static Logger log = Logger.getLogger(GenericController.class.getName());
	@Autowired
	@LazyInit
	EmployeeService service;
	@Autowired
	EmployeeRepositaryImpl employeeRepositaryImpl;
	
	@Autowired
	Validator validator;
	
	private String controllerPath = null;

	public String getControllerPath() {
		return controllerPath;
	}

	public void setControllerPath(String controllerPath) {
		this.controllerPath = controllerPath;
	}

	public GenericController getObject() {

		return new GenericController();
	}
	@MethodName(MethodName="addData")
	@CrossOrigin
	public ResponseEntity<Map<String, Object>> addData(@PathVariable("service") String service,
			@RequestBody String params,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey) throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		Map<String, String> jsonMap = new HashMap<>();
		jsonMap = mapper.readValue(params, new TypeReference<Map<String, String>>() {
		}); // converts JSON to Map
		jsonMap.put(Constant.VIN_SERVICE, service);
		Set<ConstraintViolation<HashMap>> constraintViolation = validator
				.validate(new VinMap<String, String>(jsonMap));
		if (!constraintViolation.isEmpty()) {
			throw new ConstraintViolationException(constraintViolation);
		}
		return new ResponseEntity<Map<String, Object>>(employeeRepositaryImpl.insertData(service, jsonMap, apiKey, dataStoreKey),
				new HttpHeaders(), HttpStatus.OK);
	}
	@MethodName(MethodName="updateData")
	@CrossOrigin
	public ResponseEntity<Map<String, Object>> updateData(@PathVariable("service") String service,
			@RequestBody String params,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey) throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		Map<String, String> jsonMap = new HashMap<>();
		jsonMap = mapper.readValue(params, new TypeReference<Map<String, String>>() {
		}); // converts JSON to Map
		Set<ConstraintViolation<HashMap>> constraintViolation = validator
				.validate(new VinMap<String, String>(jsonMap));
		if (!constraintViolation.isEmpty()) {
			throw new ConstraintViolationException(constraintViolation);
		}
		return new ResponseEntity<Map<String, Object>>(employeeRepositaryImpl.updateData(service, jsonMap, apiKey, dataStoreKey),
				new HttpHeaders(), HttpStatus.OK);
	}
	
	@MethodName(MethodName="getDatum")
	@CrossOrigin
	public ResponseEntity<List<Map<String, Object>>> getDatum(@PathVariable("service") String service,
			@RequestParam   Map<String, String> params,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey)    {
		 
		params.put(Constant.VIN_SERVICE, service);
		Set<ConstraintViolation<HashMap>> constraintViolation = validator
				.validate(new VinMap<String, String>(params));
		
		  System.out.println("start validation"); System.out.println(validator);
			Set<ConstraintViolation<HashMap>> constraintViolation1 = validator
					.validate(new VinMap<String, String>(params));
			for (Iterator iterator = constraintViolation.iterator(); iterator.hasNext();) {
				ConstraintViolation<HashMap> constraintViolation2 = (ConstraintViolation<HashMap>) iterator.next();
				System.out.println(constraintViolation2.getMessage());
			}
			if (!constraintViolation.isEmpty()) {
				throw new ConstraintViolationException(constraintViolation);
			}
			 
			  
		 
		System.out.println("end validation");
		
		return new ResponseEntity<List<Map<String, Object>>>(employeeRepositaryImpl.getDataForParams(service, params, apiKey, dataStoreKey),
				new HttpHeaders(), HttpStatus.OK);
	}
	@MethodName(MethodName="getData")
	@CrossOrigin
	public ResponseEntity<Map<String, Object>> getData(@PathVariable("service") String service,
			@PathVariable("uniquekey") @Valid @NotNull String uniquekey,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey) throws Exception {

		return new ResponseEntity<Map<String, Object>>(employeeRepositaryImpl.getData(service, uniquekey, apiKey, dataStoreKey),
				new HttpHeaders(), HttpStatus.OK);
	}
@MethodName(MethodName="delData")
@CrossOrigin
	public ResponseEntity<Map<String, Object>> delData(@PathVariable("service") String service,
			@PathVariable("uniquekey") @Valid @NotNull String uniquekey,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey) throws Exception {
		return new ResponseEntity<Map<String, Object>>(employeeRepositaryImpl.deleteData(service, uniquekey, apiKey, dataStoreKey),
				new HttpHeaders(), HttpStatus.OK);
	}

	public ResponseEntity<String>	refreshMataData(@PathVariable("service") String service,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey) throws RecordNotFoundException
	{
		
		return new ResponseEntity<String>(employeeRepositaryImpl.refreshMataData(service),new HttpHeaders(), HttpStatus.OK)	;
	}
	@MethodName(MethodName="clearCache")
	@CrossOrigin
	public ResponseEntity<String>	 clearCache(@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey)
	{
		
		return new ResponseEntity<String>(employeeRepositaryImpl.clearCache(),new HttpHeaders(), HttpStatus.OK)	;
	}
	
	@MethodName(MethodName="home")
	    public String home() {
	        System.out.println("Going home...");
	        return "index";
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
		@MethodName(MethodName="initGC")
		@CrossOrigin
		public ResponseEntity<String>	initGC(@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey)
		{
			employeeRepositaryImpl.init();
			return new ResponseEntity<String>("initilized",new HttpHeaders(), HttpStatus.OK)	;
		}
		
}
