package com.vin.intercept;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class SignupInterceptor  extends HandlerInterceptorAdapter{
	
	Logger log = Logger.getLogger(SignupInterceptor.class.getName());
	
	@Override
    public boolean preHandle(HttpServletRequest request, 
           HttpServletResponse response, Object handler) throws Exception {

	log.info("interceptor"+request.getParameterMap());


      return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, 
                                HttpServletResponse response, 
                                Object handler, Exception exception)
    throws Exception {
       
    }

    @Override
    public void postHandle(HttpServletRequest request, 
                HttpServletResponse response, 
                Object handler, ModelAndView modelAndView)
    throws Exception {
       
    }
}
