package com.vin.rest.demo;


import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    
    @Before
    public void setUp() {
      mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    
	@Test
	public void getData() throws Exception {
		
			      mvc.perform( MockMvcRequestBuilders
			    	      .get("/myApps/tbl student/getdata")
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
	public void postData() throws Exception {
		
		Map<String ,String> student=new HashMap<String ,String>();
		student.put("first name", "Lokesh");
		student.put("email", "howtodoinjava@gmail.com");
		student.put("last name", "jede");
        mvc.perform(MockMvcRequestBuilders.post("/myApps/tbl student/addData")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(student)))
            .andExpect(MockMvcResultMatchers.status().isOk());
		 
		  mvc.perform( MockMvcRequestBuilders
			  .get("/myApps/tbl student/getdata")
			  .accept(MediaType.APPLICATION_JSON))
			  .andDo(MockMvcResultHandlers.print())
			  .andExpect(MockMvcResultMatchers.status().isOk())
			  .andExpect(MockMvcResultMatchers.jsonPath("$.[*]").exists());
			    	      //.andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").isNotEmpty()); 
		  
		
		
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
}
