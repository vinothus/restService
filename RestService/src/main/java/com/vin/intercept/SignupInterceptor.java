package com.vin.intercept;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
@Component
public class SignupInterceptor  extends HandlerInterceptorAdapter{
	@Override
    public boolean preHandle(HttpServletRequest request, 
           HttpServletResponse response, Object handler) throws Exception {

	System.out.println("interceptor"+request.getParameterMap());


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
