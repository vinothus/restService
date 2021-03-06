package com.vin.rest.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
//import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.dbspec.Constraint;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbConstraint;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class MysqlTableGeneration {
	static DbSchema schemaObj;
	static DbSpec specficationObj;

	private void loadSQLBuilderSchema() {
		specficationObj = new DbSpec();

		schemaObj = specficationObj.addDefaultSchema();
	}
	
	 @Autowired 
	    JdbcTemplate jdbcTemplate;
	    
	    
	    @Autowired
	    Environment env;
	    
		Logger log = Logger.getLogger(MysqlTableGeneration.class.getName());
	   // @Test
		public void executeQuery()
		{
	    	String[] tableName = new String[100];
			try {
				String[] nonscaling = {"LONGVARCHAR","NCHAR","NVARCHAR","NCLOB","CLOB","LONGVARBINARY","VARBINARY","LONGNVARCHAR",
						/* "LONGVARBINARY","LONGVARCHAR","NCHAR","LONGNVARCHAR","NVARCHAR","NCLOB", "BLOB","CLOB",*/"NULL","OTHER","JAVA_OBJECT","ARRAY", "DISTINCT", "STRUCT", "REF", "DATALINK", "ROWID", "SQLXML", "?" };
				List<String> invalidElement = new ArrayList<String>();
				for (int i = 0; i < nonscaling.length; i++) {
					invalidElement.add(nonscaling[i]);
				}
				
				int[] sqlTypes = getSqlTypes();
				// initilize table names
				
				loadSQLBuilderSchema();
				for (int i = 0; i < tableName.length; i++) {
					tableName[i] ="Test"+ generateRandomString();
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
					insertDbTable(randomTable,10000);
				}
				rest(tableName);
			 
			 
			 }catch(Exception e) {
				 
			 }finally {
				 
             //dropTable(tableName);
			 }
	    	
			 
		 
			
		}
	    
	    private void rest(String[] tableName) throws Exception {

for (int i = 0; i < tableName.length; i++) {
	String tablename = tableName[i];
	log.info("http://localhost:8080/myApps/"+tablename.toLowerCase()+"/getdata");
	  URL oracle = new URL("http://localhost:8080/myApps/"+tablename.toLowerCase()+"/getdata");
      URLConnection yc = oracle.openConnection();
      BufferedReader in = new BufferedReader(new InputStreamReader(
                              yc.getInputStream()));
      String inputLine;
      String data="";
      while ((inputLine = in.readLine()) != null) {
          System.out.println(inputLine);
          data=data+inputLine;
          
      }
      in.close();
      
      ObjectMapper mapper = new ObjectMapper();

		List<Map<String, Object>> jsonMap = new ArrayList<Map<String, Object>>();
		jsonMap = mapper.readValue(data, new TypeReference<List<Map<String, Object>>>() {
		});
		Map postDataMap=jsonMap.get(0);
		String putStr=new ObjectMapper().writeValueAsString(postDataMap);
		String id=Integer.toString( (int)postDataMap.get("id"));
		postDataMap.remove("id");
		String postStr=new ObjectMapper().writeValueAsString(postDataMap);
		getData("http://localhost:8080/myApps/"+tablename.toLowerCase()+"/getdataForKey/"+id,tablename);
		postData("http://localhost:8080/myApps/"+tablename.toLowerCase()+"/addData", postStr,tablename);
		putData("http://localhost:8080/myApps/"+tablename.toLowerCase()+"/updateData", putStr,tablename);
		delData("http://localhost:8080/myApps/"+tablename.toLowerCase()+"/deleteData/"+id);
}

	    }
	    
	    
	    private void getData(String url,String tablename) throws IOException
	    {
	    	URL geturl = new URL(url);
	        URLConnection yc = geturl.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(
	                                yc.getInputStream()));
	        String inputLine;
	        while ((inputLine = in.readLine()) != null) 
	            System.out.println(inputLine);
	        in.close();
	        	
	    	
	    }
	private void postData(String url,String dataToPost,String tablename) throws Exception
	{
		URL postUrl = new URL (url);
		String dataval=generateInsertData(tablename,"insert");
		HttpURLConnection con = (HttpURLConnection)postUrl.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json; utf-8");
		con.setRequestProperty("Accept", "application/json");
		con.setDoOutput(true);
		try(OutputStream os = con.getOutputStream()) {
		    byte[] input = dataval.getBytes("utf-8");
		    os.write(input, 0, input.length);			
		}
	String message=	con.getResponseMessage();
	log.info(message);

	}
		private void delData(String url) throws IOException
		{
			URL postUrl = new URL (url);
			HttpURLConnection con = (HttpURLConnection)postUrl.openConnection();
			con.setRequestMethod("DELETE");
			con.setRequestProperty("Content-Type", "application/json; utf-8");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);
		 
		
			String message=	con.getResponseMessage();
			log.info(message);
	 
		}
	private void putData(String url,String dataToPost,String tablename) throws Exception
	{
		URL postUrl = new URL (url);
		String dataval=generateInsertData(tablename,"");
		HttpURLConnection con = (HttpURLConnection)postUrl.openConnection();
		con.setRequestMethod("PUT");
		con.setRequestProperty("Content-Type", "application/json; utf-8");
		con.setRequestProperty("Accept", "application/json");
		con.setDoOutput(true);
		try(OutputStream os = con.getOutputStream()) {
		    byte[] input = dataval.getBytes("utf-8");
		    os.write(input, 0, input.length);			
		}
		
		
		String message=	con.getResponseMessage();
		log.info(message);
	 
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
						}else if(dbColumn.getTypeNameSQL().equals("VARCHAR"))
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
		
		public static int getSqlTypeName(String type) {
		     
		    if("BIT".equalsIgnoreCase(type))
		    	{ return Types.BIT;
		    	}
		        
		    
		    	if("TINYINT".equalsIgnoreCase(type))
		    	{
		    		return Types.TINYINT;
		    	}
		    
		    	if("SMALLINT".equalsIgnoreCase(type))
		    	{
		    		return Types.SMALLINT;
		    	}
		   
		    	if("INTEGER".equalsIgnoreCase(type))
		    	{
		    		 return Types.INTEGER;
		    	}
		    
		    	if("BIGINT".equalsIgnoreCase(type))
		    	{
		    		return Types.BIGINT;
		    	}
		    
		    	if("FLOAT".equalsIgnoreCase(type))
		    	{
		    		return Types.FLOAT;
		    	}
		    
		    	if("REAL".equalsIgnoreCase(type))
		    	{
		    		return Types.REAL;
		    	}
		    
		    	if("DOUBLE".equalsIgnoreCase(type))
		    	{
		    		return Types.DOUBLE;
		    	}
		   
		    	if("NUMERIC".equalsIgnoreCase(type))
		    	{
		    		 return Types.NUMERIC;
		    	}
		  
		    	if("DECIMAL".equalsIgnoreCase(type))
		    	{
		    		  return Types.DECIMAL;
		    	}
		    
		    	if("CHAR".equalsIgnoreCase(type))
		    	{
		    		return Types.CHAR;
		    	}
		   
		    	if("VARCHAR".equalsIgnoreCase(type))
		    	{
		    		 return Types.VARCHAR;
		    	}
		    
		    	if("LONGVARCHAR".equalsIgnoreCase(type))
		    	{
		    		return Types.LONGVARCHAR;
		    	}
		   
		    	if("DATE".equalsIgnoreCase(type))
		    	{
		    		 return Types.DATE;
		    	}
		    
		    	if("TIME".equalsIgnoreCase(type))
		    	{
		    		return Types.TIME;
		    	}
		   
		    	if("TIMESTAMP".equalsIgnoreCase(type))
		    	{
		    		 return Types.TIMESTAMP;
		    	}
		    
		    	if("BINARY".equalsIgnoreCase(type))
		    	{
		    		return Types.BINARY;
		    	}
		   
		    	if("VARBINARY".equalsIgnoreCase(type))
		    	{
		    		 return Types.VARBINARY;
		    	}
		    
		    	if("LONGVARBINARY".equalsIgnoreCase(type))
		    	{
		    		return Types.LONGVARBINARY;
		    	}
		   
		    	if("NULL".equalsIgnoreCase(type))
		    	{
		    		 return Types.NULL;
		    	}
		   
		    	if("OTHER".equalsIgnoreCase(type))
		    	{
		    		 return Types.OTHER;
		    	}
		   
		    	if("JAVA_OBJECT".equalsIgnoreCase(type))
		    	{
		    		 return Types.JAVA_OBJECT;
		    	}
		  
		    	if("DISTINCT".equalsIgnoreCase(type))
		    	{
		    		  return Types.DISTINCT;
		    	}
		   
		    	if("STRUCT".equalsIgnoreCase(type))
		    	{
		    		 return Types.STRUCT;
		    	}
		   
		    	if( "ARRAY".equalsIgnoreCase(type))
		    	{
		    		 return Types.ARRAY;
		    	}
		    
		    	if( "BLOB".equalsIgnoreCase(type))
		    	{
		    		return Types.BLOB;
		    	}
		    
		    	if( "CLOB".equalsIgnoreCase(type))
		    	{
		    		return Types.CLOB;
		    	}
		  
		    	if( "REF".equalsIgnoreCase(type))
		    	{
		    		  return Types.REF;
		    	}
		    
		    	if( "DATALINK".equalsIgnoreCase(type))
		    	{
		    		return Types.DATALINK;
		    	}
		    
		    	if( "BOOLEAN".equalsIgnoreCase(type))
		    	{
		    		return Types.BOOLEAN;
		    	}
		   
		    	if( "ROWID".equalsIgnoreCase(type))
		    	{
		    		 return Types.ROWID;
		    	}
		    
		    	if( "NCHAR".equalsIgnoreCase(type))
		    	{
		    		return Types.NCHAR;
		    	}
		    
		    	if( "NVARCHAR".equalsIgnoreCase(type))
		    	{
		    		return Types.NVARCHAR;
		    	}
		   
		    	if( "LONGNVARCHAR".equalsIgnoreCase(type))
		    	{
		    		 return Types.LONGNVARCHAR;
		    	}
		  
		    	if( "NCLOB".equalsIgnoreCase(type))
		    	{
		    		  return Types.NCLOB;
		    	}
		   
		    	if( "SQLXML".equalsIgnoreCase(type))
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
					if (dbColumn.getTypeNameSQL().contains("BINARY")||dbColumn.getTypeNameSQL().contains("BOOLEAN")) {
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
		public static String getSampleData(String type) {
		     
		    if("BIT".equalsIgnoreCase(type))
		    	{ return "0";
		    	}
		    if("INT".equalsIgnoreCase(type))
	    	{ return "0";
	    	}
		    
		    	if("TINYINT".equalsIgnoreCase(type))
		    	{
		    		return "0";
		    	}
		    
		    	if("SMALLINT".equalsIgnoreCase(type))
		    	{
		    		return "0";
		    	}
		   
		    	if("INTEGER".equalsIgnoreCase(type))
		    	{
		    		 return "9";
		    	}
		    
		    	if("BIGINT".equalsIgnoreCase(type))
		    	{
		    		return "100";
		    	}
		    
		    	if("FLOAT".equalsIgnoreCase(type))
		    	{
		    		return "1.1";
		    	}
		    
		    	if("REAL".equalsIgnoreCase(type))
		    	{
		    		return "1";
		    	}
		    
		    	if("DOUBLE".equalsIgnoreCase(type))
		    	{
		    		return "1.1";
		    	}
		   
		    	if("NUMERIC".equalsIgnoreCase(type))
		    	{
		    		 return "111";
		    	}
		  
		    	if("DECIMAL".equalsIgnoreCase(type))
		    	{
		    		  return "11";
		    	}
		    
		    	if("CHAR".equalsIgnoreCase(type))
		    	{
		    		return "C";
		    	}
		   
		    	if("VARCHAR".equalsIgnoreCase(type))
		    	{
		    		 return "test";
		    	}
		    
		    	if("LONGVARCHAR".equalsIgnoreCase(type))
		    	{
		    		return "test";
		    	}
		   
		    	if("DATE".equalsIgnoreCase(type))
		    	{
		    		 return new java.sql.Date(1988, 17, 1).toString();
		    	}
		    
		    	if("TIME".equalsIgnoreCase(type))
		    	{
		    		return new java.sql.Time(10, 17, 1).toString();
		    	}
		   
		    	if("TIMESTAMP".equalsIgnoreCase(type))
		    	{
		    		
		    		return "2020-09-17 12:47:07";
		    		// return new java.sql.Timestamp(2020,01,01,10, 17, 1,1).toString();
		    	}
		    
		    	if("BINARY".equalsIgnoreCase(type))
		    	{
		    		return "1";
		    	}
		   
		    	if("VARBINARY".equalsIgnoreCase(type))
		    	{
		    		 return "0xABCDEF";
		    	}
		    
		    	if("LONGVARBINARY".equalsIgnoreCase(type))
		    	{
		    		return "0xABCDEF";
		    	}
		   
		    	if("NULL".equalsIgnoreCase(type))
		    	{
		    		 return "null";
		    	}
		   
		    	if("OTHER".equalsIgnoreCase(type))
		    	{
		    		 return "";
		    	}
		   
		    	if("JAVA_OBJECT".equalsIgnoreCase(type))
		    	{
		    		 return "";
		    	}
		  
		    	if("DISTINCT".equalsIgnoreCase(type))
		    	{
		    		  return "";
		    	}
		   
		    	if("STRUCT".equalsIgnoreCase(type))
		    	{
		    		 return "";
		    	}
		   
		    	if( "ARRAY".equalsIgnoreCase(type))
		    	{
		    		 return "";
		    	}
		    
		    	if( "BLOB".equalsIgnoreCase(type))
		    	{
		    		return "DE";
		    	}
		    
		    	if( "CLOB".equalsIgnoreCase(type))
		    	{
		    		return "";
		    	}
		  
		    	if( "REF".equalsIgnoreCase(type))
		    	{
		    		  return "";
		    	}
		    
		    	if( "DATALINK".contentEquals(type))
		    	{
		    		return "";
		    	}
		    
		    	if( "BOOLEAN".contentEquals(type))
		    	{
		    		return "1";
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
		
		public String generateInsertData(String tableName,String update) throws Exception
		{
			
			
			
			
			List<Map<String,Object>>	colums=jdbcTemplate.queryForList("desc  "+tableName);
			
			 Map<String, String> dataInsert=new HashMap<String, String>();
			for (Iterator<Map<String, Object>> iterator2 = colums.iterator(); iterator2.hasNext();) {
				Map<String, Object> coluMaps = (Map<String, Object>) iterator2.next();
				coluMaps.get("Field");
				coluMaps.get("Type");
				coluMaps.get("Key");
				
			String dataType=	getSqlTypeWithoutScal((String)coluMaps.get("Type"));
			String key=(String)coluMaps.get("Field");
			String dataSampleValue=getSampleData(dataType);
			if(!dataType.equalsIgnoreCase("BIT"))
			{
				dataInsert.put(key.toLowerCase(), dataSampleValue);
				}
				
			}
			if(update.equals("insert")) {
			dataInsert.remove("id");
			String id=  findMax(tableName);
			dataInsert.put("id", id);
			}
			
			String putStr=new ObjectMapper().writeValueAsString(dataInsert);
			
			
			return putStr;
		}

		private String getSqlTypeWithoutScal(String string) {

			char[] lengthData=string.toCharArray();
			String returnStr="";
			for (int i = 0; i < lengthData.length; i++) {
				char chardata= lengthData[i];
				if (chardata >= '0' && chardata <= '9')
				{
					returnStr=returnStr+chardata;
					string=string.replace(chardata+"", "");
				}
			}
			
			 
			
			 
			
			return string.replace("(", "").replace(")", "").replace(returnStr, "").replace(",","");
		
			
		}
		//@Test
		public void setData()
		{
			log.info(getSqlTypeWithoutScal("Decimal(10,0)"));
			
		}
		private String findMax(String table) {
			String query = "SELECT MAX( id )+1 as id FROM " + table;
			Map<String, Object> data = new HashMap<String, Object>();
			try {
				data = jdbcTemplate.queryForMap(query);
			} catch (Exception e) {
				log.info(e.getMessage());
			}

			String attrID = null;
			if (data.get("ID") != null) {
				try {attrID = Integer.toString((int) data.get("ID"));
				}catch(Exception e) {
					
					try{attrID = Long.toString((Long) data.get("ID"));	}catch(Exception es) {
						BigDecimal datas =(BigDecimal) data.get("ID");
						attrID=datas.toString();
						
					}
				}
			}
			if(attrID==null)
			{attrID="0";}
			return attrID;

		}
}
