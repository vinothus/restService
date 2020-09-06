package com.vin.rest;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.vin.intercept.SimpleFilter;
import com.vin.rest.dynamic.GenericController;
import com.vin.rest.repository.EmployeeRepositaryImpl;

 
@SpringBootApplication
public class Application {
	static Logger log = Logger.getLogger(Application.class.getName());
	@Autowired
	GenericController userController;
	
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

		SpringApplication.run(Application.class, args);

	}

	
	@Bean
	RequestMappingHandlerMapping getObject(ApplicationContext app, DataSource dataSource,
			  Environment env) {
		RequestMappingHandlerMapping	handlerMapping = app.getBean(RequestMappingHandlerMapping.class);
		userController = app.getBean(GenericController.class);
		try {
			handlerMapping
					.registerMapping(
							RequestMappingInfo.paths("/users").methods(RequestMethod.GET)
									.produces(MediaType.APPLICATION_JSON_VALUE).build(),
							userController, userController.getClass().getMethod("getAll", Map.class));
			handlerMapping.registerMapping(
					RequestMappingInfo.paths("/users/emp").methods(RequestMethod.POST)
							.produces(MediaType.APPLICATION_JSON_VALUE).build(),
					userController, userController.getClass().getMethod("getAllEmp", String.class));

			EmployeeRepositaryImpl employeeRepositaryImpl = app.getBean(EmployeeRepositaryImpl.class);

			employeeRepositaryImpl.init();
			String appName = env.getProperty("spring.application.name");
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
		 
		} catch (  Exception e) {
			e.printStackTrace();
		}

		return handlerMapping;
	}

	@Bean
	public FilterRegistrationBean<SimpleFilter> filterRegistrationBean() {
		FilterRegistrationBean<SimpleFilter> registrationBean = new FilterRegistrationBean<>();
		SimpleFilter customURLFilter = new SimpleFilter();

		registrationBean.setFilter(customURLFilter);
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(1);
		// set precedence
		return registrationBean;
	}

}
