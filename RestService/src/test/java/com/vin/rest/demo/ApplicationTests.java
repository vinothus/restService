package com.vin.rest.demo;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
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
    
    
    @Before
    public void setUp() {
      mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    
	@Test
	public void getData() throws Exception {
		
			      mvc.perform( MockMvcRequestBuilders
			    	      .get("/myApps/tbl student/getdata?iden=234")
			    	      .accept(MediaType.APPLICATION_JSON))
			    	      .andDo(MockMvcResultHandlers.print())
			    	      .andExpect(MockMvcResultMatchers.status().isOk())
			    	      .andExpect(MockMvcResultMatchers.jsonPath("$.[*]").exists());
			    	      //.andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").isNotEmpty());     
		
	}
	
	@Test
	public void getDataForSingleRec() throws Exception {
		Map<String ,String> student=new HashMap<String ,String>();
		student.put("first name", "Lokesh");
		student.put("email", "howtodoinjava@gmail.com");
		student.put("last name", "jede");
		
		
		MvcResult resultPost = mvc.perform(MockMvcRequestBuilders.post("/myApps/tbl student/addData")
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
			    	      .get("/myApps/tbl student/getdataForKey/"+id)
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
		student.put("first name", "Lokesh");
		student.put("email", "howtodoinjava@gmail.com");
		student.put("last name", "jede");
		
		
		MvcResult resultPost = mvc.perform(MockMvcRequestBuilders.post("/myApps/tbl student/addData")
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
	    	      .get("/myApps/tbl student/getdataForKey/"+id)
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
		student.put("first name", "Lokesh");
		student.put("email", "howtodoinjava@gmail.com");
		student.put("last name", "jede");
		
		
		MvcResult resultPost = mvc.perform(MockMvcRequestBuilders.post("/myApps/tbl student/addData")
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
		student.put("last name", "jedeupdate");
		MvcResult resultPost1 =  mvc.perform(MockMvcRequestBuilders.put("/myApps/tbl student/updateData")
		            .contentType(MediaType.APPLICATION_JSON)
		            .content(new ObjectMapper().writeValueAsString(student)))
				    .andDo(MockMvcResultHandlers.print())
		            .andExpect(MockMvcResultMatchers.status().isOk())
		            .andReturn();
		
		String jsonData1= resultPost1.getResponse().getContentAsString();
		Assert.hasText(student.get("last name"),jsonData1);
	}
	
	@Test
	public void getDeleteRec() throws Exception {
		Map<String ,String> student=new HashMap<String ,String>();
		student.put("first name", "Lokesh");
		student.put("email", "howtodoinjava@gmail.com");
		student.put("last name", "jede");
		
		
		MvcResult resultPost = mvc.perform(MockMvcRequestBuilders.post("/myApps/tbl student/addData")
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
			    	      .delete("/myApps/tbl student/deleteData/"+id)
			    	      .accept(MediaType.APPLICATION_JSON))
			    	      .andDo(MockMvcResultHandlers.print())
			    	      .andExpect(MockMvcResultMatchers.status().isOk())
			    	      .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
			    	      .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty()) 
			              .andExpect(MockMvcResultMatchers.jsonPath("$.id",is(Integer.parseInt(id))));
			      
			      mvc.perform( MockMvcRequestBuilders
			    	      .get("/myApps/tbl student/getdataForKey/"+id)
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
		
		MvcResult resultPost = mvc.perform(MockMvcRequestBuilders.get("/myApps/service/getdata")
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
      
      MvcResult resultPost2 =  mvc.perform(MockMvcRequestBuilders.put("/myApps/service/updateData")
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
		MvcResult resultPost3 = mvc.perform(MockMvcRequestBuilders.get("/myApps/service/getdata")
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
		MvcResult resultPost1 = mvc.perform(MockMvcRequestBuilders.get("/myApps/studentService/getdata")
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
		mvc.perform(MockMvcRequestBuilders.put("/myApps/service/updateData")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(new ObjectMapper().writeValueAsString(serviceData)))
			    .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
	}
	
	@Test
	public void updateAttrb() throws Exception
	{
		mvc.perform(MockMvcRequestBuilders.get("/myApps/tbl student/getdata")
	            .contentType(MediaType.APPLICATION_JSON)
	             
	            )
		        .andDo(MockMvcResultHandlers.print())
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();
		MvcResult resultPost = mvc.perform(MockMvcRequestBuilders.get("/myApps/service attr/getdata")
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
		String serviceID=serviceAttrData.get("service id");
		String serviceAttrbBeforChange=serviceAttrData.get("attrname");
		 serviceAttrData.put("attrname", "servicenameupdate");
		 MvcResult resultPost2 =  mvc.perform(MockMvcRequestBuilders.put("/myApps/service attr/updateData")
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
		
			mvc.perform(MockMvcRequestBuilders.get("/myApps/service attr/getdata")
		            .contentType(MediaType.APPLICATION_JSON)
		             
		            )
			        .andDo(MockMvcResultHandlers.print())
		            .andExpect(MockMvcResultMatchers.status().isOk())
		            .andReturn();
			mvc.perform(MockMvcRequestBuilders.get("/myApps/service/getdata")
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
				MvcResult resultPost4 = mvc.perform(MockMvcRequestBuilders.get("/myApps/service/getdataForKey/"+serviceID)
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
				
				MvcResult resultPost3=	mvc.perform(MockMvcRequestBuilders.get("/myApps/"+serviceName+"/getdata")
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
					 mvc.perform(MockMvcRequestBuilders.put("/myApps/service attr/updateData")
					            .contentType(MediaType.APPLICATION_JSON)
					            .content(new ObjectMapper().writeValueAsString(serviceAttrData)))
							    .andDo(MockMvcResultHandlers.print())
					            .andExpect(MockMvcResultMatchers.status().isOk())
					            .andReturn();
					
					
				
	}
}
