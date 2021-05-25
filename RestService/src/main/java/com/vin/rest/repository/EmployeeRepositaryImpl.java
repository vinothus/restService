package com.vin.rest.repository;

import static com.vin.validation.ParamsValidator.dsidMap;
import static com.vin.validation.ParamsValidator.UserApiMap;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthmarketscience.sqlbuilder.BetweenCondition;
import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.DeleteQuery;
import com.healthmarketscience.sqlbuilder.InCondition;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.OrderObject;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.UpdateQuery;
import com.healthmarketscience.sqlbuilder.dbspec.Constraint;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbConstraint;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import com.vin.rest.dynamic.MultiServiceImpl;
import com.vin.rest.exception.DatabaseAuthException;
import com.vin.rest.exception.RecordNotFoundException;
import com.vin.rest.exception.ServiceNotFoundException;
import com.vin.rest.model.EmployeeEntity;
import com.vin.validation.ParamsValidator;
import com.vin.validation.ServiceConstraintViolation;

import vin.rest.common.Constant;

@Component
public class EmployeeRepositaryImpl {

	Logger log = Logger.getLogger(EmployeeRepositaryImpl.class.getName());
	@Autowired
	private Environment env;
	//@Autowired
	//JdbcTemplate jdbcTemplate;
	static DbSchema schemaObj;
	static DbSpec specficationObj;
	public static Map<String, JdbcTemplate> jdbcTemplateMap= new ConcurrentHashMap<>();
	public static Map<String,Map<String, String>> userServiceTableMap= new ConcurrentHashMap<>();
	public static Map<String,Map<DbTable, List<DbColumn>>> userTableColumnMap= new ConcurrentHashMap<>();
	static Map<String,Map<String, Map<String, String>>> userServiceAttrbMap= new ConcurrentHashMap<>();
	String tableName;
	public synchronized String getTableName() {
		return tableName;
	}

	public synchronized void setTableName(String tableName) {
		this.tableName = tableName;
	}
	//public static Map<String, String> serviceTableMap= new ConcurrentHashMap<>();
	//public static Map<DbTable, List<DbColumn>> tableColumnMap= new ConcurrentHashMap<>();
	//static Map<String, Map<String, String>> serviceAttrbMap= new ConcurrentHashMap<>();
    String serviceNTFEx="Service not found Exception";
    
	String[] nonscaling = {"NCLOB","BLOB","CLOB","NULL","OTHER","JAVA_OBJECT","ARRAY", "DISTINCT", "STRUCT", "REF", "DATALINK", "ROWID", "SQLXML", "?" };
	List<String> invalidElement = new ArrayList<String>();
	
