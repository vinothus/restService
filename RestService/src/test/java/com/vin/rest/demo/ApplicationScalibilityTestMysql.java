package com.vin.rest.demo;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.dbspec.Constraint;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbConstraint;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationScalibilityTestMysql {

	
	Logger log = Logger.getLogger(ApplicationScalibilityTest.class.getName());
	
	static DbSchema schemaObj;
	static DbSpec specficationObj;

	private void loadSQLBuilderSchema() {
		specficationObj = new DbSpec();

		schemaObj = specficationObj.addDefaultSchema();
	}
	 private MockMvc mvc;
	 
	    @Autowired 
	    JdbcTemplate jdbcTemplate;
	    
	    @Autowired
	    private WebApplicationContext webApplicationContext;
	    
	    @Autowired
	    Environment env;
	    
	    
	    @Before
	    public void setUp() {
	      mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	    }

		@Test
		public void columntypes() throws Exception
		{
			int[] sqlTypes = getSqlTypes();

			String[] nonscaling = { "DISTINCT", "STRUCT", "REF", "DATALINK", "ROWID", "SQLXML", "?" };
			List<String> invalidElement = new ArrayList<String>();
			for (int i = 0; i < nonscaling.length; i++) {
				invalidElement.add(nonscaling[i]);
			}
			for (int i = 0; i < sqlTypes.length; i++) {

				String TableName = generateRandomString();
				String sqlTypesStr = getSqlTypeName(sqlTypes[i]);
				String query = null;
				if (!invalidElement.contains(sqlTypesStr)) {
					query = "CREATE TABLE " + TableName + "( " + generateRandomString() + "  " + sqlTypesStr + " )";

				}

				try {
					if (query != null) {
						jdbcTemplate.execute(query);
						log.info(query);
						String insertQuery = "INSERT INTO " + TableName + " values ('" + getSampleData(sqlTypesStr)
								+ "')";
						if (sqlTypesStr.contains("BINARY")) {
							insertQuery = "INSERT INTO " + TableName + " values (" + getSampleData(sqlTypesStr) + ")";
						}
						log.info(insertQuery);
						log.info(sqlTypesStr);
						log.info(getSampleData(sqlTypesStr));
						try {
							jdbcTemplate.execute(insertQuery);
						} catch (Exception e) {
							log.info(sqlTypesStr + " insert fails");
						}
						jdbcTemplate.execute("drop table " + TableName);

					}
					// log.info(getSqlTypeName(sqlTypes[i])+" without scaling");

				} catch (Exception e) {
					log.info(sqlTypesStr);
					query = "CREATE TABLE " + TableName + "( " + generateRandomString() + "  "
							+ getSqlTypeName(sqlTypes[i]) + " (10))";
					try {
						jdbcTemplate.execute(query);
						jdbcTemplate.execute("drop table " + TableName);
						log.info(getSqlTypeName(sqlTypes[i]) + " with scaling");
					} catch (Exception ex) {

					}
				}

			}
		}
		@Test
		public void testJdbcTemplate() throws Exception
		{
			String[] tableName = new String[1];
		 try {	String[] nonscaling = {"LONGVARBINARY","LONGVARCHAR","NCHAR","LONGNVARCHAR","NVARCHAR","NCLOB","BLOB","CLOB","NULL","OTHER","JAVA_OBJECT","ARRAY", "DISTINCT", "STRUCT", "REF", "DATALINK", "ROWID", "SQLXML", "?" };
			List<String> invalidElement = new ArrayList<String>();
			for (int i = 0; i < nonscaling.length; i++) {
				invalidElement.add(nonscaling[i]);
			}

			mvc.perform(MockMvcRequestBuilders.get("/myApps/service/getdata").contentType(MediaType.APPLICATION_JSON)
					.param("tableName", "TBL_STUDENT")).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
			System.out.println(jdbcTemplate.queryForList(" select * from service "));
			int[] sqlTypes = getSqlTypes();
			// initilize table names
			
			loadSQLBuilderSchema();
			for (int i = 0; i < tableName.length; i++) {
				tableName[i] = generateRandomString();
				DbTable randomTable = schemaObj.addTable(tableName[i]);
				randomTable.addColumn("id", Types.INTEGER, 10).primaryKey();
				for (int j = 0; j < sqlTypes.length; j++) {
					String columnName = generateRandomString();
					while (!(columnName.length() > 0)) {
						columnName = generateRandomString();
					}

					if (!invalidElement.contains(getSqlTypeName(sqlTypes[j]))) {
						randomTable.addColumn(columnName, sqlTypes[j], 10);
					}
				}
				createDbTable(randomTable);
				insertDbTable(randomTable,10);
			}
			rest(tableName);
		 }catch(Exception e)
			{
			
			 }finally {
				 
				 dropTable(tableName);
			 }

		}
	public void rest(String[] tableName)
	{
	for (int i = 0; i < tableName.length; i++) {
		String tablename = tableName[i];
		try {
			mvc.perform(MockMvcRequestBuilders.get("/myApps/"+tablename.toLowerCase()+"/getdata").contentType(MediaType.APPLICATION_JSON)
					.param("tableName", "TBL_STUDENT"))
			 .andDo(MockMvcResultHandlers.print());
			 //.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info(e.getMessage());
		}
	}	
	}
		
		
		
	private void insertDbTable(DbTable randomTable, int i) {
		loadSQLBuilderSchema();
		for (int j = 0; j < i; j++) {
			
		
		String manualQuery="INSERT INTO    "+randomTable.getName() + " VALUES ( ";
		List<DbColumn>  columns=randomTable.getColumns();
		for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
			DbColumn dbColumn = (DbColumn) iterator.next();
			List<DbConstraint> dbconstaints=dbColumn.getConstraints();
			boolean isPrimary=false;
			for (Iterator iterator2 = dbconstaints.iterator(); iterator2.hasNext();) {
				DbConstraint dbConstraint = (DbConstraint) iterator2.next();
				if (dbConstraint.getType().toString().equals(Constraint.Type.PRIMARY_KEY.toString())) {
					isPrimary= true;
				}
				
			}
			if(isPrimary)
			{
				manualQuery=manualQuery+  " "+j+" ";
			}
			else if(dbColumn.getTypeNameSQL().contains("INTEGER"))
			{
				manualQuery=manualQuery+  " 0 ";
			}else if(dbColumn.getTypeNameSQL().contains("VARCHAR"))
			{
				manualQuery=manualQuery+" '"+getSampleData(dbColumn.getTypeNameSQL())+"' ";
			}else if(dbColumn.getTypeNameSQL().contains("BIT"))
			{
				manualQuery=manualQuery+" "+getSampleData(dbColumn.getTypeNameSQL())+" ";
			}else 
			{
				if (dbColumn.getTypeNameSQL().contains("BINARY")) {
				manualQuery=manualQuery+" "+getSampleData(dbColumn.getTypeNameSQL())+" ";
				}else
				{
					manualQuery=manualQuery+" '"+getSampleData(dbColumn.getTypeNameSQL())+"' ";
				}
				//getSqlTypeName(dbColumn.getTypeNameSQL());
				
				
			}
			
			if( iterator.hasNext())
			{
				manualQuery=manualQuery+" , ";	
			}
		}
		manualQuery=manualQuery+" )";
		try{jdbcTemplate.update(manualQuery);}catch(Exception e) {log.info(e.getMessage());}
		log.info(manualQuery);
		}
		}

	private void dropTable(String[] tableName) {
			for (int i = 0; i < tableName.length; i++) {
				String string = tableName[i];
				try {
					jdbcTemplate.execute("drop table "+string);
					System.out.println("drop table "+string);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
				
			}
		}

	public String generateRandomString()
	{
		 String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		    StringBuilder sb = new StringBuilder();
		    Random random = new Random();
		    int length = 7;
		    for(int i = 0; i < length; i++) {
		      int index = random.nextInt(alphabet.length());
		      char randomChar = alphabet.charAt(index);
		      sb.append(randomChar);
		    }
		    String randomString = sb.toString();	
		return randomString;
	}
	
	public int getRandomSqlTypes() throws Exception
	{
		
		Field[] fields = java.sql.Types.class.getFields();
		int maxFields=fields.length;
		 Random random = new Random();
		int ret;
		ret= random.nextInt();
		while(!(0<ret &&ret <maxFields))
		{
			ret= random.nextInt();
		}
		Field field=fields[ret];
		field.setAccessible(true);
		int[] types=new int[maxFields];
		for (int i = 0; i < types.length; i++) {
			types[i]=(int) field.get(null);
		}
		return  (int) field.get(null);
	}
	
	public int[] getSqlTypes() throws Exception
	{
		
		Field[] fields = java.sql.Types.class.getFields();
		int maxFields=fields.length;
		int[] types=new int[maxFields];
		for (int i = 0; i < types.length; i++) {
			Field field=fields[i];
			field.setAccessible(true);
			types[i]=(int) field.get(null);
		}
		return  types;
	}
	
	public void createDbTable(DbTable tableName) {
		log.info("\n=======Creating '" + tableName.getName() + "' In The Database=======\n");
		loadSQLBuilderSchema();
		try {

			String createTableQuery = new CreateTableQuery(tableName, true).validate().toString();
			log.info("\nGenerated Sql Query?= " + createTableQuery + "\n");
			jdbcTemplate.execute(createTableQuery);
		}  catch (Exception sqlException) {
			if(sqlException instanceof java.sql.SQLSyntaxErrorException || sqlException instanceof org.springframework.jdbc.BadSqlGrammarException)
			{
				log.info("table creation failed with Syntax Error");
				
				String manualQuery="Create TABLE "+tableName.getName() + " ( ";
				List<DbColumn>  columns=tableName.getColumns();
				for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
					DbColumn dbColumn = (DbColumn) iterator.next();
					List<DbConstraint> dbconstaints=dbColumn.getConstraints();
					boolean isPrimary=false;
					for (Iterator iterator2 = dbconstaints.iterator(); iterator2.hasNext();) {
						DbConstraint dbConstraint = (DbConstraint) iterator2.next();
						if (dbConstraint.getType().toString().equals(Constraint.Type.PRIMARY_KEY.toString())) {
							isPrimary= true;
						}
						
					}
					if(dbColumn.getTypeNameSQL().contains("INTEGER"))
					{
						manualQuery=manualQuery+dbColumn.getName()+ " "+dbColumn.getTypeNameSQL();
					}else if(dbColumn.getTypeNameSQL().contains("VARCHAR"))
					{
						manualQuery=manualQuery+dbColumn.getName()+ " "+dbColumn.getTypeNameSQL()+"(10)";
					}else if(dbColumn.getTypeNameSQL().contains("VARBINARY"))
						{
						manualQuery=manualQuery+dbColumn.getName()+ " "+dbColumn.getTypeNameSQL()+"(10)";
						}
					else 
					{
						manualQuery=manualQuery+dbColumn.getName()+ " "+dbColumn.getTypeNameSQL();
						//getSqlTypeName(dbColumn.getTypeNameSQL());
						
						
					}
					if(isPrimary)
					{
						manualQuery=manualQuery+" PRIMARY KEY";	
					}
					if( iterator.hasNext())
					{
						manualQuery=manualQuery+" , ";	
					}
				}
				manualQuery=manualQuery+" )";
				System.out.println(manualQuery);
				try {
				jdbcTemplate.execute(manualQuery);
				}catch(Exception e)
				{
				log.info(e.getMessage());	
				}
			}
		try{List<Map<String,Object>> data=	jdbcTemplate.queryForList("select * from "+tableName.getName());
		 
			System.out.println(data);}catch(Exception e) {log.info(e.getMessage());}
			//sqlException.printStackTrace();
		}
		log.info("\n=======The '" + tableName.getName() + "' Successfully Created In The Database=======\n");
	}
	public static int getSqlTypeName(String type) {
	     
	    if("BIT".contentEquals(type))
	    	{ return Types.BIT;
	    	}
	        
	    
	    	if("TINYINT".contentEquals(type))
	    	{
	    		return Types.TINYINT;
	    	}
	    
	    	if("SMALLINT".contentEquals(type))
	    	{
	    		return Types.SMALLINT;
	    	}
	   
	    	if("INTEGER".contentEquals(type))
	    	{
	    		 return Types.INTEGER;
	    	}
	    
	    	if("BIGINT".contentEquals(type))
	    	{
	    		return Types.BIGINT;
	    	}
	    
	    	if("FLOAT".contentEquals(type))
	    	{
	    		return Types.FLOAT;
	    	}
	    
	    	if("REAL".contentEquals(type))
	    	{
	    		return Types.REAL;
	    	}
	    
	    	if("DOUBLE".contentEquals(type))
	    	{
	    		return Types.DOUBLE;
	    	}
	   
	    	if("NUMERIC".contentEquals(type))
	    	{
	    		 return Types.NUMERIC;
	    	}
	  
	    	if("DECIMAL".contentEquals(type))
	    	{
	    		  return Types.DECIMAL;
	    	}
	    
	    	if("CHAR".contentEquals(type))
	    	{
	    		return Types.CHAR;
	    	}
	   
	    	if("VARCHAR".contentEquals(type))
	    	{
	    		 return Types.VARCHAR;
	    	}
	    
	    	if("LONGVARCHAR".contentEquals(type))
	    	{
	    		return Types.LONGVARCHAR;
	    	}
	   
	    	if("DATE".contentEquals(type))
	    	{
	    		 return Types.DATE;
	    	}
	    
	    	if("TIME".contentEquals(type))
	    	{
	    		return Types.TIME;
	    	}
	   
	    	if("TIMESTAMP".contentEquals(type))
	    	{
	    		 return Types.TIMESTAMP;
	    	}
	    
	    	if("BINARY".contentEquals(type))
	    	{
	    		return Types.BINARY;
	    	}
	   
	    	if("VARBINARY".contentEquals(type))
	    	{
	    		 return Types.VARBINARY;
	    	}
	    
	    	if("LONGVARBINARY".contentEquals(type))
	    	{
	    		return Types.LONGVARBINARY;
	    	}
	   
	    	if("NULL".contentEquals(type))
	    	{
	    		 return Types.NULL;
	    	}
	   
	    	if("OTHER".contentEquals(type))
	    	{
	    		 return Types.OTHER;
	    	}
	   
	    	if("JAVA_OBJECT".contentEquals(type))
	    	{
	    		 return Types.JAVA_OBJECT;
	    	}
	  
	    	if("DISTINCT".contentEquals(type))
	    	{
	    		  return Types.DISTINCT;
	    	}
	   
	    	if("STRUCT".contentEquals(type))
	    	{
	    		 return Types.STRUCT;
	    	}
	   
	    	if( "ARRAY".contentEquals(type))
	    	{
	    		 return Types.ARRAY;
	    	}
	    
	    	if( "BLOB".contentEquals(type))
	    	{
	    		return Types.BLOB;
	    	}
	    
	    	if( "CLOB".contentEquals(type))
	    	{
	    		return Types.CLOB;
	    	}
	  
	    	if( "REF".contentEquals(type))
	    	{
	    		  return Types.REF;
	    	}
	    
	    	if( "DATALINK".contentEquals(type))
	    	{
	    		return Types.DATALINK;
	    	}
	    
	    	if( "BOOLEAN".contentEquals(type))
	    	{
	    		return Types.BOOLEAN;
	    	}
	   
	    	if( "ROWID".contentEquals(type))
	    	{
	    		 return Types.ROWID;
	    	}
	    
	    	if( "NCHAR".contentEquals(type))
	    	{
	    		return Types.NCHAR;
	    	}
	    
	    	if( "NVARCHAR".contentEquals(type))
	    	{
	    		return Types.NVARCHAR;
	    	}
	   
	    	if( "LONGNVARCHAR".contentEquals(type))
	    	{
	    		 return Types.LONGNVARCHAR;
	    	}
	  
	    	if( "NCLOB".contentEquals(type))
	    	{
	    		  return Types.NCLOB;
	    	}
	   
	    	if( "SQLXML".contentEquals(type))
	    	{
	    		 return Types.SQLXML;
	    }

	    return 0;
	}
	public static String getSqlTypeName(int type) {
	    switch (type) {
	    case Types.BIT:
	        return "BIT";
	    case Types.TINYINT:
	        return "TINYINT";
	    case Types.SMALLINT:
	        return "SMALLINT";
	    case Types.INTEGER:
	        return "INTEGER";
	    case Types.BIGINT:
	        return "BIGINT";
	    case Types.FLOAT:
	        return "FLOAT";
	    case Types.REAL:
	        return "REAL";
	    case Types.DOUBLE:
	        return "DOUBLE";
	    case Types.NUMERIC:
	        return "NUMERIC";
	    case Types.DECIMAL:
	        return "DECIMAL";
	    case Types.CHAR:
	        return "CHAR";
	    case Types.VARCHAR:
	        return "VARCHAR";
	    case Types.LONGVARCHAR:
	        return "LONGVARCHAR";
	    case Types.DATE:
	        return "DATE";
	    case Types.TIME:
	        return "TIME";
	    case Types.TIMESTAMP:
	        return "TIMESTAMP";
	    case Types.BINARY:
	        return "BINARY";
	    case Types.VARBINARY:
	        return "VARBINARY";
	    case Types.LONGVARBINARY:
	        return "LONGVARBINARY";
	    case Types.NULL:
	        return "NULL";
	    case Types.OTHER:
	        return "OTHER";
	    case Types.JAVA_OBJECT:
	        return "JAVA_OBJECT";
	    case Types.DISTINCT:
	        return "DISTINCT";
	    case Types.STRUCT:
	        return "STRUCT";
	    case Types.ARRAY:
	        return "ARRAY";
	    case Types.BLOB:
	        return "BLOB";
	    case Types.CLOB:
	        return "CLOB";
	    case Types.REF:
	        return "REF";
	    case Types.DATALINK:
	        return "DATALINK";
	    case Types.BOOLEAN:
	        return "BOOLEAN";
	    case Types.ROWID:
	        return "ROWID";
	    case Types.NCHAR:
	        return "NCHAR";
	    case Types.NVARCHAR:
	        return "NVARCHAR";
	    case Types.LONGNVARCHAR:
	        return "LONGNVARCHAR";
	    case Types.NCLOB:
	        return "NCLOB";
	    case Types.SQLXML:
	        return "SQLXML";
	    }

	    return "?";
	}
	public static String getSampleData(String type) {
	     
	    if("BIT".contentEquals(type))
	    	{ return "0";
	    	}
	        
	    
	    	if("TINYINT".contentEquals(type))
	    	{
	    		return "0";
	    	}
	    
	    	if("SMALLINT".contentEquals(type))
	    	{
	    		return "0";
	    	}
	   
	    	if("INTEGER".contentEquals(type))
	    	{
	    		 return "9";
	    	}
	    
	    	if("BIGINT".contentEquals(type))
	    	{
	    		return "100";
	    	}
	    
	    	if("FLOAT".contentEquals(type))
	    	{
	    		return "1.1";
	    	}
	    
	    	if("REAL".contentEquals(type))
	    	{
	    		return "1";
	    	}
	    
	    	if("DOUBLE".contentEquals(type))
	    	{
	    		return "1.1";
	    	}
	   
	    	if("NUMERIC".contentEquals(type))
	    	{
	    		 return "111";
	    	}
	  
	    	if("DECIMAL".contentEquals(type))
	    	{
	    		  return "11";
	    	}
	    
	    	if("CHAR".contentEquals(type))
	    	{
	    		return "C";
	    	}
	   
	    	if("VARCHAR".contentEquals(type))
	    	{
	    		 return "test";
	    	}
	    
	    	if("LONGVARCHAR".contentEquals(type))
	    	{
	    		return "test";
	    	}
	   
	    	if("DATE".contentEquals(type))
	    	{
	    		 return new java.sql.Date(1988, 17, 1).toString();
	    	}
	    
	    	if("TIME".contentEquals(type))
	    	{
	    		return new java.sql.Time(10, 17, 1).toString();
	    	}
	   
	    	if("TIMESTAMP".contentEquals(type))
	    	{
	    		
	    		return "2016-4-05 13 −45 −21";
	    		// return new java.sql.Timestamp(2020,01,01,10, 17, 1,1).toString();
	    	}
	    
	    	if("BINARY".contentEquals(type))
	    	{
	    		return "0xABCDEF";
	    	}
	   
	    	if("VARBINARY".contentEquals(type))
	    	{
	    		 return "0xABCDEF";
	    	}
	    
	    	if("LONGVARBINARY".contentEquals(type))
	    	{
	    		return "0xABCDEF";
	    	}
	   
	    	if("NULL".contentEquals(type))
	    	{
	    		 return "null";
	    	}
	   
	    	if("OTHER".contentEquals(type))
	    	{
	    		 return "";
	    	}
	   
	    	if("JAVA_OBJECT".contentEquals(type))
	    	{
	    		 return "";
	    	}
	  
	    	if("DISTINCT".contentEquals(type))
	    	{
	    		  return "";
	    	}
	   
	    	if("STRUCT".contentEquals(type))
	    	{
	    		 return "";
	    	}
	   
	    	if( "ARRAY".contentEquals(type))
	    	{
	    		 return "";
	    	}
	    
	    	if( "BLOB".contentEquals(type))
	    	{
	    		return "";
	    	}
	    
	    	if( "CLOB".contentEquals(type))
	    	{
	    		return "";
	    	}
	  
	    	if( "REF".contentEquals(type))
	    	{
	    		  return "";
	    	}
	    
	    	if( "DATALINK".contentEquals(type))
	    	{
	    		return "";
	    	}
	    
	    	if( "BOOLEAN".contentEquals(type))
	    	{
	    		return "true";
	    	}
	   
	    	if( "ROWID".contentEquals(type))
	    	{
	    		 return "";
	    	}
	    
	    	if( "NCHAR".contentEquals(type))
	    	{
	    		return "a";
	    	}
	    
	    	if( "NVARCHAR".contentEquals(type))
	    	{
	    		return "s";
	    	}
	   
	    	if( "LONGNVARCHAR".contentEquals(type))
	    	{
	    		 return "s";
	    	}
	  
	    	if( "NCLOB".contentEquals(type))
	    	{
	    		  return "";
	    	}
	   
	    	if( "SQLXML".contentEquals(type))
	    	{
	    		 return "";
	    }

	    return "";
	}
	@Test
	public void executeQuery()
	{
		List<Map<String,Object>> data=jdbcTemplate.queryForList("show tables ");
		for (Iterator iterator = data.iterator(); iterator.hasNext();) {
			Map<String, Object> map = (Map<String, Object>) iterator.next();
			System.out.println(map);
			String tablename=(String) map.get("Tables_in_cameldb");
			System.out.println(tablename);
			try {
				if(!tablename.isEmpty())
				mvc.perform(MockMvcRequestBuilders.get("/myApps/" + tablename.toLowerCase().replace("_", " ") + "/getdata")
						.contentType(MediaType.APPLICATION_JSON).param("tableName", "TBL_STUDENT"))
						.andDo(MockMvcResultHandlers.print());
				 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	@Test
	public void testJdbcconn() throws Exception
	{
		System.out.println(jdbcTemplate.queryForList("show tables "));
		
	}
}
