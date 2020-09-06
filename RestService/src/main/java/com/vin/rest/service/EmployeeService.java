package com.vin.rest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vin.rest.exception.RecordNotFoundException;
import com.vin.rest.model.EmployeeEntity;
import com.vin.rest.repository.EmployeeRepository;
 
@Service
public class EmployeeService {
     String recNFE="No employee record exist for given id";
    @Autowired
    EmployeeRepository repository;
     
    public List<EmployeeEntity> getAllEmployees()
    {
        List<EmployeeEntity> employeeList = repository.findAll();
         
        if(!employeeList.isEmpty()) {
            return employeeList;
        } else {
            return new ArrayList<>();
        }
    }
     
    public EmployeeEntity getEmployeeById(Long id) throws RecordNotFoundException
    {
        Optional<EmployeeEntity> employee = repository.findById(id);
         
        if(employee.isPresent()) {
            return employee.get();
        } else {
            throw new RecordNotFoundException(recNFE);
        }
    }
     
    public EmployeeEntity createOrUpdateEmployee(EmployeeEntity entity)  
    {
        Optional<EmployeeEntity> employee = repository.findById(entity.getId());
         
        if(employee.isPresent())
        {
            EmployeeEntity newEntity = employee.get();
            newEntity.setEmail(entity.getEmail());
            newEntity.setFirstName(entity.getFirstName());
            newEntity.setLastName(entity.getLastName());
 
            newEntity = repository.save(newEntity);
             
            return newEntity;
        } else {
            entity = repository.save(entity);
             
            return entity;
        }
    }
     
    public void deleteEmployeeById(Long id) throws RecordNotFoundException
    {
        Optional<EmployeeEntity> employee = repository.findById(id);
         
        if(employee.isPresent())
        {
            repository.deleteById(id);
        } else {
            throw new RecordNotFoundException(recNFE);
        }
    }
    
    
    
    
    public EmployeeEntity getEmployeeByName() throws RecordNotFoundException
    {
        Optional<EmployeeEntity> employee=null; 
         
        if(employee.isPresent()) {
            return employee.get();
        } else {
            throw new RecordNotFoundException(recNFE);
        }
    }
    
    
}