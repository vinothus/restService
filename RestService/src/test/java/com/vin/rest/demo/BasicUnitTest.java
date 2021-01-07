package com.vin.rest.demo;

import static org.mockito.Mockito.when;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vin.processor.ProcessParam;
import com.vin.processor.Processor;
import com.vin.processor.PropertyProcessor;
import com.vin.processor.UpperLowerParamProcessor;
import com.vin.processor.VinRestProcessor;
import com.vin.rest.dynamic.MultiService;
import com.vin.rest.dynamic.ServiceType;
import com.vin.rest.exception.DatabaseAuthException;
import com.vin.rest.repository.EmployeeRepositaryImpl;
import com.vin.validatior.ClassValidator;
import com.vin.validatior.EmailMustContainFirst;
import com.vin.validatior.Validator;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
@RunWith(MockitoJUnitRunner.class)
public class BasicUnitTest {
	
	   JdbcTemplate jdbctemp;
	   @InjectMocks 
	    EmployeeRepositaryImpl empRep =new EmployeeRepositaryImpl();
	    @Mock
	    Environment env;
	    static Logger log = Logger.getLogger(BasicUnitTest.class.getName());
	    
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
	@Test
	public void testCustomValidation() throws JsonParseException, JsonMappingException, IOException
	{
	Validator< String>	validator=new EmailMustContainFirst();
	Map<String,String> mapofval=new HashMap<>();
	mapofval.put("firstname", "vinoth");
	ObjectMapper om=new ObjectMapper();
	Map<String, String> env=new  HashMap<>();
	env.put("EmailMustContainFirst.keys", "firstname");
	boolean validaity=validator.isValid("vinoth.paulraj@vinrest.com","","","",om.writeValueAsString(mapofval),om.writeValueAsString(mapofval),om.writeValueAsString(env));
	System.out.println("EmailMustContainFirst: "+validaity);
		
	}
	//@Test
	public void testCustomClassValidation() throws JsonParseException, JsonMappingException, IOException
	{
		Validator< String>	validator=new  ClassValidator();
		ObjectMapper om=new ObjectMapper();
		Map<String,String> mapofval=new HashMap<>();
		boolean validaity=validator.isValid("com.vin.validatior.EmailMustContainFirst","","","",om.writeValueAsString(mapofval),om.writeValueAsString(mapofval),om.writeValueAsString(mapofval));
		System.out.println("validaity :"+validaity);
			
	}
	
	@Test
	public void testPostProcess() throws JsonParseException, JsonMappingException, IOException
	{
		ProcessParam  	processor=new  UpperLowerParamProcessor();
		ObjectMapper om=new ObjectMapper();
		Map<String,String> mapofval=new HashMap<>();
		String validaity=processor.doPostProcess("com.vin.validatior.EmailMustContainFirst","","","",om.writeValueAsString(mapofval),om.writeValueAsString(mapofval),om.writeValueAsString(mapofval));
		System.out.println("validaity :"+validaity);
			
	}
	
	@Test
	public void testPreProcess() throws JsonParseException, JsonMappingException, IOException
	{
		ProcessParam  	processor=new  UpperLowerParamProcessor();
		ObjectMapper om=new ObjectMapper();
		Map<String,String> mapofval=new HashMap<>();
		String validaity=processor.doPreProcess("com.vin.validatior.EmailMustContainFirst","","","",om.writeValueAsString(mapofval),om.writeValueAsString(mapofval),om.writeValueAsString(mapofval));
		System.out.println("validaity :"+validaity);
			
	}
	
