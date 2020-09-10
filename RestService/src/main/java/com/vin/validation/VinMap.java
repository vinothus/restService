package com.vin.validation;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;


 


public class VinMap<K,V> extends HashMap<K,V>{
	@ParamMapValidator
	HashMap<String,String> value;	
	 public VinMap(@Valid Map<K, V> params)
	 {
		 
		 this.putAll( params);
		 for (Map.Entry<K,V> entry : this.entrySet())  
	            System.out.println("Key = " + entry.getKey() + 
	                             ", Value = " + entry.getValue()); 
		 this.value=(HashMap<String, String>) params;
	 }
	 public VinMap()
	 {
		 
		 
	 }
	 
	private static final long serialVersionUID = 1L;

}
