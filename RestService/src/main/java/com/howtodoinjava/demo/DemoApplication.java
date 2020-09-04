package com.howtodoinjava.demo;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.dynamic.ControllerBeanFactoryPostProcessor;
import com.dynamic.GenericController;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import com.howtodoinjava.demo.repository.EmployeeRepositaryImpl;
import com.vin.intercept.SimpleFilter;



@SpringBootApplication
public class DemoApplication {
	@Autowired
	 GenericController  userController;
	@Autowired
	 RequestMappingHandlerMapping handlerMapping;
	@FunctionalInterface
	 interface FuncInter1 
	    { 
		@RequestMapping("/test")
	        void operation(); 
	    } 
	 
	@Autowired
	DataSource dataSource;
	@Autowired
	  private Environment env;
	@Autowired
	static ApplicationContext app ;
	
	public static void main(String[] args) {
		
		/*
		 * LocalDateTime now = LocalDateTime.now(); LocalDateTime hourLater =
		 * LocalDateTime.now().plusHours(1); Duration span = Duration.between(now,
		 * hourLater); Thread t1=new Thread(
		 * 
		 * ()->System.out.println("In Run method") ); t1.start(); FuncInter1 test =() ->
		 * System.out.println("Zero parameter lambda"); test.operation();
		 */
		/*
		 * app = SpringApplication.run(DemoApplication.class, args);
		 * System.out.println((String)app.getBean("YearDataSource"));
		 * System.out.println((Object)app.getBean("gbdctrl")); Class
		 * c=app.getBean("gbdctrl").getClass(); Annotation ano[]=
		 * c.getDeclaredAnnotations(); for (int i = 0; i < ano.length; i++) {
		 * System.out.println(ano[i].getClass().getCanonicalName()); }
		 */
		
		SpringApplication.run(DemoApplication.class, args);
		
		
		
	}
	@Bean
	GenericController getGC()
	{
		
		return new GenericController();
	}
	
	 @Bean
	 ControllerBeanFactoryPostProcessor myConfigBean () {
         return new ControllerBeanFactoryPostProcessor(env);
     }
	 @Bean
	 RequestMappingHandlerMapping getObject(ApplicationContext app,DataSource dataSource,RequestMappingHandlerMapping handlerMapping) throws Exception
	 {
		 handlerMapping=app.getBean(RequestMappingHandlerMapping.class);
		 userController= app.getBean(GenericController.class);
		  handlerMapping.registerMapping(
	                RequestMappingInfo.paths("/users")
	                        .methods(RequestMethod.GET)
	                        .produces(MediaType.APPLICATION_JSON_VALUE)
	                        .build(),
	                userController,
	                userController.getClass()
	                        .getMethod("getAll", Map.class));
		  
		  handlerMapping=app.getBean(RequestMappingHandlerMapping.class);
			 userController= app.getBean(GenericController.class);
			  handlerMapping.registerMapping(
		                RequestMappingInfo.paths("/users/emp")
		                        .methods(RequestMethod.POST)
		                        .produces(MediaType.APPLICATION_JSON_VALUE)
		                        .build(),
		                userController,
		                userController.getClass()
		                        .getMethod("getAllEmp", String.class));
				/*
				 * GetTableNames getTableNames = new GetTableNames(); try { Object o =
				 * JdbcUtils.extractDatabaseMetaData(dataSource, getTableNames);
				 * System.out.println(o); } catch (MetaDataAccessException e) {
				 * System.out.println(e); }
				 */
			
			  
			  EmployeeRepositaryImpl employeeRepositaryImpl=	  app.getBean(EmployeeRepositaryImpl.class);
				/*
				 * System.out.println(employeeRepositaryImpl.getMetaDatum());
				 * Map<DbTable,List<DbColumn>> tc=employeeRepositaryImpl.getMetaDatum(); for
				 * (Map.Entry<DbTable,List<DbColumn>> entry : tc.entrySet()) { DbTable table=
				 * entry.getKey(); employeeRepositaryImpl.createDbTable(table); List<DbColumn>
				 * column=entry.getValue(); System.out.println("Key = " + entry.getKey() +
				 * ", Value = " + entry.getValue()); }
				 * 
				 * List<DbTable> inilis=employeeRepositaryImpl.initializeTable() ; for (Iterator
				 * iterator = inilis.iterator(); iterator.hasNext();) { DbTable dbTable =
				 * (DbTable) iterator.next(); employeeRepositaryImpl.createDbTable(dbTable); }
				 */
			  employeeRepositaryImpl.init();
			 // EmployeeRepositaryImpl.tableColumnMap=  employeeRepositaryImpl.getMetaDatum() ;
		//System.out.println( employeeRepositaryImpl.getData("student","1"));
		Map<String,String> params=new HashMap<String,String>();
		params.put("id", "1");
		params.put("first name","Lokesh");
		
		employeeRepositaryImpl.getDataForParams("tbl student",params);
		 System.out.println(employeeRepositaryImpl.getDataForParams("tbl student",params));
		 Map<String,String> insertParams=new HashMap<String,String>();
		 insertParams.put("first name","Lokeshs");
		 insertParams.put("email","howtodoinjava@gmail.com");
		 insertParams.put("last name","jede");
		 insertParams.put("id", "5");
		 //employeeRepositaryImpl.insertData("tbl student",insertParams);
		 insertParams.put("last name","jeddsds");
		 employeeRepositaryImpl.updateData("tbl student",insertParams);
		 employeeRepositaryImpl.deleteData("tbl student","5");
		 return handlerMapping;
	 }
	/*
	 * @Bean public LocalSessionFactoryBean getSessionFactory() {
	 * LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
	 * sessionFactory.setDataSource(dataSource);
	 * sessionFactory.setPackagesToScan(new String[] { "*" });
	 * sessionFactory.setHibernateProperties(getHibernateProperties()); return
	 * sessionFactory; }
	 * 
	 * @Bean public HibernateTransactionManager transactionManager(SessionFactory
	 * sessionFactory) { HibernateTransactionManager txManager = new
	 * HibernateTransactionManager(); txManager.setSessionFactory(sessionFactory);
	 * return txManager; }
	 * 
	 * private Properties getHibernateProperties() { Properties properties = new
	 * Properties(); properties.put(AvailableSettings.DIALECT,
	 * env.getRequiredProperty("hibernate.dialect"));
	 * properties.put(AvailableSettings.SHOW_SQL,
	 * env.getRequiredProperty("hibernate.show_sql"));
	 * properties.put(AvailableSettings.STATEMENT_BATCH_SIZE,
	 * env.getRequiredProperty("hibernate.batch.size"));
	 * properties.put(AvailableSettings.HBM2DDL_AUTO,
	 * env.getRequiredProperty("hibernate.hbm2ddl.auto"));
	 * properties.put(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS,
	 * env.getRequiredProperty("hibernate.current.session.context.class")); return
	 * properties; }
	 */

