package com.vin.rest.demo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;

import com.vin.rest.dynamic.MultiService;
import com.vin.rest.dynamic.ServiceType;


public class BasicUnitTest {
	
	
	@Test
	public void test()
	{
		Map<String, List<MultiService>> MultiServiceMap= new ConcurrentHashMap<>();
		List<MultiService> serviceList =new ArrayList<>();
		
		MultiService service2=new MultiService();
		service2.setId(1);
		service2.setServiceType(ServiceType.SINGLE);
		service2.setPriproty(3);
		service2.setServiceName("tbl studentlow Priority");
		//service.setRelationwithParam("..");
		serviceList.add(service2);
		
		MultiService service=new MultiService();
		service.setId(1);
		service.setServiceType(ServiceType.SINGLE);
		service.setPriproty(1);
		service.setServiceName("tbl student");
		//service.setRelationwithParam("..");
		serviceList.add(service);
		
		MultiService service1=new MultiService();
		service1.setId(1);
		service1.setServiceType(ServiceType.SINGLE);
		service1.setServiceName("tbl employees");
		service1.setPriproty(2);
		//service.setRelationwithParam("..");
		serviceList.add(service1);
		for (Iterator iterator = serviceList.iterator(); iterator.hasNext();) {
			MultiService multiService = (MultiService) iterator.next();
			System.out.println(multiService.getServiceName()+"  "+multiService.getPriproty());
		}
		serviceList.sort(new MultiService());
		for (Iterator iterator = serviceList.iterator(); iterator.hasNext();) {
			MultiService multiService = (MultiService) iterator.next();
			System.out.println(multiService.getServiceName()+"  "+multiService.getPriproty());
		}
		MultiServiceMap.put("student", serviceList);
		
	}
	
	
	public static String removeLastCharOptional(String s) {
	    return Optional.ofNullable(s)
	      .filter(str -> str.length() != 0)
	      .map(str -> str.substring(0, str.length() - 1))
	      .orElse(s);
	    }
	private String replaceDoubleQute(String primaryKey) {
		if(primaryKey.charAt(0)=='"')
		{
			primaryKey=primaryKey.replaceFirst("\"", "");
		}
		if(primaryKey.charAt(primaryKey.length()-1)=='"')
		{
			primaryKey=removeLastCharOptional(primaryKey);
		}
		return primaryKey;
	}
	
	@Test
	public void testString()
	{
		String testStr="\"100000000\"";
		System.out.println(testStr);
		testStr=replaceDoubleQute(testStr);
		System.out.println(testStr);
	}

}