	public void init() {

		try {
			//tableColumnMap = getMetaDatum();
			initGoldenTables("SYSTEM","SYSTEM","none");

			getServiceTableMap("SYSTEM","SYSTEM");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void initGoldenTables(String apiKey, String dataStoreKey,String passToken) throws DatabaseAuthException {
		List<DbTable> serviceTables = initializeTable();
		boolean serviceTabisPresent = false;
		for (Iterator<DbTable> iterator = serviceTables.iterator(); iterator.hasNext();) {
			DbTable dbTable = iterator.next();
			for (Map.Entry<String,Map<DbTable, List<DbColumn>>> entryUsr : userTableColumnMap.entrySet()) {
				String userApiKey=entryUsr.getKey();
				Map<DbTable, List<DbColumn>> tableColumnMapUsr=entryUsr.getValue();
			for (Map.Entry<DbTable, List<DbColumn>> entry : tableColumnMapUsr.entrySet()) {
				DbTable table = entry.getKey();

				if (dbTable.getName().equalsIgnoreCase(table.getName())) {
					serviceTabisPresent = true;
					log.info("Table Already Present: ");
				}
			}
			}

		}
		if (!serviceTabisPresent) {
			createSysTable() ;

		}
		userTableColumnMap=getMetaDatumUsr( apiKey,  dataStoreKey,passToken);
		//tableColumnMap = getMetaDatum( apiKey,  dataStoreKey,passToken);
		insertServiceTables( apiKey,  dataStoreKey);
		insertServiceTablesUsr( apiKey,  dataStoreKey);

	}

 

 

	private void insertServiceTablesUsr(String apiKey, String dataStoreKey) throws  DatabaseAuthException {
		String userId=getUidForapiKey( apiKey);
		String dsId =null;
		if (dsidMap.get(dataStoreKey + ":" + apiKey) == null) {
			dsId = getdsidFordsName(dataStoreKey);
			dsidMap.put(dataStoreKey + ":" + apiKey, dsId);
		} else {
			dsId = dsidMap.get(dataStoreKey + ":" + apiKey);
		}
		for (Map.Entry<String,Map<DbTable,List<DbColumn>>> entryUsr : userTableColumnMap.entrySet()) {
             String userApi= entryUsr.getKey();
             Map<DbTable,List<DbColumn>> tablecolumnMap=entryUsr.getValue();
		for (Map.Entry<DbTable, List<DbColumn>> entry : tablecolumnMap.entrySet()) {
			DbTable table = entry.getKey();
			String tableName = table.getName();

			String serviceInsertQuery = "INSERT INTO   Service (id ,tableName , serviceName, uid , dsid )   values( (SELECT MAX( id )+1 FROM Service ser) , '"
					+ tableName + "', '" + tableName.toLowerCase().replace("_", " ") + "' , '"+userId+"' , '"+dsId+"' )";
			String serviceid = getServiceID(tableName, apiKey,  dataStoreKey,userId,dsId);
			String maxRec = findMax("Service", apiKey,  "system","none");
			if (maxRec != null && serviceid == null) {
				setUserDataStore(apiKey, "system","none").execute(serviceInsertQuery);
				serviceid = getServiceID(tableName, apiKey,  dataStoreKey,userId,dsId);
			} else if (maxRec == null) {
				serviceInsertQuery = "INSERT INTO   Service (id ,tableName , serviceName , uid , dsid )   values( 0, '" + tableName
						+ "', '" + tableName.toLowerCase().replace("_", " ") + "' , '"+userId+"' , '"+dsId+"'  )";
				setUserDataStore(apiKey, "system","none").execute(serviceInsertQuery);
				serviceid = getServiceID(tableName, apiKey,  dataStoreKey,userId,dsId);
			}

			List<DbColumn> column = entry.getValue();
			 List<Map<String, Object>> attrbData=getServiceAttrIDByServiceID(serviceid, apiKey, dataStoreKey);
			 String maxRecAttr = findMax("Service_Attr", apiKey,  "system","none");
			 List<String> quries=new ArrayList<String>();
			for (Iterator<DbColumn> iterator = column.iterator(); iterator.hasNext();) {
				DbColumn dbColumn = iterator.next();
				String serviceAttrid = getServiceAttrID(serviceid, dbColumn.getName(), apiKey,  dataStoreKey);
				if (maxRecAttr != null && serviceAttrid == null) {
					String serviceAttrQuery = " INSERT INTO Service_Attr (id, service_id , attrName, colName ,colType) values ((SELECT MAX( id )+1 FROM Service_Attr serA) ,'"
							+ serviceid + "','" + dbColumn.getName().toLowerCase().replace("_", "") + "','"
							+ dbColumn.getName() + "' ,'"+dbColumn.getTypeNameSQL()+"') ";
					quries.add(serviceAttrQuery);
					//setUserDataStore(apiKey, "system","none").execute(serviceAttrQuery);
				} else if (maxRecAttr == null) {

					{
						String serviceAttrQuery = " INSERT INTO Service_Attr (id, service_id , attrName, colName,colType) values (0 ,'"
								+ serviceid + "','" + dbColumn.getName().toLowerCase().replace("_", "") + "','"
								+ dbColumn.getName() + "' ,'"+dbColumn.getTypeNameSQL()+"') ";
						//quries.add(serviceAttrQuery);
						setUserDataStore(apiKey, "system","none").execute(serviceAttrQuery);
						maxRecAttr = findMax("Service_Attr", apiKey,  "system","none");
					}

				}
			}
			if (quries.size() > 0) {
				setUserDataStore(apiKey, "system", "none").batchUpdate(quries.toArray(new String[quries.size()]));
			}
		}
		}

	
		
	}

	private Map<String, Map<DbTable, List<DbColumn>>> getMetaDatumUsr(String apiKey, String dataStoreKey,
			String passToken) throws  DatabaseAuthException{
		loadSQLBuilderSchema();
		Map<String,Map<DbTable, List<DbColumn>>> metaDatum = new HashMap<>();
		Map<DbTable, List<DbColumn>> metaDatumUsr = new HashMap<>();
		DatabaseMetaData md;
		DatabaseMetaData mdsys = null;
		ResultSet rs ,rs3 ;
		 
		try {
			//user Database
			md=setUserDataStore(apiKey, dataStoreKey, passToken).execute(new ConnectionCallback<DatabaseMetaData>() {

				@Override
				public DatabaseMetaData doInConnection(Connection con) throws SQLException, DataAccessException {
					// TODO Auto-generated method stub
					return con.getMetaData();
				}
			});
			mdsys=setUserDataStore(apiKey, "system", passToken).execute(new ConnectionCallback<DatabaseMetaData>() {

				@Override
				public DatabaseMetaData doInConnection(Connection con) throws SQLException, DataAccessException {
					// TODO Auto-generated method stub
					return con.getMetaData();
				}
			});
			
			if(md.getURL().contains("mysql"))
			{
				//user Database  and system database
			List<Map<String,Object>>	tableresult=setUserDataStore(apiKey, dataStoreKey, passToken).queryForList("show tables");
			if (!dataStoreKey.equalsIgnoreCase("system")) {
				if (setUserDataStore(apiKey, "system", passToken).execute(new ConnectionCallback<DatabaseMetaData>() {

					@Override
					public DatabaseMetaData doInConnection(Connection con) throws SQLException, DataAccessException {
						// TODO Auto-generated method stub
						return con.getMetaData();
					}
				}).getURL().contains("mysql")) {// system metadata table  setup
					tableresult.addAll(setUserDataStore(apiKey, "system","none").queryForList("show tables"));
				}

			}
				for (Iterator iterator = tableresult.iterator(); iterator.hasNext();) {
					Map<String, Object> map = (Map<String, Object>) iterator.next();
					System.out.println(map);
					for (Entry<String, Object> entry : map.entrySet())  
					{   System.out.println("Key = " + entry.getKey() + 
			                             ", Value = " + entry.getValue()); 
					DbTable tableNameT = schemaObj.addTable(entry.getValue().toString());
					List<DbColumn> listArray = new ArrayList<>();
					 
					List<Map<String,Object>>	colums=setUserDataStore(apiKey, dataStoreKey, passToken).queryForList("desc "+entry.getValue());
					
					if (!dataStoreKey.equalsIgnoreCase("system")) {
						if (setUserDataStore(apiKey, "system", passToken).execute(new ConnectionCallback<DatabaseMetaData>() {

							@Override
							public DatabaseMetaData doInConnection(Connection con) throws SQLException, DataAccessException {
								// TODO Auto-generated method stub
								return con.getMetaData();
							}
						}).getURL().contains("mysql")) {// system metadata column setup
							colums.addAll(setUserDataStore(apiKey, "system","none").queryForList("desc "+entry.getValue()));
						}

					}
					
					
					for (Iterator iterator2 = colums.iterator(); iterator2.hasNext();) {
						Map<String, Object> coluMaps = (Map<String, Object>) iterator2.next();
						coluMaps.get("Field");
						coluMaps.get("Type");
						coluMaps.get("Key");
						 
						DbColumn column1 = tableNameT.addColumn((String)coluMaps.get("Field"),
								getSqlTypeWithoutScal((String)coluMaps.get("Type"))	, getLength((String)coluMaps.get("Type")));
						column1.setDefaultValue(getSqlTypeName((String)coluMaps.get("Type")));
						if(coluMaps.get("Key").equals("PRI"))
							{
							column1.primaryKey();
							}
						listArray.add(column1);
						 
					}
					metaDatumUsr.put(tableNameT, listArray);
			    } 
				} 	 
			} 
			else {	 
			//dataSource.getConnection().setCatalog("cameldb");
		    rs = md.getTables(null, null, "%", new String[] { "TABLE",
				"VIEW"/*
						 * , "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"
						 */ });
		    rs3 = mdsys.getTables(null, null, "%", new String[] { "TABLE",
					"VIEW"/*
							 * , "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"
							 */ });
		    
		while (rs.next()) {

			DbTable tableNameT = schemaObj.addTable(rs.getString("TABLE_NAME"));
			String tableName = rs.getString("TABLE_NAME");
			log.info(tableName);
			if(tableName.contains("/")||tableName.contains("$"))
			{
				continue;
			}
			SelectQuery selectQuery = new SelectQuery();
			List<DbColumn> listArray = new ArrayList<>();
			ResultSet rs1 = md.getColumns(null, null,tableName, null);
			ResultSet rs2 = md.getPrimaryKeys(null, null, tableName);
			String primaryKey = "";
			while (rs2.next()) {
				primaryKey = rs2.getString("COLUMN_NAME");
			}
			rs2.close();

			while (rs1.next()) {
				DbColumn column1 = null ;
				try {
				 column1 = tableNameT.addColumn(rs1.getString("COLUMN_NAME"),
						Integer.parseInt(rs1.getString("DATA_TYPE")), Integer.parseInt(rs1.getString("COLUMN_SIZE")));
				}catch(Exception e)
				{
					 column1 = tableNameT.addColumn(rs1.getString("COLUMN_NAME"),
								4, Integer.parseInt(rs1.getString("COLUMN_SIZE")));
				}

				if (rs1.getString("COLUMN_NAME").equals(primaryKey)) {
					column1.primaryKey();
				}
				listArray.add(column1);
				log.info(rs1.getString("DATA_TYPE"));
				log.info(rs1.getString("COLUMN_NAME"));
				log.info(rs1.getString("COLUMN_SIZE"));

			}
			rs1.close();
			log.info(selectQuery.addAllTableColumns(tableNameT).validate().toString());
			metaDatumUsr.put(tableNameT, listArray);
			
		}
		
		while (rs3.next()&&!dataStoreKey.equalsIgnoreCase("system")) {

			DbTable tableNameT = schemaObj.addTable(rs3.getString("TABLE_NAME"));
			String tableName = rs3.getString("TABLE_NAME");
			log.info(tableName);
			if(tableName.contains("/")||tableName.contains("$"))
			{
				continue;
			}
			SelectQuery selectQuery = new SelectQuery();
			List<DbColumn> listArray = new ArrayList<>();
			ResultSet rs1 = md.getColumns(null, null,tableName, null);
			ResultSet rs2 = md.getPrimaryKeys(null, null, tableName);
			String primaryKey = "";
			while (rs2.next()) {
				primaryKey = rs2.getString("COLUMN_NAME");
			}
			rs2.close();

			while (rs1.next()) {
				DbColumn column1 = null ;
				try {
				 column1 = tableNameT.addColumn(rs1.getString("COLUMN_NAME"),
						Integer.parseInt(rs1.getString("DATA_TYPE")), Integer.parseInt(rs1.getString("COLUMN_SIZE")));
				}catch(Exception e)
				{
					 column1 = tableNameT.addColumn(rs1.getString("COLUMN_NAME"),
								4, Integer.parseInt(rs1.getString("COLUMN_SIZE")));
				}

				if (rs1.getString("COLUMN_NAME").equals(primaryKey)) {
					column1.primaryKey();
				}
				listArray.add(column1);
				log.info(rs1.getString("DATA_TYPE"));
				log.info(rs1.getString("COLUMN_NAME"));
				log.info(rs1.getString("COLUMN_SIZE"));

			}
			rs1.close();
			log.info(selectQuery.addAllTableColumns(tableNameT).validate().toString());
			metaDatumUsr.put(tableNameT, listArray);
			
		}
		rs3.close();
		rs.close();
			
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		catch (DatabaseAuthException e) {
			 throw e;
				}
		catch(Exception e)
		{
			
		}finally {
			
		}
		metaDatum.put(dataStoreKey, metaDatumUsr);
		return metaDatum;
	}

	private  void loadSQLBuilderSchema() {
		
		if (specficationObj == null) {
			specficationObj = new DbSpec();
		}

		schemaObj = specficationObj.addDefaultSchema();
	}



	public Map<DbTable, List<DbColumn>> getMetaDatum(String apiKey, String dataStoreKey,String passToken) throws  DatabaseAuthException {
		loadSQLBuilderSchema();
		Map<DbTable, List<DbColumn>> metaDatum = new HashMap<>();
		DatabaseMetaData md;
		DatabaseMetaData mdsys = null;
		ResultSet rs ,rs3 ;
		 
		try {
			//user Database
			md=setUserDataStore(apiKey, dataStoreKey, passToken).execute(new ConnectionCallback<DatabaseMetaData>() {

				@Override
				public DatabaseMetaData doInConnection(Connection con) throws SQLException, DataAccessException {
					// TODO Auto-generated method stub
					return con.getMetaData();
				}
			});
			mdsys=setUserDataStore(apiKey, "system", passToken).execute(new ConnectionCallback<DatabaseMetaData>() {

				@Override
				public DatabaseMetaData doInConnection(Connection con) throws SQLException, DataAccessException {
					// TODO Auto-generated method stub
					return con.getMetaData();
				}
			});
			
			if(md.getURL().contains("mysql"))
			{
				//user Database  and system database
			List<Map<String,Object>>	tableresult=setUserDataStore(apiKey, dataStoreKey, passToken).queryForList("show tables");
			if (!dataStoreKey.equalsIgnoreCase("system")) {
				if (setUserDataStore(apiKey, "system", passToken).execute(new ConnectionCallback<DatabaseMetaData>() {

					@Override
					public DatabaseMetaData doInConnection(Connection con) throws SQLException, DataAccessException {
						// TODO Auto-generated method stub
						return con.getMetaData();
					}
				}).getURL().contains("mysql")) {// system metadata table  setup
					tableresult.addAll(setUserDataStore(apiKey, "system","none").queryForList("show tables"));
				}

			}
				for (Iterator iterator = tableresult.iterator(); iterator.hasNext();) {
					Map<String, Object> map = (Map<String, Object>) iterator.next();
					System.out.println(map);
					for (Entry<String, Object> entry : map.entrySet())  
					{   System.out.println("Key = " + entry.getKey() + 
			                             ", Value = " + entry.getValue()); 
					DbTable tableNameT = schemaObj.addTable(entry.getValue().toString());
					List<DbColumn> listArray = new ArrayList<>();
					 
					List<Map<String,Object>>	colums=setUserDataStore(apiKey, dataStoreKey, passToken).queryForList("desc "+entry.getValue());
					
					if (!dataStoreKey.equalsIgnoreCase("system")) {
						if (setUserDataStore(apiKey, "system", passToken).execute(new ConnectionCallback<DatabaseMetaData>() {

							@Override
							public DatabaseMetaData doInConnection(Connection con) throws SQLException, DataAccessException {
								// TODO Auto-generated method stub
								return con.getMetaData();
							}
						}).getURL().contains("mysql")) {// system metadata column setup
							colums.addAll(setUserDataStore(apiKey, "system","none").queryForList("desc "+entry.getValue()));
						}

					}
					
					
					for (Iterator iterator2 = colums.iterator(); iterator2.hasNext();) {
						Map<String, Object> coluMaps = (Map<String, Object>) iterator2.next();
						coluMaps.get("Field");
						coluMaps.get("Type");
						coluMaps.get("Key");
						 
						DbColumn column1 = tableNameT.addColumn((String)coluMaps.get("Field"),
								getSqlTypeWithoutScal((String)coluMaps.get("Type"))	, getLength((String)coluMaps.get("Type")));
						column1.setDefaultValue(getSqlTypeName((String)coluMaps.get("Type")));
						if(coluMaps.get("Key").equals("PRI"))
							{
							column1.primaryKey();
							}
						listArray.add(column1);
						 
					}
					metaDatum.put(tableNameT, listArray);
			    } 
				} 	 
			} 
			else {	 
			//dataSource.getConnection().setCatalog("cameldb");
		    rs = md.getTables(null, null, "%", new String[] { "TABLE",
				"VIEW"/*
						 * , "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"
						 */ });
		    rs3 = mdsys.getTables(null, null, "%", new String[] { "TABLE",
					"VIEW"/*
							 * , "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"
							 */ });
		    
		while (rs.next()) {

			DbTable tableNameT = schemaObj.addTable(rs.getString("TABLE_NAME"));
			String tableName = rs.getString("TABLE_NAME");
			log.info(tableName);
			if(tableName.contains("/")||tableName.contains("$"))
			{
				continue;
			}
			SelectQuery selectQuery = new SelectQuery();
			List<DbColumn> listArray = new ArrayList<>();
			ResultSet rs1 = md.getColumns(null, null,tableName, null);
			ResultSet rs2 = md.getPrimaryKeys(null, null, tableName);
			String primaryKey = "";
			while (rs2.next()) {
				primaryKey = rs2.getString("COLUMN_NAME");
			}
			rs2.close();

			while (rs1.next()) {
				DbColumn column1 = null ;
				try {
				 column1 = tableNameT.addColumn(rs1.getString("COLUMN_NAME"),
						Integer.parseInt(rs1.getString("DATA_TYPE")), Integer.parseInt(rs1.getString("COLUMN_SIZE")));
				}catch(Exception e)
				{
					 column1 = tableNameT.addColumn(rs1.getString("COLUMN_NAME"),
								4, Integer.parseInt(rs1.getString("COLUMN_SIZE")));
				}

				if (rs1.getString("COLUMN_NAME").equals(primaryKey)) {
					column1.primaryKey();
				}
				listArray.add(column1);
				log.info(rs1.getString("DATA_TYPE"));
				log.info(rs1.getString("COLUMN_NAME"));
				log.info(rs1.getString("COLUMN_SIZE"));

			}
			rs1.close();
			log.info(selectQuery.addAllTableColumns(tableNameT).validate().toString());
			metaDatum.put(tableNameT, listArray);
			
		}
		
		while (rs3.next()&&!dataStoreKey.equalsIgnoreCase("system")) {

			DbTable tableNameT = schemaObj.addTable(rs3.getString("TABLE_NAME"));
			String tableName = rs3.getString("TABLE_NAME");
			log.info(tableName);
			if(tableName.contains("/")||tableName.contains("$"))
			{
				continue;
			}
			SelectQuery selectQuery = new SelectQuery();
			List<DbColumn> listArray = new ArrayList<>();
			ResultSet rs1 = md.getColumns(null, null,tableName, null);
			ResultSet rs2 = md.getPrimaryKeys(null, null, tableName);
			String primaryKey = "";
			while (rs2.next()) {
				primaryKey = rs2.getString("COLUMN_NAME");
			}
			rs2.close();

			while (rs1.next()) {
				DbColumn column1 = null ;
				try {
				 column1 = tableNameT.addColumn(rs1.getString("COLUMN_NAME"),
						Integer.parseInt(rs1.getString("DATA_TYPE")), Integer.parseInt(rs1.getString("COLUMN_SIZE")));
				}catch(Exception e)
				{
					 column1 = tableNameT.addColumn(rs1.getString("COLUMN_NAME"),
								4, Integer.parseInt(rs1.getString("COLUMN_SIZE")));
				}

				if (rs1.getString("COLUMN_NAME").equals(primaryKey)) {
					column1.primaryKey();
				}
				listArray.add(column1);
				log.info(rs1.getString("DATA_TYPE"));
				log.info(rs1.getString("COLUMN_NAME"));
				log.info(rs1.getString("COLUMN_SIZE"));

			}
			rs1.close();
			log.info(selectQuery.addAllTableColumns(tableNameT).validate().toString());
			metaDatum.put(tableNameT, listArray);
			
		}
		rs3.close();
		rs.close();
			
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		catch (DatabaseAuthException e) {
			 throw e;
				}
		catch(Exception e)
		{
			 
		}finally {
			
		}
		return metaDatum;
	}

	public void createDbTable(DbTable tableName,String apiKey, String dataStoreKey) throws  DatabaseAuthException{
		log.info("\n=======Creating '" + tableName.getName() + "' In The Database=======\n");
		loadSQLBuilderSchema();
		try {

			String createTableQuery = new CreateTableQuery(tableName, true).validate().toString();
			log.info("\nGenerated Sql Query?= " + createTableQuery + "\n");
			// system Database
			setUserDataStore(apiKey, "system","none").execute(createTableQuery);
			insertGoldenDataifAny(tableName,apiKey);
		}  catch (Exception sqlException) {
			if(sqlException  instanceof DatabaseAuthException)
			{
				throw sqlException;
			}
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
						manualQuery=manualQuery+dbColumn.getName()+ " "+dbColumn.getTypeNameSQL()+"("+dbColumn.getTypeLength()+")";
					}else
					{
						manualQuery=manualQuery+dbColumn.getName()+ " "+dbColumn.getTypeNameSQL();	
						
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
					// system database
					setUserDataStore(apiKey, "system","none").execute(manualQuery);
					insertGoldenDataifAny(tableName,apiKey);
				}catch(Exception e)
				{
				log.info(e.getMessage());
				
				if(e  instanceof DatabaseAuthException)
				{
					throw e;
				}
				}
			}
			
			sqlException.printStackTrace();
		}
		log.info("\n=======The '" + tableName.getName() + "' Successfully Created In The Database=======\n");
	}

	private void insertGoldenDataifAny(DbTable tableName,String apiKey) throws  DatabaseAuthException{
	 if(tableName.getName().equalsIgnoreCase("User"))
	 {
		 setUserDataStore(apiKey, "system","none").execute("INSERT INTO User (id, name, apikey, password, phoneno, email, address) VALUES " + 
		 		"(0, 'systemuser', 'system', 'vinaug@2020', '+919790524267', 'Vinoth.Paulraj@vinrest.com', 'Plot no 35,1st Street,XXX Nagar,XXXX') ;");
	 }
	 else if(tableName.getName().equalsIgnoreCase("Datastore"))
	 {
		 setUserDataStore(apiKey, "system","none").execute("INSERT INTO Datastore (id, uid, type, name, url, driver) VALUES " + 
		 		"(0, 0, 'type', 'system', 'url', 'driver') ;"); 
	 }
	 else if(tableName.getName().equalsIgnoreCase("VinProcessor"))
	 {
		 setUserDataStore(apiKey, "system","none").execute("INSERT INTO  VinProcessor (id,uid,name,classname)  VALUES " + 
		 		"(0,0,'yekeulav','com.vin.processor.PropertyProcessor');"); 
	 }
	 else if(tableName.getName().equalsIgnoreCase("VinValidation"))
	 {
		 setUserDataStore(apiKey, "system","none").execute("INSERT INTO  VinValidation (id,uid,name,classname,attr_id)  VALUES " + 
		 		"(0,0,'rotadilavssalc','com.vin.validatiorr.ClassValidator',999);"); 
	 }
		else if (tableName.getName().equalsIgnoreCase("Service")) {
			String serviceattr = getTableName("service attr", "system", "system", "none");
			if (serviceattr == null) {
				createDbTable(createServiceAttrTableObj(), "system", "system");
			}
			try {
				insertServiceTables("Service", "system", "system");
				setServiceTableMap("Service", "system", "system");
				setTableColumn("Service", "system", "system", "none");
				insertServiceTables("Service_Attr", "system", "system");
				setServiceTableMap("Service_Attr", "system", "system");
				setTableColumn("Service_Attr", "system", "system", "none");
				// service consumption
				insertServiceTables("Service_Consumption", "system", "system");
				setServiceTableMap("Service_Consumption", "system", "system");
				setTableColumn("Service_Consumption", "system", "system", "none");
				// service Error 
				insertServiceTables("Service_Error", "system", "system");
				setServiceTableMap("Service_Error", "system", "system");
				setTableColumn("Service_Error", "system", "system", "none");
				
			} catch (DatabaseAuthException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (tableName.getName().equalsIgnoreCase("Service_Attr")) {
			String service = getTableName("service", "system", "system", "none");
			if (service == null) {
				createDbTable(createServiceTableObj(), "system", "system");
			}
			try {
				insertServiceTables("Service", "system", "system");
				setServiceTableMap("Service", "system", "system");
				setTableColumn("Servicer", "system", "system", "none");
				insertServiceTables("Service_Attr", "system", "system");
				setServiceTableMap("Service_Attr", "system", "system");
				setTableColumn("Service_Attr", "system", "system", "none");
				// service consumption
				insertServiceTables("Service_Consumption", "system", "system");
				setServiceTableMap("Service_Consumption", "system", "system");
				setTableColumn("Service_Consumption", "system", "system", "none");
				// service Error 
				insertServiceTables("Service_Error", "system", "system");
				setServiceTableMap("Service_Error", "system", "system");
				setTableColumn("Service_Error", "system", "system", "none");
				
			} catch (DatabaseAuthException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	 else if(tableName.getName().equalsIgnoreCase("Multi_Service")) {
			
			try {
				String serviceattr = getTableName("service attr", "system", "system", "none");
				if (serviceattr == null) {
					createDbTable(createServiceAttrTableObj(), "system", "system");
				}
				String service = getTableName("service", "system", "system", "none");
				if (service == null) {
					createDbTable(createServiceTableObj(), "system", "system");
				}
				String serId = getServiceID("Service", "system", "system", "0", "0");
				if (serId == null) {
					insertServiceTables("Service", "system", "system");
					setServiceTableMap("Service", "system", "system");
					setTableColumn("Service", "system", "system", "none");
					serId = getServiceID("Service", "system", "system", "0", "0");
				}if(serId!=null) {
					serviceAttrbMap(serId, "Service", apiKey, "system");
				}
				String attrId = getServiceID("Service_Attr", "system", "system", "0", "0");
				if (attrId == null) {
					insertServiceTables("Service_Attr", "system", "system");
					setServiceTableMap("Service_Attr", "system", "system");
					setTableColumn("Service_Attr", "system", "system", "none");
					attrId = getServiceID("Service_Attr", "system", "system", "0", "0");
				}if(attrId!=null) {
					
					serviceAttrbMap(attrId, "Service_Attr", apiKey, "system");
				}
				 setUserDataStore(apiKey, "system","none").execute("INSERT INTO  Multi_Service (id,uid,service_id,priority,type,relationwithparam,multiserviceBussinessName,multiservicename,multiserviceEnable)  VALUES " + 
					 		"(0,0,'"+serId+"','0','Single','id.id,tablename.tablename,servicename.servicename,"+Constant.IN_CONDITION+"id."+Constant.IN_CONDITION+"id','"+env.getProperty("multiserviceBussinessName")+"','"+env.getProperty("multiservicename")+"','1');");
				 setUserDataStore(apiKey, "system","none").execute("INSERT INTO  Multi_Service (id,uid,service_id,priority,type,relationwithparam,multiserviceBussinessName,multiservicename,multiserviceEnable)  VALUES " + 
					 		"(1,0,'"+attrId+"','1','Single','service.id.serviceid','"+env.getProperty("multiserviceBussinessName")+"','"+env.getProperty("multiservicename")+"','1');");
				clearCache();
			} catch (DatabaseAuthException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 }
	 else if(tableName.getName().equalsIgnoreCase("Service_Consumption")) {
		 
		 setUserDataStore(apiKey, "system","none").execute(" INSERT INTO  Service_Consumption (id) values(1) ");
		 try {
			insertServiceTables("Service_Consumption", "system", "system");
			setServiceTableMap("Service_Consumption", "system", "system");
			setTableColumn("Service_Consumption", "system", "system", "none");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	 }
	 else if(tableName.getName().equalsIgnoreCase("Service_Error")) {
		 setUserDataStore(apiKey, "system","none").execute(" INSERT INTO   Service_Error (id) values(1) ");
		 
		 try {
				insertServiceTables("Service_Error", "system", "system");
				setServiceTableMap("Service_Error", "system", "system");
				setTableColumn("Service_Error", "system", "system", "none");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		 
	 }
	}

	public List<DbTable> initializeTable() {

		loadSQLBuilderSchema();
		
		
		// Multi_Service
		DbColumn multiseviceid;
		DbColumn multiseviceUid;
		DbColumn multisevice_id;
		DbColumn multiseviceName;
		DbColumn multisevicePriority;
		DbColumn multiseviceType;
		DbColumn multiseviceRelationwithParam;
		DbColumn sevicePriority;
		DbColumn multiserviceBussinessName;
		DbColumn multiserviceEnable;
		// User

		DbColumn userid;
		DbColumn userName;
		DbColumn userApiKey;
		DbColumn userPassword;
		DbColumn userPhoneNo;
		DbColumn userEmail;
		DbColumn userAddress;
		DbColumn activatedFlag;

		// DataStore

		DbColumn dataStoreId;
		DbColumn dataStoreUserId;
		DbColumn dataStoreType;
		DbColumn dataStoreName;
		DbColumn datastoreUrl;
		DbColumn datastoreDriver;

		// Subscription
		DbColumn subscriptionId;
		DbColumn subscriptionUserId;
		DbColumn subscriptionApiKey;
		DbColumn subscriptionExpireDate;
		DbColumn subscriptionType;

		// ServiceConsumption

		DbColumn ServiceConsumptionid;
		DbColumn ServiceConsumptionUserId;
		DbColumn ServiceConsumptionUrl;
		DbColumn ServiceConsumptionType;
		DbColumn ServiceConsumptionMethod;
		DbColumn ServiceConsumptionDate;
		DbColumn ServiceConsumptionTime;
		DbColumn ServiceRemoteHost;
		DbColumn ServiceExeDuration;

		// ServiceError
		DbColumn ServiceErrorid;
		DbColumn ServiceErrorUserId;
		DbColumn ServiceErrorUrl;
		DbColumn ServiceErrorType;
		DbColumn ServiceErrorMethod;
		DbColumn ServiceErrorMsg;
		DbColumn ServiceErrorDate;
		DbColumn ServiceErrorTime;
		DbColumn ServiceRmhost;
		DbColumn ServiceErrorDuration;
		
        // VinValidation
		
		DbColumn VinValidationid;
		DbColumn VinValidationuid;
		DbColumn VinValidationserid;
		DbColumn VinValidationattrid;
		DbColumn VinValidationName;
		DbColumn VinValidationClassName;
		DbColumn VinValidationParamClassName;
		
		// VinProcessor
		DbColumn VinProcessorid;
		DbColumn VinProcessoruid;
		DbColumn VinProcessorserid;
		DbColumn VinProcessorattrid;
		DbColumn VinProcessorName;
		DbColumn VinProcessorClassName;
		DbColumn VinProcessorParamClassName;
		
		
		
		DbTable tableService = createServiceTableObj();
		DbTable tableServiceAttr = createServiceAttrTableObj();
		DbTable tableMultiService = schemaObj.addTable("Multi_Service");
		DbTable tableUser = schemaObj.addTable("User");
		DbTable tableDataStore = schemaObj.addTable("Datastore");
		DbTable tableSubscription = schemaObj.addTable("Subscription");
		DbTable tableServiceConsumption = schemaObj.addTable("Service_Consumption");
		DbTable tableServiceError = schemaObj.addTable("Service_Error");
		DbTable tableVinValidation = schemaObj.addTable("VinValidation");
		DbTable tableVinProcessor = schemaObj.addTable("VinProcessor");
		

		// Service

	
		

		// Multi_Service

		multiseviceid = tableMultiService.addColumn("id", Types.INTEGER, 10);
		multiseviceUid = tableMultiService.addColumn("uid", Types.INTEGER, 10);
		multiseviceid.primaryKey();
		multisevice_id = tableMultiService.addColumn("service_id", Types.VARCHAR, 100);
		multiseviceName = tableMultiService.addColumn("multiservicename", Types.VARCHAR, 100);
		multisevicePriority = tableMultiService.addColumn("priority", Types.INTEGER, 10);
		multiseviceType = tableMultiService.addColumn("type", Types.VARCHAR, 100);
		multiseviceRelationwithParam = tableMultiService.addColumn("relationwithparam", Types.VARCHAR, 100);
		multiserviceBussinessName= tableMultiService.addColumn("multiserviceBussinessName", Types.VARCHAR, 100);
		multiserviceEnable= tableMultiService.addColumn("multiserviceEnable", Types.VARCHAR, 10);
		// User

		userid = tableUser.addColumn("id", Types.INTEGER, 10);
		userid.primaryKey();
		userName = tableUser.addColumn("name", Types.VARCHAR, 100);
		userApiKey = tableUser.addColumn("apikey", Types.VARCHAR, 100);
		userPassword = tableUser.addColumn("password", Types.VARCHAR, 100);
		userPhoneNo = tableUser.addColumn("phoneno", Types.VARCHAR, 100);
		userEmail = tableUser.addColumn("email", Types.VARCHAR, 100);
		userAddress = tableUser.addColumn("address", Types.VARCHAR, 100);
		activatedFlag = tableUser.addColumn("activatedflag", Types.VARCHAR, 100);

		// DataStore
		dataStoreId = tableDataStore.addColumn("id", Types.INTEGER, 10);
		dataStoreId.primaryKey();
		dataStoreUserId = tableDataStore.addColumn("uid", Types.INTEGER, 10);
		dataStoreType = tableDataStore.addColumn("type", Types.VARCHAR, 100);
		dataStoreName = tableDataStore.addColumn("name", Types.VARCHAR, 100);
		datastoreUrl = tableDataStore.addColumn("url", Types.VARCHAR, 100);
		datastoreDriver = tableDataStore.addColumn("driver", Types.VARCHAR, 100);

		// Subscription

		subscriptionId = tableSubscription.addColumn("id", Types.INTEGER, 10);
		subscriptionId.primaryKey();
		subscriptionUserId = tableSubscription.addColumn("uid", Types.INTEGER, 10);
		subscriptionApiKey = tableSubscription.addColumn("apikey", Types.VARCHAR, 100);
		subscriptionExpireDate = tableSubscription.addColumn("date", Types.DATE, 10);
		
		subscriptionType = tableSubscription.addColumn("type", Types.VARCHAR, 100);

		// ServiceConsumption
		ServiceConsumptionid = tableServiceConsumption.addColumn("id", Types.INTEGER, 10);
		ServiceConsumptionid.primaryKey();
		ServiceConsumptionUserId = tableServiceConsumption.addColumn("uid", Types.INTEGER, 10);
		ServiceConsumptionUrl = tableServiceConsumption.addColumn("url", Types.VARCHAR, 100);
		ServiceConsumptionType = tableServiceConsumption.addColumn("type", Types.VARCHAR, 100);
		ServiceConsumptionMethod = tableServiceConsumption.addColumn("method", Types.VARCHAR, 100);
		ServiceConsumptionDate = tableServiceConsumption.addColumn("date", Types.DATE, 10);
		ServiceConsumptionTime = tableServiceConsumption.addColumn("time", Types.TIME, 10);
		ServiceRemoteHost= tableServiceConsumption.addColumn("rmhost", Types.VARCHAR, 100);
		ServiceExeDuration= tableServiceConsumption.addColumn("duration", Types.VARCHAR, 100);
		// ServiceError

		ServiceErrorid = tableServiceError.addColumn("id", Types.INTEGER, 10);
		ServiceErrorid.primaryKey();
		ServiceErrorUserId = tableServiceError.addColumn("uid", Types.INTEGER, 10);
		ServiceErrorUrl = tableServiceError.addColumn("url", Types.VARCHAR, 100);
		ServiceErrorType = tableServiceError.addColumn("type", Types.VARCHAR, 100);
		ServiceErrorMethod = tableServiceError.addColumn("method", Types.VARCHAR, 100);
		ServiceErrorMsg = tableServiceError.addColumn("errormsg", Types.VARCHAR, 100);
		ServiceErrorDate = tableServiceError.addColumn("date", Types.DATE, 100);
		ServiceErrorTime = tableServiceError.addColumn("time", Types.TIME, 100);
		ServiceRmhost= tableServiceError.addColumn("rmhost", Types.VARCHAR, 100);
		ServiceErrorDuration= tableServiceError.addColumn("duration", Types.VARCHAR, 100);

		// VinValidation
		
		 VinValidationid=tableVinValidation.addColumn("id", Types.INTEGER, 10);
		 VinValidationid.primaryKey();
		 VinValidationuid=tableVinValidation.addColumn("uid", Types.INTEGER, 10);
		 VinValidationserid=tableVinValidation.addColumn("service_id", Types.INTEGER, 10);
		 VinValidationattrid=tableVinValidation.addColumn("attr_id", Types.INTEGER, 10);
		 VinValidationName=tableVinValidation.addColumn("name", Types.VARCHAR, 100);
		 VinValidationClassName=tableVinValidation.addColumn("classname", Types.VARCHAR, 100);
		 VinValidationParamClassName=tableVinValidation.addColumn("paramclassname", Types.VARCHAR, 100);
		 
		
		// VinProcessor
		 
		VinProcessorid=tableVinProcessor.addColumn("id", Types.INTEGER, 10);
		VinProcessorid.primaryKey();
		VinProcessoruid=tableVinProcessor.addColumn("uid", Types.INTEGER, 10);
		VinProcessorserid=tableVinProcessor.addColumn("service_id", Types.INTEGER, 10);
		VinProcessorattrid=tableVinProcessor.addColumn("attr_id", Types.INTEGER, 10);
		VinProcessorName=tableVinProcessor.addColumn("name", Types.VARCHAR, 100);
		VinProcessorClassName=tableVinProcessor.addColumn("classname", Types.VARCHAR, 100);
		VinProcessorParamClassName=tableVinProcessor.addColumn("paramclassname", Types.VARCHAR, 100);
		 
		 
		
		List<DbTable> initialTable = new ArrayList<DbTable>();
		initialTable.add(tableService);
		initialTable.add(tableServiceAttr);
		initialTable.add(tableMultiService);
		initialTable.add(tableUser);
		initialTable.add(tableDataStore);
		initialTable.add(tableSubscription);
		initialTable.add(tableServiceConsumption);
		initialTable.add(tableServiceError);
		initialTable.add(tableVinValidation);
		initialTable.add(tableVinProcessor);
		
		return initialTable;
	}

	public  DbTable createServiceTableObj()
	{
		loadSQLBuilderSchema();
		// Service
		DbTable tableService = schemaObj.addTable("Service");
		DbColumn id;
		DbColumn tableName;
		DbColumn serviceName;
		DbColumn serUId;
		DbColumn serDSId;
		DbColumn serviceBussinessName;
		DbColumn addFlag;
		DbColumn updateFlag;
		DbColumn deleteFlag;
		DbColumn retriveFlag;
		DbColumn workFlowFlag;
		DbColumn addBuName;
		DbColumn upBuName;
		DbColumn delName;
		DbColumn serviceEnable;
		DbColumn serviceUIDesign;

		id = tableService.addColumn("id", Types.INTEGER, 10);
		id.primaryKey();
		tableName = tableService.addColumn("tableName", Types.VARCHAR, 100);
		serviceName = tableService.addColumn("serviceName", Types.VARCHAR, 100);
		serUId = tableService.addColumn("uid", Types.INTEGER, 10);
		serDSId = tableService.addColumn("dsid", Types.INTEGER, 10);
		serviceBussinessName = tableService.addColumn("serviceBussinessName", Types.VARCHAR, 100);
		addFlag = tableService.addColumn("addFlag", Types.VARCHAR, 10);
		updateFlag = tableService.addColumn("updateFlag", Types.VARCHAR, 10);
		deleteFlag = tableService.addColumn("deleteFlag", Types.VARCHAR, 10);
		retriveFlag = tableService.addColumn("retriveFlag", Types.VARCHAR, 10);
		workFlowFlag = tableService.addColumn("workFlowFlag", Types.VARCHAR, 100);
		addBuName = tableService.addColumn("addBuName", Types.VARCHAR, 100);
		upBuName = tableService.addColumn("upBuName", Types.VARCHAR, 100);
		delName = tableService.addColumn("delName", Types.VARCHAR, 100);
		serviceEnable = tableService.addColumn("serviceEnable", Types.VARCHAR, 10);
		serviceUIDesign = tableService.addColumn("serviceUIDesign", Types.VARCHAR, 100);

		return tableService;
	}
	public  DbTable createServiceAttrTableObj()
	{
		loadSQLBuilderSchema();
		// Service_Attr
		DbColumn attrid;
		DbColumn serid;
		DbColumn attrName;
		DbColumn colName;
		DbColumn colType;
		DbColumn attrEnable;
		DbColumn attrBuName;
		DbColumn attrBuIcon;
		DbColumn attrCusValidation;
		DbColumn attrminLength;
		DbColumn attrMaxLength;
		DbColumn attrRegXvalidation;
		DbColumn attrRegXvalidationMsg;
		DbColumn attrIsMandatory;
		DbColumn attrIsProcessor;
		DbColumn attrProcessorName;
		DbColumn attrValidatorName;
		DbColumn attrValidatorMsg;
		DbColumn attrValMethods;
		DbTable tableServiceAttr = schemaObj.addTable("Service_Attr");
		// Service_Attr

		attrid = tableServiceAttr.addColumn("id", Types.INTEGER, 10);
		attrid.primaryKey();
		serid = tableServiceAttr.addColumn("service_id", Types.INTEGER, 10);
		// serUId = tableDataStore.addColumn("uid", Types.INTEGER, 10);
		attrName = tableServiceAttr.addColumn("attrName", Types.VARCHAR, 100);
		colName = tableServiceAttr.addColumn("colName", Types.VARCHAR, 100);
		colType = tableServiceAttr.addColumn("colType", Types.VARCHAR, 100);
		attrEnable = tableServiceAttr.addColumn("attrEnable", Types.VARCHAR, 100);
		attrBuName = tableServiceAttr.addColumn("attrBuName", Types.VARCHAR, 10);
		attrBuIcon = tableServiceAttr.addColumn("attrBuIcon", Types.VARCHAR, 100);
		attrCusValidation = tableServiceAttr.addColumn("attrCusValidation", Types.VARCHAR, 100);
		attrminLength = tableServiceAttr.addColumn("attrminLength", Types.VARCHAR, 100);
		attrMaxLength = tableServiceAttr.addColumn("attrMaxLength", Types.VARCHAR, 100);
		attrRegXvalidation = tableServiceAttr.addColumn("attrRegXvalidation", Types.VARCHAR, 100);
		attrRegXvalidationMsg= tableServiceAttr.addColumn("attrRegXvalidationMsg", Types.VARCHAR, 100);
		attrIsMandatory = tableServiceAttr.addColumn("attrIsMandatory", Types.VARCHAR, 10);
		attrIsProcessor = tableServiceAttr.addColumn("attrIsProcessor", Types.VARCHAR, 10);
		attrProcessorName= tableServiceAttr.addColumn("attrProcessorName", Types.VARCHAR, 100);
		attrValidatorName= tableServiceAttr.addColumn("attrValidatorName", Types.VARCHAR, 100);
		attrValMethods= tableServiceAttr.addColumn("attrValMethods", Types.VARCHAR, 100);
		attrValidatorMsg=tableServiceAttr.addColumn("attrValidatorMsg", Types.VARCHAR, 100);
		return tableServiceAttr;
	}
	public Map<String, Object> insertData(String service, Map<String, String> params,String apiKey, String dataStoreKey,String passToken) throws Exception,DatabaseAuthException {
		loadSQLBuilderSchema();
		ObjectMapper mapper = new ObjectMapper();
		String tableName = null;
		if(userServiceTableMap.get(dataStoreKey)!=null)
		{ 
			tableName=userServiceTableMap.get(dataStoreKey).get(service);
			}
		//String tableName = serviceTableMap.get(service);
		int updatedata=0;
		setGDValues(service, tableName, apiKey,  dataStoreKey,passToken);
		tableName = userServiceTableMap.get(dataStoreKey).get(service);
		if (tableName == null) {
			throw new ServiceNotFoundException(serviceNTFEx);
		}
		InsertQuery insertQuery;
		String primaryKey = null;
		boolean isUpdate = false;
		//Map<String, String> attribParamMap = serviceAttrbMap.get(service);
		Map<String, String> attribParamMap = userServiceAttrbMap.get(dataStoreKey).get(service);
		if(attribParamMap==null) {
			setServiceTableMap(tableName, apiKey, "system");
			attribParamMap = userServiceAttrbMap.get(dataStoreKey).get(service);
		}
		for (Map.Entry<DbTable, List<DbColumn>> entry : userTableColumnMap.get(dataStoreKey).entrySet()) {
			DbTable table = entry.getKey();
			if (table.getName().equalsIgnoreCase(tableName)) {
				table = schemaObj.addTable(table.getName());
				insertQuery = new InsertQuery(table);
				List<DbColumn> column = entry.getValue();
				for (Iterator<DbColumn> iterator = column.iterator(); iterator.hasNext();) {
					DbColumn dbColumn = iterator.next();
					List<DbConstraint> dbConstr = dbColumn.getConstraints();
					boolean isPrimaryKey = false;
					for (Iterator<DbConstraint> iterator2 = dbConstr.iterator(); iterator2.hasNext();) {
						DbConstraint dbConstraint = iterator2.next();
						if (dbConstraint.getType().toString().equals(Constraint.Type.PRIMARY_KEY.toString())) {

							if ((params.get(attribParamMap.get(dbColumn.getName())) != null)) {
								isPrimaryKey = true;
								primaryKey =( mapper.writeValueAsString(params.get(attribParamMap.get(dbColumn.getName()))) );
								primaryKey=primaryKey.replace("\"", "");
								 Map<String, Object> data=getData(service, primaryKey, apiKey,  dataStoreKey,passToken) ;
								 String  valFMDB= String.valueOf(data.get(dbColumn.getName()));
								if (valFMDB!=null&&valFMDB.equals(primaryKey)) {
									isUpdate = true;
								}
							} else {

								primaryKey = findMax(table.getName(), apiKey,  dataStoreKey,passToken);
								if(primaryKey==null)
								{
									primaryKey="0";
								}
								params.put(attribParamMap.get(dbColumn.getName()), primaryKey);

							}
						}
					}

					if (params.get(attribParamMap.get(dbColumn.getName())) != null) {
						insertQuery.addColumn(dbColumn, params.get(attribParamMap.get(dbColumn.getName())));
					}
				}

				log.info("Key = " + entry.getKey() + ", Value = " + entry.getValue());
				log.info(insertQuery.toString());
				
				if (primaryKey != null) {

					if (!isUpdate) {
						if(dataStoreKey.equalsIgnoreCase("SYSTEM"))
						{
					 updatedata=setUserDataStore(apiKey, "system","none").update(insertQuery.toString());
						}else
						{
						JdbcTemplate jdbcTemplate=setUserDataStore(apiKey, dataStoreKey, passToken);
						updatedata=jdbcTemplate.update(insertQuery.toString());
						}
					} else {
						throw new org.springframework.dao.DuplicateKeyException("Duplicate Primary key Try Update");
						//updateData(service, params, apiKey,  dataStoreKey,passToken);
					}

				} else {

					throw new Exception("Primary key not found");
				}
				break;
			}

		}
		primaryKey = replaceDoubleQute(primaryKey);
		Map<String, Object>  datatrt=getData(service, primaryKey, apiKey,  dataStoreKey,passToken);
		if(datatrt==null||datatrt.isEmpty())
		{
			if(updatedata==1&&primaryKey.equals("0"))
			{datatrt=	getDataForParams(service, new HashMap<String, String>(), apiKey,  dataStoreKey, passToken).get(0);}
		}
		
		return datatrt;

	}

	
	private void testJDBC(JdbcTemplate userJdbcTemplate) throws  DatabaseAuthException{
		 
		List<Map<String,Object>> data=userJdbcTemplate.queryForList(" show tables ");
		System.out.println(data);
	}

	public JdbcTemplate setUserDataStore(String apiKey, String dataStoreKey,String passToken) throws  DatabaseAuthException{
		 JdbcTemplate userJdbcTemplate;
		 try {	
		if(jdbcTemplateMap.get(dataStoreKey)==null)
		{
			if(dataStoreKey.equalsIgnoreCase("SYSTEM"))
		{
				DriverManagerDataSource dataSource = new DriverManagerDataSource();
		    dataSource.setDriverClassName(env.getProperty("sys.spring.datasource.driver-class-name"));
		    dataSource.setUrl(env.getProperty("sys.spring.datasource.url"));
		    dataSource.setUsername(env.getProperty("sys.spring.datasource.username"));
		    dataSource.setPassword(env.getProperty("sys.spring.datasource.password"));
		     userJdbcTemplate=new JdbcTemplate();
		    userJdbcTemplate.setDataSource(dataSource);
		    jdbcTemplateMap.put(dataStoreKey, userJdbcTemplate);
		}
			else {
				JdbcTemplate JdbcTemplate = null;
				if (jdbcTemplateMap.get( "system") == null) {

					JdbcTemplate = setUserDataStore( apiKey, "system", passToken);

				} else {
					JdbcTemplate = jdbcTemplateMap.get( "system");
				}
				List<Map<String,Object>> DataStoreData=JdbcTemplate.queryForList("select dst.url as url ,dst.driver as driver from Datastore dst,  User us where dst.name = ? and dst.uid = us.id and us.apikey = ? " ,new Object[] { dataStoreKey ,apiKey});
				DriverManagerDataSource dataSource = new DriverManagerDataSource();
				   dataSource.setDriverClassName(DataStoreData.get(0).get("driver").toString());
				    dataSource.setUrl(DataStoreData.get(0).get("url").toString());
				    byte[] decodedBytes = Base64.getDecoder().decode(passToken);
				    String decodedString = new String(decodedBytes);
				    dataSource.setUsername(decodedString.split(":")[0]);
				    String passWord=null;
				    if(decodedString.split(":").length==2)
				    { dataSource.setPassword(decodedString.split(":")[1]);}
				    userJdbcTemplate=new JdbcTemplate();
				    userJdbcTemplate.setDataSource(dataSource);
				    if(userJdbcTemplate.getDataSource().getConnection().isValid(10))
				    {
				    	 jdbcTemplateMap.put(dataStoreKey, userJdbcTemplate);	
				    }
			}	
		
		}else
		{
			userJdbcTemplate=jdbcTemplateMap.get(dataStoreKey);
		}
	}catch(Exception e)
	{
		log.warning(e.getMessage());
		throw new DatabaseAuthException("Exception during database Authentication");
	}
		return userJdbcTemplate;
	}

	private void arrangeGoldenDataForTable(String tableName,String apiKey, String dataStoreKey,String passToken) throws DatabaseAuthException, Exception {
		loadSQLBuilderSchema();
		setTableColumn(tableName, apiKey,  dataStoreKey,passToken);
		
	}
	private void arrangeGoldenData(String service,String apiKey, String dataStoreKey,String passToken) throws DatabaseAuthException {
		loadSQLBuilderSchema();
		if(!isPresentinDB(service, apiKey,  dataStoreKey,passToken))
		{
			String tableName=service.replace(" ", "_");
		setTableColumn(tableName, apiKey,  dataStoreKey,passToken);
		}
	}

	 
	private boolean isPresentinDB(String service,String apiKey, String dataStoreKey,String passToken)  throws DatabaseAuthException{
        
		String selectQuery = " select tableName from Service where serviceName = '" + service + "' and uid = ( select id  from User where   apikey =  '"+apiKey+"' ) and dsid= (select id   from Datastore  where   name =  '"+dataStoreKey+"') ";
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		boolean isPresent=false;
		try {
			//system database
			data = setUserDataStore(apiKey, "system","none").queryForList(selectQuery);
		} catch (Exception e) {
			log.info(e.getMessage());
			createSysTable();
			return isPresent;
		}
		String tableName = null;
		if (data != null)
			if (data.size() != 0) {
				isPresent=true;
				if (data.get(0).get("tableName") != null) {
					try {
						tableName = (String) data.get(0).get("tableName");
						setTableColumn(tableName, apiKey,  dataStoreKey, passToken);
					}catch (DatabaseAuthException e) {
						 throw e;
							}
					catch (Exception e) { 
						
					}
				}
			}
		return isPresent;
	}

	public void createSysTable() throws DatabaseAuthException{
		List<DbTable> serviceTables = initializeTable();
		for (Iterator<DbTable> iterator = serviceTables.iterator(); iterator.hasNext();) {
			DbTable dbTable = iterator.next();
			if(!isTablePresent(dbTable.getName(), "system",  "system", "none"))
			createDbTable(dbTable,  "system",  "system");
		}
	}
	
	private boolean isPresentinDBOnly(String service,String apiKey, String dataStoreKey,String passToken) {

		String selectQuery = " show tables ";
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		boolean isPresent=false;
		try {
			data = setUserDataStore(apiKey, dataStoreKey, passToken).queryForList(selectQuery);
			for (Iterator iterator = data.iterator(); iterator.hasNext();) {
				Map<String, Object> rowMap = (Map<String, Object>) iterator.next();
				for (Map.Entry<String, Object> entry : rowMap.entrySet()) {

					if (String.valueOf(entry.getValue()).equalsIgnoreCase(service)) {
						return true;
					}
				}

			}
		} catch (Exception e) {
			if(e instanceof DatabaseAuthException)
			{
				throw e;
			}
			log.info(e.getMessage());
			return isPresent;
		}
		 
		return isPresent;
	}
	private String getTableName(String service,String apiKey, String dataStoreKey,String passToken) throws  DatabaseAuthException{

		String selectQuery = " show tables ";
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		JdbcTemplate jdbcTemp=setUserDataStore(apiKey, dataStoreKey, passToken);
		
		DriverManagerDataSource dsd=(DriverManagerDataSource) jdbcTemp.getDataSource();
		String url=dsd.getUrl();
		
		String isPresent=null;
		try {
			if(url!=null&&url.contains(":mysql:")) {
			data = setUserDataStore(apiKey, dataStoreKey, passToken).queryForList(selectQuery);
			for (Iterator iterator = data.iterator(); iterator.hasNext();) {
				Map<String, Object> rowMap = (Map<String, Object>) iterator.next();
				for (Map.Entry<String, Object> entry : rowMap.entrySet()) {

					if (String.valueOf(entry.getValue()).equalsIgnoreCase(service)) {
						return String.valueOf(entry.getValue());
					}
				}

			}
			data = setUserDataStore(apiKey, "system","none").queryForList(selectQuery);
			for (Iterator iterator = data.iterator(); iterator.hasNext();) {
				Map<String, Object> rowMap = (Map<String, Object>) iterator.next();
				for (Map.Entry<String, Object> entry : rowMap.entrySet()) {

					if (String.valueOf(entry.getValue()).equalsIgnoreCase(service)) {
						return String.valueOf(entry.getValue());
					}
				}

			}}else
			{
			String tableNameFMDB=	 jdbcTemp.execute(	new ConnectionCallback<String>() {

					@Override
					public String doInConnection(Connection con) throws SQLException, DataAccessException {
						
						ResultSet rs1= con.getMetaData().getTables(null, null, "%", new String[] { "TABLE",
								"VIEW"/*
										 * , "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"
										 */ });
						while(rs1.next())
						{
							String tableName = rs1.getString("TABLE_NAME");
							if(tableName.equalsIgnoreCase(service))
							{
								return tableName;
							}
						}
						
						return null;
					}
				});
			if(tableNameFMDB!=null&&tableNameFMDB.equalsIgnoreCase(service))
			{
				return tableNameFMDB;
			}
			
				
			}
		} catch (Exception e) {
			if(e instanceof DatabaseAuthException)
			{
				throw e;
			}
			log.info(e.getMessage());
			return isPresent;
		}
		 
		return isPresent;
	}
	
	public String refreshMataData(String serviceName,String apiKey, String dataStoreKey) throws RecordNotFoundException , DatabaseAuthException
	{
		List<Map<String, Object>> serviceDatum = setUserDataStore(apiKey, "system","none")
				.queryForList("select id, tableName, serviceName from Service where serviceName= '"+serviceName+"'");
		Map<String,String> serviceTableMap;
		if(!(serviceDatum.size()>0))
		{
			throw new RecordNotFoundException(serviceName +" not found ;");
		}
		String cacheTn=null;
		if(userServiceTableMap.get(dataStoreKey)==null) {
			serviceTableMap=new ConcurrentHashMap<String, String>();
		}else
		{
			serviceTableMap=userServiceTableMap.get(dataStoreKey);
			cacheTn=	userServiceTableMap.get(dataStoreKey).get(serviceName);
		}
		
		//serviceTableMap.get(serviceName);
		for (Iterator<Map<String, Object>> iterator = serviceDatum.iterator(); iterator.hasNext();) {
			Map<String, Object> map = (Map<String, Object>) iterator.next();
			String dbTn=(String) map.get("tableName");
			if(cacheTn==null)
			{
				serviceTableMap.put(serviceName,dbTn);	
			}
		}
		userServiceTableMap.put(dataStoreKey, serviceTableMap);
		String id=getID(serviceDatum.get(0),"id");
		serviceAttrbMap(id, serviceName, apiKey,  dataStoreKey);
		
		return id;
	}
	private String getID(Map<String, Object> data,String key) {
		String id = null;
		if (data.get(key) != null) {
			try {
				id = Integer.toString((int) data.get(key));
			} catch (Exception e) {

				try {
					id = Long.toString((Long) data.get(key));
				} catch (Exception es) {

					BigDecimal datas = (BigDecimal) data.get(key);
					id = datas.toString();

				}
			}
		}
		return id;
	}

	private void setTableColumn(String tableName,String apiKey, String dataStoreKey,String passToken) throws DatabaseAuthException {
		boolean isTablePresent=false;
		DatabaseMetaData md;
		DbTable tableNameT;
		DatabaseMetaData mdsys;
		List<DbColumn> listArray = new ArrayList<>();
		Map<DbTable, List<DbColumn>>  tableColumnMapusr=userTableColumnMap.get(dataStoreKey);//
		if(tableColumnMapusr==null) {
			tableColumnMapusr=new ConcurrentHashMap<>(); 
		}
		boolean isPresent=false;
		if(userTableColumnMap.get(dataStoreKey)!=null) {
		for (Map.Entry<DbTable, List<DbColumn>> entry : userTableColumnMap.get(dataStoreKey).entrySet()) {
			DbTable table = entry.getKey();
			if (table.getName().equalsIgnoreCase(tableName)) {
				isPresent=true;
			}
		}}
		try {
			if(!isPresent) {
				DriverManagerDataSource dmds=(DriverManagerDataSource) setUserDataStore(apiKey, dataStoreKey, passToken).getDataSource();
				String url=dmds.getUrl();
				if(url.contains("jdbc:h2:"))
				{

					setTableName(tableName);
					listArray.addAll(setUserDataStore(apiKey, dataStoreKey, "none")
							.execute(new ConnectionCallback<List<DbColumn>>() {

								@Override
								public List<DbColumn> doInConnection(Connection con)
										throws SQLException, DataAccessException { // TODO Auto-generated method stub
																					// return
									DatabaseMetaData md = con.getMetaData();
									List<DbColumn> inlistArray = new ArrayList<>();
									DbTable tableNameT;
									ResultSet rs1 = md.getColumns(null, null, getTableName().toUpperCase(), null);
									ResultSet rs2 = md.getPrimaryKeys(null, null, getTableName().toUpperCase());
									String primaryKey = "";
									tableNameT = schemaObj.addTable(getTableName());
									while (rs2.next()) {
										primaryKey = rs2.getString("COLUMN_NAME");
									}
									rs2.close();

									while (rs1.next()) {
										DbColumn column1 = null;
										try {

											column1 = tableNameT.addColumn(rs1.getString("COLUMN_NAME"),
													Integer.parseInt(rs1.getString("DATA_TYPE")),
													Integer.parseInt(rs1.getString("COLUMN_SIZE")));
											// isTablePresent=true;
										} catch (Exception e) {
											column1 = tableNameT.addColumn(rs1.getString("COLUMN_NAME"), 4,
													Integer.parseInt(rs1.getString("COLUMN_SIZE")));
										}

										if (rs1.getString("COLUMN_NAME").equals(primaryKey)) {
											column1.primaryKey();
										}
										inlistArray.add(column1);
										log.info(rs1.getString("DATA_TYPE"));
										log.info(rs1.getString("COLUMN_NAME"));
										log.info(rs1.getString("COLUMN_SIZE"));

									}
									rs1.close();
									if (inlistArray.size() > 0) {
										Map<DbTable, List<DbColumn>> tableColumnMapusr = userTableColumnMap
												.get(dataStoreKey);//
										if (tableColumnMapusr == null) {
											tableColumnMapusr = new ConcurrentHashMap<>();
										}
										tableColumnMapusr.put(tableNameT, inlistArray);

										if (userTableColumnMap.get(dataStoreKey) != null) {
											userTableColumnMap.get(dataStoreKey).putAll(tableColumnMapusr);
										} else {
											userTableColumnMap.put(dataStoreKey, tableColumnMapusr);
										}

									}

									return inlistArray;

								}
							}));

				}
				else if(url.contains("mysql"))
			{
				tableName=getTableName(tableName, apiKey,  dataStoreKey,passToken);
				 tableNameT = schemaObj.addTable(tableName);
				List<DbColumn> listArraycol = new ArrayList<>();
				if(tableName!=null)
				{
					List<Map<String,Object>>	colums=setUserDataStore(apiKey, dataStoreKey, passToken).queryForList("desc "+tableName);
					/*
					 * if((!dataStoreKey.equalsIgnoreCase("system"))&&setUserDataStore(apiKey,
					 * "system","none").execute(new ConnectionCallback<DatabaseMetaData>() {
					 * 
					 * @Override public DatabaseMetaData doInConnection(Connection con) throws
					 * SQLException, DataAccessException { // TODO Auto-generated method stub return
					 * con.getMetaData(); } }).getURL().contains("mysql")) {
					 * colums.addAll(setUserDataStore(apiKey,
					 * "system","none").queryForList("desc "+tableName)); }
					 */
					
					
				for (Iterator iterator2 = colums.iterator(); iterator2.hasNext();) {
					 isTablePresent=true;
					Map<String, Object> coluMaps = (Map<String, Object>) iterator2.next();
					coluMaps.get("Field");
					coluMaps.get("Type");
					coluMaps.get("Key");
					 
					 
					DbColumn column1 = tableNameT.addColumn((String)coluMaps.get("Field"),
							getSqlTypeWithoutScal((String)coluMaps.get("Type")), getLength((String)coluMaps.get("Type")));
					//column1.setDefaultValue(getSqlTypeName((String)coluMaps.get("Type")));
					if(coluMaps.get("Key").equals("PRI"))
						{
						column1.primaryKey();
						}
					listArraycol.add(column1);
					 
				}
				if(listArraycol.size()>0)
				{
					tableColumnMapusr.put(tableNameT, listArraycol);
				}
				
				}else {
					
					
				}
				
				}else {
				// user Database
					md=	setUserDataStore(apiKey, dataStoreKey, passToken).execute(new ConnectionCallback<DatabaseMetaData>() {

						@Override
						public DatabaseMetaData doInConnection(Connection con) throws SQLException, DataAccessException {
							// TODO Auto-generated method stub
							return con.getMetaData();
						}
					});
			ResultSet rs1 = md.getColumns(null, null,tableName.toUpperCase(), null);
			ResultSet rs2 = md.getPrimaryKeys(null, null, tableName.toUpperCase());
			String primaryKey = "";
			 tableNameT = schemaObj.addTable(tableName);
			while (rs2.next()) {
				primaryKey = rs2.getString("COLUMN_NAME");
			}
			rs2.close();

			while (rs1.next()) {
				DbColumn column1 = null ;
				try {
					
				 column1 = tableNameT.addColumn(rs1.getString("COLUMN_NAME"),
						Integer.parseInt(rs1.getString("DATA_TYPE")), Integer.parseInt(rs1.getString("COLUMN_SIZE")));
				 isTablePresent=true;
				}catch(Exception e)
				{
					 column1 = tableNameT.addColumn(rs1.getString("COLUMN_NAME"),
								4, Integer.parseInt(rs1.getString("COLUMN_SIZE")));
				}

				if (rs1.getString("COLUMN_NAME").equals(primaryKey)) {
					column1.primaryKey();
				}
				listArray.add(column1);
				log.info(rs1.getString("DATA_TYPE"));
				log.info(rs1.getString("COLUMN_NAME"));
				log.info(rs1.getString("COLUMN_SIZE"));

			}
			rs1.close();
			// system database
			if(listArray.size()==0)
			{
			 
				 mdsys=setUserDataStore(apiKey, "system","none").execute(new
				  ConnectionCallback<DatabaseMetaData>() {
				   
				  @Override public DatabaseMetaData doInConnection(Connection con) throws
				   SQLException, DataAccessException {  
					  
				return con.getMetaData(); } });
				 
		
				ResultSet rs3 = mdsys.getColumns(null, null,tableName.toUpperCase(), null);
			    ResultSet rs4 = mdsys.getPrimaryKeys(null, null, tableName.toUpperCase());
			    while (rs4.next()) {
					primaryKey = rs3.getString("COLUMN_NAME");
				}
				rs4.close();
				
				
				while (rs3.next()) {
					DbColumn column1 = null ;
					try {
						
					 column1 = tableNameT.addColumn(rs3.getString("COLUMN_NAME"),
							Integer.parseInt(rs3.getString("DATA_TYPE")), Integer.parseInt(rs3.getString("COLUMN_SIZE")));
					 isTablePresent=true;
					}catch(Exception e)
					{
						 column1 = tableNameT.addColumn(rs3.getString("COLUMN_NAME"),
									4, Integer.parseInt(rs3.getString("COLUMN_SIZE")));
					}

					if (rs3.getString("COLUMN_NAME").equals(primaryKey)) {
						column1.primaryKey();
					}
					listArray.add(column1);
					log.info(rs3.getString("DATA_TYPE"));
					log.info(rs3.getString("COLUMN_NAME"));
					log.info(rs3.getString("COLUMN_SIZE"));

				}
				rs3.close();
				
			}
			
			
			if(listArray.size()>0)
			{
				tableColumnMapusr.put(tableNameT, listArray);
				}
			}
			}
			 
				List<DbTable> serviceTables = initializeTable();
				boolean serviceTabisPresent = false;
				for (Iterator<DbTable> iterator = serviceTables.iterator(); iterator.hasNext();) {
					DbTable dbTable = iterator.next();
					for (Map.Entry<DbTable, List<DbColumn>> entry : tableColumnMapusr.entrySet()) {
						DbTable table = entry.getKey();

						if (dbTable.getName().equalsIgnoreCase(table.getName())) {
							serviceTabisPresent = true;
							log.info("Table Already Present: ");
						}
					}

				}
				String serviceTablesStr="";
				for (Iterator<DbTable> iterator = serviceTables.iterator(); iterator.hasNext();) {
					 
					serviceTablesStr=serviceTablesStr+":"+iterator.next().getName()+":";
				}
				String cacheTablesStr="";
				for (Map.Entry<DbTable, List<DbColumn>> entry : tableColumnMapusr.entrySet()) {
					cacheTablesStr=cacheTablesStr+":"+entry.getKey().getName()+":";
				}
				if (serviceTablesStr.contains((":" + tableName + ":"))) {
					if (!cacheTablesStr.contains((":" + tableName + ":"))) {
						createSysTable();
					}
				}
				if (!serviceTabisPresent) {
					createSysTable() ;

				}
				if(userTableColumnMap.get(dataStoreKey)!=null)
				{
					userTableColumnMap.get(dataStoreKey).putAll(tableColumnMapusr);
				}else
				{
					userTableColumnMap.put(dataStoreKey, tableColumnMapusr);
				}
			setServiceTableMap(tableName, apiKey,  dataStoreKey);
		} catch (SQLException e1) {
			e1.printStackTrace();
			
		}catch(DatabaseAuthException e)
		{
			 
				throw e;
			 
		}finally
		{
			 
		}
	}

	public Map<String, Object> updateData(String service, Map<String, String> params,String apiKey, String dataStoreKey,String passToken) throws Exception , DatabaseAuthException {
		ObjectMapper mapper = new ObjectMapper();
		String tableName = null;
		if(userServiceTableMap.get(dataStoreKey)!=null)
		{ 
			tableName=userServiceTableMap.get(dataStoreKey).get(service);
			}
		setGDValues(service, tableName, apiKey,  dataStoreKey,passToken);
		tableName = userServiceTableMap.get(dataStoreKey).get(service);
		if (tableName == null) {
			throw new ServiceNotFoundException(serviceNTFEx);
		}
		UpdateQuery updateQuery = new UpdateQuery(tableName);
		int isUpdate = 0;
		String primaryKey = "";
		String primaryKeyAttr="";
		//Map<String, String> attribParamMap = serviceAttrbMap.get(service);
		Map<String, String> attribParamMap = userServiceAttrbMap.get(dataStoreKey).get(service);
		if(attribParamMap==null) {
			setServiceTableMap(tableName, apiKey, "system");
			attribParamMap = userServiceAttrbMap.get(dataStoreKey).get(service);
		}
		for (Map.Entry<DbTable, List<DbColumn>> entry : userTableColumnMap.get(dataStoreKey).entrySet()) {
			DbTable table = entry.getKey();
			if (table.getName().equalsIgnoreCase(tableName)) {
				table = schemaObj.addTable(table.getName());
				updateQuery = new UpdateQuery(table);
				List<DbColumn> column = entry.getValue();
				for (Iterator<DbColumn> iterator = column.iterator(); iterator.hasNext();) {
					DbColumn dbColumn = iterator.next();
					List<DbConstraint> dbConstr = dbColumn.getConstraints();
					boolean isPrimaryKey = false;
					for (Iterator<DbConstraint> iterator2 = dbConstr.iterator(); iterator2.hasNext();) {
						DbConstraint dbConstraint = iterator2.next();
						if (dbConstraint.getType().toString().equals(Constraint.Type.PRIMARY_KEY.toString())) {
							isPrimaryKey = true;
							if ((params.get(attribParamMap.get(dbColumn.getName())) != null))
								updateQuery.addCondition(BinaryCondition.equalTo(dbColumn,
										params.get(attribParamMap.get(dbColumn.getName()))));
							primaryKey =( mapper.writeValueAsString(params.get(attribParamMap.get(dbColumn.getName()))) );
							primaryKeyAttr=attribParamMap.get(dbColumn.getName());
						}
					}
					if ((params.get(attribParamMap.get(dbColumn.getName())) != null))
						updateQuery.addSetClause(dbColumn, params.get(attribParamMap.get(dbColumn.getName())));

				}

				log.info("Key = " + entry.getKey() + ", Value = " + entry.getValue());
				// params.get(attribParamMap.get(dbColumn.getName()))
				log.info(updateQuery.toString());
				try {
					if(!(primaryKey==null||primaryKey.isEmpty()||primaryKey.equalsIgnoreCase("null")))
					{
						if(dataStoreKey.equalsIgnoreCase("SYSTEM"))
						{
							isUpdate = setUserDataStore(apiKey, "system","none").update(updateQuery.toString());
						}else
						{
							isUpdate = setUserDataStore(apiKey, dataStoreKey, passToken).update(updateQuery.toString());	
						}
					}
				} catch (Exception e) {
				}
				break;
			}
		}
		if (isUpdate > 0) {
		if(tableName.equalsIgnoreCase("SERVICE_ATTR"))
		{
			ParamsValidator.clearCache();
			System.out.println("select S.id as id, S.tableName as tableName, S.serviceName as serviceName, SA.colName as colName, SA.attrName as attrName  from Service S Service_Attr SA, jdbcTemplate where S.id=  '"+String.valueOf(params.get("serviceid"))+"'  and S.id=SA.service_id ");
			List<Map<String, Object>> serviceDatum = setUserDataStore(apiKey, "system","none")
					.queryForList("select S.id as id, S.tableName as tableName, S.serviceName as serviceName, SA.colName as colName, SA.attrName as attrName  from Service S, Service_Attr SA  where S.id=  '"+String.valueOf(params.get("serviceid"))+"'  and S.id=SA.service_id ");
			
			String serviceName=(String)serviceDatum.get(0).get("serviceName");
			Map<String, String> AttrbMap = null; 
			Map<String, Map<String, String>> serviceAttrbMapUsr =userServiceAttrbMap.get(dataStoreKey);
			if(serviceAttrbMapUsr!=null)
			{
				AttrbMap=serviceAttrbMapUsr.get(serviceName);
			}else
			{
				serviceAttrbMapUsr= new ConcurrentHashMap<>(); 
			}
			//AttrbMap=serviceAttrbMap.get(serviceName);
			if(AttrbMap==null)
			{
				AttrbMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
			}
			 
			for (Iterator iterator = serviceDatum.iterator(); iterator.hasNext();) {
				Map<String, Object> map = (Map<String, Object>) iterator.next();
				serviceName=(String)map.get("serviceName");
				AttrbMap.put((String) map.get("colName".toUpperCase()), (String) map.get("attrName".toUpperCase()));
				//serviceAttrbMap.put((String)map.get("serviceName"),AttrbMap);
			}
			if(serviceName!=null)
			{
				serviceAttrbMapUsr.put(serviceName,AttrbMap);
				}
			if(userServiceAttrbMap.get(dataStoreKey)==null)
			{
				userServiceAttrbMap.put(dataStoreKey, serviceAttrbMapUsr);
			}else
			{
				userServiceAttrbMap.get(dataStoreKey).putAll(serviceAttrbMapUsr);	
			}
			
			/*
			 * List<Map<String, Object>> serviceDatum1 = jdbcTemplate
			 * .queryForList("select colName, attrName from Service_Attr where service_id = '"
			 * + params.get("service id") + "'"); Map<String, String> AttrbMap; AttrbMap =
			 * new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER); for
			 * (Iterator<Map<String, Object>> iterator = serviceDatum1.iterator();
			 * iterator.hasNext();) { Map<String, Object> map = iterator.next();
			 * 
			 * AttrbMap.put((String) map.get("colName".toUpperCase()), (String)
			 * map.get("attrName".toUpperCase())); serviceAttrbMap.put((String)
			 * serviceDatum.get(0).get("serviceName"), AttrbMap); }
			 */
				
				//serviceAttrbMap(id,(String) serviceDatum.get(0).get("serviceName"));
		     
		}
		params=new HashMap<String, String>();
		primaryKey = replaceDoubleQute(primaryKey);
		params.put(primaryKeyAttr,primaryKey);
			return getDataForParams(service, params, apiKey,  dataStoreKey, passToken).get(0);
		} else {
			try {
				if(primaryKey==null||primaryKey.isEmpty()||primaryKey.equalsIgnoreCase("null"))
				{
					return insertData(service, params, apiKey,  dataStoreKey, passToken);
				}
			} catch (Exception e) {
				throw new Exception("update Failed");
			}
			if (!(isUpdate > 0)) {
				throw new Exception("update Failed");
			}else
			{
				return getDataForParams(service, params,apiKey,  dataStoreKey, passToken).get(0);
			}
		}

}

	public Map<String, Object> getData(String serviceName, String primaryKeyValue,String apiKey, String dataStoreKey,String passToken) throws Exception , DatabaseAuthException {

		SelectQuery selectQuery = new SelectQuery();
		String tableName = null;
		if(userServiceTableMap.get(dataStoreKey)!=null)
		{ 
			tableName=userServiceTableMap.get(dataStoreKey).get(serviceName);
			}
		setGDValues(serviceName, tableName, apiKey,  dataStoreKey,passToken);
		tableName = userServiceTableMap.get(dataStoreKey).get(serviceName);
		if (tableName == null) {
			throw new ServiceNotFoundException(serviceNTFEx);
		}
		 
		Map<String, String> attribParamMap = userServiceAttrbMap.get(dataStoreKey).get(serviceName);
		if(attribParamMap==null) {
			setServiceTableMap(tableName, apiKey, "system");
			attribParamMap = userServiceAttrbMap.get(dataStoreKey).get(serviceName);
		}
		for (Map.Entry<DbTable, List<DbColumn>> entry :  userTableColumnMap.get(dataStoreKey).entrySet()) {
			DbTable table = entry.getKey();
			if (table.getName().equalsIgnoreCase(tableName)) {
				List<DbColumn> column = entry.getValue();
				for (Iterator<DbColumn> iterator = column.iterator(); iterator.hasNext();) {
					DbColumn dbColumn = iterator.next();
					List<DbConstraint> dbConstr = dbColumn.getConstraints();
					for (Iterator<DbConstraint> iterator2 = dbConstr.iterator(); iterator2.hasNext();) {
						DbConstraint dbConstraint = iterator2.next();
						if (dbConstraint.getType().toString().equals(Constraint.Type.PRIMARY_KEY.toString())) {
							selectQuery.addCondition(BinaryCondition.equalTo(dbColumn, primaryKeyValue));

						}
					}
					if (attribParamMap.get(dbColumn.getName())!= null)
						{
						if(isValidForSelect(dbColumn.getTypeNameSQL()))
						{
							selectQuery.addAliasedColumn(dbColumn, "\"" + attribParamMap.get(dbColumn.getName()) + "\"");
						}
						}
				}
				break;
			}
		}
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if(dataStoreKey.equalsIgnoreCase("SYSTEM"))
			{
			result = setUserDataStore(apiKey, "system","none").queryForMap(selectQuery.validate().toString());
			}else
			{
			result = setUserDataStore(apiKey, dataStoreKey, passToken).queryForMap(selectQuery.validate().toString());	
			}

		} catch (Exception e) {

			log.info(e.getMessage());
		}
		return result;
	}

	public Map<String, Object> deleteData(String serviceName, String primaryKeyValue,String apiKey, String dataStoreKey,String passToken) throws Exception , DatabaseAuthException{

		DeleteQuery deleteQuery = null;
		String tableName = null;
		if(userServiceTableMap.get(dataStoreKey)!=null)
		{ 
			tableName=userServiceTableMap.get(dataStoreKey).get(serviceName);
			}
		setGDValues(serviceName, tableName, apiKey,  dataStoreKey,passToken);
		tableName = userServiceTableMap.get(dataStoreKey).get(serviceName);
		if (tableName == null) {
			throw new ServiceNotFoundException(serviceNTFEx);
		}
		 
		for (Map.Entry<DbTable, List<DbColumn>> entry :  userTableColumnMap.get(dataStoreKey).entrySet()) {
			DbTable table = entry.getKey();
			if (table.getName().equalsIgnoreCase(tableName)) {
				deleteQuery = new DeleteQuery(table);
				List<DbColumn> column = entry.getValue();
				for (Iterator<DbColumn> iterator = column.iterator(); iterator.hasNext();) {
					DbColumn dbColumn = iterator.next();
					List<DbConstraint> dbConstr = dbColumn.getConstraints();
					for (Iterator<DbConstraint> iterator2 = dbConstr.iterator(); iterator2.hasNext();) {
						DbConstraint dbConstraint = iterator2.next();
						if (dbConstraint.getType().toString().equals(Constraint.Type.PRIMARY_KEY.toString())) {
							deleteQuery.addCondition(BinaryCondition.equalTo(dbColumn, primaryKeyValue));

						}
					}
				}
				log.info(deleteQuery.validate().toString());
				break;
			}
		}
		log.info(deleteQuery.validate().toString());
		Map<String, Object> retValue = getData(serviceName, primaryKeyValue, apiKey,  dataStoreKey,passToken);
		int result = 0;
		try {
			if(dataStoreKey.equalsIgnoreCase("SYSTEM"))
			{
			result = setUserDataStore(apiKey, "system","none").update(deleteQuery.validate().toString());
			}else
			{
				result = setUserDataStore(apiKey, dataStoreKey, passToken).update(deleteQuery.validate().toString());	
			}
		} catch (Exception e) {
		}
		if (result > 0) {
			return retValue;
		} else {
			throw new Exception("Deletion Failed ");
		}

	}

	public List<Map<String, Object>> getDataForParams(String serviceName, Map<String, String> params,String apiKey, String dataStoreKey,String passToken)   throws Exception ,DatabaseAuthException {
		for (int i = 0; i < nonscaling.length; i++) {
			invalidElement.add(nonscaling[i]);
		}
		JdbcTemplate userJdbcTemplate=jdbcTemplateMap.get(apiKey);
		/*
		 * if(userJdbcTemplate==null) {
		 * userJdbcTemplate=setUserDataStore(userJdbcTemplate,apiKey,dataStoreKey,passToken);
		 * testJDBC(userJdbcTemplate); }
		 */
		SelectQuery selectQuery = new SelectQuery();
		String tableName = null;
		if(userServiceTableMap.get(dataStoreKey)!=null)
		{ 
			tableName=userServiceTableMap.get(dataStoreKey).get(serviceName);
			}
		setGDValues(serviceName, tableName, apiKey,  dataStoreKey,passToken);
		tableName = userServiceTableMap.get(dataStoreKey).get(serviceName);
		 
		if (tableName == null) {
			Set<ConstraintViolation<HashMap>> constraintViolation =new HashSet<ConstraintViolation<HashMap>>();
			Map errorMessages=new HashMap<String,String>();
			ConstraintViolation<HashMap> cv=new ServiceConstraintViolation<String,String>("Service Not Found "," / "+serviceName); 
			constraintViolation.add(cv);
			throw new ConstraintViolationException(constraintViolation);
		}
		Map<String, String> attribParamMap = userServiceAttrbMap.get(dataStoreKey).get(serviceName);
		if(attribParamMap==null) {
			setServiceTableMap(tableName, apiKey, "system");
			attribParamMap = userServiceAttrbMap.get(dataStoreKey).get(serviceName);
		}
		for (Map.Entry<DbTable, List<DbColumn>> entry :  userTableColumnMap.get(dataStoreKey).entrySet()) {
			DbTable table = entry.getKey();
			if (table.getName().equalsIgnoreCase(tableName)) {
				List<DbColumn> column = entry.getValue();
				
				for (Iterator<DbColumn> iterator = column.iterator(); iterator.hasNext();) {
					DbColumn dbColumn = iterator.next();
					if (attribParamMap.containsKey(dbColumn.getName())
							&& params.get(attribParamMap.get(dbColumn.getName())) != null) {
						selectQuery.addCondition(
								BinaryCondition.equalTo(dbColumn, params.get(attribParamMap.get(dbColumn.getName()))));
					}
				String columnType=	 dbColumn.getTypeNameSQL() ;
				 if(attribParamMap.get(dbColumn.getName())!=null)
				 { 
					 if(isValidForSelect(dbColumn.getTypeNameSQL())) {
					 selectQuery.addAliasedColumn(dbColumn, "\"" + attribParamMap.get(dbColumn.getName()) + "\"");
					 }	 
				 } 
				 
				 if (attribParamMap.containsKey(dbColumn.getName())
							&& params.get(Constant.IN_CONDITION+attribParamMap.get(dbColumn.getName())) != null) {
					 selectQuery.addCondition(new InCondition(dbColumn,params.get(Constant.IN_CONDITION+attribParamMap.get(dbColumn.getName())).split(":")));
					}
				 if (attribParamMap.containsKey(dbColumn.getName())
							&& params.get(Constant.BETWEEN+attribParamMap.get(dbColumn.getName())) != null) {
					 selectQuery.addCondition(new BetweenCondition(dbColumn,params.get(Constant.IN_CONDITION+attribParamMap.get(dbColumn.getName())).split(":")[0],params.get(Constant.IN_CONDITION+attribParamMap.get(dbColumn.getName())).split(":")[1]));
					}
				 if (attribParamMap.containsKey(dbColumn.getName())
							&& params.get(Constant.ORDER_BY + attribParamMap.get(dbColumn.getName())) != null) {
						OrderObject.Dir order;
						if (params.get(Constant.ORDER_BY + attribParamMap.get(dbColumn.getName()))
								.equalsIgnoreCase("asc")) {
							order = OrderObject.Dir.ASCENDING;
							selectQuery.addOrdering(dbColumn, order);
						}

						if (params.get(Constant.ORDER_BY + attribParamMap.get(dbColumn.getName()))
								.equalsIgnoreCase("desc")) {
							order = OrderObject.Dir.DESCENDING;

							selectQuery.addOrdering(dbColumn, order);
						}
					}
				 
				}
				log.info(selectQuery.validate().toString());
				break;
			}
		}
		
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			if(dataStoreKey.equalsIgnoreCase("SYSTEM"))
			{
			result = setUserDataStore(apiKey, "system","none").queryForList(selectQuery.validate().toString());
			}else
			{
				result =setUserDataStore(apiKey, dataStoreKey, passToken).queryForList(selectQuery.validate().toString());
			}

		} catch (Exception e) {

			log.info(e.getMessage());
			if(e instanceof DatabaseAuthException)
			{
				throw e;
			}
		}

		return result;
	}

	private void setGDValues(String serviceName, String tableName,String apiKey, String dataStoreKey,String passToken)  throws Exception{
		if (tableName == null) {
			arrangeGoldenData(serviceName, apiKey,  dataStoreKey,passToken);

		} else {
			boolean isPresent = false;
			for (Map.Entry<String,Map<DbTable, List<DbColumn>>> entryUsr :userTableColumnMap.entrySet()) {
				String dataStoreKeyStr=entryUsr.getKey();
				String userApiKey=entryUsr.getKey();
				Map<DbTable, List<DbColumn>> tableColumnMapUsr= entryUsr.getValue();
			for (Map.Entry<DbTable, List<DbColumn>> entry : tableColumnMapUsr.entrySet()) {
				DbTable table = entry.getKey();
				if (table.getName().equalsIgnoreCase(tableName)&&dataStoreKeyStr.equals(dataStoreKey)) {
					isPresent = true;
				}
			}
			}
			if (!isPresent) {
				arrangeGoldenDataForTable(tableName, apiKey,  dataStoreKey,passToken);
			}
		}
	}

	public void getServiceTableMap(String apiKey, String dataStoreKey) throws DatabaseAuthException{

		 
		List<Map<String, Object>> serviceDatum = setUserDataStore(apiKey, "system","none")
				.queryForList("select ser.id as id, ser.tableName as tableName, ser.serviceName as serviceName  ,ds.name as name from Service ser , Datastore ds where ds.uid=ser.uid");
		for (Iterator<Map<String, Object>> iterator = serviceDatum.iterator(); iterator.hasNext();) {
			Map<String, Object> map = iterator.next();
			String dataSource=String.valueOf(map.get("name"));
			if(userServiceTableMap.get(dataSource)==null)
			{
			Map<String,String> serviceTableMap=new ConcurrentHashMap<>();
			serviceTableMap.put((String) map.get("serviceName"), (String) map.get("tableName"));
			userServiceTableMap.put(dataSource, serviceTableMap);
			
			}else
			{
				userServiceTableMap.get(dataSource).put((String) map.get("serviceName"), (String) map.get("tableName"));
			}
			//serviceTableMap.put((String) map.get("serviceName"), (String) map.get("tableName"));
			String id="";
			
			try{
				id=Integer.toString((int) map.get("id"));
				}catch(Exception e) {
				
				id=(	(BigDecimal) map.get("id")).toString();
			}
			serviceAttrbMap(id, (String) map.get("serviceName"), apiKey,  dataSource);

		}
		 
	}
	public void setServiceTableMap(String tableName,String apiKey, String dataStoreKey) throws DatabaseAuthException {

		
		List<Map<String, Object>> serviceDatum = setUserDataStore(apiKey, "system","none")
				.queryForList("select ser.id as id, ser.tableName as tableName, ser.serviceName as serviceName  ,ds.name as name from Service ser , Datastore ds where ds.uid=ser.uid and ser.tableName= ? and ds.name= ? ", new Object[] {tableName,dataStoreKey});
		boolean isPresent=false;
		for (Iterator<Map<String, Object>> iterator = serviceDatum.iterator(); iterator.hasNext();) {
			isPresent=true;
			Map<String, Object> map = iterator.next();
			String dataSource=String.valueOf(map.get("name"));
			if(userServiceTableMap.get(dataSource)==null)
			{
			Map<String,String> serviceTableMap=new ConcurrentHashMap<>();
			serviceTableMap.put((String) map.get("serviceName"), (String) map.get("tableName"));
			userServiceTableMap.put(dataSource, serviceTableMap);
			
			}else
			{
				userServiceTableMap.get(dataSource).put((String) map.get("serviceName"), (String) map.get("tableName"));
			}
			//	serviceTableMap.put((String) map.get("serviceName"), (String) map.get("tableName"));
			 
			String id="";
			try{
				id=Integer.toString((int) map.get("id"));
				}catch(Exception e) {
				
				id=(	(BigDecimal) map.get("id")).toString();
			}
			if(!serviceAttrbMap(id, (String) map.get("serviceName"), apiKey,  dataSource))
			{
				  try {
					insertServiceTables(tableName, apiKey,  dataStoreKey);
				}  catch (DatabaseAuthException e) {
					 
						throw e;
					 
				}
				  catch (Exception e) {
					 
					e.printStackTrace();
				}
			}

		}
		 if(!isPresent)
		 {
			 
			 try {
				 boolean isPresentTable=false;
					for (Map.Entry<String,Map<DbTable, List<DbColumn>>> entryUsr : userTableColumnMap.entrySet()) 
					{
						String dataStoreKeyStr=entryUsr.getKey();
						Map<DbTable, List<DbColumn>>  tableColumnMapUsr=entryUsr.getValue();
					for (Map.Entry<DbTable, List<DbColumn>> entry : tableColumnMapUsr.entrySet()) {
						DbTable table = entry.getKey();
						if (table.getName().equalsIgnoreCase(tableName)&&dataStoreKeyStr.equals(dataStoreKey)) {
							isPresentTable=true;
						}
					}
					}
				  if(isPresentTable)
				{
					  insertServiceTables(tableName, apiKey,  dataStoreKey);
				      setServiceTableMap(tableName, apiKey,  dataStoreKey);
				      insertServiceTables(tableName, apiKey,  dataStoreKey);
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		 }
	}

	public boolean serviceAttrbMap(String id, String serviceName,String apiKey, String dataStoreKey) throws DatabaseAuthException{
		boolean setService=false;
		Map<String, String> studentAttrbMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
		List<Map<String, Object>> serviceDatum = setUserDataStore(apiKey, "system","none")
				.queryForList("select colName, attrName from Service_Attr where service_id = '" + id + "'");
		Map<String, String> AttrbMap;
		AttrbMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
		Map<String, Map<String, String>>  usrServiceAttrbMap=userServiceAttrbMap.get(dataStoreKey);
		if(usrServiceAttrbMap==null)
		{
			usrServiceAttrbMap=new ConcurrentHashMap<>();
		}
		for (Iterator<Map<String, Object>> iterator = serviceDatum.iterator(); iterator.hasNext();) {
			Map<String, Object> map = iterator.next();
			setService=true;
			AttrbMap.put((String) map.get("colName".toUpperCase()), (String) map.get("attrName".toUpperCase()));
			usrServiceAttrbMap.put(serviceName, AttrbMap);
			//serviceAttrbMap.put(serviceName, AttrbMap);
		}
		if(userServiceAttrbMap.get(dataStoreKey)==null)
		{
			userServiceAttrbMap.put(dataStoreKey, usrServiceAttrbMap);	
		}else
		{
			userServiceAttrbMap.get(dataStoreKey).putAll(usrServiceAttrbMap);
		}
		return  setService;
	}
	private String getUidForSerId(String id,String apiKey) throws DatabaseAuthException
	{
		List<Map<String, Object>> serviceDatum = setUserDataStore(apiKey, "system","none")
				.queryForList("select usr.id as id ,usr.apikey as apikey from User usr , Service ser  where     usr.id=ser.uid and ser.id = ?" , new Object[] {id});
		return	(String) serviceDatum.get(0).get("id");
	}
	public String getUidForapiKey(String apiKey) throws DatabaseAuthException
	{
		if(UserApiMap.get(apiKey)!=null) {
			return UserApiMap.get(apiKey);
		}else {
		List<Map<String, Object>> serviceDatum = setUserDataStore(apiKey, "system","none")
				.queryForList("select id ,apikey  from User where   apikey = ?" , new Object[] {apiKey});
		String uid=String.valueOf(serviceDatum.get(0).get("id"));
		UserApiMap.put(apiKey, uid);
		return	uid;
		}
		
	}
	public String getdsidFordsName(String dataStoreKey)  throws DatabaseAuthException
	{
		List<Map<String, Object>> serviceDatum =null;
		try {
			serviceDatum = setUserDataStore("system", "system", "none")
					.queryForList("select id ,name   from Datastore  where   name = ?", new Object[] { dataStoreKey });
		} catch (Exception e) {
			createSysTable();
			serviceDatum = setUserDataStore("system", "system", "none")
					.queryForList("select id ,name   from Datastore  where   name = ?", new Object[] { dataStoreKey });
		
		}
		String uid=String.valueOf(serviceDatum.get(0).get("id"));
		return	uid;
	}
	
	private void insertServiceTables(String tableName,String apiKey, String dataStoreKey) throws Exception ,DatabaseAuthException {
		String userId=getUidForapiKey( apiKey);
		String dsId =null;
		if (dsidMap.get(dataStoreKey + ":" + apiKey) == null) {
			dsId = getdsidFordsName(dataStoreKey);
			dsidMap.put(dataStoreKey + ":" + apiKey, dsId);
		} else {
			dsId = dsidMap.get(dataStoreKey + ":" + apiKey);
		}
			String serviceInsertQuery = "INSERT INTO   Service (id ,tableName , serviceName , uid , dsid )   values( (SELECT MAX( id )+1 FROM Service ser) , '"
					+ tableName + "', '" + tableName.toLowerCase().replace("_", " ") + "','"+userId+"' , '"+dsId+"'  )";
			String serviceid = getServiceID(tableName, apiKey,  dataStoreKey,userId,dsId);
			String maxRec = findMax("Service", apiKey,  "system","none");
			if (maxRec != null && serviceid == null) {
				setUserDataStore(apiKey, "system","none").execute(serviceInsertQuery);
				serviceid = getServiceID(tableName, apiKey,  dataStoreKey,userId,dsId);
			} else if (maxRec == null) {
				serviceInsertQuery = "INSERT INTO   Service (id ,tableName , serviceName,uid , dsid )   values( 0, '" + tableName
						+ "', '" + tableName.toLowerCase().replace("_", " ") + "' ,'"+userId+"' , '"+dsId+"' )";
				setUserDataStore(apiKey, "system","none").execute(serviceInsertQuery);
				serviceid = getServiceID(tableName, apiKey,  dataStoreKey,userId,dsId);
			}
			for (Map.Entry<String,Map<DbTable, List<DbColumn>>> entryUsr : userTableColumnMap.entrySet()) {
				Map<DbTable, List<DbColumn>>  tableColumnMapUsr=entryUsr.getValue();
				String dataStoreKeyStr=entryUsr.getKey();
			for (Map.Entry<DbTable, List<DbColumn>> entry : tableColumnMapUsr.entrySet()) {
				DbTable table = entry.getKey();
				if (table.getName().equalsIgnoreCase(tableName)&&dataStoreKeyStr.equals(dataStoreKey)) {
			List<DbColumn> column = entry.getValue();
			String maxRecAttr = findMax("Service_Attr", apiKey,  "system","none");
			 List<Map<String, Object>> attrbData=getServiceAttrIDByServiceID(serviceid, apiKey, dataStoreKey);
			 List<String> quries=new ArrayList<String>();
			for (Iterator<DbColumn> iterator = column.iterator(); iterator.hasNext();) {
				DbColumn dbColumn = iterator.next();
				//String serviceAttrid = getServiceAttrID(serviceid, dbColumn.getName(), apiKey,  dataStoreKey);
				String serviceAttrid = getServiceAttrIDByList(attrbData, serviceid,  dbColumn.getName().toLowerCase().replace("_", ""));
				if (maxRecAttr != null && serviceAttrid == null) {
					String serviceAttrQuery = " INSERT INTO Service_Attr (id, service_id , attrName, colName,colType) values ((SELECT MAX( id )+1 FROM Service_Attr serA) ,'"
							+ serviceid + "','" + dbColumn.getName().toLowerCase().replace("_", "") + "','"
							+ dbColumn.getName() + "','"+dbColumn.getTypeNameSQL()+"') ";
					quries.add(serviceAttrQuery);
					//setUserDataStore(apiKey, "system","none").execute(serviceAttrQuery);
				} else if (maxRecAttr == null) {

					{
						String serviceAttrQuery = " INSERT INTO Service_Attr (id, service_id , attrName, colName,colType) values (0 ,'"
								+ serviceid + "','" + dbColumn.getName().toLowerCase().replace("_", "") + "','"
								+ dbColumn.getName() + "' ,'"+dbColumn.getTypeNameSQL()+"') ";
						//quries.add(serviceAttrQuery);
						setUserDataStore(apiKey, "system","none").execute(serviceAttrQuery);
						maxRecAttr = findMax("Service_Attr", apiKey,  "system","none");
					}

				}
			}
			if (quries.size() > 0) {
				setUserDataStore(apiKey, "system", "none").batchUpdate(quries.toArray(new String[quries.size()]));
			}
				}}
			}
		

	}
	private void insertServiceTables(String apiKey, String dataStoreKey) throws  DatabaseAuthException{
		String userId=getUidForapiKey( apiKey);
		String dsId =null;
		if (dsidMap.get(dataStoreKey + ":" + apiKey) == null) {
			dsId = getdsidFordsName(dataStoreKey);
			dsidMap.put(dataStoreKey + ":" + apiKey, dsId);
		} else {
			dsId = dsidMap.get(dataStoreKey + ":" + apiKey);
		}
		for (Map.Entry<String,Map<DbTable, List<DbColumn>>> entryUsr : userTableColumnMap.entrySet()) {
			Map<DbTable, List<DbColumn>>  tableColumnMapUsr=entryUsr.getValue();
		for (Map.Entry<DbTable, List<DbColumn>> entry : tableColumnMapUsr.entrySet()) {
			DbTable table = entry.getKey();
			String tableName = table.getName();

			String serviceInsertQuery = "INSERT INTO   Service (id ,tableName , serviceName , uid , dsid )   values( (SELECT MAX( id )+1 FROM Service ser) , '"
					+ tableName + "', '" + tableName.toLowerCase().replace("_", " ") + "' , '"+userId+"' , '"+dsId+"' )";
			String serviceid = getServiceID(tableName, apiKey,  dataStoreKey,userId,dsId);
			String maxRec = findMax("Service", apiKey,  "system","none");
			if (maxRec != null && serviceid == null) {
				setUserDataStore(apiKey, "system","none").execute(serviceInsertQuery);
				serviceid = getServiceID(tableName, apiKey,  dataStoreKey,userId,dsId);
			} else if (maxRec == null) {
				serviceInsertQuery = "INSERT INTO   Service (id ,tableName , serviceName , uid , dsid )   values( 0, '" + tableName
						+ "', '" + tableName.toLowerCase().replace("_", " ") + "' ,'"+userId+"' , '"+dsId+"' )";
				setUserDataStore(apiKey, "system","none").execute(serviceInsertQuery);
				serviceid = getServiceID(tableName, apiKey,  dataStoreKey,userId,dsId);
			}

			List<DbColumn> column = entry.getValue();
			String maxRecAttr = findMax("Service_Attr", apiKey,  "system","none");
			 List<Map<String, Object>> attrbData=getServiceAttrIDByServiceID(serviceid, apiKey, dataStoreKey);
			 List<String> quries=new ArrayList<String>();
			for (Iterator<DbColumn> iterator = column.iterator(); iterator.hasNext();) {
				DbColumn dbColumn = iterator.next();
				//String serviceAttrid = getServiceAttrID(serviceid, dbColumn.getName(), apiKey,  dataStoreKey);
				String serviceAttrid = getServiceAttrIDByList(attrbData, serviceid,  dbColumn.getName().toLowerCase().replace("_", ""));
				if (maxRecAttr != null && serviceAttrid == null) {
					String serviceAttrQuery = " INSERT INTO Service_Attr (id, service_id , attrName, colName,colType) values ((SELECT MAX( id )+1 FROM Service_Attr serA) ,'"
							+ serviceid + "','" + dbColumn.getName().toLowerCase().replace("_", "") + "','"
							+ dbColumn.getName() + "' ,'"+dbColumn.getTypeNameSQL()+"') ";
					quries.add(serviceAttrQuery);
					//setUserDataStore(apiKey, "system","none").execute(serviceAttrQuery);
				} else if (maxRecAttr == null) {

					{
						String serviceAttrQuery = " INSERT INTO Service_Attr (id, service_id , attrName, colName,colType) values (0 ,'"
								+ serviceid + "','" + dbColumn.getName().toLowerCase().replace("_", "") + "','"
								+ dbColumn.getName() + "'  ,'"+dbColumn.getTypeNameSQL()+"') ";
						//quries.add(serviceAttrQuery);
						setUserDataStore(apiKey, "system","none").execute(serviceAttrQuery);
						maxRecAttr = findMax("Service_Attr", apiKey,  "system","none");
					}

				}
			}
			if (quries.size() > 0) {
				setUserDataStore(apiKey, "system", "none").batchUpdate(quries.toArray(new String[quries.size()]));
			}
		}
		}

	}

	private String getServiceID(String tableName,String apiKey, String dataStoreKey, String userId, String dsId) {

		String selectQuery = " select id from Service where tableName = '" + tableName + "' and uid ='"+userId+"' and dsid = '"+dsId+"'";
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		try {
			data = setUserDataStore(apiKey, "system","none").queryForList(selectQuery);
		} catch (Exception e) {
			if(e  instanceof DatabaseAuthException)
			{
				throw e;
			}
			log.info(e.getMessage());
			createSysTable() ;
		}
		String serID = null;
		if (data != null)
			if (data.size() != 0) {
				if (data.get(0).get("ID") != null) {
					try
					{
						serID = Integer.toString((int) data.get(0).get("id"));
					}catch(Exception e) {
						java.math.BigDecimal setIDs=	(java.math.BigDecimal ) data.get(0).get("id");
						serID=setIDs.toString();
					}
				}
			}
		return serID;
	}

	private List<Map<String, Object>> getServiceAttrIDByServiceID(String serviceId,String apiKey, String dataStoreKey) {

		String selectQuery = " select id,colName  from Service_Attr where service_id  = '" + serviceId + "'  ";
		List<Map<String, Object>> data = new ArrayList<>();
		try {
			data = setUserDataStore(apiKey, "system","none").queryForList(selectQuery);
		} catch (Exception e) {
			if(e  instanceof DatabaseAuthException)
			{
				throw e;
			}
			log.info(e.getMessage());
		}

		 
		return data;
	}
	private String getServiceAttrIDByList(List<Map<String, Object>> data,String serviceId, String attributeName) {

		String attrID = null;
		String attrbName=null;
		if (data != null)
			if (!data.isEmpty() ) {
				for (Iterator iterator = data.iterator(); iterator.hasNext();) {
					Map<String, Object> map = (Map<String, Object>) iterator.next();
					attrID =String.valueOf( map.get("id"));
					attrbName=String.valueOf( map.get("colName"));
					if(attrID!=null&&attrbName!=null&&attrbName.equalsIgnoreCase(attrbName))
					{
						return attrID;
					}
				}
				
			
			}
		return attrID;
	}
	
	private String getServiceAttrID(String serviceId, String attributeName,String apiKey, String dataStoreKey) throws DatabaseAuthException{

		String selectQuery = " select id  from Service_Attr where service_id  = '" + serviceId + "' and colName ='"
				+ attributeName + "'";
		List<Map<String, Object>> data = new ArrayList<>();
		try {
			data = setUserDataStore(apiKey, "system","none").queryForList(selectQuery);
		} catch (Exception e) {
			log.info(e.getMessage());
		}

		String attrID = null;
		if (data != null)
			if (!data.isEmpty() ) {
				if (data.get(0).get("ID") != null) {
					try {attrID = Integer.toString((int) data.get(0).get("id"));}catch(Exception e) {
						
						try{attrID = Long.toString((Long) data.get(0).get("id"));	}catch(Exception es) {
							BigDecimal datas =(BigDecimal) data.get(0).get("id");
							attrID=datas.toString();
							
						}
					}
				}
			}
		return attrID;
	}

	private String findMax(String table,String apiKey, String dataStoreKey,String passToken) throws  DatabaseAuthException {
		String query = "SELECT MAX( id )+1 as id FROM " + table;
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			data = setUserDataStore(apiKey, dataStoreKey, passToken).queryForMap(query);
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
		return attrID;

	}
	public int getSQLType(String mysqlDataType)
	{
		if(mysqlDataType.contains("int"))
		{
			return  Types.INTEGER;
		}
		
		return 0;
	}
	public int getLength(String mysqlDataType)
	{
		char[] lengthData=mysqlDataType.toCharArray();
		String returnStr="";
		for (int i = 0; i < lengthData.length; i++) {
			char chardata= lengthData[i];
			if (chardata >= '0' && chardata <= '9')
			{
				returnStr=returnStr+chardata;
			}
		}
		
		int returnInt=0;
		
		try{returnInt=Integer.parseInt(returnStr);}catch(Exception e) {log.info(e.getMessage());}
		
		return returnInt;
	}
	
	public String getSqlTypeWithoutScal(String mysqlDataType)
	{
		char[] lengthData=mysqlDataType.toCharArray();
		String returnStr="";
		for (int i = 0; i < lengthData.length; i++) {
			char chardata= lengthData[i];
			if (chardata >= '0' && chardata <= '9')
			{
				returnStr=returnStr+chardata;
			}
		}
		
		 
		
		 
		
		return mysqlDataType.replace("(", "").replace(")", "").replace(returnStr, "");
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
	
	public boolean isValidForSelect(String type)
	{

		if ("TEXT".equalsIgnoreCase(type)) {
			return true;
		}
		if ("BIT".equalsIgnoreCase(type)) {
			return true;
		}
		if ("INT".equalsIgnoreCase(type)) {
			return true;
		}
		if ("TINYINT".equalsIgnoreCase(type)) {
			return true;
		}

		if ("SMALLINT".equalsIgnoreCase(type)) {
			return true;
		}

		if ("INTEGER".equalsIgnoreCase(type)) {
			return true;
		}

		if ("BIGINT".equalsIgnoreCase(type)) {
			return true;
		}

		if ("FLOAT".equalsIgnoreCase(type)) {
			return true;
		}

		if ("REAL".equalsIgnoreCase(type)) {
			return true;
		}

		if ("DOUBLE".equalsIgnoreCase(type)) {
			return true;
		}

		if ("NUMERIC".equalsIgnoreCase(type)) {
			return true;
		}

		if ("DECIMAL".equalsIgnoreCase(type)) {
			return true;
		}

		if ("CHAR".equalsIgnoreCase(type)) {
			return true;
		}

		if ("VARCHAR".equalsIgnoreCase(type)) {
			return true;
		}

		if ("LONGVARCHAR".equalsIgnoreCase(type)) {
			return true;
		}

		if ("DATE".equalsIgnoreCase(type)) {
			return true;
		}

		if ("TIME".equalsIgnoreCase(type)) {
			return true;
		}

		if ("TIMESTAMP".equalsIgnoreCase(type)) {
			return true;
		}

		if ("BINARY".equalsIgnoreCase(type)) {
			return false;
		}

		if ("VARBINARY".equalsIgnoreCase(type)) {
			return false;
		}

		if ("LONGVARBINARY".equalsIgnoreCase(type)) {
			return false;
		}

		if ("NULL".equalsIgnoreCase(type)) {
			return false;
		}

		if ("OTHER".equalsIgnoreCase(type)) {
			return false;
		}

		if ("JAVA_OBJECT".equalsIgnoreCase(type)) {
			return false;
		}

		if ("DISTINCT".equalsIgnoreCase(type)) {
			return false;
		}

		if ("STRUCT".equalsIgnoreCase(type)) {
			return false;
		}

		if ("ARRAY".equalsIgnoreCase(type)) {
			return false;
		}

		if ("BLOB".equalsIgnoreCase(type)) {
			return false;
		}

		if ("CLOB".equalsIgnoreCase(type)) {
			return false;
		}

		if ("REF".equalsIgnoreCase(type)) {
			return false;
		}

		if ("DATALINK".equalsIgnoreCase(type)) {
			return false;
		}

		if ("BOOLEAN".equalsIgnoreCase(type)) {
			return true;
		}

		if ("ROWID".equalsIgnoreCase(type)) {
			return false;
		}

		if ("NCHAR".equalsIgnoreCase(type)) {
			return true;
		}

		if ("NVARCHAR".equalsIgnoreCase(type)) {
			return true;
		}

		if ("LONGNVARCHAR".equalsIgnoreCase(type)) {
			return true;
		}

		if ("NCLOB".equalsIgnoreCase(type)) {
			return false;
		}

		if ("SQLXML".equalsIgnoreCase(type)) {
			return false;
		}

		return false;
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
	
	public String clearCache()
	{
		// serviceTableMap= new ConcurrentHashMap<>();
		// tableColumnMap= new ConcurrentHashMap<>();
		 //serviceAttrbMap= new ConcurrentHashMap<>();
		 userTableColumnMap= new ConcurrentHashMap<>();
		 userServiceAttrbMap=new ConcurrentHashMap<>();
		 jdbcTemplateMap =new ConcurrentHashMap<>();
		MultiServiceImpl.MultiServiceMap= new ConcurrentHashMap<>();
		ParamsValidator.clearCache();
		log.info( "Cache Cleared");
		return "Cache Cleared";
	}
	
	public boolean isTablePresent(String tableName,String apiKey, String dataStoreKey,String passToken) {
		List<Map<String, Object>> map = new ArrayList<>();
		try {
			JdbcTemplate jdbcTemp=setUserDataStore(apiKey, dataStoreKey, passToken);
			
			DriverManagerDataSource dsd=(DriverManagerDataSource) jdbcTemp.getDataSource();
			String url=dsd.getUrl();
			if(url!=null&&url.contains(":mysql:"))
			{
				map = jdbcTemp.queryForList("desc " + tableName);
			}else if(url!=null&&url.contains("jdbc:h2:"))
			{
				map = jdbcTemp.queryForList("show columns from	"+ tableName);
			}else
			{
				return jdbcTemp.execute(	new ConnectionCallback<Boolean>() {

					@Override
					public Boolean doInConnection(Connection con) throws SQLException, DataAccessException {
						
						ResultSet rs1 = con.getMetaData().getColumns(null, null,tableName.toUpperCase(), null);
						while(rs1.next())
						{
							return true;
						}
						
						return false;
					}
				});
			}
				
			if (map == null) {
				return false;
			}
			if (map.size() > 0) {
				log.info("table present");
				return true;
			}
		} catch (Exception e) {
			if(e  instanceof DatabaseAuthException)
			{
				throw e;
			}
			log.info(e.getMessage());
		}

		return false;
	}
	public static String removeLastCharOptional(String s) {
	    return Optional.ofNullable(s)
	      .filter(str -> str.length() != 0)
	      .map(str -> str.substring(0, str.length() - 1))
	      .orElse(s);
	    }
	private String replaceDoubleQute(String primaryKey) {
		if(primaryKey.charAt(0)=='"')
		{
			primaryKey=primaryKey.replaceFirst("\"", "");
		}
		if(primaryKey.charAt(primaryKey.length()-1)=='"')
		{
			primaryKey=removeLastCharOptional(primaryKey);
		}
		return primaryKey;
	}
}
