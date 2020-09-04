package com.dynamic;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.errorprone.annotations.concurrent.LazyInit;
import com.howtodoinjava.demo.exception.RecordNotFoundException;
import com.howtodoinjava.demo.model.EmployeeEntity;
import com.howtodoinjava.demo.repository.EmployeeRepositaryImpl;
import com.howtodoinjava.demo.service.EmployeeService;

import net.bytebuddy.implementation.bind.annotation.Argument;

@Component
public class GenericController {
	
    @Autowired
    @LazyInit
    EmployeeService service;
    @Autowired
    EmployeeRepositaryImpl employeeRepositaryImpl;
private String controllerPath=null;

public String getControllerPath() {
	return controllerPath;
}


public void setControllerPath(String controllerPath) {
	this.controllerPath = controllerPath;
}


@GetMapping("/{id}")
public ResponseEntity<EmployeeEntity> getEmployeeById(@PathVariable("id") Long id)
                                                throws RecordNotFoundException {
    EmployeeEntity entity = service.getEmployeeById(id);

    return new ResponseEntity<EmployeeEntity>(entity, new HttpHeaders(), HttpStatus.OK);
}
 
public ResponseEntity<List<EmployeeEntity>> getAll(@RequestParam Map<String,String> params)
                                                throws RecordNotFoundException {
	System.out.println(params);
    List<EmployeeEntity> entity = employeeRepositaryImpl.getAll();

    return new ResponseEntity< List<EmployeeEntity>>(entity, new HttpHeaders(), HttpStatus.OK);
}

public ResponseEntity<List<EmployeeEntity>> getAllEmp(@RequestBody String params)
        throws RecordNotFoundException {
System.out.println(params);
List<EmployeeEntity> entity = employeeRepositaryImpl.getAll();

return new ResponseEntity< List<EmployeeEntity>>(entity, new HttpHeaders(), HttpStatus.OK);
}
public GenericController getObject()
{
	
return new  GenericController();
}
}
