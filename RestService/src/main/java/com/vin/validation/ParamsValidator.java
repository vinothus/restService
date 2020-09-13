package com.vin.validation;

import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ParamsValidator implements ConstraintValidator<ParamMapValidator,Map<String,String>>{

 
	
	@Override
	public void initialize(ParamMapValidator constraintAnnotation) {

	}

	@Override
	public boolean isValid(Map<String, String> value, ConstraintValidatorContext context) {
		 System.out.println("Validator :"+value);
		return true;
	}

	 
 
}
