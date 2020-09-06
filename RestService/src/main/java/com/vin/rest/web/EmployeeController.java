package com.vin.rest.web;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vin.rest.exception.RecordNotFoundException;
import com.vin.rest.model.EmployeeEntity;
import com.vin.rest.repository.EmployeeRepositaryImpl;
import com.vin.rest.service.EmployeeService;
 
@RestController
@RequestMapping("/employees")
public class EmployeeController
{
	Logger log = Logger.getLogger(EmployeeController.class.getName());
    @Autowired
    EmployeeService service;
    
    @Autowired
    EmployeeRepositaryImpl employeeRepositaryImpl;
    
    @GetMapping("/get/all")
    public ResponseEntity<List<EmployeeEntity>> getAllEmp() {
        List<EmployeeEntity> list = service.getAllEmployees();
 
        return new ResponseEntity<List<EmployeeEntity>>(list, new HttpHeaders(), HttpStatus.OK);
    }
    
	@GetMapping("/name/{name}")
	public ResponseEntity<EmployeeEntity> getEmployeeByName(@PathVariable("name") String name)
			throws RecordNotFoundException {
		EmployeeEntity entity = employeeRepositaryImpl.getEmployeeByName(name);// = service.getEmployeeById(id);

		return new ResponseEntity<EmployeeEntity>(entity, new HttpHeaders(), HttpStatus.OK);
	}
	 
 
    @GetMapping("/id/{id}")
    public ResponseEntity<EmployeeEntity> getEmployeeById(@PathVariable("id") Long id)
                                                    throws RecordNotFoundException {
        EmployeeEntity entity = service.getEmployeeById(id);
 
        return new ResponseEntity<EmployeeEntity>(entity, new HttpHeaders(), HttpStatus.OK);
    }
 
    @PostMapping
    public ResponseEntity<EmployeeEntity> createOrUpdateEmployee(EmployeeEntity employee)
                                                    throws RecordNotFoundException {
        EmployeeEntity updated = service.createOrUpdateEmployee(employee);
        return new ResponseEntity<EmployeeEntity>(updated, new HttpHeaders(), HttpStatus.OK);
    }
 
    @DeleteMapping("/{id}")
    public HttpStatus deleteEmployeeById(@PathVariable("id") Long id)
                                                    throws RecordNotFoundException {
        service.deleteEmployeeById(id);
        return HttpStatus.FORBIDDEN;
    }
 
    
    @GetMapping("/service/{name}")
	public ResponseEntity<List<Map<String,Object>>> getServiceDataByName(@PathVariable("name") String name)
			throws Exception {
    	List<Map<String,Object>> entity = employeeRepositaryImpl.getServiceDataByName(name);// = service.getEmployeeById(id);

		return new ResponseEntity<List<Map<String,Object>>>((List<Map<String, Object>>) entity, new HttpHeaders(), HttpStatus.OK);
	}
    @PostConstruct
    public void init() {
    	log.info("Test post construct");
    }	 
    
}