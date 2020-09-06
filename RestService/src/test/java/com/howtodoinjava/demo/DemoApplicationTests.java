package com.howtodoinjava.demo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
 
import com.dynamic.GenericController;

@RunWith(SpringRunner.class)
@SpringBootTest
//@WebMvcTest(GenericController.class)
public class DemoApplicationTests {

	//@Autowired
    private MockMvc mvc;
 
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Before
    public void setUp() {
      mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    
	@Test
	public void contextLoads() throws Exception {
		
		
		 
			      mvc.perform( MockMvcRequestBuilders
			    	      .get("/myApps/tbl student/getdata")
			    	      .accept(MediaType.APPLICATION_JSON))
			    	      .andDo(MockMvcResultHandlers.print())
			    	      .andExpect(MockMvcResultMatchers.status().isOk())
			    	      .andExpect(MockMvcResultMatchers.jsonPath("$.[*]").exists());
			    	      //.andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").isNotEmpty());     
		
	}

}
