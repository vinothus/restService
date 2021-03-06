package com.vin.rest.web;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vin.rest.exception.RecordNotFoundException;
import com.vin.rest.model.EmployeeEntity;
import com.vin.rest.repository.EmployeeRepositaryImpl;
import com.vin.rest.service.EmployeeService;
import com.vin.validation.ServiceConstraintViolation;
 
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
 
    
 
    @PostConstruct
    public void init() {
    	log.info("Test post construct");
    }
    
    @GetMapping("/testservice/{name}")
    @ExceptionHandler({ ConstraintViolationException.class })
   	public ResponseEntity<List<Map<String,Object>>> getServiceData(@PathVariable("name") String name)
   			  {
      
    	Set<ConstraintViolation<HashMap>> constraintViolation =new HashSet<ConstraintViolation<HashMap>>();
		Map errorMessages=new HashMap<String,String>();
		ConstraintViolation<HashMap> cv=new ServiceConstraintViolation<String,String>("Service Not Found "," / "+"test"); 
		constraintViolation.add(cv);
    	throw new ConstraintViolationException(constraintViolation);
   	}
    
}