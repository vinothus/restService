package com.vin.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;


@Retention(RetentionPolicy.RUNTIME)
//@Target(ElementType.PARAMETER)
@Target({ElementType.CONSTRUCTOR,ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,ElementType.PARAMETER})
@Constraint(validatedBy = { ParamsValidator.class })
@Documented
public @interface ParamMapValidator {

	
	 String message() default "Invalid Parameters";
	 
	 Class<?>[] groups() default {};
	    Class<? extends Payload>[] payload() default {};
}
