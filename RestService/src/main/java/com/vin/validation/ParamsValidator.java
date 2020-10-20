package com.vin.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vin.rest.repository.EmployeeRepositaryImpl;

import vin.rest.common.Constant;
@Component
public class ParamsValidator implements ConstraintValidator<ParamMapValidator,Map<String,String>>{

	@Autowired
	EmployeeRepositaryImpl employeeRepositaryImpl;
	
	@Override
	public void initialize(ParamMapValidator constraintAnnotation) {

	}

	@Override
	public boolean isValid(Map<String, String> value, ConstraintValidatorContext context) {
		/*
		 * String serviceName= value.get(Constant.VIN_SERVICE);
		 * value.remove(Constant.VIN_SERVICE); Map<String, String> params=new
		 * HashMap<String,String>(); params.put(Constant.SERVICENAME,serviceName);
		 * List<Map<String, Object>>
		 * resultObj=employeeRepositaryImpl.getDataForParams(Constant.SERVICE, params);
		 * String service_id=null; if(resultObj.size()>0) { service_id=
		 * String.valueOf(resultObj.get(0).get("id")); }
		 * 
		 * for (Map.Entry<String,String> entry : value.entrySet()) {
		 * System.out.println("Key = " + entry.getKey() + ", Value = " +
		 * entry.getValue()); params.put(Constant.ATTRNAME, entry.getKey() ); String
		 * attr_id=null; List<Map<String, Object>>
		 * resultObjattr=employeeRepositaryImpl.getDataForParams(Constant.SERVICE_ATTR,
		 * params); if(resultObjattr.size()>0) { attr_id=
		 * String.valueOf(resultObjattr.get(0).get("id")); }
		 * params.put(Constant.service_id, service_id ); params.put(Constant.attr_id,
		 * attr_id ); List<Map<String, Object>>
		 * resultObjval=employeeRepositaryImpl.getDataForParams(Constant.VIN_VALIDATION,
		 * params); String validationName; String validationClass; String
		 * validationParams; if(resultObjval.size()>0) { validationName=
		 * String.valueOf(resultObjattr.get(0).get("name")); validationClass=
		 * String.valueOf(resultObjattr.get(0).get("classname")); validationParams=
		 * String.valueOf(resultObjattr.get(0).get("paramclassname"));
		 * try{com.vin.validatior.Validator<String>
		 * validator=(com.vin.validatior.Validator)Class.forName(validationClass).
		 * newInstance(); validator.isValid(entry.getValue()); }catch(Exception e) {
		 * System.out.println(e.getMessage()); } } }
		 */
	
	
	
		 System.out.println("Validator :"+value);
		return true;
	}

	 
 
}
