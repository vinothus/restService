package com.vin.rest;

import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.vin.intercept.SimpleFilter;
import com.vin.processor.VinRestProcessor;
import com.vin.rest.dynamic.GenericController;
import com.vin.rest.dynamic.MultiServiceController;
import com.vin.validation.ParamsValidator;
import com.vin.validation.WebConfig;

 
@SpringBootApplication
public class Application {
	
	public static void main(String[] args) throws Exception {

		 new SpringApplicationBuilder(Application.class)
         .beanNameGenerator(new CustomBeanNameGenerator())
         .run(args);
		
	}
	static Logger log = Logger.getLogger(Application.class.getName());
	@Autowired
	GenericController userController;
	
	@Autowired
	MultiServiceController  multiServiceController;
	
	
	@Bean
	RequestMappingHandlerMapping handlerMapping(ApplicationContext app,
			  Environment env,MultiServiceController  multiServiceController,GenericController userController) {
		RequestMappingHandlerMapping	handlerMapping = app.getBean(RequestMappingHandlerMapping.class);
		userController = app.getBean(GenericController.class);
		  multiServiceController=app.getBean(MultiServiceController.class);
		try {
			String apiKey="apiKey";
			String dataStoreKey="dataStoreKey";
			String appName = env.getProperty("spring.application.name");
			String createData = env.getProperty("createData");
			String updateData = env.getProperty("updateData");
			String getAllData = env.getProperty("getAllData");
			String getOnedata = env.getProperty("getOnedata");
			String delete = env.getProperty("delete");
			String multiService = env.getProperty("multiService");
			String getMultipleOnedata =env.getProperty("getMultipleOnedata");
			String getMultipleAllData=env.getProperty("getMultipleAllData");
			String deleteMultiple= env.getProperty("deleteMultiple");
			String updateMultipleData=env.getProperty("updateMultipleData");
			String createMultipleData=env.getProperty("createMultipleData");
			handlerMapping.registerMapping(
					RequestMappingInfo.paths("/" + appName + "/{"+apiKey+"}/{"+dataStoreKey+"}/{service}/"+createData).methods(RequestMethod.POST)
							.produces(MediaType.APPLICATION_JSON_VALUE).build(),
					userController, userController.getClass().getMethod(GenericController.getMethodName("addData"), String.class, String.class, String.class, String.class, String.class,Map.class));

			handlerMapping.registerMapping(
					RequestMappingInfo.paths("/" + appName + "/{"+apiKey+"}/{"+dataStoreKey+"}/{service}/"+updateData).methods(RequestMethod.PUT)
							.produces(MediaType.APPLICATION_JSON_VALUE).build(),
					userController, userController.getClass().getMethod(GenericController.getMethodName("updateData"), String.class, String.class, String.class, String.class, String.class,Map.class));

			handlerMapping.registerMapping(
					RequestMappingInfo.paths("/" + appName + "/{"+apiKey+"}/{"+dataStoreKey+"}/{service}/"+getAllData).methods(RequestMethod.GET)
							.produces(MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE).build(),
					userController, userController.getClass().getMethod(GenericController.getMethodName("getDatum"), String.class, Map.class, String.class, String.class, String.class,Map.class));
			handlerMapping.registerMapping(
					RequestMappingInfo.paths("/" + appName + "/{"+apiKey+"}/{"+dataStoreKey+"}/{service}/"+getOnedata+"/{uniquekey}")
							.methods(RequestMethod.GET).produces(MediaType.APPLICATION_JSON_VALUE).build(),
					userController, userController.getClass().getMethod(GenericController.getMethodName("getData"), String.class, String.class, String.class, String.class, String.class,Map.class));
			handlerMapping.registerMapping(
					RequestMappingInfo.paths("/" + appName + "/{"+apiKey+"}/{"+dataStoreKey+"}/{service}/"+delete+"/{uniquekey}")
							.methods(RequestMethod.DELETE).produces(MediaType.APPLICATION_JSON_VALUE).build(),
					userController, userController.getClass().getMethod(GenericController.getMethodName("delData"), String.class, String.class, String.class, String.class, String.class,Map.class));
			handlerMapping.registerMapping(
					RequestMappingInfo.paths("/" + appName + "/{"+apiKey+"}/{"+dataStoreKey+"}/clearCache")
							.methods(RequestMethod.GET).produces(MediaType.APPLICATION_JSON_VALUE).build(),
					userController, userController.getClass().getMethod(GenericController.getMethodName("clearCache"), String.class, String.class));
			handlerMapping.registerMapping(
					RequestMappingInfo.paths("/" + appName + "/{"+apiKey+"}/{"+dataStoreKey+"}/initGC")
							.methods(RequestMethod.GET).produces(MediaType.APPLICATION_JSON_VALUE).build(),
					userController, userController.getClass().getMethod(GenericController.getMethodName("initGC"), String.class, String.class,Map.class));
			
			handlerMapping.registerMapping(
					RequestMappingInfo.paths("/" + appName + "/{"+apiKey+"}/{"+dataStoreKey+"}/"+multiService+"/{service}/"+getMultipleAllData).methods(RequestMethod.GET)
							.produces(MediaType.APPLICATION_JSON_VALUE).build(),
							multiServiceController, multiServiceController.getClass().getMethod(MultiServiceController.getMethodName("getDatum"), String.class, Map.class, String.class, String.class, String.class,Map.class));
			handlerMapping.registerMapping(
					RequestMappingInfo.paths("/" + appName + "/{"+apiKey+"}/{"+dataStoreKey+"}/"+multiService+"/{service}/"+getMultipleOnedata+"/{uniquekey}").methods(RequestMethod.GET)
							.produces(MediaType.APPLICATION_JSON_VALUE).build(),
							multiServiceController, multiServiceController.getClass().getMethod(MultiServiceController.getMethodName("getData"), String.class, String.class, String.class, String.class, String.class,Map.class));
			handlerMapping.registerMapping(
					RequestMappingInfo.paths("/" + appName + "/{"+apiKey+"}/{"+dataStoreKey+"}/"+multiService+"/{service}/"+createMultipleData).methods(RequestMethod.POST)
							.produces(MediaType.APPLICATION_JSON_VALUE).build(),
							multiServiceController, multiServiceController.getClass().getMethod(MultiServiceController.getMethodName("addData"), String.class, String.class, String.class, String.class, String.class,Map.class));
			handlerMapping.registerMapping(
					RequestMappingInfo.paths("/" + appName + "/{"+apiKey+"}/{"+dataStoreKey+"}/"+multiService+"/{service}/"+updateMultipleData).methods(RequestMethod.PUT)
							.produces(MediaType.APPLICATION_JSON_VALUE).build(),
							multiServiceController, multiServiceController.getClass().getMethod(MultiServiceController.getMethodName("updateData"), String.class, String.class, String.class, String.class, String.class,Map.class));
			handlerMapping.registerMapping(
					RequestMappingInfo.paths("/" + appName + "/{"+apiKey+"}/{"+dataStoreKey+"}/"+multiService+"/{service}/"+deleteMultiple+"/{uniquekey}")
							.methods(RequestMethod.DELETE).produces(MediaType.APPLICATION_JSON_VALUE).build(),
							multiServiceController, multiServiceController.getClass().getMethod(MultiServiceController.getMethodName("delData"), String.class, String.class, String.class, String.class, String.class,Map.class));
			// mapping for validation and processing
			
			handlerMapping.registerMapping(
					RequestMappingInfo.paths("/" + appName + "/{"+apiKey+"}/{"+dataStoreKey+"}/{service}/validateData").methods(RequestMethod.GET)
							.produces(MediaType.APPLICATION_JSON_VALUE).build(),
					userController, userController.getClass().getMethod(GenericController.getMethodName("validateData"), String.class, Map.class, String.class, String.class, String.class));
		
			handlerMapping.registerMapping(
					RequestMappingInfo.paths("/" + appName + "/{"+apiKey+"}/{"+dataStoreKey+"}/{service}/validateDatum").methods(RequestMethod.GET)
							.produces(MediaType.APPLICATION_JSON_VALUE).build(),
					userController, userController.getClass().getMethod(GenericController.getMethodName("validateDatum"), String.class, Map.class, String.class, String.class, String.class));
			handlerMapping.registerMapping(
					RequestMappingInfo.paths("/" + appName + "/{"+apiKey+"}/{"+dataStoreKey+"}/{service}/preProcessData").methods(RequestMethod.GET)
							.produces(MediaType.APPLICATION_JSON_VALUE).build(),
					userController, userController.getClass().getMethod(GenericController.getMethodName("preProcessData"), String.class, Map.class, String.class, String.class, String.class));
			handlerMapping.registerMapping(
					RequestMappingInfo.paths("/" + appName + "/{"+apiKey+"}/{"+dataStoreKey+"}/{service}/postProcessData").methods(RequestMethod.GET)
							.produces(MediaType.APPLICATION_JSON_VALUE).build(),
					userController, userController.getClass().getMethod(GenericController.getMethodName("postProcessData"), String.class, Map.class, String.class, String.class, String.class));
        
                    	handlerMapping.registerMapping(
					RequestMappingInfo.paths("/" + appName + "/{"+apiKey+"}/{"+dataStoreKey+"}/{service}/"+getAllData+"/getDatumStream").methods(RequestMethod.GET)
							.produces(MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE).build(),
					userController, userController.getClass().getMethod(GenericController.getMethodName("getDatumStream"), HttpServletResponse.class,String.class, Map.class, String.class, String.class, String.class,Map.class));
        
                    
			
			//handlerMapping
			//.registerMapping(
			//		RequestMappingInfo.paths("/login").methods(RequestMethod.GET)
			//				.produces(MediaType.ALL_VALUE).build(),
			//		userController, userController.getClass().getMethod(GenericController.getMethodName("home")));
			
		} catch (  Exception e) {
			e.printStackTrace();
		}

		return handlerMapping;
	}

	@Bean 
	public VinRestProcessor vinRestProcessor()
	{
		return new VinRestProcessor();
	}
	
	@Bean
	public ParamsValidator paramsValidator()
	{
		return new ParamsValidator();
	}
	
	@Bean
	 public MethodValidationPostProcessor methodValidationPostProcessor() {
	      return new MethodValidationPostProcessor();
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
	
	private static class CustomBeanNameGenerator implements BeanNameGenerator {
        @Override
        public String generateBeanName(BeanDefinition d, BeanDefinitionRegistry r) {
            return d.getBeanClassName();
        }

	 
    }
		
}
