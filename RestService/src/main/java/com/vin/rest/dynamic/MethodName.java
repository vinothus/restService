package com.vin.rest.dynamic;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
public @interface MethodName {
public String MethodName() default "none";
}
