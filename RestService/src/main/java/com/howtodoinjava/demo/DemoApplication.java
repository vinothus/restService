package com.howtodoinjava.demo;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.dynamic.ControllerBeanFactoryPostProcessor;
import com.dynamic.GenericController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.howtodoinjava.demo.repository.EmployeeRepositaryImpl;
import com.vin.intercept.SimpleFilter;

@SpringBootApplication
public class DemoApplication {
	static Logger log = Logger.getLogger(DemoApplication.class.getName());
	@Autowired
	GenericController userController;
	@Autowired
	RequestMappingHandlerMapping handlerMapping;

	@FunctionalInterface
	interface FuncInter1 {
		@RequestMapping("/test")
		void operation();
	}

	@Autowired
	DataSource dataSource;
	@Autowired
	private Environment env;
	@Autowired
	static ApplicationContext app;

	public static void main(String[] args) {

		SpringApplication.run(DemoApplication.class, args);

	}

	@Bean
	GenericController getGC() {

		return new GenericController();
	}

	@Bean
	ControllerBeanFactoryPostProcessor myConfigBean(Environment env) {
		return new ControllerBeanFactoryPostProcessor(env);
	}

	@Bean
	RequestMappingHandlerMapping getObject(ApplicationContext app, DataSource dataSource,
			RequestMappingHandlerMapping handlerMapping, Environment env) throws Exception {
		handlerMapping = app.getBean(RequestMappingHandlerMapping.class);
		userController = app.getBean(GenericController.class);
		handlerMapping
				.registerMapping(
						RequestMappingInfo.paths("/users").methods(RequestMethod.GET)
								.produces(MediaType.APPLICATION_JSON_VALUE).build(),
						userController, userController.getClass().getMethod("getAll", Map.class));

		handlerMapping = app.getBean(RequestMappingHandlerMapping.class);
		userController = app.getBean(GenericController.class);
		handlerMapping.registerMapping(
				RequestMappingInfo.paths("/users/emp").methods(RequestMethod.POST)
						.produces(MediaType.APPLICATION_JSON_VALUE).build(),
				userController, userController.getClass().getMethod("getAllEmp", String.class));

		EmployeeRepositaryImpl employeeRepositaryImpl = app.getBean(EmployeeRepositaryImpl.class);

		employeeRepositaryImpl.init();
		String appName = env.getProperty("spring.application.name");
		log.info("appName :" + appName);
		handlerMapping.registerMapping(
				RequestMappingInfo.paths("/" + appName + "/{service}/addData").methods(RequestMethod.POST)
						.produces(MediaType.APPLICATION_JSON_VALUE).build(),
				userController, userController.getClass().getMethod("addData", String.class, String.class));

		handlerMapping.registerMapping(
				RequestMappingInfo.paths("/" + appName + "/{service}/updateData").methods(RequestMethod.PUT)
						.produces(MediaType.APPLICATION_JSON_VALUE).build(),
				userController, userController.getClass().getMethod("updateData", String.class, String.class));

		handlerMapping.registerMapping(
				RequestMappingInfo.paths("/" + appName + "/{service}/getdata").methods(RequestMethod.GET)
						.produces(MediaType.APPLICATION_JSON_VALUE).build(),
				userController, userController.getClass().getMethod("getDatum", String.class, Map.class));
		handlerMapping.registerMapping(
				RequestMappingInfo.paths("/" + appName + "/{service}/getdataForKey/{uniquekey}")
						.methods(RequestMethod.GET).produces(MediaType.APPLICATION_JSON_VALUE).build(),
				userController, userController.getClass().getMethod("getData", String.class, String.class));
		handlerMapping.registerMapping(
				RequestMappingInfo.paths("/" + appName + "/{service}/deleteData/{uniquekey}")
						.methods(RequestMethod.DELETE).produces(MediaType.APPLICATION_JSON_VALUE).build(),
				userController, userController.getClass().getMethod("delData", String.class, String.class));

		Map<String, String> params = new HashMap<String, String>();
		params.put("id", "1");
		params.put("first name", "Lokesh");

		// employeeRepositaryImpl.getDataForParams("tbl student",params);
		log.info(employeeRepositaryImpl.getDataForParams("tbl student", params).toString());
		Map<String, String> insertParams = new HashMap<String, String>();
		insertParams.put("first name", "Lokeshs");
		insertParams.put("email", "howtodoinjava@gmail.com");
		insertParams.put("last name", "jede");
		// insertParams.put("id", "5");
		// log.info(employeeRepositaryImpl.insertData("tbl
		// student",insertParams).toString());
		ObjectMapper mapper = new ObjectMapper();
		log.info(mapper.writeValueAsString(insertParams).toString().toString());
		ResponseEntity<Map<String, Object>> dataum=userController.addData("tbl student", mapper.writeValueAsString(insertParams));
		
		insertParams.put("last name", "jeddsds");
		log.info(employeeRepositaryImpl.updateData("tbl student", insertParams).toString());
		log.info(employeeRepositaryImpl.deleteData("tbl student",dataum.getBody().get("id").toString()).toString());
		return handlerMapping;
	}

	@Bean
	public FilterRegistrationBean<SimpleFilter> filterRegistrationBean() {
		FilterRegistrationBean<SimpleFilter> registrationBean = new FilterRegistrationBean();
		SimpleFilter customURLFilter = new SimpleFilter();

		registrationBean.setFilter(customURLFilter);
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(1);
		// set precedence
		return registrationBean;
	}

}
