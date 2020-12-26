package com.vin.rest.demo;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import  static  org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vin.rest.repository.EmployeeRepositaryImpl;

import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
 

@RunWith(SpringRunner.class)
@SpringBootTest
//@WebMvcTest(GenericController.class)
public class ApplicationTests {

	//@Autowired
    private MockMvc mvc;
 
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    Environment env;
    
    @Autowired
    EmployeeRepositaryImpl empRep;
    JdbcTemplate jdbctemp;
   @Before
    public void setUp() {
      mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
      jdbctemp= empRep.setUserDataStore("system", "system", "none");
      if(!empRep.isTablePresent("TBL_STUDENT", "system",  "system", "none")) {
    try {  String createQuery="CREATE TABLE TBL_STUDENT (\n" + 
      		"  id INT AUTO_INCREMENT  PRIMARY KEY,\n" + 
      		"  first_name VARCHAR(250) NOT NULL,\n" + 
      		"  last_name VARCHAR(250) NOT NULL,\n" + 
      		"  email VARCHAR(250) DEFAULT NULL\n" + 
      		")";
      jdbctemp.execute(createQuery);
      String insertQuery="INSERT INTO \n" + 
      		"	TBL_STUDENT (first_name, last_name, email) \n" + 
      		"VALUES\n" + 
      		"  	('lokesh', 'Gupta', 'vinrest@gmail.com'),\n" + 
      		"  	('john', 'Doe', 'xyz@email.com') ; ";
      
      jdbctemp.execute(insertQuery);
      
    }catch(Exception e)
    {
    	
    }}
    }
    @After
    public void teardown()
    {
    	 jdbctemp.execute("drop table TBL_STUDENT ");		
    }
   
