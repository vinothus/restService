package com.vin.rest.dynamic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.vin.rest.exception.RecordNotFoundException;
import com.vin.rest.model.EmployeeEntity;
import com.vin.rest.repository.EmployeeRepositaryImpl;
import com.vin.rest.service.EmployeeService;

@Component
public class GenericController {

	static Logger log = Logger.getLogger(GenericController.class.getName());
	@Autowired
	@LazyInit
	EmployeeService service;
	@Autowired
	EmployeeRepositaryImpl employeeRepositaryImpl;
	private String controllerPath = null;

	public String getControllerPath() {
		return controllerPath;
	}

	public void setControllerPath(String controllerPath) {
		this.controllerPath = controllerPath;
	}

	@GetMapping("/{id}")
	public ResponseEntity<EmployeeEntity> getEmployeeById(@PathVariable("id") Long id) throws RecordNotFoundException {
		EmployeeEntity entity = service.getEmployeeById(id);

		return new ResponseEntity<EmployeeEntity>(entity, new HttpHeaders(), HttpStatus.OK);
	}

	public ResponseEntity<List<EmployeeEntity>> getAll(@RequestParam Map<String, String> params)
			throws RecordNotFoundException {
		log.info(params.toString());
		List<EmployeeEntity> entity = employeeRepositaryImpl.getAll();

		return new ResponseEntity<List<EmployeeEntity>>(entity, new HttpHeaders(), HttpStatus.OK);
	}

	public ResponseEntity<List<EmployeeEntity>> getAllEmp(@RequestBody String params) throws RecordNotFoundException {
		log.info(params.toString());
		List<EmployeeEntity> entity = employeeRepositaryImpl.getAll();

		return new ResponseEntity<List<EmployeeEntity>>(entity, new HttpHeaders(), HttpStatus.OK);
	}

	public GenericController getObject() {

		return new GenericController();
	}

	public ResponseEntity<Map<String, Object>> addData(@PathVariable("service") String service,
			@RequestBody String params) throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		Map<String, String> jsonMap = new HashMap<String, String>();
		jsonMap = mapper.readValue(params, new TypeReference<Map<String, String>>() {
		}); // converts JSON to Map

		return new ResponseEntity<Map<String, Object>>(employeeRepositaryImpl.insertData(service, jsonMap),
				new HttpHeaders(), HttpStatus.OK);
	}

	public ResponseEntity<Map<String, Object>> updateData(@PathVariable("service") String service,
			@RequestBody String params) throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		Map<String, String> jsonMap = new HashMap<String, String>();
		jsonMap = mapper.readValue(params, new TypeReference<Map<String, String>>() {
		}); // converts JSON to Map

		return new ResponseEntity<Map<String, Object>>(employeeRepositaryImpl.updateData(service, jsonMap),
				new HttpHeaders(), HttpStatus.OK);
	}

	public ResponseEntity<List<Map<String, Object>>> getDatum(@PathVariable("service") String service,
			@RequestParam Map<String, String> params) throws Exception {

		return new ResponseEntity<List<Map<String, Object>>>(employeeRepositaryImpl.getDataForParams(service, params),
				new HttpHeaders(), HttpStatus.OK);
	}

	public ResponseEntity<Map<String, Object>> getData(@PathVariable("service") String service,
			@PathVariable("uniquekey") String uniquekey) throws Exception {

		return new ResponseEntity<Map<String, Object>>(employeeRepositaryImpl.getData(service, uniquekey),
				new HttpHeaders(), HttpStatus.OK);
	}

	public ResponseEntity<Map<String, Object>> delData(@PathVariable("service") String service,
			@PathVariable("uniquekey") String uniquekey) throws Exception {
		return new ResponseEntity<Map<String, Object>>(employeeRepositaryImpl.deleteData(service, uniquekey),
				new HttpHeaders(), HttpStatus.OK);
	}
}
