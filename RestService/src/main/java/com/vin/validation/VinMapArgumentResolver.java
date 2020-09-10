package com.vin.validation;

import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class VinMapArgumentResolver
implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter methodParameter) {
      return methodParameter.getParameterAnnotation(VinMaps.class) != null;
  }

  @Override
  public Object resolveArgument(
    MethodParameter methodParameter, 
    ModelAndViewContainer modelAndViewContainer, 
    NativeWebRequest nativeWebRequest, 
    WebDataBinderFactory webDataBinderFactory) throws Exception {
VinMap<String,String> map=new VinMap<>();
      HttpServletRequest request 
        = (HttpServletRequest) nativeWebRequest.getNativeRequest();
      Enumeration<String> parameterNames = request.getParameterNames();
      while (parameterNames.hasMoreElements()) {
    	  
          String paramName = parameterNames.nextElement();
          map.put(paramName, request.getParameter(paramName));
          
      }
      return map;
  }
}