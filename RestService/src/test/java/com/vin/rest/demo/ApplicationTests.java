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
      		"  	('Lokesh', 'Gupta', 'howtodoinjava@gmail.com'),\n" + 
      		"  	('John', 'Doe', 'xyz@email.com') ; ";
      
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
	@Test
	public void getData() throws Exception {
		
			      mvc.perform( MockMvcRequestBuilders
			    	      .get("/myApps/system/system/tbl student/getdata?iden=234")
			    	      .accept(MediaType.APPLICATION_JSON))
			    	      .andDo(MockMvcResultHandlers.print())
			    	      .andExpect(MockMvcResultMatchers.status().isOk())
			    	      .andExpect(MockMvcResultMatchers.jsonPath("$.[*]").exists());
			    	      //.andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").isNotEmpty());     
		
	}
	
	@Test
	public void getDataForSingleRec() throws Exception {
		Map<String ,String> student=new HashMap<String ,String>();
		student.put("firstname", "Lokesh");
		student.put("email", "howtodoinjava@gmail.com");
		student.put("lastname", "jede");
		
		
		MvcResult resultPost = mvc.perform(MockMvcRequestBuilders.post("/myApps/system/system/tbl student/addData")
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
			    	      .get("/myApps/system/system/tbl student/getdataForKey/"+id)
			    	      .accept(MediaType.APPLICATION_JSON))
			    	      .andDo(MockMvcResultHandlers.print())
			    	      .andExpect(MockMvcResultMatchers.status().isOk())
			    	      .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
			    	      .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty()) 
			              .andExpect(MockMvcResultMatchers.jsonPath("$.id",is(Integer.parseInt(id))));
		
	}
	
	@Test
	public void insert() throws Exception {
		
		Map<String ,String> student=new HashMap<String ,String>();
		student.put("firstname", "Lokesh");
		student.put("email", "howtodoinjava@gmail.com");
		student.put("lastname", "jede");
		
		
		MvcResult resultPost = mvc.perform(MockMvcRequestBuilders.post("/myApps/system/system/tbl student/addData")
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
	    	      .get("/myApps/system/system/tbl student/getdataForKey/"+id)
	    	      .accept(MediaType.APPLICATION_JSON))
	    	      .andDo(MockMvcResultHandlers.print())
	    	      .andExpect(MockMvcResultMatchers.status().isOk())
	    	      .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
	    	      .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty()) 
	              .andExpect(MockMvcResultMatchers.jsonPath("$.id",is(Integer.parseInt(id))));
		 
		  
		
	}
	@Test
	public void updateData() throws Exception {
		Map<String ,String> student=new HashMap<String ,String>();
		student.put("firstname", "Lokesh");
		student.put("email", "howtodoinjava@gmail.com");
		student.put("lastname", "jede");
		
		
		MvcResult resultPost = mvc.perform(MockMvcRequestBuilders.post("/myApps/system/system/tbl student/addData")
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
		MvcResult resultPost1 =  mvc.perform(MockMvcRequestBuilders.put("/myApps/system/system/tbl student/updateData")
		            .contentType(MediaType.APPLICATION_JSON)
		            .content(new ObjectMapper().writeValueAsString(student)))
				    .andDo(MockMvcResultHandlers.print())
		            .andExpect(MockMvcResultMatchers.status().isOk())
		            .andReturn();
		
		String jsonData1= resultPost1.getResponse().getContentAsString();
		Assert.hasText(student.get("lastname"),jsonData1);
	}
	
	@Test
	public void getDeleteRec() throws Exception {
		Map<String ,String> student=new HashMap<String ,String>();
		student.put("firstname", "Lokesh");
		student.put("email", "howtodoinjava@gmail.com");
		student.put("lastname", "jede");
		
		
		MvcResult resultPost = mvc.perform(MockMvcRequestBuilders.post("/myApps/system/system/tbl student/addData")
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
			    	      .delete("/myApps/system/system/tbl student/deleteData/"+id)
			    	      .accept(MediaType.APPLICATION_JSON))
			    	      .andDo(MockMvcResultHandlers.print())
			    	      .andExpect(MockMvcResultMatchers.status().isOk())
			    	      .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
			    	      .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty()) 
			              .andExpect(MockMvcResultMatchers.jsonPath("$.id",is(Integer.parseInt(id))));
			      
			      mvc.perform( MockMvcRequestBuilders
			    	      .get("/myApps/system/system/tbl student/getdataForKey/"+id)
			    	      .accept(MediaType.APPLICATION_JSON))
			    	      .andDo(MockMvcResultHandlers.print())
			    	      .andExpect(MockMvcResultMatchers.status().isOk())
			    	      .andExpect(MockMvcResultMatchers.jsonPath("$.id").doesNotExist());
		
	}
	@Test
	public void updateService() throws Exception {
	// CREATE TABLE Service (id INTEGER(10) PRIMARY KEY,tableName VARCHAR(100),serviceName VARCHAR(100))
		Map<String ,String> serviceData=new HashMap<String ,String>();
		serviceData.put("tableName", "TBL_STUDENT");
		//student.put("serviceName", "studentService");
		
		MvcResult resultPost = mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/service/getdata")
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
      
      MvcResult resultPost2 =  mvc.perform(MockMvcRequestBuilders.put("/myApps/system/system/service/updateData")
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
		MvcResult resultPost3 = mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/service/getdata")
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
		MvcResult resultPost1 = mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/studentService/getdata")
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
		mvc.perform(MockMvcRequestBuilders.put("/myApps/system/system/service/updateData")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(serviceData)))
			    .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
	}
	
	@Test
	public void updateAttrb() throws Exception
	{
		mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/tbl student/getdata")
	            .contentType(MediaType.APPLICATION_JSON)
	             
	            )
		        .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
		MvcResult resultPost = mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/service attr/getdata")
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
		 MvcResult resultPost2 =  mvc.perform(MockMvcRequestBuilders.put("/myApps/system/system/service attr/updateData")
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
		
			mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/service attr/getdata")
		            .contentType(MediaType.APPLICATION_JSON)
		             
		            )
			        .andDo(MockMvcResultHandlers.print())
		            .andExpect(MockMvcResultMatchers.status().isOk())
		            .andReturn();
			mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/service/getdata")
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
				MvcResult resultPost4 = mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/service/getdataForKey/"+serviceID)
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
				
				MvcResult resultPost3=	mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/"+serviceName+"/getdata")
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
					 mvc.perform(MockMvcRequestBuilders.put("/myApps/system/system/service attr/updateData")
					            .contentType(MediaType.APPLICATION_JSON)
					            .content(new ObjectMapper().writeValueAsString(serviceAttrData)))
							    .andDo(MockMvcResultHandlers.print())
					            .andExpect(MockMvcResultMatchers.status().isOk())
					            .andReturn();
					
					
				
	}
	@Test
	public void testminValidation() throws Exception
	{
		ObjectMapper mapper = new ObjectMapper();

		mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/tbl student/getdata")
				.contentType(MediaType.APPLICATION_JSON)

		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		MvcResult resultService = mvc
				.perform(MockMvcRequestBuilders.get("/myApps/system/system/service/getdata")
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
		MvcResult resultServiceAttr=mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/service attr/getdata")
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
		 
		MvcResult updatedAttrb =  mvc.perform(MockMvcRequestBuilders.put("/myApps/system/system/service attr/updateData")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(attrbParam)))
			    .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
      String updatedAttrbStr= updatedAttrb.getResponse().getContentAsString();
      Map<String, String> updatedAttrbMap = new HashMap<String,String>();
      updatedAttrbMap = mapper.readValue(updatedAttrbStr, new TypeReference<Map<String, String>>() {
		});
      mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/tbl student/getdata")
				.contentType(MediaType.APPLICATION_JSON)

		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
      
      mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/tbl student/getdata")
				.contentType(MediaType.APPLICATION_JSON)
				 .param("firstname", "Lokesh")
		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
      
      attrbParam.put("attrminlength", "null");
      MvcResult againUpdatedAttrb =  mvc.perform(MockMvcRequestBuilders.put("/myApps/system/system/service attr/updateData")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(attrbParam)))
			    .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
      
      mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/tbl student/getdata")
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
      
		
	}
	@Test
	public void testmaxValidation() throws Exception
	{
		ObjectMapper mapper = new ObjectMapper();

		mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/tbl student/getdata")
				.contentType(MediaType.APPLICATION_JSON)

		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		MvcResult resultService = mvc
				.perform(MockMvcRequestBuilders.get("/myApps/system/system/service/getdata")
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
		MvcResult resultServiceAttr=mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/service attr/getdata")
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
		 
		MvcResult updatedAttrb =  mvc.perform(MockMvcRequestBuilders.put("/myApps/system/system/service attr/updateData")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(attrbParam)))
			    .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
      String updatedAttrbStr= updatedAttrb.getResponse().getContentAsString();
      Map<String, String> updatedAttrbMap = new HashMap<String,String>();
      updatedAttrbMap = mapper.readValue(updatedAttrbStr, new TypeReference<Map<String, String>>() {
		});
      mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/tbl student/getdata")
				.contentType(MediaType.APPLICATION_JSON)
				.param("firstname", "Lokesh111111111111111111111")

		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
      
     // mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/tbl student/getdata")
	//			.contentType(MediaType.APPLICATION_JSON)
		//		 .param("firstname", "Lokesh111111111111111111111")
		//).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
      
      attrbParam.put("attrmaxlength", "null");
      MvcResult againUpdatedAttrb =  mvc.perform(MockMvcRequestBuilders.put("/myApps/system/system/service attr/updateData")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(attrbParam)))
			    .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
      
      mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/tbl student/getdata")
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
      
		
	
	
		
	}
	
	@Test
	public void testmanditoryValidation() throws Exception
	{

		ObjectMapper mapper = new ObjectMapper();

		mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/tbl student/getdata")
				.contentType(MediaType.APPLICATION_JSON)

		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		MvcResult resultService = mvc
				.perform(MockMvcRequestBuilders.get("/myApps/system/system/service/getdata")
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
		MvcResult resultServiceAttr=mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/service attr/getdata")
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
		 
		MvcResult updatedAttrb =  mvc.perform(MockMvcRequestBuilders.put("/myApps/system/system/service attr/updateData")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(attrbParam)))
			    .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
      String updatedAttrbStr= updatedAttrb.getResponse().getContentAsString();
      Map<String, String> updatedAttrbMap = new HashMap<String,String>();
      updatedAttrbMap = mapper.readValue(updatedAttrbStr, new TypeReference<Map<String, String>>() {
		});
      mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/tbl student/getdata")
				.contentType(MediaType.APPLICATION_JSON)

		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
      
      mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/tbl student/getdata")
				.contentType(MediaType.APPLICATION_JSON)
				 .param("firstname", "Lokesh111111111111111111111")
		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
      
      attrbParam.put("attrismandatory", "null");
      MvcResult againUpdatedAttrb =  mvc.perform(MockMvcRequestBuilders.put("/myApps/system/system/service attr/updateData")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(attrbParam)))
			    .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
      
      mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/tbl student/getdata")
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
      
	}
	@Test
	public void testregxValidation() throws Exception
	{

		ObjectMapper mapper = new ObjectMapper();

		mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/tbl student/getdata")
				.contentType(MediaType.APPLICATION_JSON)

		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		MvcResult resultService = mvc
				.perform(MockMvcRequestBuilders.get("/myApps/system/system/service/getdata")
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
		MvcResult resultServiceAttr=mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/service attr/getdata")
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
		 
		MvcResult updatedAttrb =  mvc.perform(MockMvcRequestBuilders.put("/myApps/system/system/service attr/updateData")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(attrbParam)))
			    .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
      String updatedAttrbStr= updatedAttrb.getResponse().getContentAsString();
      Map<String, String> updatedAttrbMap = new HashMap<String,String>();
      updatedAttrbMap = mapper.readValue(updatedAttrbStr, new TypeReference<Map<String, String>>() {
		});
      mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/tbl student/getdata")
				.contentType(MediaType.APPLICATION_JSON)
				 .param("email", "Lokesh")

		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
      
      mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/tbl student/getdata")
				.contentType(MediaType.APPLICATION_JSON)
				 .param("email", "Lokesh@dfgdf.dfgd")
		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
      
      attrbParam.put("attrregxvalidation", "null");
      MvcResult againUpdatedAttrb =  mvc.perform(MockMvcRequestBuilders.put("/myApps/system/system/service attr/updateData")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(attrbParam)))
			    .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
      
      mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/tbl student/getdata")
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
      
	}
	@Test
	public void testcusValidation() throws Exception
	{


		ObjectMapper mapper = new ObjectMapper();

		mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/tbl student/getdata")
				.contentType(MediaType.APPLICATION_JSON)

		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		MvcResult resultService = mvc
				.perform(MockMvcRequestBuilders.get("/myApps/system/system/service/getdata")
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
		MvcResult resultServiceAttr=mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/service attr/getdata")
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
		 
		MvcResult updatedAttrb =  mvc.perform(MockMvcRequestBuilders.put("/myApps/system/system/service attr/updateData")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(attrbParam)))
			    .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
      String updatedAttrbStr= updatedAttrb.getResponse().getContentAsString();
      Map<String, String> updatedAttrbMap = new HashMap<String,String>();
      updatedAttrbMap = mapper.readValue(updatedAttrbStr, new TypeReference<Map<String, String>>() {
		});
      mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/tbl student/getdata")
				.contentType(MediaType.APPLICATION_JSON)
				 .param("email", "Lokesh")

		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
      
      mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/tbl student/getdata")
				.contentType(MediaType.APPLICATION_JSON)
				 .param("email", "Lokesh@dfgdf.dfgd")
		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
      
      attrbParam.put("attrcusvalidation", "null");
      MvcResult againUpdatedAttrb =  mvc.perform(MockMvcRequestBuilders.put("/myApps/system/system/service attr/updateData")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(attrbParam)))
			    .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
      
      mvc.perform(MockMvcRequestBuilders.get("/myApps/system/system/tbl student/getdata")
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
      
	}
	@Test
	public void testMultiDataInsert()
	{
		
	}
	@Test
	public void testMultiDataUpdata()
	{
		
	}
	@Test
	public void testMultiDataDelete()
	{
		
	}

	@Test
	public void testMultiDataGet()
	{
		
	}
}
