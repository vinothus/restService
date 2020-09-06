package com.howtodoinjava.demo;

import java.util.function.Function;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.mail.*;
import javax.mail.internet.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import java.net.URL;

public class SendEmail {
 
	static Logger log = Logger.getLogger(SendEmail.class.getName());
	
	public static void main(String [] args)  {  
		//Thread t=new Thread(()->{
			
			 runNgrok(); 
			 
				/*
				 * }); t.setDaemon(true); t.start();
				 */
			/*
			 * try {
			 * 
			 * String serviceUrl=geturl(); LocalDateTime dateTime = LocalDateTime.now();
			 * DateTimeFormatter f1 = DateTimeFormatter.ofPattern("MMM_dd_yyyy_HH_mm_ss");
			 * mailingURL(serviceUrl,"Ngrok start at "+dateTime.format(f1)); } catch
			 * (Exception e) { // TODO Auto-generated catch block e.printStackTrace(); }
			 */
		  
	   }

	private static void runNgrok() {
		ProcessBuilder processBuilder = new ProcessBuilder();
		  processBuilder.command("cmd.exe", "/c", "ping -n 3 google.com");
		  processBuilder.command("cmd.exe", "/c","cd  D:\\ngrok\\ngrok-stable-windows-amd64 ");
		  processBuilder.command("cmd.exe", "/c","dir");
		  try {

	            Process process = processBuilder.start();
	            process = Runtime.getRuntime().exec("D:\\\\ngrok\\\\ngrok-stable-windows-amd64\\\\startngrok.bat"); 
	            process.waitFor(); 
	            BufferedReader reader =
	                    new BufferedReader(new InputStreamReader(process.getInputStream()));

	            String line;
	            while ((line = reader.readLine()) != null) {
	            	log.info(line);
	               
	            }

	            int exitCode = process.waitFor();
	            log.info("\nExited with error code : " + exitCode);

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	}

	public static void mailingURL(String content,String subject) {
		// Recipient's email ID needs to be mentioned.
	      String to = "uspanigai@gmail.com";

	      // Sender's email ID needs to be mentioned
	      String from = "web@gmail.com";

	      // Assuming you are sending email from localhost
	      String host = "smtp.gmail.com";

	      // Get system properties
	      Properties props = System.getProperties();
	      final String fromUN ="vinothatjob@gmail.com";
	      final  String password ="vinaug@2020";
	      // Setup mail server
	     // props.setProperty("mail.smtp.host", host);
	      
	      props.setProperty("mail.transport.protocol", "smtp");     
	      props.setProperty("mail.host", "smtp.gmail.com");  
	      //props.setProperty("mail.smtp.ssl.enable","false");
	      //props.put("mail.smtp.starttls.enable", "true");
	      props.put("mail.smtp.auth", "true");  
	      props.put("mail.smtp.port", "465");  
	      props.put("mail.debug", "true");  
	      props.put("mail.smtp.socketFactory.port", "465");  
	      props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");  
	      props.put("mail.smtp.socketFactory.fallback", "false");  
	      // Get the default Session object.
	      Session session = Session.getDefaultInstance(props,  
	    		    new javax.mail.Authenticator() {
	          protected PasswordAuthentication getPasswordAuthentication() {  
	          return new PasswordAuthentication(fromUN,password);  
	      }  
	      });

	      try {
	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(from));

	         // Set To: header field of the header.
	         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

	         // Set Subject: header field
	         message.setSubject(subject);

	         // Now set the actual message
	         message.setText(content);

	         // Send message
	         Transport.send(message);
	         log.info("Sent message successfully....");
	      } catch (MessagingException mex) {
	         mex.printStackTrace();
	      }
	}
	
	
	public static String geturl() throws  Exception {
		  WebDriver driver;
		   Map<String, Object> vars;
		  JavascriptExecutor js;
			/*
			 * Options op = webdriver.ChromeOptions(); op.add_argument("headless"); driver =
			 * webdriver.Chrome(options=op);
			 */
		  System.setProperty("webdriver.chrome.driver",
                  "D:\\ngrok\\ngrok-stable-windows-amd64\\chromedriver.exe"); 
		
		  ChromeOptions options = new ChromeOptions();
		   
		  options.addArguments("--headless");
		  driver = new ChromeDriver(options);
		  //desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, options);
		  	  
		    js = (JavascriptExecutor) driver;
		    vars = new HashMap<String, Object>();
		    WebDriverWait wait = new WebDriverWait(driver, 10);
		    
	    // Test name: geturl
	    // Step # | name | target | value
	    // 1 | open | / | 
	    driver.get("https://ngrok.com/");
	    // 2 | setWindowSize | 1382x744 | 
	    driver.manage().window().setSize(new Dimension(1382, 744));
	    // 3 | click | css=.nav-links-right | 
	   // driver.findElement(By.cssSelector(".nav-links-right")).click();
	    // 4 | click | css=.nav-links-right .nav-link-text | 
	    driver.findElement(By.cssSelector(".nav-links-right .nav-link-text")).click();
	    // 5 | click | id=email | 
	    waitForPageLoad(driver);
	    driver.findElement(By.id("email")).click();
	    // 6 | type | id=email | uspanigai@gmail.com
	    driver.findElement(By.id("email")).sendKeys("uspanigai@gmail.com");
	    // 7 | click | id=password | 
	    driver.findElement(By.id("password")).click();
	    // 8 | type | id=password | vinaug@2020
	    driver.findElement(By.id("password")).sendKeys("vinaug@2020");
	    // 9 | click | css=.ant-btn-primary | 
	    driver.findElement(By.cssSelector(".ant-btn-primary")).click();
	    waitForPageLoad(driver);
		
		  String currentURL = driver.getCurrentUrl();
		 
		  String tunnelurl="/status/tunnels"; URL url = new URL(currentURL); String
		  path = url.getFile().substring(0, url.getFile().lastIndexOf('/')); String
		  base = url.getProtocol() + "://" + url.getHost() + path;
		  //wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("root")));
		  log.info(currentURL);
		  driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		  Thread.sleep(10000);
		  driver.navigate().to(base+tunnelurl);
		  
		 Thread.sleep(10000);
	    waitForPageLoad(driver);
	    log.info("current url :"+driver.getCurrentUrl());
	    log.info(driver.findElement(By.cssSelector(".ant-table-row > .ant-table-cell:nth-child(3)")).getText());
	    String ngrokServiceurl=driver.findElement(By.cssSelector(".ant-table-row > .ant-table-cell:nth-child(3)")).getText();
	   
	    
	    // 17 | click | linkText=Logout | 
	    //driver.findElement(By.linkText("Logout")).click();
	    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	    driver.navigate().to(base+"/logout");
	    Thread.sleep(10000);
	    // 18 | close |  | 
	    driver.close();
	    driver.quit();
	    return ngrokServiceurl;
	  }
	
	public static void waitForPageLoad(WebDriver driver_) {

	    Wait<WebDriver> wait = new WebDriverWait(driver_, 30);
	    wait.until(new Function<WebDriver, Boolean>() {
	        public Boolean apply(WebDriver driver) {
	        	log.info("Current Window State       : "
	                + String.valueOf(((JavascriptExecutor) driver).executeScript("return document.readyState")));
	            return String
	                .valueOf(((JavascriptExecutor) driver).executeScript("return document.readyState"))
	                .equals("complete");
	        }
	    });
	}
}