	/*
	 * @Bean(name = "entityManagerFactory") public EntityManagerFactory
	 * entityManagerFactory() { LocalContainerEntityManagerFactoryBean emf = new
	 * LocalContainerEntityManagerFactoryBean(); emf.setDataSource(dataSource);
	 * //emf.setJpaVendorAdapter(jpaVendorAdapter); emf.setPackagesToScan("*");
	 * emf.setPersistenceUnitName("default"); emf.afterPropertiesSet(); return
	 * emf.getObject(); }
	 */
	 
	/*
	 * @Autowired private EntityManagerFactory entityManagerFactory;
	 * 
	 * @Bean public SessionFactory getSessionFactory() { if
	 * (entityManagerFactory.unwrap(SessionFactory.class) == null) { throw new
	 * NullPointerException("factory is not a hibernate factory"); } return
	 * entityManagerFactory.unwrap(SessionFactory.class); }
	 */
	
	/*
	 * @Bean public DataSource dataSource() { HikariDataSource ds = new
	 * HikariDataSource(); ds.setMaximumPoolSize(100);
	 * ds.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
	 * ds.addDataSourceProperty("url", "jdbc:mysql://localhost:3306/test");
	 * ds.addDataSourceProperty("user", "root");
	 * ds.addDataSourceProperty("password", "password");
	 * ds.addDataSourceProperty("cachePrepStmts", true);
	 * ds.addDataSourceProperty("prepStmtCacheSize", 250);
	 * ds.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
	 * ds.addDataSourceProperty("useServerPrepStmts", true); return ds; }
	 */
	  @Bean public FilterRegistrationBean < SimpleFilter >
	   filterRegistrationBean() { FilterRegistrationBean < SimpleFilter >
	   registrationBean = new FilterRegistrationBean(); SimpleFilter customURLFilter
	   = new SimpleFilter();
	   
	   registrationBean.setFilter(customURLFilter);
	   registrationBean.addUrlPatterns("/*"); registrationBean.setOrder(1);
	   //set	   precedence 
	   return registrationBean; }
	 
}

