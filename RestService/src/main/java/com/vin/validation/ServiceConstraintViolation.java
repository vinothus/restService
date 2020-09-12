package com.vin.validation;

import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

public class ServiceConstraintViolation<K,V> implements ConstraintViolation<HashMap> {

	String message;
	String propertyPath;
	
	public ServiceConstraintViolation(String message, String propertyPath) {
		 this.message=message;
		 this.propertyPath=propertyPath;
	}

	 

	public void setMessage(String message) {
		this.message = message;
	}

	public void setPropertyPath(String propertyPath) {
		this.propertyPath = propertyPath;
	}

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return this.message;
	}

	@Override
	public String getMessageTemplate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<K, V> getRootBean() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class getRootBeanClass() {
		// TODO Auto-generated method stub
		return this.getClass();
	}

	@Override
	public Object getLeafBean() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getExecutableParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getExecutableReturnValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Path getPropertyPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getInvalidValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConstraintDescriptor getConstraintDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object unwrap(Class type) {
		// TODO Auto-generated method stub
		return null;
	}

}
