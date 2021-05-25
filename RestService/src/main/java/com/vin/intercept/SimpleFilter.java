package com.vin.intercept;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.vin.rest.exception.DatabaseAuthException;
import com.vin.rest.repository.EmployeeRepositaryImpl;

import java.sql.*;
@Component
public class SimpleFilter implements Filter {
	Logger log = Logger.getLogger(SimpleFilter.class.getName());
	public static BlockingQueue<Map<String,String>> dbLogger= new ArrayBlockingQueue<Map<String,String>>(1024);
	Runnable dbRunnable=new Runnable() {
		 public void run() { 
			while (!dbLogger.isEmpty()) {

				Map<String, String> mapFMQueue = null;
				try {
					mapFMQueue = dbLogger.take();
					String urlKey=mapFMQueue.get("urlkey");
					if(!urlKey.equalsIgnoreCase("none")&&urlKey.length()>0) {
					try{
						String uid=	employeeRepositaryImpl.getUidForapiKey(urlKey);
						mapFMQueue.put("uid", uid);
					}catch(Exception e) {}
					
					}
					Map<String, String> copyMap = new Hashtable<String, String>();
					try{copyMap.putAll(mapFMQueue);}catch(Exception e) {}
					if (mapFMQueue.get("status").equalsIgnoreCase("200")
							|| mapFMQueue.get("status").equalsIgnoreCase("201")
							|| mapFMQueue.get("status").equalsIgnoreCase("203")) {
						employeeRepositaryImpl.insertData("service consumption", copyMap, "system", "system", "none");
					} else {
						employeeRepositaryImpl.insertData("service error", copyMap, "system", "system", "none");
					}

				} catch (org.springframework.dao.DuplicateKeyException ex) {
					try {
						if (mapFMQueue != null) {
							Thread.sleep(1000);
							dbLogger.put(mapFMQueue);

						}
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					// ex.printStackTrace();
				} catch (Exception e) {
				 
					if(e.getMessage()!=null) {
					if (e.getMessage().equals("Duplicate Primary key Try Update")) {
						try {
							if (mapFMQueue != null) {
								Thread.sleep(1000);
								dbLogger.put(mapFMQueue);

							}
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					}
					 e.printStackTrace();
				}

			}
		 
		 }
		 
		};
	
	@Override
   public void destroy() {}

	@Autowired
	EmployeeRepositaryImpl employeeRepositaryImpl;
	
	@Autowired
	private Environment env;
	
   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterchain) 
      throws IOException, ServletException {
	   SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, request.getServletContext());
	   long startTime = System.currentTimeMillis();
	   log.info("Remote Host:"+request.getRemoteHost());
	   log.info("Remote Address:"+request.getRemoteAddr());
	 //  final CopyPrintWriter writer = new CopyPrintWriter(response.getWriter());
     filterchain.doFilter(request, response);
		/*
		 * filterchain.doFilter(request, new
		 * HttpServletResponseWrapper((HttpServletResponse) response) {
		 * 
		 * @Override public PrintWriter getWriter() { return writer; } });
		 */
      //logger.log(writer.getCopy());
      long duration = System.currentTimeMillis() - startTime;
      log.info(("Request take " + duration + " ms"));
      log.info(("Request url " + getCurrentUrlFromRequest(request)));
      log.info(("Request Method " +((HttpServletRequest)request).getMethod()));
      String rmHost=request.getRemoteHost();
      String rmAddress=request.getRemoteAddr();
      String takeDuration= Long.toString(duration);
      String reqUrl=  getCurrentUrlFromRequest(request);
      String uidKey="";
      URL url=new URL(reqUrl);
      String path[]=url.getPath().split("/");
      if(path.length>=3) {
    	  uidKey=path[2];  
      }
      String reqMethod= ((HttpServletRequest)request).getMethod();
      String date=new Date(new java.util.Date().getTime()).toString();
      String time=new java.sql.Time(new java.util.Date().getTime()).toString();
      Map<String,String> successMap=new HashMap<String,String> ();
      Map<String,String> failureMap=new HashMap<String,String> ();
      String type =((HttpServletResponse)response).getContentType();
		if (type == null || type.equalsIgnoreCase("null")) {
			type = "un known";
		}
      successMap.put("rmhost", rmHost);
      if(reqUrl.length()>99)
      {  successMap.put("url",reqUrl.substring(0, 98));}
      else {
    	  successMap.put("url",reqUrl); 
      }
      successMap.put("urlkey", uidKey);
      successMap.put("date", date);
      successMap.put("time", time);
      successMap.put("method", reqMethod);
      successMap.put("type", type);
      successMap.put("duration",takeDuration);
      successMap.put("status",Integer.toString( ((HttpServletResponse) response).getStatus()));
      
      failureMap.put("rmhost", rmHost);
      
      if(reqUrl.length()>99)
      {  failureMap.put("url",reqUrl.substring(0, 98));}
      else {
    	  failureMap.put("url",reqUrl); 
      }
      failureMap.put("urlkey", uidKey);
      failureMap.put("date", date);
      failureMap.put("time", time);
      failureMap.put("method", reqMethod);
      failureMap.put("type", type);
      failureMap.put("duration",takeDuration);
      failureMap.put("status", Integer.toString( ((HttpServletResponse) response).getStatus()));
      if(response.toString().length()>99)
      {  failureMap.put("errormsg",response.toString().substring(0, 98));}
      else {
    	  failureMap.put("errormsg",response.toString()); 
      }
      
	  int status =  ((HttpServletResponse) response).getStatus();
		if (status == 200 || status == 201 || status == 203) {
			dbLogger.add(successMap);

		} else {
			dbLogger.add(failureMap);

		}
		Thread loggerThread = new Thread(dbRunnable);
		loggerThread.start();
 
      
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
class CopyPrintWriter extends PrintWriter {

    private StringBuilder copy = new StringBuilder();

    public CopyPrintWriter(Writer writer) {
        super(writer);
    }

    @Override
    public void write(int c) {
        copy.append((char) c); // It is actually a char, not an int.
        super.write(c);
    }

    @Override
    public void write(char[] chars, int offset, int length) {
        copy.append(chars, offset, length);
        super.write(chars, offset, length);
    }

    @Override
    public void write(String string, int offset, int length) {
        copy.append(string, offset, length);
        super.write(string, offset, length);
    }

    public String getCopy() {
        return copy.toString();
    }

}