	@Test
	public void testTotalPreProcess() throws DatabaseAuthException, Exception
	{
		Map<String, Object>  data = null;
		initStudent();
		List<Map<String, Object>> obj = jdbctemp.queryForList(
				"select sa.id as id, sa.service_id as sid ,sa.attrName as name from Service ser , Service_Attr sa where ser.id=sa.service_id and ser.tableName = 'TBL_STUDENT' and sa.attrName= 'firstname' ");
		Map<String, String> params = new HashMap<>();
		params.put("serviceid", String.valueOf(( obj.get(0).get("sid"))));
		params.put("attrid", String.valueOf( obj.get(0).get("id")));
		params.put("name", "uppertolower");
		params.put("classname", "com.vin.processor.UpperLowerParamProcessor");
		try {
			  empRep.getDataForParams("tbl student", params,"system", "system", "none");
			  params.put("id", String.valueOf( obj.get(0).get("id")));
			  params.put("attrisprocessor", "yes");
			  empRep. updateData("service attr", params, "system", "system", "none");
			  params.remove("id");
			   data=	empRep.insertData("vinprocessor", params, "system", "system", "none");
		} catch (Exception e) {

			e.printStackTrace();
		}
		Processor<String, Object> processor = new VinRestProcessor();
		ObjectMapper om = new ObjectMapper();
		Map<String, String> mapofval = new HashMap<>();
		mapofval.put("firstname", "firstName");
		  ReflectionTestUtils.setField(processor, "employeeRepositaryImpl", empRep);
		Map validaity = processor.doPreProcess(mapofval, "system", "system", "tbl student");
		System.out.println("validaity :" + validaity);
		params.put("id", String.valueOf(data.get("id")));
		params.put("serviceid", String.valueOf( obj.get(0).get("sid")));
		params.put("attrid",  String.valueOf( obj.get(0).get("id")));
		params.put("name", "uppertolower");
		params.put("classname", "null");
		try {
			 data=empRep.deleteData("vinprocessor", String.valueOf(data.get("id")),  "system", "system", "none");//	empRep.insertData("vinprocessor", params, "system", "system", "none");
			   System.out.println(data);
		} catch (Exception e) {

			e.printStackTrace();
		}
		teardown();

	}
	@Test
	public void testTotalPostProcess() throws DatabaseAuthException, Exception
	{
		Map<String, Object>  data = null;
		initStudent();
		List<Map<String, Object>> obj = jdbctemp.queryForList(
				"select sa.id as id, sa.service_id as sid ,sa.attrName as name from Service ser , Service_Attr sa where ser.id=sa.service_id and ser.tableName = 'TBL_STUDENT' and sa.attrName= 'firstname' ");
		Map<String, String> params = new HashMap<>();
		params.put("serviceid", String.valueOf(( obj.get(0).get("sid"))));
		params.put("attrid", String.valueOf( obj.get(0).get("id")));
		params.put("name", "uppertolower");
		params.put("classname", "com.vin.processor.UpperLowerParamProcessor");
		try {
			
			  empRep.getDataForParams("tbl student", params,"system", "system", "none");
			  params.put("id", String.valueOf( obj.get(0).get("id")));
			  params.put("attrisprocessor", "yes");
			  empRep.updateData("service attr", params, "system", "system", "none");
			  params.remove("id");
			   data=	empRep.insertData("vinprocessor", params, "system", "system", "none");
		} catch (Exception e) {

			e.printStackTrace();
		}
		Processor<String, Object> processor = new VinRestProcessor();
		ObjectMapper om = new ObjectMapper();
		Map<String, Object> mapofval = new HashMap<>();
		mapofval.put("firstname", "firstName");
		 ReflectionTestUtils.setField(processor, "employeeRepositaryImpl", empRep);
		Map validaity = processor.doPostProcess(mapofval, "system", "system", "tbl student");
		System.out.println("validaity :" + validaity);
		params.put("id", String.valueOf(data.get("id")));
		params.put("serviceid",  String.valueOf(obj.get(0).get("sid")));
		params.put("attrid",  String.valueOf( obj.get(0).get("id")));
		params.put("name", "uppertolower");
		params.put("classname", "null");
		try {
			   data=empRep.deleteData("vinprocessor", String.valueOf(data.get("id")),  "system", "system", "none");//	empRep.insertData("vinprocessor", params, "system", "system", "none");
			   System.out.println(data);
		} catch (Exception e) {

			e.printStackTrace();
		}
		teardown();

	}
	public void initStudent() throws DatabaseAuthException, Exception
	{
		env=new MockEnvironment().withProperty("sys.spring.datasource.driver-class-name", "com.mysql.jdbc.Driver")
				.withProperty("sys.spring.datasource.url", "jdbc:mysql://152.67.161.222/cameldb")
				.withProperty("sys.spring.datasource.username", "root")
				.withProperty("sys.spring.datasource.password", "vinaug@2020")
				.withProperty("multiservicename", "multiserviceTest")
				.withProperty("multiserviceBussinessName", "multiserviceTest");
		 
		 ReflectionTestUtils.setField(empRep, "env",env);
		//when(env.getProperty("sys.spring.datasource.driver-class-name") ).thenReturn("");
		//when( env.getProperty("sys.spring.datasource.driver-class-name") ).thenReturn("com.mysql.jdbc.Driver");
		//when( env.getProperty("sys.spring.datasource.url")).thenReturn("jdbc:mysql://remotemysql.com/H228Vnp5dM");
		//when( env.getProperty("sys.spring.datasource.username")).thenReturn("H228Vnp5dM");
		//when( env.getProperty("sys.spring.datasource.password")).thenReturn("mNbPRHduxC");
		
		jdbctemp = empRep.setUserDataStore("system", "system", "none");
		if (!empRep.isTablePresent("TBL_STUDENT", "system", "system", "none")) {
			try {
				String createQuery = "CREATE TABLE TBL_STUDENT (\n" + "  id INT AUTO_INCREMENT  PRIMARY KEY,\n"
						+ "  first_name VARCHAR(250) NOT NULL,\n" + "  last_name VARCHAR(250) NOT NULL,\n"
						+ "  email VARCHAR(250) DEFAULT NULL\n" + ")";
				jdbctemp.execute(createQuery);
				String insertQuery = "INSERT INTO \n" + "	TBL_STUDENT (first_name, last_name, email) \n" + "VALUES\n"
						+ "  	('Lokesh', 'Gupta', 'howtodoinjava@gmail.com'),\n"
						+ "  	('John', 'Doe', 'xyz@email.com') ; ";

				jdbctemp.execute(insertQuery);

			} catch (Exception e) {

			}
		}
		empRep.getDataForParams("tbl student", new HashMap<>(), "system", "system", "none");
	}
	
	public void teardown()
    {
    	 jdbctemp.execute("drop table TBL_STUDENT ");		
    }
	
	@Test
	public void testEncrypt()
	{
		log.info("test");	
		log.info(PropertyProcessor.encryptText("plain Test", "password"));
		log.info(PropertyProcessor.decryptText(new String( java.util.Base64.getEncoder().encode(PropertyProcessor.encryptText("plain Test", "password").getBytes()),StandardCharsets.UTF_8), "password"));
		 String key = "Bar12345Bar12345"; // 128 bit key
	        String initVector = "RandomInitVector"; // 16 bytes IV

	        System.out.println(PropertyProcessor.decrypt(key, initVector,   PropertyProcessor.encrypt(key, initVector, "Hello World")));
	}
}
