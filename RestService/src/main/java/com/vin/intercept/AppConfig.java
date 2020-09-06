package com.vin.intercept;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//@Configuration
//@EnableWebMvc
public class AppConfig extends WebMvcConfigurerAdapter    {

	@Autowired
	SignupInterceptor demoInterceptor;
	
	 @Override
	 public void addInterceptors(InterceptorRegistry registry) {
	 registry.addInterceptor(demoInterceptor);
	 }
}
