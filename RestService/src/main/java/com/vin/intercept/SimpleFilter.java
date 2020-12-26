package com.vin.intercept;

import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
//@Component
public class SimpleFilter implements Filter {
	Logger log = Logger.getLogger(SimpleFilter.class.getName());
	@Override
   public void destroy() {}

   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterchain) 
      throws IOException, ServletException {
	   long startTime = System.currentTimeMillis();
	   log.info("Remote Host:"+request.getRemoteHost());
	   log.info("Remote Address:"+request.getRemoteAddr());
      filterchain.doFilter(request, response);
      long duration = System.currentTimeMillis() - startTime;
      log.info(("Request take " + duration + " ms"));
      log.info(("Request url " + getCurrentUrlFromRequest(request)));
      log.info(("Request Method " +((HttpServletRequest)request).getMethod()));
   }

   @Override
   public void init(FilterConfig filterconfig) throws ServletException {}
   
   
   public static String getCurrentUrlFromRequest(ServletRequest request)
   {
      if (! (request instanceof HttpServletRequest))
          return null;

      return getCurrentUrlFromRequest((HttpServletRequest)request);
   }

   public static String getCurrentUrlFromRequest(HttpServletRequest request)
   {
       StringBuffer requestURL = request.getRequestURL();
       String queryString = request.getQueryString();

       if (queryString == null)
           return requestURL.toString();

       return requestURL.append('?').append(queryString).toString();
   }
   
}