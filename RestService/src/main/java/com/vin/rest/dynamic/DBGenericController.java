package com.vin.rest.dynamic;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper; 
import com.vin.validation.VinMap;

public class DBGenericController {

	static Logger log = Logger.getLogger(GenericController.class.getName());
	 
	@Autowired
	DBGenericControllerImpl dbGenericControllerImpl;
	
	@Autowired
	Validator validator;
	
	public ResponseEntity<Map<String, Object>> addData(@PathVariable("datastore") String datastore,@PathVariable("service") String service,
			@RequestBody String params) throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		Map<String, String> jsonMap = new HashMap<>();
		jsonMap = mapper.readValue(params, new TypeReference<Map<String, String>>() {
		}); // converts JSON to Map
		Set<ConstraintViolation<HashMap>> constraintViolation = validator
				.validate(new VinMap<String, String>(jsonMap));
		if (!constraintViolation.isEmpty()) {
			throw new ConstraintViolationException(constraintViolation);
		}
		return new ResponseEntity<Map<String, Object>>(dbGenericControllerImpl.insertData(datastore,service, jsonMap),
				new HttpHeaders(), HttpStatus.OK);
	}

	public ResponseEntity<Map<String, Object>> updateData(@PathVariable("datastore") String datastore,@PathVariable("service") String service,
			@RequestBody String params) throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		Map<String, String> jsonMap = new HashMap<>();
		jsonMap = mapper.readValue(params, new TypeReference<Map<String, String>>() {
		}); // converts JSON to Map
		Set<ConstraintViolation<HashMap>> constraintViolation = validator
				.validate(new VinMap<String, String>(jsonMap));
		if (!constraintViolation.isEmpty()) {
			throw new ConstraintViolationException(constraintViolation);
		}
		return new ResponseEntity<Map<String, Object>>(dbGenericControllerImpl.updateData(datastore,service, jsonMap),
				new HttpHeaders(), HttpStatus.OK);
	}
	public ResponseEntity<List<Map<String, Object>>> getDatum(@PathVariable("datastore") String datastore,@PathVariable("service") String service,
			@RequestParam   Map<String, String> params)    {
		
			Set<ConstraintViolation<HashMap>> constraintViolation = validator
					.validate(new VinMap<String, String>(params));
			for (Iterator iterator = constraintViolation.iterator(); iterator.hasNext();) {
				ConstraintViolation<HashMap> constraintViolation2 = (ConstraintViolation<HashMap>) iterator.next();
				System.out.println(constraintViolation2.getMessage());
			}
			if (!constraintViolation.isEmpty()) {
				throw new ConstraintViolationException(constraintViolation);
			}
			 
		return new ResponseEntity<List<Map<String, Object>>>(dbGenericControllerImpl.getDataForParams(datastore,service, params),
				new HttpHeaders(), HttpStatus.OK);
	}

	public ResponseEntity<Map<String, Object>> getData(@PathVariable("datastore") String datastore,@PathVariable("service") String service,
			@PathVariable("uniquekey") @Valid @NotNull String uniquekey) throws Exception {

		return new ResponseEntity<Map<String, Object>>(dbGenericControllerImpl.getData(datastore,service, uniquekey),
				new HttpHeaders(), HttpStatus.OK);
	}

	public ResponseEntity<Map<String, Object>> delData(@PathVariable("datastore") String datastore,@PathVariable("service") String service,
			@PathVariable("uniquekey") @Valid @NotNull String uniquekey) throws Exception {
		return new ResponseEntity<Map<String, Object>>(dbGenericControllerImpl.deleteData(datastore,service, uniquekey),
				new HttpHeaders(), HttpStatus.OK);
	}

	public ResponseEntity<String>	 clearCache()
	{
		return new ResponseEntity<String>(dbGenericControllerImpl.clearCache(),new HttpHeaders(), HttpStatus.OK)	;
	}
	
	
 
}