	//@Test
	public void getData() throws Exception {
		
			      mvc.perform( MockMvcRequestBuilders
			    	      .get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("getAllData")+"?iden=234")
			    	      .accept(MediaType.APPLICATION_JSON))
			    	      .andDo(MockMvcResultHandlers.print())
			    	      .andExpect(MockMvcResultMatchers.status().isOk())
			    	      .andExpect(MockMvcResultMatchers.jsonPath("$.[*]").exists());
			    	      //.andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").isNotEmpty());     
		
	}
	
	//@Test
	public void getDataForSingleRec() throws Exception {
		Map<String ,String> student=new HashMap<String ,String>();
		student.put("firstname", "Lokesh");
		student.put("email", "howtodoinjava@gmail.com");
		student.put("lastname", "jede");
		
		
		MvcResult resultPost = mvc.perform(MockMvcRequestBuilders.post("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("createData"))
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(student)))
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
		String jsonData= resultPost.getResponse().getContentAsString();
		ObjectMapper mapper = new ObjectMapper();

		Map<String, String> jsonMap = new HashMap<>();
		jsonMap = mapper.readValue(jsonData, new TypeReference<Map<String, String>>() {
		}); 
		String id=jsonMap.get("id");
			      mvc.perform( MockMvcRequestBuilders
			    	      .get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("getOnedata")+"/"+id)
			    	      .accept(MediaType.APPLICATION_JSON))
			    	      .andDo(MockMvcResultHandlers.print())
			    	      .andExpect(MockMvcResultMatchers.status().isOk())
			    	      .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
			    	      .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty()) 
			              .andExpect(MockMvcResultMatchers.jsonPath("$.id",is(Integer.parseInt(id))));
		
	}
	
	//@Test
	public void insert() throws Exception {
		
		Map<String ,String> student=new HashMap<String ,String>();
		student.put("firstname", "Lokesh");
		student.put("email", "howtodoinjava@gmail.com");
		student.put("lastname", "jede");
		
		
		MvcResult resultPost = mvc.perform(MockMvcRequestBuilders.post("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("createData"))
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(student)))
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
		String jsonData= resultPost.getResponse().getContentAsString();
		ObjectMapper mapper = new ObjectMapper();

		Map<String, String> jsonMap = new HashMap<>();
		jsonMap = mapper.readValue(jsonData, new TypeReference<Map<String, String>>() {
		}); 
		String id=jsonMap.get("id");
		
		mvc.perform( MockMvcRequestBuilders
	    	      .get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("getOnedata")+"/"+id)
	    	      .accept(MediaType.APPLICATION_JSON))
	    	      .andDo(MockMvcResultHandlers.print())
	    	      .andExpect(MockMvcResultMatchers.status().isOk())
	    	      .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
	    	      .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty()) 
	              .andExpect(MockMvcResultMatchers.jsonPath("$.id",is(Integer.parseInt(id))));
		 
		  
		
	}
	//@Test
	public void updateData() throws Exception {
		Map<String ,String> student=new HashMap<String ,String>();
		student.put("firstname", "Lokesh");
		student.put("email", "howtodoinjava@gmail.com");
		student.put("lastname", "jede");
		
		
		MvcResult resultPost = mvc.perform(MockMvcRequestBuilders.post("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("createData"))
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(student)))
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
		String jsonData= resultPost.getResponse().getContentAsString();
		ObjectMapper mapper = new ObjectMapper();

		Map<String, String> jsonMap = new HashMap<>();
		jsonMap = mapper.readValue(jsonData, new TypeReference<Map<String, String>>() {
		}); 
		String id=jsonMap.get("id");
		student.put("id", id);
		student.put("lastname", "jedeupdate");
		MvcResult resultPost1 =  mvc.perform(MockMvcRequestBuilders.put("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("updateData"))
		            .contentType(MediaType.APPLICATION_JSON)
		            .content(new ObjectMapper().writeValueAsString(student)))
				    .andDo(MockMvcResultHandlers.print())
		            .andExpect(MockMvcResultMatchers.status().isOk())
		            .andReturn();
		
		String jsonData1= resultPost1.getResponse().getContentAsString();
		Assert.hasText(student.get("lastname"),jsonData1);
	}
	
	//@Test
	public void getDeleteRec() throws Exception {
		Map<String ,String> student=new HashMap<String ,String>();
		student.put("firstname", "Lokesh");
		student.put("email", "howtodoinjava@gmail.com");
		student.put("lastname", "jede");
		
		
		MvcResult resultPost = mvc.perform(MockMvcRequestBuilders.post("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("createData"))
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(student)))
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
		String jsonData= resultPost.getResponse().getContentAsString();
		ObjectMapper mapper = new ObjectMapper();

		Map<String, String> jsonMap = new HashMap<>();
		jsonMap = mapper.readValue(jsonData, new TypeReference<Map<String, String>>() {
		}); 
		String id=jsonMap.get("id");
			      mvc.perform( MockMvcRequestBuilders
			    	      .delete("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("delete")+"/"+id)
			    	      .accept(MediaType.APPLICATION_JSON))
			    	      .andDo(MockMvcResultHandlers.print())
			    	      .andExpect(MockMvcResultMatchers.status().isOk())
			    	      .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
			    	      .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty()) 
			              .andExpect(MockMvcResultMatchers.jsonPath("$.id",is(Integer.parseInt(id))));
			      
			      mvc.perform( MockMvcRequestBuilders
			    	      .get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("getOnedata")+"/"+id)
			    	      .accept(MediaType.APPLICATION_JSON))
			    	      .andDo(MockMvcResultHandlers.print())
			    	      .andExpect(MockMvcResultMatchers.status().isOk())
			    	      .andExpect(MockMvcResultMatchers.jsonPath("$.id").doesNotExist());
		
	}
	//@Test
	public void updateService() throws Exception {
	// CREATE TABLE Service (id INTEGER(10) PRIMARY KEY,tableName VARCHAR(100),serviceName VARCHAR(100))
		Map<String ,String> serviceData=new HashMap<String ,String>();
		serviceData.put("tableName", "TBL_STUDENT");
		//student.put("serviceName", "studentService");
		
		MvcResult resultPost = mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service/"+env.getProperty("getAllData"))
	            .contentType(MediaType.APPLICATION_JSON)
	            .param("tableName", "TBL_STUDENT"))
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
		String jsonData= resultPost.getResponse().getContentAsString();
		ObjectMapper mapper = new ObjectMapper();

		List<Map<String, String>> jsonMap = new ArrayList<Map<String,String>>();
		jsonMap = mapper.readValue(jsonData, new TypeReference<List<Map<String, String>>>() {
		}); 
       System.out.println(jsonMap);  
      String id= jsonMap.get(0).get("id");
       
      serviceData.put("tablename", "TBL_STUDENT");
      serviceData .put("servicename", "studentService") ;
      serviceData.put("id", id) ;
      
      MvcResult resultPost2 =  mvc.perform(MockMvcRequestBuilders.put("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service/"+env.getProperty("updateData"))
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(serviceData)))
			    .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
      String jsonData2= resultPost2.getResponse().getContentAsString();
      Map<String, String> jsonMap2 = new HashMap<String,String>();
		jsonMap2 = mapper.readValue(jsonData2, new TypeReference<Map<String, String>>() {
		});
		
		assertTrue(jsonMap2.get("servicename").equals("studentService"));
		MvcResult resultPost3 = mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service/"+env.getProperty("getAllData"))
	            .contentType(MediaType.APPLICATION_JSON)
	             )
				.andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
		String jsonData3= resultPost3.getResponse().getContentAsString();
		//ObjectMapper mapper = new ObjectMapper();

		List<Map<String, String>> jsonMap3 = new ArrayList<Map<String,String>>();
		jsonMap3 = mapper.readValue(jsonData3, new TypeReference<List<Map<String, String>>>() {
		});
		System.out.println(jsonMap3);
		assertTrue(jsonMap3.size()>0);
		MvcResult resultPost1 = mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/studentService/"+env.getProperty("getAllData"))
	            .contentType(MediaType.APPLICATION_JSON)
	             )
				.andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
		String jsonData1= resultPost1.getResponse().getContentAsString();
		//ObjectMapper mapper = new ObjectMapper();

		List<Map<String, String>> jsonMap1 = new ArrayList<Map<String,String>>();
		jsonMap1 = mapper.readValue(jsonData1, new TypeReference<List<Map<String, String>>>() {
		});
		assertTrue(jsonMap1.size()>0);
		 serviceData .put("servicename", "tbl student") ;
		mvc.perform(MockMvcRequestBuilders.put("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service/"+env.getProperty("updateData"))
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(serviceData)))
			    .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
	}
	
	//@Test
	public void updateAttrb() throws Exception
	{
		mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("getAllData"))
	            .contentType(MediaType.APPLICATION_JSON)
	             
	            )
		        .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
		MvcResult resultPost = mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service attr/"+env.getProperty("getAllData"))
	            .contentType(MediaType.APPLICATION_JSON)
	             )
				.andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
		String jsonData= resultPost.getResponse().getContentAsString();
		ObjectMapper mapper = new ObjectMapper();

		List<Map<String, String>> jsonMap = new ArrayList<Map<String,String>>();
		jsonMap = mapper.readValue(jsonData, new TypeReference<List<Map<String, String>>>() {
		}); 
		
		Map<String ,String> serviceAttrData=jsonMap.get(0);
		//serviceAttrData.put("id", "2");
		//serviceAttrData.put("service id", "0");
		//serviceAttrData.put("attrname", "servicenameupdate");
		//serviceAttrData.put("colname", "serviceName");
		//{"id":2,"service id":0,"attrname":"servicename","colname":"serviceName"}
		String serviceID=serviceAttrData.get("serviceid");
		String attrID=serviceAttrData.get("id");
		String serviceAttrbBeforChange=serviceAttrData.get("attrname");
		 serviceAttrData.put("attrname", "servicenameupdate");
		 MvcResult resultPost2 =  mvc.perform(MockMvcRequestBuilders.put("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service attr/"+env.getProperty("updateData"))
		            .contentType(MediaType.APPLICATION_JSON)
		            .content(new ObjectMapper().writeValueAsString(serviceAttrData)))
				    .andDo(MockMvcResultHandlers.print())
		            .andExpect(MockMvcResultMatchers.status().isOk())
		            .andReturn();
	      String jsonData2= resultPost2.getResponse().getContentAsString();
	      Map<String, String> jsonMap2 = new HashMap<String,String>();
			jsonMap2 = mapper.readValue(jsonData2, new TypeReference<Map<String, String>>() {
			});
			
			assertTrue(jsonMap2.get("attrname").equals("servicenameupdate"));
		
			mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service attr/"+env.getProperty("getAllData"))
		            .contentType(MediaType.APPLICATION_JSON)
		             
		            )
			        .andDo(MockMvcResultHandlers.print())
		            .andExpect(MockMvcResultMatchers.status().isOk())
		            .andReturn();
			mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service/"+env.getProperty("getAllData"))
		            .contentType(MediaType.APPLICATION_JSON)
		             
		            )
			        .andDo(MockMvcResultHandlers.print())
		            .andExpect(MockMvcResultMatchers.status().isOk())
		            .andReturn();
					/*
					 * mvc.perform(MockMvcRequestBuilders.get("/myApps/service/refreshMataData")
					 * .contentType(MediaType.APPLICATION_JSON)
					 * 
					 * ) .andDo(MockMvcResultHandlers.print())
					 * .andExpect(MockMvcResultMatchers.status().isOk()) .andReturn();
					 */
			
			
				Map<String ,String> servicData=new HashMap<>(); 
				MvcResult resultPost4 = mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service/"+env.getProperty("getOnedata")+"/"+serviceID)
			            .contentType(MediaType.APPLICATION_JSON)
			            //.content(new ObjectMapper().writeValueAsString(student))
			            )
			            .andExpect(MockMvcResultMatchers.status().isOk())
			            .andReturn();
				String jsonDat4a= resultPost4.getResponse().getContentAsString();

				Map<String, String> jsonMap4 = new HashMap<>();
				jsonMap4 = mapper.readValue(jsonDat4a, new TypeReference<Map<String, String>>() {
				}); 
				String serviceName= jsonMap4.get("servicename");
				
				MvcResult resultPost3=	mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/"+serviceName+"/"+env.getProperty("getAllData"))
			            .contentType(MediaType.APPLICATION_JSON)
			             
			            )
				        .andDo(MockMvcResultHandlers.print())
			            .andExpect(MockMvcResultMatchers.status().isOk())
			            .andReturn();
				 String jsonData3= resultPost3.getResponse().getContentAsString();
			     List< Map<String, String>> jsonMap3 = new  ArrayList<Map<String,String>>();
					jsonMap3 = mapper.readValue(jsonData3, new TypeReference<List< Map<String, String>>>() {
					});
					boolean isPresent = false;
					for (Iterator iterator = jsonMap3.iterator(); iterator.hasNext();) {
						Map<String, String> map = (Map<String, String>) iterator.next();
						System.out.println("map:"+map);
						if(map.get("servicenameupdate")!=null)
						{
							System.out.println();
							isPresent=true;
							break;
						}
						
					}
					assertTrue(isPresent);
					
					serviceAttrData.put("attrname", serviceAttrbBeforChange);
					 mvc.perform(MockMvcRequestBuilders.put("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service attr/"+env.getProperty("updateData"))
					            .contentType(MediaType.APPLICATION_JSON)
					            .content(new ObjectMapper().writeValueAsString(serviceAttrData)))
							    .andDo(MockMvcResultHandlers.print())
					            .andExpect(MockMvcResultMatchers.status().isOk())
					            .andReturn();
					
					
				
	}
	//@Test
	public void testminValidation() throws Exception
	{
		ObjectMapper mapper = new ObjectMapper();

		mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("getAllData"))
				.contentType(MediaType.APPLICATION_JSON)

		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		MvcResult resultService = mvc
				.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service/"+env.getProperty("getAllData"))
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		String resultServiceStr = resultService.getResponse().getContentAsString();
		List<Map<String, String>> resultServiceMap = new ArrayList<Map<String, String>>();
		resultServiceMap = mapper.readValue(resultServiceStr, new TypeReference<List<Map<String, String>>>() {
		});
		System.out.println(resultServiceMap);
		String serViceID = null;
		for (Iterator iterator = resultServiceMap.iterator(); iterator.hasNext();) {
			Map<String, String> serviceMap = (Map<String, String>) iterator.next();
			if (serviceMap.get("servicename").equalsIgnoreCase("tbl student")) {
				serViceID = serviceMap.get("id");
			}
		}
		MvcResult resultServiceAttr=mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service attr/"+env.getProperty("getAllData"))
				.contentType(MediaType.APPLICATION_JSON)
				 .param("serviceid", serViceID)

		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		String resultServiceAttrStr = resultServiceAttr.getResponse().getContentAsString();
		List<Map<String, String>> resultServiceAttrMap = new ArrayList<Map<String, String>>();
		resultServiceAttrMap = mapper.readValue(resultServiceAttrStr, new TypeReference<List<Map<String, String>>>() {
		});
		Map<String, String> attrbParam = new HashMap<>();
		for (Iterator iterator = resultServiceAttrMap.iterator(); iterator.hasNext();) {
			Map<String, String> map = (Map<String, String>) iterator.next();
			if(map.get("attrname").equals("firstname"))
			{
				attrbParam=	map;
			}
			
		}
		
		attrbParam.put("attrminlength", "3");
		 
		MvcResult updatedAttrb =  mvc.perform(MockMvcRequestBuilders.put("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service attr/"+env.getProperty("updateData"))
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(attrbParam)))
			    .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
      String updatedAttrbStr= updatedAttrb.getResponse().getContentAsString();
      Map<String, String> updatedAttrbMap = new HashMap<String,String>();
      updatedAttrbMap = mapper.readValue(updatedAttrbStr, new TypeReference<Map<String, String>>() {
		});
      mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/getdata")
				.contentType(MediaType.APPLICATION_JSON)

		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
      
      mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/getdata")
				.contentType(MediaType.APPLICATION_JSON)
				 .param("firstname", "Lokesh")
		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
      
      attrbParam.put("attrminlength", "null");
      MvcResult againUpdatedAttrb =  mvc.perform(MockMvcRequestBuilders.put("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service attr/"+env.getProperty("updateData"))
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(attrbParam)))
			    .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
      
      mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("getAllData"))
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
      
		
	}
	//@Test
	public void testmaxValidation() throws Exception
	{
		ObjectMapper mapper = new ObjectMapper();

		mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("getAllData"))
				.contentType(MediaType.APPLICATION_JSON)

		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		MvcResult resultService = mvc
				.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service/"+env.getProperty("getAllData"))
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		String resultServiceStr = resultService.getResponse().getContentAsString();
		List<Map<String, String>> resultServiceMap = new ArrayList<Map<String, String>>();
		resultServiceMap = mapper.readValue(resultServiceStr, new TypeReference<List<Map<String, String>>>() {
		});
		System.out.println(resultServiceMap);
		String serViceID = null;
		for (Iterator iterator = resultServiceMap.iterator(); iterator.hasNext();) {
			Map<String, String> serviceMap = (Map<String, String>) iterator.next();
			if (serviceMap.get("servicename").equalsIgnoreCase("tbl student")) {
				serViceID = serviceMap.get("id");
			}
		}
		MvcResult resultServiceAttr=mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service attr/"+env.getProperty("getAllData"))
				.contentType(MediaType.APPLICATION_JSON)
				 .param("serviceid", serViceID)

		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		String resultServiceAttrStr = resultServiceAttr.getResponse().getContentAsString();
		List<Map<String, String>> resultServiceAttrMap = new ArrayList<Map<String, String>>();
		resultServiceAttrMap = mapper.readValue(resultServiceAttrStr, new TypeReference<List<Map<String, String>>>() {
		});
		Map<String, String> attrbParam = new HashMap<>();
		for (Iterator iterator = resultServiceAttrMap.iterator(); iterator.hasNext();) {
			Map<String, String> map = (Map<String, String>) iterator.next();
			if(map.get("attrname").equals("firstname"))
			{
				attrbParam=	map;
			}
			
		}
		
		attrbParam.put("attrmaxlength", "20");
		 
		MvcResult updatedAttrb =  mvc.perform(MockMvcRequestBuilders.put("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service attr/"+env.getProperty("updateData"))
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(attrbParam)))
			    .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
      String updatedAttrbStr= updatedAttrb.getResponse().getContentAsString();
      Map<String, String> updatedAttrbMap = new HashMap<String,String>();
      updatedAttrbMap = mapper.readValue(updatedAttrbStr, new TypeReference<Map<String, String>>() {
		});
      mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("getAllData"))
				.contentType(MediaType.APPLICATION_JSON)
				.param("firstname", "Lokesh111111111111111111111")

		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
      
     // mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/getdata")
	//			.contentType(MediaType.APPLICATION_JSON)
		//		 .param("firstname", "Lokesh111111111111111111111")
		//).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
      
      attrbParam.put("attrmaxlength", "null");
      MvcResult againUpdatedAttrb =  mvc.perform(MockMvcRequestBuilders.put("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service attr/"+env.getProperty("updateData"))
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(attrbParam)))
			    .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
      
      mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("getAllData"))
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
      
		
	
	
		
	}
	
	//@Test
	public void testmanditoryValidation() throws Exception
	{

		ObjectMapper mapper = new ObjectMapper();

		mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("getAllData"))
				.contentType(MediaType.APPLICATION_JSON)

		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		MvcResult resultService = mvc
				.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service/"+env.getProperty("getAllData"))
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		String resultServiceStr = resultService.getResponse().getContentAsString();
		List<Map<String, String>> resultServiceMap = new ArrayList<Map<String, String>>();
		resultServiceMap = mapper.readValue(resultServiceStr, new TypeReference<List<Map<String, String>>>() {
		});
		System.out.println(resultServiceMap);
		String serViceID = null;
		for (Iterator iterator = resultServiceMap.iterator(); iterator.hasNext();) {
			Map<String, String> serviceMap = (Map<String, String>) iterator.next();
			if (serviceMap.get("servicename").equalsIgnoreCase("tbl student")) {
				serViceID = serviceMap.get("id");
			}
		}
		MvcResult resultServiceAttr=mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service attr/"+env.getProperty("getAllData"))
				.contentType(MediaType.APPLICATION_JSON)
				 .param("serviceid", serViceID)

		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		String resultServiceAttrStr = resultServiceAttr.getResponse().getContentAsString();
		List<Map<String, String>> resultServiceAttrMap = new ArrayList<Map<String, String>>();
		resultServiceAttrMap = mapper.readValue(resultServiceAttrStr, new TypeReference<List<Map<String, String>>>() {
		});
		Map<String, String> attrbParam = new HashMap<>();
		for (Iterator iterator = resultServiceAttrMap.iterator(); iterator.hasNext();) {
			Map<String, String> map = (Map<String, String>) iterator.next();
			if(map.get("attrname").equals("firstname"))
			{
				attrbParam=	map;
			}
			
		}
		
		attrbParam.put("attrismandatory", "yes");
		 
		MvcResult updatedAttrb =  mvc.perform(MockMvcRequestBuilders.put("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service attr/"+env.getProperty("updateData"))
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(attrbParam)))
			    .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
      String updatedAttrbStr= updatedAttrb.getResponse().getContentAsString();
      Map<String, String> updatedAttrbMap = new HashMap<String,String>();
      updatedAttrbMap = mapper.readValue(updatedAttrbStr, new TypeReference<Map<String, String>>() {
		});
      mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("getAllData"))
				.contentType(MediaType.APPLICATION_JSON)

		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
      
      mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("getAllData"))
				.contentType(MediaType.APPLICATION_JSON)
				 .param("firstname", "Lokesh111111111111111111111")
		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
      
      attrbParam.put("attrismandatory", "null");
      MvcResult againUpdatedAttrb =  mvc.perform(MockMvcRequestBuilders.put("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service attr/"+env.getProperty("updateData"))
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(attrbParam)))
			    .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
      
      mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("getAllData"))
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
      
	}
	//@Test
	public void testregxValidation() throws Exception
	{

		ObjectMapper mapper = new ObjectMapper();

		mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("getAllData"))
				.contentType(MediaType.APPLICATION_JSON)

		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		MvcResult resultService = mvc
				.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service/"+env.getProperty("getAllData"))
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		String resultServiceStr = resultService.getResponse().getContentAsString();
		List<Map<String, String>> resultServiceMap = new ArrayList<Map<String, String>>();
		resultServiceMap = mapper.readValue(resultServiceStr, new TypeReference<List<Map<String, String>>>() {
		});
		System.out.println(resultServiceMap);
		String serViceID = null;
		for (Iterator iterator = resultServiceMap.iterator(); iterator.hasNext();) {
			Map<String, String> serviceMap = (Map<String, String>) iterator.next();
			if (serviceMap.get("servicename").equalsIgnoreCase("tbl student")) {
				serViceID = serviceMap.get("id");
			}
		}
		MvcResult resultServiceAttr=mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service attr/"+env.getProperty("getAllData"))
				.contentType(MediaType.APPLICATION_JSON)
				 .param("serviceid", serViceID)

		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		String resultServiceAttrStr = resultServiceAttr.getResponse().getContentAsString();
		List<Map<String, String>> resultServiceAttrMap = new ArrayList<Map<String, String>>();
		resultServiceAttrMap = mapper.readValue(resultServiceAttrStr, new TypeReference<List<Map<String, String>>>() {
		});
		Map<String, String> attrbParam = new HashMap<>();
		for (Iterator iterator = resultServiceAttrMap.iterator(); iterator.hasNext();) {
			Map<String, String> map = (Map<String, String>) iterator.next();
			if(map.get("attrname").equals("email"))
			{
				attrbParam=	map;
			}
			
		}
		
		attrbParam.put("attrregxvalidation", "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$");
		 
		MvcResult updatedAttrb =  mvc.perform(MockMvcRequestBuilders.put("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service attr/"+env.getProperty("updateData"))
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(attrbParam)))
			    .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
      String updatedAttrbStr= updatedAttrb.getResponse().getContentAsString();
      Map<String, String> updatedAttrbMap = new HashMap<String,String>();
      updatedAttrbMap = mapper.readValue(updatedAttrbStr, new TypeReference<Map<String, String>>() {
		});
      mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("getAllData"))
				.contentType(MediaType.APPLICATION_JSON)
				 .param("email", "Lokesh")

		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
      
      mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("getAllData"))
				.contentType(MediaType.APPLICATION_JSON)
				 .param("email", "Lokesh@dfgdf.dfgd")
		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
      
      attrbParam.put("attrregxvalidation", "null");
      MvcResult againUpdatedAttrb =  mvc.perform(MockMvcRequestBuilders.put("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service attr/"+env.getProperty("updateData"))
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(attrbParam)))
			    .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
      
      mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("getAllData"))
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
      
	}
	//@Test
	public void testcusValidation() throws Exception
	{


		ObjectMapper mapper = new ObjectMapper();

		mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("getAllData"))
				.contentType(MediaType.APPLICATION_JSON)

		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		MvcResult resultService = mvc
				.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service/"+env.getProperty("getAllData"))
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		String resultServiceStr = resultService.getResponse().getContentAsString();
		List<Map<String, String>> resultServiceMap = new ArrayList<Map<String, String>>();
		resultServiceMap = mapper.readValue(resultServiceStr, new TypeReference<List<Map<String, String>>>() {
		});
		System.out.println(resultServiceMap);
		String serViceID = null;
		for (Iterator iterator = resultServiceMap.iterator(); iterator.hasNext();) {
			Map<String, String> serviceMap = (Map<String, String>) iterator.next();
			if (serviceMap.get("servicename").equalsIgnoreCase("tbl student")) {
				serViceID = serviceMap.get("id");
			}
		}
		MvcResult resultServiceAttr=mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service attr/"+env.getProperty("getAllData"))
				.contentType(MediaType.APPLICATION_JSON)
				 .param("serviceid", serViceID)

		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		String resultServiceAttrStr = resultServiceAttr.getResponse().getContentAsString();
		List<Map<String, String>> resultServiceAttrMap = new ArrayList<Map<String, String>>();
		resultServiceAttrMap = mapper.readValue(resultServiceAttrStr, new TypeReference<List<Map<String, String>>>() {
		});
		Map<String, String> attrbParam = new HashMap<>();
		for (Iterator iterator = resultServiceAttrMap.iterator(); iterator.hasNext();) {
			Map<String, String> map = (Map<String, String>) iterator.next();
			if(map.get("attrname").equals("email"))
			{
				attrbParam=	map;
			}
			
		}
		
		attrbParam.put("attrcusvalidation", "yes");
		 
		MvcResult updatedAttrb =  mvc.perform(MockMvcRequestBuilders.put("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service attr/"+env.getProperty("updateData"))
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(attrbParam)))
			    .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
      String updatedAttrbStr= updatedAttrb.getResponse().getContentAsString();
      Map<String, String> updatedAttrbMap = new HashMap<String,String>();
      updatedAttrbMap = mapper.readValue(updatedAttrbStr, new TypeReference<Map<String, String>>() {
		});
      Map<String,String> vinValidationMap=new HashMap<>();
      vinValidationMap.put("serviceid", serViceID);
      vinValidationMap.put("attrid", String.valueOf(updatedAttrbMap.get("id")));
      vinValidationMap.put("name", "EmailMustContainFirst");
      vinValidationMap.put("classname", "com.vin.validatior.EmailMustContainFirst");
      
      MvcResult resultPost = mvc.perform(MockMvcRequestBuilders.post("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/vinvalidation/"+env.getProperty("createData"))
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(vinValidationMap)))
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
		String jsonData= resultPost.getResponse().getContentAsString();
		 

		Map<String, String> jsonMap = new HashMap<>();
		jsonMap = mapper.readValue(jsonData, new TypeReference<Map<String, String>>() {
		}); 
		String customvalatatorId=jsonMap.get("id");
      
      mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("getAllData"))
				.contentType(MediaType.APPLICATION_JSON)
				 .param("email", "howtodoinjava@vinrest.com")
				 .param("firstname", "Lokesh")

		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
      
      mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("getAllData"))
				.contentType(MediaType.APPLICATION_JSON)
				 .param("email", "Lokesh@dfgdf.dfgd")
		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
      
      attrbParam.put("attrcusvalidation", "null");
      MvcResult againUpdatedAttrb =  mvc.perform(MockMvcRequestBuilders.put("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service attr/"+env.getProperty("updateData"))
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(attrbParam)))
			    .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
      mvc.perform( MockMvcRequestBuilders
       	      .delete("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/vinvalidation/"+env.getProperty("delete")+"/"+customvalatatorId)
       	      .accept(MediaType.APPLICATION_JSON))
       	      .andDo(MockMvcResultHandlers.print())
       	      .andExpect(MockMvcResultMatchers.status().isOk())
       	      .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
       	      .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty()) 
                 .andExpect(MockMvcResultMatchers.jsonPath("$.id",is(Integer.parseInt(customvalatatorId))));
      mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("getAllData"))
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
      
	} 
	@Test
	public void testMultiDataInsert()
	{
	System.out.println("multi test");	
	}
	//@Test
	public void testMultiDataUpdata()
	{
		
	}
	//@Test
	public void testMultiDataDelete()
	{
		
	}

	//@Test
	public void testMultiDataGet() throws Exception
	{System.out.println("test multi test get");
	 ObjectMapper mapper = new ObjectMapper();
	MvcResult serviceAttrResult = mvc.perform( MockMvcRequestBuilders
   	      .get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service attr/"+env.getProperty("getAllData"))
   	      .accept(MediaType.APPLICATION_JSON))
   	      .andDo(MockMvcResultHandlers.print())
   	      .andExpect(MockMvcResultMatchers.status().isOk())
   	      .andExpect(MockMvcResultMatchers.jsonPath("$.[*]").exists()).andReturn();
	String serviceAttrStr= serviceAttrResult.getResponse().getContentAsString();
     List<Map<String, String>> serviceAttrMap = new ArrayList<>();
     serviceAttrMap = mapper.readValue(serviceAttrStr, new TypeReference<List<Map<String, String>>>() {
		});
	MvcResult serviceResult = mvc.perform( MockMvcRequestBuilders
   	      .get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service/"+env.getProperty("getAllData"))
   	      .accept(MediaType.APPLICATION_JSON))
   	      .andDo(MockMvcResultHandlers.print())
   	      .andExpect(MockMvcResultMatchers.status().isOk())
   	      .andExpect(MockMvcResultMatchers.jsonPath("$.[*]").exists()).andReturn();
	String serviceStr= serviceResult.getResponse().getContentAsString();
	List<Map<String, String>> serviceMap = new ArrayList<>();
     serviceMap = mapper.readValue(serviceStr, new TypeReference<List<Map<String, String>>>() {
		});
	String serviceServiceId = null;
	String serviceServiceAttrId = null;
	
	for (Iterator<Map<String, String>> iterator = serviceMap.iterator(); iterator.hasNext();) {
		Map<String, String> map = (Map<String, String>) iterator.next();
		String id = map.get("id");
		String servicename = map.get("servicename");
		if(servicename!=null) {
		if(servicename.equalsIgnoreCase("service"))
		{
			serviceServiceId=id;	
		}
		else if(servicename.equalsIgnoreCase("service attr"))
		{
			serviceServiceAttrId=id;
		}
		}

	}
	
	 // multi service
	Map<String,String> multiServiceParam =new HashMap<>();
	multiServiceParam.put("serviceid", serviceServiceId);
	multiServiceParam.put("multiservicename", "multiTestgetService");
	multiServiceParam.put("priority", "1");
	multiServiceParam.put("type", "Single");
	multiServiceParam.put("relationwithparam", "id.id");
	
		MvcResult multiServiceResult =  mvc.perform(MockMvcRequestBuilders.post("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/multi service/"+env.getProperty("createData"))
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(multiServiceParam)))
			    .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
     String multiServiceStr= multiServiceResult.getResponse().getContentAsString();
     Map<String, String> multiServiceMap = new HashMap<String,String>();
     multiServiceMap = mapper.readValue(multiServiceStr, new TypeReference<Map<String, String>>() {
		});
     
     
     Map<String,String> multiServiceAttrParam =new HashMap<>();
     multiServiceAttrParam.put("serviceid", serviceServiceAttrId);
     multiServiceAttrParam.put("multiservicename", "multiTestgetService");
     multiServiceAttrParam.put("priority", "2");
     multiServiceAttrParam.put("type", "Single");
     multiServiceAttrParam.put("relationwithparam", "service.id.serviceid");
		
     
     MvcResult multiServiceAttrResult =  mvc.perform(MockMvcRequestBuilders.post("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/multi service/"+env.getProperty("createData"))
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(multiServiceParam)))
			    .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
     String multiServiceAttrStr= multiServiceAttrResult.getResponse().getContentAsString();
     Map<String, String> multiServiceAttrMap = new HashMap<String,String>();
     multiServiceAttrMap = mapper.readValue(multiServiceAttrStr, new TypeReference<Map<String, String>>() {
		});
     
    // calling multiservice
     
     MvcResult multiServiceCallResult = mvc.perform( MockMvcRequestBuilders
   	      .get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/"+env.getProperty("multiService")+"/multiTestgetService/"+env.getProperty("getMultipleAllData")+"?id="+serviceServiceId)
   	      .accept(MediaType.APPLICATION_JSON))
   	      .andDo(MockMvcResultHandlers.print())
   	      .andExpect(MockMvcResultMatchers.status().isOk())
   	      .andExpect(MockMvcResultMatchers.jsonPath("$.[*]").exists()).andReturn();
	String multiServiceCallResultStr= multiServiceCallResult.getResponse().getContentAsString();
	List<Map<String, List<Map<String, Object>>>> multiServiceCallResulteMap = new ArrayList<>();
	multiServiceCallResulteMap = mapper.readValue(multiServiceCallResultStr, new TypeReference<List<Map<String, List<Map<String, Object>>>>>() {
		});
	String delServiceID=String.valueOf(multiServiceMap.get("id"));
   mvc.perform( MockMvcRequestBuilders
   	      .delete("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/multi service/"+env.getProperty("delete")+"/"+delServiceID)
   	      .accept(MediaType.APPLICATION_JSON))
   	      .andDo(MockMvcResultHandlers.print())
   	      .andExpect(MockMvcResultMatchers.status().isOk())
   	      .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
   	      .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty()) 
             .andExpect(MockMvcResultMatchers.jsonPath("$.id",is(Integer.parseInt(delServiceID))));
   
   String delServiceAttrID=String.valueOf(multiServiceAttrMap.get("id"));
   mvc.perform( MockMvcRequestBuilders
   	      .delete("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/multi service/"+env.getProperty("delete")+"/"+delServiceAttrID)
   	      .accept(MediaType.APPLICATION_JSON))
   	      .andDo(MockMvcResultHandlers.print())
   	      .andExpect(MockMvcResultMatchers.status().isOk())
   	      .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
   	      .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty()) 
             .andExpect(MockMvcResultMatchers.jsonPath("$.id",is(Integer.parseInt(delServiceAttrID))));
	 
}
	
	//@Test
	public void testPreProcessSingleService() throws Exception {
		 mvc.perform( MockMvcRequestBuilders
	    	      .get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("getAllData")+"?iden=234")
	    	      .accept(MediaType.APPLICATION_JSON))
	    	      .andDo(MockMvcResultHandlers.print())
	    	      .andExpect(MockMvcResultMatchers.status().isOk())
	    	      .andExpect(MockMvcResultMatchers.jsonPath("$.[*]").exists());
			MvcResult resultPost = mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service/"+env.getProperty("getAllData"))
		            .contentType(MediaType.APPLICATION_JSON)
		            .param("tableName", "TBL_STUDENT"))
		            .andExpect(MockMvcResultMatchers.status().isOk())
		            .andReturn();
			String jsonData= resultPost.getResponse().getContentAsString();
			ObjectMapper mapper = new ObjectMapper();
			List<Map<String, String>> jsonMap = new ArrayList<Map<String,String>>();
			jsonMap = mapper.readValue(jsonData, new TypeReference<List<Map<String, String>>>() {
			}); 
	       System.out.println(jsonMap);  
	       Map<String ,String> serviceAttrData=jsonMap.get(0);
		   String serviceID=serviceAttrData.get("id");
	       MvcResult attrbResult = mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service attr/"+env.getProperty("getAllData"))
		            .contentType(MediaType.APPLICATION_JSON)
		            .param("serviceid", serviceID)
		            .param("attrname", "firstname"))
					.andDo(MockMvcResultHandlers.print())
		            .andExpect(MockMvcResultMatchers.status().isOk())
		            .andReturn();
			String attrbData= attrbResult.getResponse().getContentAsString();
			 

			List<Map<String, String>> attrbnMap = new ArrayList<Map<String,String>>();
			attrbnMap = mapper.readValue(attrbData, new TypeReference<List<Map<String, String>>>() {
			}); 
			
			 Map<String ,String> AttrData=attrbnMap.get(0);
			   String attrID=AttrData.get("id");
			  
				
				 MvcResult vinprocessorResult = mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/vinprocessor/"+env.getProperty("getAllData"))
				            .contentType(MediaType.APPLICATION_JSON)
				            .param("serviceid", serviceID)
				            .param("name", "uppertolower")
				            .param("classname", "com.vin.processor.UpperLowerParamProcessor")
				            .param("attrid", attrID))
							.andDo(MockMvcResultHandlers.print())
				            .andExpect(MockMvcResultMatchers.status().isOk())
				            .andReturn();	
				 String vinprocessorData= vinprocessorResult.getResponse().getContentAsString();
					List<Map<String, String>> vinprocessorMap = new ArrayList<Map<String,String>>();
					vinprocessorMap = mapper.readValue(vinprocessorData, new TypeReference<List<Map<String, String>>>() {
					}); 
					if(vinprocessorMap.size()==0)
					{
						 Map<String, String> params = new HashMap<>();
							params.put("serviceid", serviceID);
							params.put("attrid", attrID);
							params.put("name", "uppertolower");
							params.put("classname", "com.vin.processor.UpperLowerParamProcessor");  
							vinprocessorResult	=	  mvc.perform(MockMvcRequestBuilders.post("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/vinprocessor/"+env.getProperty("createData"))
						            .contentType(MediaType.APPLICATION_JSON)
						            .content(new ObjectMapper().writeValueAsString(params)))
								    .andDo(MockMvcResultHandlers.print())
						            .andExpect(MockMvcResultMatchers.status().isOk())
						            .andReturn();
							vinprocessorData= vinprocessorResult.getResponse().getContentAsString();
							Map<String, String>  vinprocessorMaps =new HashMap<String,String>();
							vinprocessorMaps = mapper.readValue(vinprocessorData, new TypeReference<Map<String, String>>() {
							});
							vinprocessorMap.add(vinprocessorMaps);
					}
					
					 mvc.perform( MockMvcRequestBuilders
				    	      .get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("getAllData")+"?firstname=LOKESH")
				    	      .accept(MediaType.APPLICATION_JSON))
				    	      .andDo(MockMvcResultHandlers.print())
				    	      .andExpect(MockMvcResultMatchers.status().isOk())
				    	      .andExpect(MockMvcResultMatchers.jsonPath("$.[*]").exists());
					 String id=String.valueOf(vinprocessorMap.get(0).get("id"));
				      mvc.perform( MockMvcRequestBuilders
				    	      .delete("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/vinprocessor/"+env.getProperty("delete")+"/"+id)
				    	      .accept(MediaType.APPLICATION_JSON))
				    	      .andDo(MockMvcResultHandlers.print())
				    	      .andExpect(MockMvcResultMatchers.status().isOk())
				    	      .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
				    	      .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty()) 
				              .andExpect(MockMvcResultMatchers.jsonPath("$.id",is(Integer.parseInt(id))));
					 
	}

	//@Test
	public void testPostProcessSingleService() throws Exception {
		 mvc.perform( MockMvcRequestBuilders
	    	      .get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("getAllData")+"?iden=234")
	    	      .accept(MediaType.APPLICATION_JSON))
	    	      .andDo(MockMvcResultHandlers.print())
	    	      .andExpect(MockMvcResultMatchers.status().isOk())
	    	      .andExpect(MockMvcResultMatchers.jsonPath("$.[*]").exists());
			MvcResult resultPost = mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service/"+env.getProperty("getAllData"))
		            .contentType(MediaType.APPLICATION_JSON)
		            .param("tableName", "TBL_STUDENT"))
		            .andExpect(MockMvcResultMatchers.status().isOk())
		            .andReturn();
			String jsonData= resultPost.getResponse().getContentAsString();
			ObjectMapper mapper = new ObjectMapper();

			List<Map<String, String>> jsonMap = new ArrayList<Map<String,String>>();
			jsonMap = mapper.readValue(jsonData, new TypeReference<List<Map<String, String>>>() {
			}); 
	       System.out.println(jsonMap);
	       
	       Map<String ,String> serviceAttrData=jsonMap.get(0);
		   String serviceID=String.valueOf(serviceAttrData.get("id"));
	       MvcResult attrbResult = mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/service attr/"+env.getProperty("getAllData"))
		            .contentType(MediaType.APPLICATION_JSON)
		            .param("serviceid", serviceID)
		            .param("attrname", "firstname"))
					.andDo(MockMvcResultHandlers.print())
		            .andExpect(MockMvcResultMatchers.status().isOk())
		            .andReturn();
			String attrbData= attrbResult.getResponse().getContentAsString();
			 

			List<Map<String, String>> attrbnMap = new ArrayList<Map<String,String>>();
			attrbnMap = mapper.readValue(attrbData, new TypeReference<List<Map<String, String>>>() {
			}); 
			
			 Map<String ,String> AttrData=attrbnMap.get(0);
			   String attrID=String.valueOf(AttrData.get("id"));
			  
		   MvcResult vinprocessorResult = mvc.perform(MockMvcRequestBuilders.get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/vinprocessor/"+env.getProperty("getAllData"))
				            .contentType(MediaType.APPLICATION_JSON)
				            .param("serviceid", serviceID)
				            .param("name", "uppertolower")
				            .param("classname", "com.vin.processor.UpperLowerParamProcessor")
				            .param("attrid", attrID))
							.andDo(MockMvcResultHandlers.print())
				            .andExpect(MockMvcResultMatchers.status().isOk())
				            .andReturn();	
		   
		   String vinprocessorData= vinprocessorResult.getResponse().getContentAsString();
			List<Map<String, String>> vinprocessorMap = new ArrayList<Map<String,String>>();
			vinprocessorMap = mapper.readValue(vinprocessorData, new TypeReference<List<Map<String, String>>>() {
			}); 
			if(vinprocessorMap.size()==0)
			{
				 Map<String, String> params = new HashMap<>();
					params.put("serviceid", serviceID);
					params.put("attrid", attrID);
					params.put("name", "uppertolower");
					params.put("classname", "com.vin.processor.UpperLowerParamProcessor");  
					vinprocessorResult	=	  mvc.perform(MockMvcRequestBuilders.post("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/vinprocessor/"+env.getProperty("createData"))
				            .contentType(MediaType.APPLICATION_JSON)
				            .content(new ObjectMapper().writeValueAsString(params)))
						    .andDo(MockMvcResultHandlers.print())
				            .andExpect(MockMvcResultMatchers.status().isOk())
				            .andReturn();
					vinprocessorData= vinprocessorResult.getResponse().getContentAsString();
					Map<String, String>  vinprocessorMaps =new HashMap<String,String>();
					vinprocessorMaps = mapper.readValue(vinprocessorData, new TypeReference<Map<String, String>>() {
					});
					vinprocessorMap.add(vinprocessorMaps);
			} 
			
			
			 mvc.perform( MockMvcRequestBuilders
		    	      .get("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/tbl student/"+env.getProperty("getAllData")+"?firstname=LOKESH")
		    	      .accept(MediaType.APPLICATION_JSON))
		    	      .andDo(MockMvcResultHandlers.print())
		    	      .andExpect(MockMvcResultMatchers.status().isOk())
		    	      .andExpect(MockMvcResultMatchers.jsonPath("$.[*]").exists());
			  
				String id=String.valueOf(vinprocessorMap.get(0).get("id"));
			      mvc.perform( MockMvcRequestBuilders
			    	      .delete("/"+env.getProperty("spring.application.name")+"/"+env.getProperty("systemUser")+"/"+env.getProperty("systemDatasource")+"/vinprocessor/"+env.getProperty("delete")+"/"+id)
			    	      .accept(MediaType.APPLICATION_JSON))
			    	      .andDo(MockMvcResultHandlers.print())
			    	      .andExpect(MockMvcResultMatchers.status().isOk())
			    	      .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
			    	      .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty()) 
			              .andExpect(MockMvcResultMatchers.jsonPath("$.id",is(Integer.parseInt(id))));
			      
			  

	}
}
