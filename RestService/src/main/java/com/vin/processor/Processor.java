package com.vin.processor;

import java.util.List;
import java.util.Map;

public interface Processor<K,V> {

	public Map<String,String> doPreProcess(Map<String,String> params,String ...value);
	public Map<K,V> doPostProcess(Map<K,V> params,String ...value);
 
}
